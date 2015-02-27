package controller;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Deck;
import model.Game;
import utils.ComUtils;
import utils.Protocol;

/**
 *
 * @author simorgh & dzigor92
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket serverSocket=null;
        Socket socket=null;
        Protocol pr;
        Game g;
        
        File f = new File("deck.txt");
        
        Deck d = null;
        
        try {
            d = new Deck(f);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        int portServidor = 1234;
        int value;

        if (args.length > 1){
            System.out.println("Us: java Servidor [<numPort>]");
            System.exit(1);
        }

        if (args.length == 1)
            portServidor = Integer.parseInt(args[0]);

            try {
                serverSocket = new ServerSocket(portServidor);  /* Creem el servidor */
                System.out.println("Servidor socket preparat al port " + portServidor);

                while (true) {
                    System.out.println("Esperant una connexio d'un client...");
                    socket = serverSocket.accept(); /* Esperem a que un client es connecti amb el servidor */
                    System.out.println("Connexio acceptada d'un client.");
                    pr= new Protocol(socket); /* Associem un flux d'entrada/sortida amb el client */ 
                    
                    
                    //TODO: game logic for server comes here
                    g = new Game(d);
                    
                    pr.recieveStart();
                    g.getDeck().shuffle();
                    char[] card = g.drawCard();
                    g.updatePlayerScore(card[0]);
                    
                   
                    pr.sendCard(card[0], card[1]);
                    
                    
                    boolean end = false;
                    
                    
                    do{
                        String cmd = pr.readHeader();
                        switch(cmd){
                            case Protocol.DRAW:
                                card = g.drawCard();
                                g.updatePlayerScore(card[0]);
                                pr.sendCard(card[0], card[1]);
                                
                                if (g.getPlayerScore() > 7.5f){
                                    pr.sendBusting();
                                    end = true;
                                }
                                
                                break;
                            case Protocol.ANTE:
                                int raise = pr.recieveRaise();
                                g.raiseBet(raise);
                                break;
                            case Protocol.PASS:
                                end = true;
                                break;
                            default:
                                break;
                        }
         
                    } while(!end);
                    
                    g.playBank();
                    
                    pr.sendBankScore(g.getHandBank().size(), g.getHandBank(), g.getBankScore());
                    int gain = 0;
                    
                    if(g.getPlayerScore() > g.getBankScore() && g.getPlayerScore() <= 7.5f) gain = g.getBet();
                    else if(g.getPlayerScore() == g.getBankScore() && g.getPlayerScore() <= 7.5f) gain = 0;
                    else gain = -1*g.getBet();
                    
                    pr.sendGains(gain);
                    
                }
                
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } finally {
                /* Tanquem la comunicacio amb el client */
                try {
                    if(serverSocket != null) serverSocket.close();
                } catch (IOException ex) {
                    System.out.println("Els errors han de ser tractats correctament pel vostre programa");
                }
          }
    } // fi del main
    
    
}
