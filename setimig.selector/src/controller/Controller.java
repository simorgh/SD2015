/**
 * Selector Server Controller
 */

package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
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
        // preparing server...
        loadDeck();
        initSelector();
        
        // main loop
        try {
            while(true) playTurn();
        } catch (IOException ex) {  // broken pipe
            System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted.");
        } finally {
            closeServer();
        }
   
    } // end start
    
    
    /**
     *
     * @param csocket
     * @param key
     * @return 
     */
    public Protocol initClientSocket(SocketChannel csocket, SelectionKey key){
        Protocol pr = null;
        
        try {
            pr = new Protocol(csocket, new FileOutputStream("Server" + key.toString().substring(key.toString().indexOf('@') )+ ".log") ); /* binding IO stream to client */
        } catch (IOException ex) {
            if(!pr.sendError(Protocol.ERR_SSE));
            System.out.println("ERROR: Failed to open logfile. Connection aborted."); 
            try {
                csocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
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
            //Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null);
            System.exit(1);
        }
    }
    
    /**
     * initialize ServerSocket &  Selector.
     */
    private void initSelector(){

        try {
            this.server = ServerSocketChannel.open();
            this.server.socket().bind(new java.net.InetSocketAddress(this.port) );
            this.server.configureBlocking(false);
           
            this.selector = Selector.open();
            this.serverkey = server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Selector Initialized: \t[serverKey] := " + serverkey.toString() );
        } catch (IOException ex) {
            System.out.println("ERROR: Unable to establish connection through the port " + this.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + this.port + "' on your shell to spot that connection.");
            closeServer();
        }
    }
 
    
    /**
     * Terminates thread and closes selector resources
     */
    private void closeServer() {
        try {
            if(this.server != null)
                if(this.server.isOpen()) this.server.close();
            if(this.selector != null)
                if(this.selector.isOpen()) this.selector.close();
        } catch (IOException ex) {
            System.exit(2);
        }
        System.exit(1);
    }
    
    
    
    /**
     * - suport method -
     */
    private void serveCard(Game g, Protocol pr){
        char[] card;
        
        card = g.drawCard();
        g.updatePlayerScore(new String(card));
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

                    System.out.println("\tnew Client Connected!");
                    SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ); // read incoming stream

                    /* initializing new Game-model and socket */
                    connMap.put(clientkey, initClientSocket(client, clientkey) );
                    clientkey.attach(new Game( this.deck, this.strt_bet) );
                }

            } else {
                System.out.println("\t[CLIENT TURN]");
                
                // retrieve Client game & status data
                Protocol pr = connMap.get(key);
                Game g = (Game) key.attachment();

                try {
                    do {  // loop while received client data is enough
                        String cmd = null;
                        int raise = -1;
                        pr.showState();
                        
                        if( pr.isCurrentState(Protocol.ANTE) ){
                            System.out.println("Current state is ANTE (1)... receiving RAISE");
                            raise = pr.receiveRaise();
                            if(raise != -1) cmd = Protocol.ANTE;
                        } else {
                            if( pr.isCurrentState(Protocol.ANTE) ){
                                System.out.println("Current state is ANTE (0)... receiving RAISE");
                                raise = pr.receiveRaise();
                            } else {
                                cmd = pr.readHeader();
                                //pr.showState();
                                
                                // let's deal with protocol restrictive situations...
                                if( pr.isLastState(Protocol.START) && ( !cmd.isEmpty() && !cmd.equals(Protocol.DRAW) ) ) throw new SyntaxErrorException();
                                if( cmd.equals(Protocol.START) && (g.getPlayerScore() > 0.0f) ) throw new SyntaxErrorException();
                            }
                        }

                        if(cmd != null) {
                            switch(cmd) {
                                case Protocol.DRAW:
                                    serveCard(g, pr);
                                    break;

                                case Protocol.ANTE:
                                    g.raiseBet(raise);
                                    break;

                                case Protocol.PASS:
                                    g.setFinished(true);
                                    break;

                                case Protocol.START:
                                    pr.sendStartingBet(this.strt_bet);
                                    break;
                                    
                                default: throw new SyntaxErrorException();
                            }
                        }
                        
                        if(g.isFinished()){
                            // endgame status messages.
                            g.playBank();
                            pr.sendBankScore(g.getHandBank().size(), g.getHandBank(), g.getBankScore());
                            pr.sendGains(g.computeGains());
                            
                            pr.close();
                            key.cancel();
                            //key.channel().close();
                        }
                        
                    } while( pr.isDataReady() && !g.isFinished() );
                
                
                 
                } catch (InterruptedIOException e){
                    System.out.println("ERROR: Remote host timed out during read operation");
                    if( !( pr.sendError(Protocol.ERR_TIMEOUT) ) )
                        System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted."); 

                    key.cancel();
                    pr.close();
                    
                } catch (SyntaxErrorException e) {      // syntax error treatment
                    System.out.println("ERROR: Client message has Syntax Error - Connection aborted.");
                    pr.sendError(Protocol.ERR_SYNTAX);
 
                    key.cancel();
                    pr.close();
                   
                } catch (ProtocolErrorException e) {    // ERRO header received
                    try {
                        String err = pr.receiveErrorDescription();
                        System.out.println("ERROR " + err);
                    } catch (IOException | SyntaxErrorException ex) {
                        System.out.println("ERROR Message received. Connection closed");
                    }
                    
                    key.cancel();
                    pr.close();
                    
                } catch(IOException e) {
                    System.out.println("ERROR: Broken Pipe. Closing client socket");
                    
                    key.cancel();
                    pr.close();
                }
                
            } // client treatment (end)
        } // looping over selectedkeys (end)
    }
    
   
}
