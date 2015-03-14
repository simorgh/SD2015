/**
 * Client Controller
 */

package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Game;
import utils.Protocol;
import utils.ProtocolErrorException;
import view.Console;

/**
 * @author simorgh & dzigor92
 */
public class ClientController {
    private static final String ERROR_OPT = "ERROR: Wrong option. Please enter a valid action.";
    
    /* Client constant arg-relationed variables */
    private final String nomMaquina;
    private final int port;
    private float topcard;
    private final boolean autoplay;
    
    
    /**
     * Autoplay Enabled Constructor.
     * -------------------------
     * Gets overrided org.apache.commonds.CLI implementation 'ClientCLI'
     * to parse and control over all possible arguments as a HashMap.
     * 
     * @param server
     * @param port
     * @param topcard
     */
    public ClientController(String server, int port, float topcard) {
        this.nomMaquina = server;
        this.port = port;
        this.topcard = topcard;
        this.autoplay = true;
    }
    
    /**
     * Autoplay Disabled Constructor.
     * -------------------------
     * Gets overrided org.apache.commonds.CLI implementation 'ClientCLI'
     * to parse and control over all possible arguments as a HashMap.
     * 
     * @param server
     * @param port
     */
    public ClientController(String server, int port) {
        this.nomMaquina = server;
        this.port = port;
        this.autoplay = false;
    }
    
    
    /**
     * starts a new client connection
     */
    public void start() {
        Console console = new Console();
        Game g;
        Socket socket = null;
        Protocol pr = null;

        try{
            socket = new Socket(InetAddress.getByName(nomMaquina), port); // Obrim una connexio amb el servidor
            console.showConnection(socket);
            pr = new Protocol(socket);
            g = new Game();
            
            console.printWelcome();
            pr.sendStart();
            int str_bet = pr.receiveStartingBet();
            console.printStartingBet(str_bet);
            
            pr.sendDraw();  /* DRAW command is mandatory after a STARTING_BET is received */
            char [] card = pr.receiveCard();
            console.printNewCard(card);
            g.updatePlayerScore(card[0]);
            
            // game loop
            boolean end = false;
            do{
                char opt;
                if(!autoplay) opt = console.printInGameOptions(g.getPlayerScore());
                else opt = choseOptionAutoplay(g.getPlayerScore(), topcard);
                switch(opt){
                    case '1': 
                        pr.sendDraw();
                        card = pr.receiveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                         
                        if(g.isBusted()){ 
                            pr.receiveBusting();
                            end = true;
                        }
                        break;
                        
                    case '2': 
                        int rise = console.enterRaise();
                        pr.sendAnte(rise);
                        pr.sendDraw();
                        card = pr.receiveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                        
                        if(g.isBusted()){ 
                            pr.receiveBusting();
                            end = true;
                        } 
                        break;
                        
                    case '3': 
                        pr.sendPass();
                        end = true;
                        break;
                    default: 
                        console.printError(ClientController.ERROR_OPT);
                        break;
                }  
            } while(!end);
            
            ArrayList <String> bank_score = pr.receiveBankScore();
            if(bank_score != null) console.printBankScore(bank_score);
            else{
                System.err.print("Error al rebre BKSC!");
            }
            
            int gain = pr.receiveGains();
            console.printGains(gain);
              
        } catch(ProtocolErrorException e){
            try {
                String des = pr.receiveErrorDescription();
                console.printError(Protocol.ERROR + des);
            } catch (IOException ex) {
                console.printError("ERROR: There was a problem trying to close the socket connection.");
            }     
        } catch(IOException e) {
            console.printError("ERROR: A problem with the server appeared - Closing now the connection.");
            if(pr.sendError(ERROR_OPT)) console.printError("ERROR: cannot communicate the message to the server.");
        } finally {
            try {
                if(socket != null) socket.close();
            } catch (IOException ex) {
                console.printError("ERROR: There was a problem trying to close the socket connection.");
            } // fi del catch    
        }
    } // fi del main
  
    
    
    /**
     * Autoplay option choser. The method implements the behaviour that the automatic ClientController will
     * follow when topcard option is activated.
     * 
     * @param currentScore Current client's score.
     * @param autoplay The score to reach by client.
     * @return Option to chose. If the desired score is reached, the method will return 'Pass' option. If not, the method will return 'Draw'. 
     */
    private static char choseOptionAutoplay(float currentScore, float autoplay){
        char opt = '0';
        if(currentScore >= autoplay) opt = '3';  
        else opt = '1';
        return opt;
    }
    
}
