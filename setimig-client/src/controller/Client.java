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
        int numPort, value;
        Console console = new Console();
        Game g;

        InetAddress maquinaServidora;
        Socket socket = null;
        Protocol pr;
/*
        if (args.length != 2){
            System.out.println("Us: java Client <maquina_servidora> <port>");
            System.exit(1);
        }

        nomMaquina = args[0];
        numPort    = Integer.parseInt(args[1]); 
*/
        nomMaquina = "localhost";
        numPort = 1234;
        
        try{
            
            maquinaServidora = InetAddress.getByName(nomMaquina); /* Obtenim la IP de la maquina servidora */
            socket = new Socket(maquinaServidora, numPort); /* Obrim una connexio amb el servidor */
            pr = new Protocol(socket);
            g = new Game();
            
            console.printWelcome();
            pr.sendStart();
            int str_bet = pr.recieveStartingBet();
            console.printStartingBet(str_bet);
            
            // game loop
            boolean end = false;
            do{
                char opt = console.printInGameOptions(g.getPlayerScore());
                char[] card;
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
