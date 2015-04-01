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
public class ServerController implements Runnable {
    private final int TIMEOUT = 90000; /* Socket timeout in miliseconds */ 
    
    /* ServerController initial attributes */
    private Socket csocket;
    
    /* Server constant arg-relationed variables */
    private File deckfile;
    private static int strt_bet;
    private static int port;
    private static Deck deck;
    
    /* Game encapsulated fields */
    private Game g;
    private ServerProtocol pr;
    
    /** 
     * Constructor used on every new Runnable Thread.
     * ----------------------------------------------
     * Tiggred when new Client requests starting a new connection throught
     * the correct port.
     */
    ServerController(Socket csocket) {
        this.csocket = csocket;
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
        ServerController.strt_bet = strt_bet;
        ServerController.port = port;
        this.deckfile = deckfile;
    }
        

    /**
     * starts the server.
     */
    public void start(){  
        ServerSocket serverSocket = null;
        
        try {
            ServerController.deck = new Deck(this.deckfile);
        } catch (InvalidDeckFileException ex) {
            System.out.println("ERROR: " + this.deckfile.getName() + " doesn't contain the deck or has an incorrect format");
            System.exit(2);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null);
            System.exit(1);
        }
        
        
        try {
            serverSocket = new ServerSocket(ServerController.port);
            System.out.println("Server socket ready at port " + ServerController.port);

            while (true) {
                System.out.println("Waiting for a connection...");
                Socket socket = serverSocket.accept(); /* blocking instr. awaits until client requested connection */
                socket.setSoTimeout(TIMEOUT);
                System.out.println("Connection established! -> Creating new game.");
                
                new Thread(new ServerController(socket)).start();
            }
        } catch (IOException ex) {
            System.out.println("ERROR: Unable to establish connection through the port: " + ServerController.port
                    + ": Port might be protected or already being used.\n"
                    + "Run 'netstat -an | grep " + ServerController.port + "' on your shell to spot that connection");
        } finally {
            /* closing connection */
            try {
                if(serverSocket != null) serverSocket.close();
            } catch (IOException ex) {
                System.exit(1);
            }
        }
        
    } /* end main */

    
    
    
    @Override
    public void run() {
        /* establishing LogFile File stream */
        OutputStream logout = null;
        try {
            logout = new FileOutputStream("Server"+Thread.currentThread().getName()+".log");
            this.pr = new ServerProtocol(this.csocket, logout); /* binding IO stream to client */
        } catch (IOException ex) {
            this.pr.sendError(ServerProtocol.ERR_SSE);
            System.out.println("ERROR: Failed to open logfile OutputStream. - Connection aborted.");
            closeAll(logout);
        }
        
        /* creates new (shuffled) game */
        this.g = new Game(ServerController.deck, ServerController.strt_bet); 
        
        /* here comes the magic... */
        try {
            this.pr.receiveStart();
           
            /* a mandotry DRAW is expected after STBT, if not -> the protocol is broken! */
            this.pr.sendStartingBet(ServerController.strt_bet);
            String aux = this.pr.readHeader();
            if(aux.equals(ServerProtocol.DRAW)) serveCard();
            else throw new SyntaxErrorException();
                
            /* game loop */       
            do{
                String cmd;
                if(this.csocket.isClosed()) this.g.setFinished(true);
                
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
                        this.g.setFinished(true);
                        break;
                    
                    /* other valid 'header' (STRT) is received -> the protocol is broken! */
                    default: throw new SyntaxErrorException();    
                }
                
            } while(!this.g.isFinished());

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
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("ERROR Message received. Connection closed");
        } catch (IOException ex) {              /* broken pipe */
            System.out.println("ERROR: Cannot communicate to the peer. Connection interrupted.");
        } finally {
            closeAll(logout);
        }
        
    } /* end Thread run() */
    
    
    /**
     * Terminates thread and closes client resources
     */
    private void closeAll(OutputStream out) {
        try {
            if(out!=null) out.close();
            this.csocket.close();
            Thread.currentThread().stop();
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
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
            this.g.setFinished(true);
        }                              
    }
    
  
    
    

        
        
        
    
    
    
    
    
    

}
