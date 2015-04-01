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
import java.util.ArrayList;

/**
 * @author simorgh & dzigor92
 */
public class ClientProtocol extends utils.ComUtils{
    /* ClientProtocol Commands Semantics */
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

    /* ClientProtocol Error Messages */
    public static final String ERR_SYNTAX = "Syntax error";
    public static final String ERR_TIMEOUT = "Timeout exceeded";
    public static final String ERR_CARD = "Card already received";
    
    /**
     * Class constructor. Constructs the Protocol using a file.
     * @param file
     * @throws IOException 
     */
    public ClientProtocol(File file) throws IOException {
        super(file);    
    }
    
    /**
     * Class constructor. Constructs the  Protocol using a socket.
     * @param socket
     * @throws IOException 
     */
    public ClientProtocol(Socket socket) throws IOException {
        super(socket);
    }
    
    /**
     * Support method to send a header through the connection.
     * @param str The header to send.
     * @throws IOException 
     */
    private void sendHeader(String str) throws IOException { 
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
     * @return true if sent correctly, false otherwise.
     */           
    public boolean sendStart() {
        try {
            sendHeader(ClientProtocol.START);
        } catch (IOException ex) {
           return false;
        }
        return true;
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
     * @return true if sent correctly, false otherwise.
     */           
    public boolean sendDraw() {
        try {
            sendHeader(ClientProtocol.DRAW);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    /**
     * ANTE. - ANTE SP/NUMBER
     * 
     * This command is optional. It has to be issued before the DRAW
     * command. It is used for increasing the bet for this game.
     * 
     * @param raise
     * @return true if sent correctly, false otherwise.
     */
    public boolean sendAnte(int raise) {
        try {
            sendHeader(ClientProtocol.ANTE);
            write_char(' ');
            write_int32(raise);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * PASS. - PASS
     * 
     * This command is used for passing  the turn to the Bank. It is
     * used instead of a DRAW command. Once this command is sent a
     * BANK_SCORE message and GAINS message are expected.
     *
     * @return true if sent correctly, false otherwise.
     */
    public boolean sendPass() {
        try {
            sendHeader(ClientProtocol.PASS);
        } catch (IOException ex){
            return false;
        }
        return true;
    }

    /**
     * ERROR. - ERRO SP/d/d/c*
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
            sendHeader(ClientProtocol.ERROR);
            write_char(' ');
            write_string_variable(2, err);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
//////////////////////////////////////////////////////////////
//               SERVER RECEPTIONS VERIFICATION
//////////////////////////////////////////////////////////////
    
    /**
     * Starting bet reception.
     * The Client receives the starting bet as indicated by the defined communication protocol.
     * 1. STBT header is expected.
     * 2. SP is expected (' ').
     * 3. Integer value , indicating the value of the starting bet is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.ProtocolErrorException
     * @throws utils.SyntaxErrorException
     * @return The received value of the starting bet.
     */
    public int receiveStartingBet() throws IOException, ProtocolErrorException, SyntaxErrorException {
        int bet;
        
        String cmd = read_string_command().toUpperCase();   
        if( cmd.equals(ClientProtocol.ERROR) ) throw new ProtocolErrorException();
        if(!cmd.equals(ClientProtocol.STARTING_BET)) throw new SyntaxErrorException();
        if( !(read_char() == ' ') ) throw new SyntaxErrorException();
        
        // bet caption
        bet = read_int32();
        return bet;
    }
    
    /**
     * Card reception. 
     * The Client receives the card as indicated by the defined communication protocol.
     * 1. CARD header is expected.
     * 2. SP is expected (' ').
     * 3. The string with value of the card is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.ProtocolErrorException
     * @throws utils.SyntaxErrorException
     * @return The card that has been received.
     */
    public char[] receiveCard() throws IOException, ProtocolErrorException, SyntaxErrorException {
        char[] card;

        String cmd = read_string_command().toUpperCase();
        if( cmd.equals(ClientProtocol.ERROR) ) throw new ProtocolErrorException();
        if(!cmd.equals(ClientProtocol.CARD)) throw new SyntaxErrorException();
        if( !(read_char() == ' ') ) throw new SyntaxErrorException();
        
        // card caption
        card = new char[2];
        card[0] = Character.toLowerCase(read_char());
        card[1] = Character.toLowerCase(read_char());
        
        return card;
    }

    /**
     * Busting reception.
     * 1. BSTG header is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.ProtocolErrorException
     * @throws utils.SyntaxErrorException
     */
    public void receiveBusting() throws IOException, ProtocolErrorException, SyntaxErrorException {
        String cmd = read_string_command().toUpperCase();
        if( cmd.equals(ClientProtocol.ERROR) ) throw new ProtocolErrorException();
        if(!cmd.equals(ClientProtocol.BUSTING)) throw new SyntaxErrorException();
    }
    
    /**
     * Bank Score Reception.  
     * The Client receives the bank score as indicated by the defined communication protocol.
     * 1. BNSC header is expected.
     * 2. SP is expected (' ').
     * 3. NUMBER indicating the number of cards to be sent is expected.
     * 4. STRING representing a car is expected as many times as indicated by NUMBER received previously.
     * 5. FLOAT indicating the score that the bank has hit is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.ProtocolErrorException
     * @throws utils.SyntaxErrorException 
     * @return ArrayList containing the list of cards and the bank score as a String. 
     */
    public ArrayList <String> receiveBankScore() throws IOException, ProtocolErrorException, SyntaxErrorException {
        ArrayList <String> bank_resume = new ArrayList();
        
        String cmd = read_string_command().toUpperCase();
        if( cmd.equals(ClientProtocol.ERROR) ) throw new ProtocolErrorException();
        if(!cmd.equals(ClientProtocol.BANK_SCORE)) throw new SyntaxErrorException();
        if( read_char() != ' ' ) throw new SyntaxErrorException();

        // card caption
        int i = read_int32();
        char[] card = new char[2];
        for(int j = 0; j < i; j++){
            card[0] = Character.toLowerCase(read_char());
            card[1] = Character.toLowerCase(read_char());
            bank_resume.add(new String(card));
        }

        if(read_char() != ' ') throw new SyntaxErrorException();
        String score = read_string_command();
        if(score.length() != 4) throw new SyntaxErrorException();
        bank_resume.add(score);

        return bank_resume;
    }
    
    /**
     * Bank Score Reception.
     * The Client receives the gains as indicated by the defined communication protocol.
     * 1. GAIN header is expected.
     * 2. SP is expected (' ').
     * 3. INTEGER representing the value of the gains of client is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.ProtocolErrorException 
     * @throws utils.SyntaxErrorException
     * @return The value of the gains.
     */
    public int receiveGains() throws IOException, ProtocolErrorException, SyntaxErrorException {
        int gains;

        String cmd = read_string_command().toUpperCase();
        if( cmd.equals(ClientProtocol.ERROR) ) throw new ProtocolErrorException();
        if(!cmd.equals(ClientProtocol.GAINS)) throw new SyntaxErrorException();
        if( !(read_char() == ' ') ) throw new SyntaxErrorException();
        gains = read_int32();
       
        return gains;
    }
    
    /**
     * ERROR paramaters reception.
     * whitespace followed by 2char-digit determining lenght of the message is expected 
     * 
     * @throws java.io.IOException
     * @throws utils.SyntaxErrorException
     * @return error description
     */
    public String receiveErrorDescription() throws IOException, SyntaxErrorException {  
        if(!(read_char() == ' ')) throw new SyntaxErrorException();
        String des = read_string_variable(2);
        return(" " + des);
    }

}
