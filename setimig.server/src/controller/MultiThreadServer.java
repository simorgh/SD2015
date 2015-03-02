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

/**
 *
 * @author simorgh & dzigor92
 */
public class MultiThreadServer implements Runnable{
    private Socket csocket;
    
    /* Server constant arg-relationed variables */
    private static File deckfile;
    private static int strt_bet;
    private static int port;
    private static Deck deck;
    
    /* Game encapsulated fields */
    private Game g;
    private Protocol pr;
    private Boolean end;
    
    MultiThreadServer(Socket csocket) {
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
        
/* TODO: Uncomment to get args info    
        if ( (args.length != 6 || args[0].equals("-h")) ) {
            System.out.println("Us: java Server -p <port> -b <starting_bet> -f <deckfile>");
            System.exit(1);
        }

        // TODO: control args input insurance (HashTable??)
        if (args.length == 6){
            try {
                MultiThreadServer.port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an integer.");
                System.exit(2);
            }
            
            try {
                MultiThreadServer.strt_bet = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[3] + " must be an integer.");
                System.exit(2);
            }
            
            MultiThreadServer.deckfile = new File(args[5]);
            if(!MultiThreadServer.deckfile.exists() || MultiThreadServer.deckfile.isDirectory()) {
                System.err.println("Wrong filename" + args[3] + ".");
                System.exit(2);
            }
        }*/
        
// <test values>
        MultiThreadServer.deckfile = new File("deck.txt");
        MultiThreadServer.strt_bet = 10;
        MultiThreadServer.port = 1234;
// <test values>
        
        try {
            MultiThreadServer.deck = new Deck(MultiThreadServer.deckfile);
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try{
            serverSocket = new ServerSocket(MultiThreadServer.port);  /* Creem el servidor */
            System.out.println("Servidor socket preparat al port " + MultiThreadServer.port);

            while (true) {
                System.out.println("Esperant una connexio d'un client...");
                Socket socket = serverSocket.accept(); /* Esperem a que un client es connecti amb el servidor */
                System.out.println("Connexio acceptada d'un client.");
                
                new Thread(new MultiThreadServer(socket)).start();
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

    
    @Override
    public void run() {
        try {
            this.pr = new Protocol(this.csocket); /* Associem un flux d'entrada/sortida amb el client */
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.g = new Game(MultiThreadServer.deck, MultiThreadServer.strt_bet); /* creates new (shuffled) game */
        this.pr.recieveStart();
        
        try {
            this.pr.sendStartingBet(MultiThreadServer.strt_bet);
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            /* Client calling DRAW command is mandatory after a STARTING_BET has been received */
            try {
                if( (this.pr.readHeader()).equals(Protocol.DRAW)) serveCard();
            } catch (IOException ex) {
                Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  
        

        /* game loop */       
        this.end = false;
        do{
            String cmd = null;
            try {
                cmd = this.pr.readHeader();
            } catch (IOException ex) {
                Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try { 
            this.pr.sendGains(this.g.computeGains());
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void serveCard(){
        char[] card;
        
        card = this.g.drawCard();
        this.g.updatePlayerScore(card[0]);

        try {
            this.pr.sendCard(card[0], card[1]);
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.g.getPlayerScore() > 7.5f){
            try {
                this.pr.sendBusting();
                this.end = true;
            } catch (IOException ex) {
                Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                              
    }
    
    
}
