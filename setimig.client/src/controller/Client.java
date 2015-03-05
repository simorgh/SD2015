package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import model.Game;
import utils.ClientCLI;
import utils.Protocol;
import view.Console;

/**
 *
 * @author simorgh & dzigor92
 */
public class Client {
    private static final String ERROR_OPT = "!! Wrong option. Please enter a valid action.";
     private static ClientCLI cli;
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String nomMaquina, str;
        int port;
        Console console = new Console();
        Game g;

        InetAddress maquinaServidora;
        Socket socket = null;
        Protocol pr;
        
        /* Command Line arguments threatment */
        cli = new ClientCLI(args);
        nomMaquina = cli.getServer();
        port = cli.getPort();
        
        try{
            //maquinaServidora = InetAddress.getByName(nomMaquina); /* Obtenim la IP de la maquina servidora */
            socket = new Socket(nomMaquina/*maquinaServidora*/, port); /* Obrim una connexio amb el servidor */
            console.showConnection(socket);
            pr = new Protocol(socket);
            g = new Game();
            
            console.printWelcome();
            pr.sendStart();
            int str_bet = pr.recieveStartingBet();
            console.printStartingBet(str_bet);
            
            pr.sendDraw();  /* DRAW command is mandatory after a STARTING_BET is received */
            char [] card = pr.recieveCard();
            console.printNewCard(card);
            g.updatePlayerScore(card[0]);
            
            // game loop
            boolean end = false;
            do{
                char opt = console.printInGameOptions(g.getPlayerScore());
                switch(opt){
                    case '1': 
                        pr.sendDraw();
                        card = pr.recieveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                         
                        if(g.isBusted()){ 
                            pr.recieveBusting();
                            end = true;
                        }
                        break;
                        
                    case '2': 
                        int rise = console.enterRaise();
                        pr.sendAnte(rise);
                        pr.sendDraw();
                        card = pr.recieveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                        
                        if(g.isBusted()){ 
                            pr.recieveBusting();
                            end = true;
                        } 
                        break;
                        
                    case '3': 
                        pr.sendPass();
                        end = true;
                        break;
                    default: 
                        console.printError(Client.ERROR_OPT);
                        break;
                }  
            } while(!end);
            
            ArrayList <String> bank_score = pr.recieveBankScore();
            if(bank_score != null) console.printBankScore(bank_score);
            else{
                System.err.print("Error al rebre BKSC!");
            }
            
            int gain = pr.recieveGains();
            console.printGains(gain);
              
        } catch (IOException e) {
            System.out.println("Els errors han de ser tractats correctament en el vostre programa.");
        } finally {
            try {
                if(socket != null) socket.close();
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch    
        }
    } // fi del main
    
}
