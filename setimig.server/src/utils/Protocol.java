/**
 * Intended Status: Proposed Standard  (University of Barcelona)
 * Expires: 12/03/2015
 *
 * A protocol for the 7 and a half game. (SERVER abstraction)
 *  This protocol is used for the client/server version of the  popular
 *  card game ’seven and a half’.
 */
package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
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
    public static final String ERR_SYNTAX = "Syntax error";
    public static final String ERR_TIMEOUT = "Timeout exceeded";
    public static final String ERR_SSE = "Server-side error";
    
    /* Log Trace Writer */
    private final PrintWriter log;
    
    /**
     * Class constructor.
     * @param socket
     * @param out - OutputStream used to write trace information about S/C communication.
     * @throws IOException 
     */
    public Protocol(Socket socket, OutputStream out) throws IOException {
        super(socket);
        this.log = new PrintWriter(out, true); /* autoflushing enabled */
    }
    
    /**
     * - support method -
     */
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
     * @return true if sent correctly, false otherwise.
     */
    public boolean sendStartingBet(int bet) {
        try {
            sendHeader(Protocol.STARTING_BET);
            write_char(' ');
            write_int32(bet);
            this.log.println("\nS: " + Protocol.STARTING_BET + ' ' + bet);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
   /**
    * CARD. - CARD SP/D/P
    * 
    * This command is sent when a DRAW command is received. Argument
    * field is required and means the card drawn.
    *
    * @param D "1" | "2" | "3" | "4" | "5" | "6" | "7" | "s" | "c" | "r" | "S" | "C" | "R"
    * @param P "o" | "c" | "e" | "b" | "O" | "C" | "E" | "B"
    * @return true if sent correctly, false otherwise.
    */
    public boolean sendCard(char D, char P) {
        try {
            sendHeader(Protocol.CARD);
            write_char(' ');
            write_char(Character.toLowerCase(D));
            write_char(Character.toLowerCase(P));
            this.log.println("\nS: " + Protocol.CARD + ' ' + Character.toLowerCase(D) + Character.toLowerCase(P));
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    /**
     * BUSTING. - BSTG
     * 
     * This command is sent after a CARD message is sent when the
     * score of the player is over seven and a half. It has no
     * arguments.
     * 
     * @return true if sent correctly, false otherwise.
     */
    public boolean sendBusting() {
        try {
            sendHeader(Protocol.BUSTING);
            this.log.println("\nS: " + Protocol.BUSTING);
        } catch (IOException ex) {
            return false;
        }
        return true;
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
     * @return true if sent correctly, false otherwise.
     */
    public boolean sendBankScore(int number, ArrayList <char[]> cards, float score) {
        try {
            sendHeader(Protocol.BANK_SCORE);
            write_char(' ');
            write_int32(number);
            
            this.log.print("\nS: " + Protocol.BANK_SCORE + ' ' + number);
            for (char[] c : cards) {
                this.log.print(c[0] + "" + c[1]);
                write_char(Character.toLowerCase(c[0]));
                write_char(Character.toLowerCase(c[1]));
            }
            
            sendHeader(customScoreFormat(score));
            this.log.println(' ' + customScoreFormat(score));
        } catch (IOException ex) {
            return false;
        }
        return true;
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
     * @return true if gains has been sent correctly, false otherwise.
     */
    public boolean sendGains(int gains) {
        try {
            sendHeader(Protocol.GAINS);
            write_char(' ');
            write_int32(gains);
            this.log.println("S: " + Protocol.GAINS + ' ' + gains);
        } catch (IOException ex) {
            return false;
        }
        return true;
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
     * @param err description of the error to be sent - should be one of the static ERR_* defined in this class - Limited to 99 char
     * @return true if message has been successfully sent, false otherwise. 
     */
    public boolean sendError(String err) {
        try {
            sendHeader(Protocol.ERROR);
            write_char(' ');
            write_string_variable(2, err);
            this.log.println("S: " + Protocol.ERROR + " " + String.format("%02d", err.length()) + err);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    
    
//////////////////////////////////////////////////////////////
//               CLIENT RECEPTIONS VERIFICATION
//////////////////////////////////////////////////////////////
    
    /**
     * Protocol command header reading.
     * 
     * @return read header
     * @throws IOException
     */
    public String readHeader() throws IOException {
        String header = read_string_command();
        this.log.print("C: " + header);
        if(!isValidHeader(header)) throw new IOException();
        
        return header;
    }
    
    /**
     * Start command reception.
     * 1. STRT header is expected.
     * 
     * @throws java.io.IOException
     * @return 
     */
    public String receiveStart() throws IOException {
        String cmd = readHeader().toUpperCase();
        if(!cmd.equals(Protocol.START) && !cmd.equals(Protocol.ERROR) ) throw new IOException();
        return cmd;
    }

    /**
     * Raise command reception. The Server receives the card as indicated by the defined communication protocol.
     * 1. ANTE header is expected.
     * 2. SP is expected (' ').
     * 3. NUMBER value representing the value of the raise is expected.
     * 
     * @return if an error occoured -1 is returned, otherwise returns raise value.
     */
    public int receiveRaise() {
        int raise;
        try {
            if( !(read_char() == ' ') ) throw new IOException();
            raise = read_int32();
            if(raise < 0) throw new IOException();
            
            this.log.println(" " + raise);
            return raise;
        } catch (IOException ex) {
            return -1;
        }
    }

    /**
     * Error command reception.
     * 1. STRT header is expected.
     * @throws java.io.IOException
     */ 
    public void receiveErrorDescription() throws IOException {
        if(!(read_char() == ' ')) throw new IOException();
        char d1 = read_char();
        char d2 = read_char();
        if( !(Character.isDigit(d1)) || !(Character.isDigit(d2)) ) throw new IOException();
        
        int len = Integer.parseInt("" + d1 + d2);
        String des = "";
        for(int i=0; i<len; i++) des += read_char();
        this.log.print(" " + d1 + d2 + des);
    }
    
    
    /**
     * - Support method -
     * 
     * Desc. Customizes the format of the float to %2.1 
     * @param value
     * @return Formatted float as a String
     */
    private String customScoreFormat( float value ) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat myFormatter = new DecimalFormat("00.0", otherSymbols);
        String output = myFormatter.format(value);
        
        return output;
    }
    
    /**
     * - Support method -
     * 
     * @param header Receivend command-string to be checked
     * @return true if cmd is any of the expected command string, false otherwise.
     */
    private boolean isValidHeader(String header){
        switch (header.toUpperCase()) {
            case Protocol.START:
                return true;
            case Protocol.DRAW:
                return true;
            case Protocol.ANTE:
                return true;
            case Protocol.PASS:
                return true;
            case Protocol.ERROR:
                return true;
            default: return false;
        } 
    }

}
