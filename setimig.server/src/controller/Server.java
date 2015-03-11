/**
 * MultiThreat Server.
 *
 */

package controller;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Deck;
import model.Game;
import utils.Protocol;
import utils.ServerCLI;


/**
 *
 * @author simorgh & dzigor92
 */
public class Server implements Runnable {
    /* Server initial attributes */
    private Socket csocket;
    private static ServerCLI cli;
    
    /* Server constant arg-relationed variables */
    private static File deckfile;
    private static int strt_bet;
    private static int port;
    private static Deck deck;
    
    /* Game encapsulated fields */
    private Game g;
    private Protocol pr;
    private Boolean end;
    
    Server(Socket csocket) {
        this.csocket = csocket;
        this.end = false;
        this.pr = null;
        this.g = null;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        
        /* Command Line arguments threatment */
        cli = new ServerCLI(args);
        Server.strt_bet = cli.getStartingBet();
        Server.port = cli.getPort();
        Server.deckfile = cli.getDeckfile();
        
        try {
            Server.deck = new Deck(Server.deckfile);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            serverSocket = new ServerSocket(Server.port);  /* Creem el servidor */
            System.out.println("Servidor socket preparat al port " + Server.port);

            while (true) {
                System.out.println("Esperant una connexio d'un client...");
                Socket socket = serverSocket.accept(); /* Esperem a que un client es connecti amb el servidor */
                System.out.println("Connexio acceptada d'un client.");
                
                new Thread(new Server(socket)).start();
            }

        } catch (IOException ex) {
            System.out.println("Unable to establish connection through the port: "+Server.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + Server.port + "' on your shell to spot that connection");
        } finally {
            /* Tanquem la comunicacio amb el client */
            try {
                if(serverSocket != null) serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            }
        }
        

    } // fi del main

    
    @Override
    public void run() {
        try {
            this.pr = new Protocol(this.csocket); /* Associem un flux d'entrada/sortida amb el client */
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.g = new Game(Server.deck, Server.strt_bet); /* creates new (shuffled) game */
        this.pr.recieveStart();
        
        try {
            this.pr.sendStartingBet(Server.strt_bet);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            /* Client calling DRAW command is mandatory after a STARTING_BET has been received */
            try {
                if( (this.pr.readHeader()).equals(Protocol.DRAW)) serveCard();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  
        

        /* game loop */       
        this.end = false;
        do{
            String cmd = null;
            try {
                cmd = this.pr.readHeader();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(cmd){
                case Protocol.DRAW:
                    serveCard(); 
                    break;

                case Protocol.ANTE:
                    int raise = pr.recieveRaise();
                    this.g.raiseBet(raise);
                    break;

                case Protocol.PASS:
                    this.end = true;
                    break;

                default: break;
            }
        } while(!this.end);

        this.g.playBank();  
        try { 
            this.pr.sendBankScore(this.g.getHandBank().size(), this.g.getHandBank(), this.g.getBankScore());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try { 
            this.pr.sendGains(this.g.computeGains());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // end Thread run()
    
    
    
    
    private void serveCard(){
        char[] card;
        
        card = this.g.drawCard();
        this.g.updatePlayerScore(card[0]);

        try {
            this.pr.sendCard(card[0], card[1]);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.g.getPlayerScore() > 7.5f){
            try {
                this.pr.sendBusting();
                this.end = true;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                              
    }
    
  
    
    

        
        
        
    
    
    
    
    
    
    
}
