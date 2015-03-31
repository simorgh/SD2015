/**
 * MultiThreat Server Controller
 */

package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Deck;
import model.Game;
import utils.InvalidDeckFileException;
import utils.ProtocolErrorException;
import utils.Protocol;
import utils.SyntaxErrorException;
//import java.util.concurrent.TimeoutException;

/**
 * @author simorgh & dzigor92
 */
public class Controller {
    
    /* Socket timeout in miliseconds */ 
    private final int TIMEOUT = 10000;
    
    /* Controller initial attributes */
    private Selector selector;
    private ServerSocketChannel server;
    private SelectionKey serverkey;
    private final HashMap <SelectionKey, Protocol> connMap = new HashMap <>();

    /* Server constant arg-relationed variables */
    private final File deckfile;
    private final int strt_bet;
    private final int port;
    private Deck deck;
    
    
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
    public Controller(int strt_bet, int port, File deckfile ){
        this.strt_bet = strt_bet;
        this.port = port;
        this.deckfile = deckfile;
    }
        

    /**
     * starts the server.
     */
    public void start() {
        
        /* preparing server */
        loadDeck();
        initSelector();
        
        /* main loop */
        try {
            while(true) playTurn();
        } catch (InterruptedIOException iioe) {
            System.out.println("ERROR: Remote host timed out during read operation");
            //if( !( this.connMap.get(clientkey).sendError(Protocol.ERR_TIMEOUT) ) System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted."); 
        } catch (IOException ex) {              // broken pipe
            System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted.");
        } finally {
            //closeAll();
        }
   
    } /* end start */
    
    
    /**
     *
     * @param csocket
     * @return 
     */
    public Protocol initClientSocket(SocketChannel csocket){
        OutputStream logout = null;
        Protocol pr = null;
        
        try {
            logout = new FileOutputStream("Server"+Thread.currentThread().getName()+".log");
            pr = new Protocol(csocket, logout); /* binding IO stream to client */
        } catch (IOException ex) {
            pr.sendError(Protocol.ERR_SSE);
            System.out.println("ERROR: Failed to open logfile OutputStream. - Connection aborted.");
            closeClient(csocket);
        }
        
        return pr;
    }
    
    /**
     * 
     */ 
    private void loadDeck(){
        try {
            this.deck = new Deck(this.deckfile);
        } catch (InvalidDeckFileException ex) {
            System.out.println("ERROR: " + this.deckfile.getName() + " doesn't contain the deck or has an incorrect format");
            System.exit(2);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null);
            System.exit(1);
        }
    }
    
    /**
     * 
     */
    private void initSelector(){
        /* initialize ServerSocket &  Selector */
        try {
            
            this.server = ServerSocketChannel.open();
            this.server.socket().bind(new java.net.InetSocketAddress("127.0.0.1", this.port));
            this.server.configureBlocking(false);
           
            this.selector = Selector.open();
             this.serverkey = server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Selector Initialized: \t[serverKey] := " + serverkey.toString() );
        } catch (IOException ex) {
            System.out.println("ERROR: Unable to establish connection through the port: " + this.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + this.port + "' on your shell to spot that connection");
        } finally {
            /* closing connection */
            //closeAll();
            
        }
    }
 
    
    /**
     * Terminates thread and closes selector resources
     */
    private void closeAll(OutputStream out) {
        try {
            if(out!=null) out.close();
            if(this.server != null) server.close();
            selector.close();
        } catch (IOException ex) {
            //Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
    
     /**
     * closes client resources
     */
    private void closeClient(SocketChannel csocket) {
        try { csocket.close(); } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * - suport method -
     */
    private void serveCard(Game g, Protocol pr){
        char[] card;
        
        card = g.drawCard();
        g.updatePlayerScore(card[0]);
        pr.sendCard(card[0], card[1]);

        if (g.getPlayerScore() > 7.5f){
            pr.sendBusting();
            g.setFinished(true);
        }                              
    }
    
    
    
    /**
     * 
     * @throws IOException 
     */
    private void playTurn() throws IOException {
        System.out.println("\n@playTurn()");
        selector.select();
        
        Set<SelectionKey> keys = selector.selectedKeys();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            SelectionKey key = (SelectionKey) i.next();
            System.out.print(">> Selected key: \t"+key.toString());
            i.remove();

            if (key == this.serverkey) {
                System.out.println("\t[SERVER TURN]");
                if (key.isAcceptable()) {
                    System.out.println("\tWaiting for a connection...");
                    SocketChannel client = server.accept();

                    client.configureBlocking(false);
                    client.socket().setSoTimeout(TIMEOUT);

                    System.out.println("\tClient Connected...");
                    SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ); // read incoming stream

                    /* initializing new Game-model and socket */
                    connMap.put(clientkey, initClientSocket(client) );
                    clientkey.attach(new Game( this.deck, this.strt_bet) );
                }

            } else {
                System.out.println("\t[CLIENT TURN]");
                /* Client treatment */
                Protocol pr = connMap.get(key);
                Game g = (Game) key.attachment();
                
                // game loop treatment
                try {
                    
                    //do {
                        String cmd = null;
                        int raise = -1;

                        if( pr.isLastState(Protocol.ANTE) ){
                            raise = pr.receiveRaise();
                            if(raise != -1) cmd = Protocol.ANTE;
                        } else {
                            cmd = pr.readHeader();
                            if(pr.isLastState(Protocol.ANTE)) raise = pr.receiveRaise();
                        } 

                        if(cmd != null) {
                            switch(cmd) {
                                case Protocol.DRAW:
                                    serveCard(g, pr);
                                    break;

                                case Protocol.ANTE:
                                    g.raiseBet(raise);
                                    //serveCard(g, pr);
                                    break;

                                case Protocol.PASS:
                                    g.setFinished(true);
                                    break;

                                case Protocol.START:
                                    pr.sendStartingBet(this.strt_bet);
                                    break;
                                /* other valid 'header' (STRT) is received -> the protocol is broken! */
                                default: throw new SyntaxErrorException();
                            }

                            if(g.isFinished()){
                                /* endgame status messages */
                                g.playBank();
                                pr.sendBankScore(g.getHandBank().size(), g.getHandBank(), g.getBankScore());
                                pr.sendGains(g.computeGains());
                                //closeAll();
                            }
                        } 
                    //} while(pr.isDataReady());
                } catch (SyntaxErrorException e) {      // syntax error treatment
                    System.out.println("ERROR: Client message has Syntax Error - Connection aborted.");
                    pr.sendError(Protocol.ERR_SYNTAX);
                } catch (ProtocolErrorException e) {    // ERRR header received
                    try {
                        pr.receiveErrorDescription();
                    } catch (IOException | SyntaxErrorException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("ERROR Message received. Connection closed");
                }
                
            }
        } // for end
    
    }
    
   
}
