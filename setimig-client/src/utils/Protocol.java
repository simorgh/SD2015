/**
 * Intended Status: Proposed Standard  (University of Barcelona)
 * Expires: 12/03/2015
 *
 * A protocol for the 7 and a half game. (CLIENT abstraction)
 *  This protocol is used for the client/server version of the  popular
 *  card game ’seven and a half’.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simorgh & dzigor92
 */
public class Protocol extends utils.ComUtils{
    /* Protocol Commands Semantics */
    public static final String START = "STRT";
    public static final String DRAW = "DRAW";
    public static final String ANTE = "ANTE";
    public static final String PASS = "PASS";
    public static final String STARTING_BET = "STBT";        
    public static final String CARD = "CARD";
    public static final String BUSTING = "BSTG";
    public static final String BANK_SCORE = "BKSC";
    public static final String GAINS = "GAIN";
    public static final String ERROR = "ERRO";


    public Protocol(File file) throws IOException {
        super(file);    
    }
    
    public Protocol(Socket socket) throws IOException {
        super(socket);
    }
    
    /* support method */
    private void sendHeader(String str) throws IOException{ 
        write_string_command(str);
    }
    
//////////////////////////////////////////////////////////////
//                  CLIENT MESSAGES (Player Commands)
//////////////////////////////////////////////////////////////
    
    /**
     * START. - STRT
     * 
     * This command is used for starting a new game. Once this command
     * is sent a STARTING_BET message is expected.
     * 
     * @throws IOException 
     */           
    public void sendStart() throws IOException{
        sendHeader(Protocol.START);
    }
    
    /**
     * DRAW. - DRAW
     * 
     * This command is used for asking for a new  card. This command is
     * mandatory after a STARTING_BET is received. Once a DRAW command
     * is sent a CARD message is expected.
     * 
     * If the sum of all the cards is bigger than 7 1/2 a following
     * BUSTING message is received. The resulting state is the same as
     * after the player sends a PASS command, thus a BANK_SCORE and
     * GAINS message are expected.
     * 
     * @throws java.io.IOException
     */           
    public void sendDraw() throws IOException{
        sendHeader(Protocol.DRAW);
    }
    
    /**
     * ANTE. - ANTE SP/NUMBER
     * 
     * This command is optional. It has to be issued before the DRAW
     * command. It is used for increasing the bet for this game.
     * 
     * @param raise
     * @throws IOException 
     */
    public void sendAnte(int raise) throws IOException{
        sendHeader(this.ANTE);
        write_char(' ');
        write_int32(raise);
    }

    /**
     * PASS. - PASS
     * 
     * This command is used for passing  the turn to the Bank. It is
     * used instead of a DRAW command. Once this command is sent a
     * BANK_SCORE message and GAINS message are expected.
     *
     * @throws IOException 
     */
    public void sendPass() throws IOException{
        sendHeader(Protocol.PASS);
    }
    
    
    
//////////////////////////////////////////////////////////////
//               SERVER RECEPTIONS VERIFICATION
//////////////////////////////////////////////////////////////
    public boolean recieveStartingBet(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.STARTING_BET)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    public boolean recieveCard(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.CARD)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean recieveBusting(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.BUSTING)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean recieveBankScore(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.BANK_SCORE)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean recieveGains(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.GAINS)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean recieveError(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.ERROR)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
