package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import model.Game;
import utils.Protocol;
import view.Console;

/**
 *
 * @author simorgh & dzigor92
 */
public class Client {
    private static final String ERROR_OPT = "!! Wrong option. Please enter a valid action.";
    
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
        
/* TODO: Uncomment to get args info    
        if (args.length < 4 || args.length > 4 ){
            System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-a topcard]");
            System.exit(1);
        }

        nomMaquina = args[0];
        port  = Integer.parseInt(args[1]);  */
        
// <test values>
        nomMaquina = "localhost";
        port = 1234;
// <test values>
        
        try{
            maquinaServidora = InetAddress.getByName(nomMaquina); /* Obtenim la IP de la maquina servidora */
            socket = new Socket(maquinaServidora/*"10.111.66.40"*/, port); /* Obrim una connexio amb el servidor */
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
            console.printBankScore(bank_score);
            
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
