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
    
    private static File deckfile;
    private static int strt_bet;
    private static int port; 
    private static Deck deck;
    
    MultiThreadServer(Socket csocket) {
        this.csocket = csocket;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
    /* <test values> */
        deckfile = new File("deck.txt");
        strt_bet = 10;
        port = 1234;
    /* <test values> */
        
        if ( (args.length == 1 && args[0].equals("-h")) ) {
            System.out.println("Us: java Server -p <port> -b <starting_bet> -f <deckfile>");
            System.exit(1);
        }

        // TODO: control args input insurance
        if (args.length == 6){
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an integer.");
                System.exit(2);
            }
            
            try {
                strt_bet = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[3] + " must be an integer.");
                System.exit(2);
            }
            
            deckfile = new File(args[5]);
            if(!deckfile.exists() || deckfile.isDirectory()) {
                System.err.println("Wrong filename" + args[3] + ".");
                System.exit(2);
            }
        }
        
        try {
            deck = new Deck(deckfile);
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try{
            serverSocket = new ServerSocket(port);  /* Creem el servidor */
            System.out.println("Servidor socket preparat al port " + port);

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
        Protocol pr = null; 
        try {
            pr = new Protocol(csocket); /* Associem un flux d'entrada/sortida amb el client */
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Game g = new Game(deck, strt_bet); /* creates new (shuffled) game */
        pr.recieveStart();

        try {
            pr.sendStartingBet(strt_bet);
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        // game loop        
        char[] card;
        boolean end = false;
        do{
            String cmd = null;
            try {
                cmd = pr.readHeader();
            } catch (IOException ex) {
                Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(cmd){
                case Protocol.DRAW:
                    card = g.drawCard();
                    g.updatePlayerScore(card[0]);
            
                    try {
                        pr.sendCard(card[0], card[1]);
                    } catch (IOException ex) {
                        Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
            
                    if (g.getPlayerScore() > 7.5f){
                        try {
                            pr.sendBusting();
                            end = true;
                        } catch (IOException ex) {
                            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }                               
                    break;

                case Protocol.ANTE:
                    int raise = pr.recieveRaise();
                    g.raiseBet(raise);
                    break;

                case Protocol.PASS:
                    end = true;
                    break;

                default: break;
            }
        } while(!end);

        g.playBank();  
        try { 
            pr.sendBankScore(g.getHandBank().size(), g.getHandBank(), g.getBankScore());
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try { 
            pr.sendGains(g.computeGains());
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
