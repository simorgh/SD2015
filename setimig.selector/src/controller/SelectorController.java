/**
 * MultiThreat Server Controller
 */

package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Deck;
import model.Game;
import utils.InvalidDeckFileException;
import utils.ProtocolErrorException;
import utils.ServerProtocol;
import utils.SyntaxErrorException;
//import java.util.concurrent.TimeoutException;

/**
 * @author simorgh & dzigor92
 */
public class SelectorController {
    private final int TIMEOUT = 10000; /* Socket timeout in miliseconds */ 
    
    /* SelectorController initial attributes */
    private static SelectionKey serverkey;
    
    /* Server constant arg-relationed variables */
    private File deckfile;
    private static int strt_bet;
    private static int port;
    private static Deck deck;
    
    /* Game encapsulated fields */
    private Game g;
    private ServerProtocol pr;
    private Boolean end;
    
    
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
    public SelectorController(int strt_bet, int port, File deckfile ){
        SelectorController.strt_bet = strt_bet;
        SelectorController.port = port;
        this.deckfile = deckfile;
    }
        

    /**
     * starts the server.
     */
    public void start() {
        /* load deck */
        try {
            SelectorController.deck = new Deck(this.deckfile);
        } catch (InvalidDeckFileException ex) {
            System.out.println("ERROR: " + this.deckfile.getName() + " doesn't contain the deck or has an incorrect format");
            System.exit(2);
        } catch (IOException ex) {
            Logger.getLogger(SelectorController.class.getName()).log(Level.SEVERE, null);
            System.exit(1);
        }
        
        /* init selector */
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.socket().bind(new java.net.InetSocketAddress(1212));
            server.configureBlocking(false);
            SelectorController.serverkey = server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            System.out.println("ERROR: Unable to establish connection through the port: " + SelectorController.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + SelectorController.port + "' on your shell to spot that connection");
        } finally {
            /* closing connection */
            try {
                if(server != null) server.close();
            } catch (IOException ex) {
                System.exit(1);
            }
        }
        
        /* here comes the magic... */
        try { 
            do {
                selector.select();
                Set keys = selector.selectedKeys();
                for (Iterator i = keys.iterator(); i.hasNext();) {
                        SelectionKey key = (SelectionKey) i.next();
                        i.remove();

                        if (key == serverkey) {
                            if (key.isAcceptable()) {
                                System.out.println("Waiting for a connection...");
                                SocketChannel client = server.accept();
                                client.configureBlocking(false);
                                client.socket().setSoTimeout(TIMEOUT);
                                System.out.println("Connection established! -> Creating new game.");
                                SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ);
                                clientkey.attach(0);
                                
                                initClient(client.socket());
                            }
                            
                        } else {
                            // Client treatment
                            

                        }
                }
            } while(!this.end);
            
        } catch (InterruptedIOException iioe) {
            System.out.println("ERROR: Remote host timed out during read operation");
            if(!pr.sendError(ServerProtocol.ERR_TIMEOUT)) System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted."); 
        /*} catch (SyntaxErrorException e) {      // syntax error treatment
            System.out.println("ERROR: Client message has Syntax Error - Connection aborted.");
            this.pr.sendError(ServerProtocol.ERR_SYNTAX);
        } catch (ProtocolErrorException e) {    // ERRR header received
            try {
                this.pr.receiveErrorDescription();
            } catch (IOException | SyntaxErrorException ex) {
                Logger.getLogger(SelectorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("ERROR Message received. Connection closed");
        */} catch (IOException ex) {              // broken pipe
            System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted.");
        } finally {
            //closeAll(logout);
        }
   
    } /* end main */

    
    /**
     *
     * @param csocket
     */
    public void initClient(Socket csocket){
        /* establishing LogFile File stream */
        OutputStream logout = null;
        try {
            logout = new FileOutputStream("Server"+Thread.currentThread().getName()+".log");
            this.pr = new ServerProtocol(csocket, logout); /* binding IO stream to client */
        } catch (IOException ex) {
            this.pr.sendError(ServerProtocol.ERR_SSE);
            System.out.println("ERROR: Failed to open logfile OutputStream. - Connection aborted.");
            closeAll(logout, csocket);
        }
        
        /* creates new (shuffled) game */
        this.g = new Game(SelectorController.deck, SelectorController.strt_bet); 
    }
    
    
     /**
     * Terminates thread and closes client resources
     */
    private void closeAll(OutputStream out, Socket csocket) {
        try {
            if(out!=null) out.close();
            csocket.close();
            Thread.currentThread().stop();
        } catch (IOException ex) {
            Logger.getLogger(SelectorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * - suport method -
     */
    private void serveCard(){
        char[] card;
        
        card = this.g.drawCard();
        this.g.updatePlayerScore(card[0]);
        this.pr.sendCard(card[0], card[1]);

        if (this.g.getPlayerScore() > 7.5f){
            this.pr.sendBusting();
            this.end = true;
        }                              
    
    
    
    
    
    
/////////////////////////////////
//    RESIDUOS DEL MULTITHREAD
/////////////////////////////////
    
    public void run() {
        
        
        /* here comes the magic... */
        try {
            this.pr.receiveStart();
           
            /* a mandotry DRAW is expected after STBT, if not -> the protocol is broken! */
            this.pr.sendStartingBet(SelectorController.strt_bet);
            String aux = this.pr.readHeader();
            if(aux.equals(ServerProtocol.DRAW)) serveCard();
            else throw new SyntaxErrorException();
                
                
            /* game loop */       
            this.end = false;
            do{
                String cmd;
                if(this.csocket.isClosed()) this.end = true;
                
                cmd = this.pr.readHeader();
                switch(cmd) {
                    case ServerProtocol.DRAW:
                        serveCard(); 
                        break;

                    case ServerProtocol.ANTE:
                        int raise = pr.receiveRaise();
                        this.g.raiseBet(raise);
                        break;

                    case ServerProtocol.PASS:
                        this.end = true;
                        break;
                    
                    /* other valid 'header' (STRT) is received -> the protocol is broken! */
                    default: throw new SyntaxErrorException();    
                }
                
            } while(!this.end);

            /* endgame status messages */
            this.g.playBank();
            this.pr.sendBankScore(this.g.getHandBank().size(), this.g.getHandBank(), this.g.getBankScore());
            this.pr.sendGains(this.g.computeGains());

        } catch (InterruptedIOException iioe) {
            System.out.println("ERROR: Remote host timed out during read operation");
            if(!pr.sendError(ServerProtocol.ERR_TIMEOUT)) System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted."); 
        } catch (SyntaxErrorException e) {      /* syntax error treatment */
            System.out.println("ERROR: Client message has Syntax Error - Connection aborted.");
            this.pr.sendError(ServerProtocol.ERR_SYNTAX);
        } catch (ProtocolErrorException e) {    /* ERRR header received */
            try {
                this.pr.receiveErrorDescription();
            } catch (IOException | SyntaxErrorException ex) {
                Logger.getLogger(SelectorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("ERROR Message received. Connection closed");
        } catch (IOException ex) {              /* broken pipe */
            System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted.");
        } finally {
            closeAll(logout);
        }
        
    } /* end Thread run() */
    
    
   
    
  
    
    

        
        
        
    
    
    
    
    
    

}
