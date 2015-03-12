/**
 * MultiThreat Server.
 *
 */

package controller;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Deck;
import model.Game;
import utils.Protocol;


/**
 *
 * @author simorgh & dzigor92
 */
public class ServerController implements Runnable {
    private final int TIMEOUT = 500; /* Socket timeout in miliseconds */ 
    
    /* ServerController initial attributes */
    private Socket csocket;
    
    /* Server constant arg-relationed variables */
    private File deckfile;
    private static int strt_bet;
    private static int port;
    private static Deck deck;
    
    /* Game encapsulated fields */
    private Game g;
    private Protocol pr;
    private Boolean end;
    
    /** 
     * Constructor used on every new Runnable Thread.
     * ----------------------------------------------
     * Tiggred when new Client requests starting a new connection throught
     * the correct port.
     */
    ServerController(Socket csocket) {
        this.csocket = csocket;
        this.end = false;
        this.pr = null;
        this.g = null;
    }
    
    /**
     * Default main Constructor.
     * -------------------------
     * Gets overrided org.apache.commonds.CLI implementation 'ServerCLI'
     * to parse and control over all possible arguements as a HashMap.
     * @param strt_bet
     * @param port
     * @param deckfile
     * @see src/controller/ServerCLI.java
     */
    public ServerController(int strt_bet, int port, File deckfile ){
        this.strt_bet = strt_bet;
        this.port = port;
        this.deckfile = deckfile;
    }
        

    /**
     * starts the server.
     */
    public void start(){  
        ServerSocket serverSocket = null;
        
        try {
            this.deck = new Deck(this.deckfile);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            serverSocket = new ServerSocket(this.port);  /* Creem el servidor */
            System.out.println("Servidor socket preparat al port " + this.port);

            while (true) {
                System.out.println("Esperant una connexio d'un client...");
                Socket socket = serverSocket.accept(); /* Esperem a que un client es connecti amb el servidor */
                //socket.setSoTimeout(TIMEOUT);
                System.out.println("Connexio acceptada d'un client.");
                
                new Thread(new ServerController(socket)).start();
            }

        } catch (IOException ex) {
            System.out.println("Unable to establish connection through the port: " + this.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + this.port + "' on your shell to spot that connection");
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
            this.pr = new Protocol(this.csocket, true); /* Associem un flux d'entrada/sortida amb el client */
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.g = new Game(this.deck, this.strt_bet); /* creates new (shuffled) game */
        this.pr.recieveStart();
        try {
            this.pr.sendStartingBet(this.strt_bet);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            /* Client calling DRAW command is mandatory after a STARTING_BET has been received */
            try {
                if( (this.pr.readHeader()).equals(Protocol.DRAW)) serveCard();
            } catch (IOException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  
        

        /* game loop */       
        this.end = false;
        do{
            String cmd = null;
            if(this.csocket.isClosed()) this.end = true;
            try {
                cmd = this.pr.readHeader();
            } catch (IOException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
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
            this.pr.sendGains(this.g.computeGains());
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // end Thread run()
    
    
    
    
    private void serveCard(){
        char[] card;
        
        card = this.g.drawCard();
        this.g.updatePlayerScore(card[0]);

        try {
            this.pr.sendCard(card[0], card[1]);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.g.getPlayerScore() > 7.5f){
            try {
                this.pr.sendBusting();
                this.end = true;
            } catch (IOException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                              
    }
    
  
    
    

        
        
        
    
    
    
    
    
    
    
}
