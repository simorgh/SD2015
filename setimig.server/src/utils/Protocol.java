/**
 * Intended Status: Proposed Standard  (University of Barcelona)
 * Expires: 12/03/2015
 *
 * A protocol for the 7 and a half game. (SERVER abstraction)
 *  This protocol is used for the client/server version of the  popular
 *  card game ’seven and a half’.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

    /* Protocol Error Messages */
    private final String ERR_SYNTAX = "Syntax error";
    //TODO more ERR definitions comes here...
    
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
//                     SERVER MESSAGES
//////////////////////////////////////////////////////////////
    
    /**
     * STARTING_BET. - STBT SP/NUMBER
     * 
     * This command is sent when a START message is received. Argument
     * field is required and means the minimum bet for playing.
     * 
     * @param bet Integer over the net: 4bytes BE.
     * @throws IOException 
     */
    public void sendStartingBet(int bet) throws IOException{
        sendHeader(Protocol.STARTING_BET);
        write_char(' ');
        write_int32(bet);
    }
    
   /**
    * CARD. - CARD SP/D/P
    * 
    * This command is sent when a DRAW command is received. Argument
    * field is required and means the card drawn.
    *
    * @param D "1" | "2" | "3" | "4" | "5" | "6" | "7" | "s" | "c" | "r" | "S" | "C" | "R"
    * @param P "o" | "c" | "e" | "b" | "O" | "C" | "E" | "B" 
    * @throws java.io.IOException
    */
    public void sendCard(char D, char P) throws IOException{
        sendHeader(Protocol.CARD);
        write_char(' ');
        write_char(D);
        write_char(P);
    }
    
    /**
     * BUSTING. - BSTG
     * 
     * This command is sent after a CARD message is sent when the
     * score of the player is over seven and a half. It has no
     * arguments.
     * 
     * @throws java.io.IOException
     */
    public void sendBusting() throws IOException{
       sendHeader(Protocol.BUSTING);
    }
    
    /**
     * BANK_SCORE. - BKSC SP/NUMBER/((D/P)+)/SCORE
     * 
     * This command is sent when a PASS command is received or
     * a BUSTING message is sent. First argument is the number of cards
     * drawn following of the cards. The second argument is the scoring
     * after the bank has played.
     * 
     * @param number
     * @param cards
     * @param score 
     * @throws java.io.IOException 
     */
    public void sendBankScore(int number, ArrayList <String> cards, float score) throws IOException{
        sendHeader(Protocol.BANK_SCORE);
        write_char(' ');
        
        for (String c : cards) {
            System.out.print(c.charAt(0) + c.charAt(1) + " ");
            write_char(c.charAt(0));
            write_char(c.charAt(1));
        }
        
        System.out.println("@sendBankScore -> " + String.format("%2.1f", score));
        sendHeader(String.format("%2.1f", score));
    }
    
    /**
     * GAINS. - GAIN SP/NUMBER
     * 
     * This command is sent after the BANK_SCORE message is sent.
     * The argument is a positive number equals to the current bet if
     * the player has won, a negative number equals to the current bet
     * if the player has lost or it is equals to 0 if both of them have
     * the same scoring. If the player gets an exactly scoring of seven
     * and a half and the bank has not, the argument is a positive
     * number equals to the double of the current bet.
     * 
     * @param gains
     * @throws java.io.IOException
     */
    public void sendGains(int gains) throws IOException{
        sendHeader(Protocol.GAINS);
        write_char(' ');
        write_int32(gains);
    }
    
    /**
     * Error Command. - ERRO SP/d/d/c*
     * 
     * Each side can issue a special error command any time instead of
     * normal command. This command has an argument field with the
     * explanation of the error. The length of the message can be from 0
     * to 99. In order to determine the length of the message, two digits
     * must to be placed prior to the message.  If no message is provided
     * two digits ’0’ have to be used.
     * 
     * @param code
     * @throws java.io.IOException
     */
    public void sendError(int code) throws IOException{
        //TODO Define & Switch for error codes
        
        sendHeader(Protocol.ERROR);
        write_char(' ');
        write_string_variable(2, this.ERR_SYNTAX);
    }
    
    
    
//////////////////////////////////////////////////////////////
//               CLIENT RECEPTIONS VERIFICATION
//////////////////////////////////////////////////////////////
    public boolean recieveStart(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.START)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean recieveDraw(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.DRAW)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public int recieveAnte(){
        int raise = 0;
        try {
            String cmd = read_string_command();
            if(!(cmd.toUpperCase()).equals(Protocol.ANTE)) return -1;
            raise = read_int32();
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return raise;
    }

    public boolean recievePass(){
        try {
            String cmd = read_string_command();
            if((cmd.toUpperCase()).equals(Protocol.PASS)) return true;
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
