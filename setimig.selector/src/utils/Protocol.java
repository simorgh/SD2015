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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author simorgh & dzigor92
 */
public class Protocol{
    
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
    private final int BUFFER_SIZE = 512;
    private final PrintWriter log;
    private final SocketChannel socket;
    private final ByteBuffer buffer;
    
    private ArrayList<Byte> backup;
    private String lastState = null;
    
    /**
     * Class constructor.
     * @param socket
     * @param out - OutputStream used to write trace information about S/C communication.
     * @throws IOException 
     */
    public Protocol(SocketChannel socket, OutputStream out) throws IOException {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.socket = socket;
        this.backup = new ArrayList<>();     
        this.log = new PrintWriter(out, true); /* autoflushing enabled */
    }
    
    /**
     * 
     * @param state
     * @return 
     */
    public boolean isLastState(String state){
        return lastState.equals(state);
    }
    
    /**
     * - support method -
     */
    private void sendHeader(String str) throws IOException {
        buffer.clear();
        buffer.put(str.getBytes());
        socket.write(buffer);
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
            buffer.clear();
            buffer.put(Protocol.STARTING_BET.getBytes());
            buffer.put( (byte) ' ');
            buffer.putInt(bet);      
            socket.write(buffer);
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
            buffer.clear();
            buffer.put(Protocol.CARD.getBytes());
            buffer.put((byte)' ');
            buffer.put((byte) Character.toLowerCase(D));
            buffer.put((byte) Character.toLowerCase(P));
            socket.write(buffer);
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
            buffer.clear();
            buffer.put(Protocol.BANK_SCORE.getBytes());
            buffer.put((byte) ' ');
            buffer.putInt(number);
            this.log.print("\nS: " + Protocol.BANK_SCORE + ' ' + number);
            for (char[] c : cards) {
                this.log.print(c[0] + "" + c[1]);
                buffer.put((byte) Character.toLowerCase(c[0]));
                buffer.put((byte) Character.toLowerCase(c[1]));
            }
            buffer.put((byte) ' ');
            buffer.put(customScoreFormat(score).getBytes());
            socket.write(buffer);
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
            buffer.clear();
            buffer.put(Protocol.GAINS.getBytes());
            buffer.put((byte) ' ');
            buffer.putInt(gains);
            socket.write(buffer);
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
            buffer.clear();
            buffer.put(Protocol.ERROR.getBytes());
            buffer.put((byte) ' ');
            buffer.put(Integer.toString(err.length()).getBytes());
            buffer.put(err.getBytes());
            socket.write(buffer);
            this.log.println("\nS: " + Protocol.ERROR + " " + String.format("%02d", err.length()) + err);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    /**
     * 
     */
    private byte[] readBytes() throws IOException{
        buffer.clear();
        int numBytes = socket.read(buffer);
        if(numBytes == -1) throw new IOException("Broken Pipe");
        
        byte[] b = new byte[numBytes];
        while(buffer.hasRemaining()) b[buffer.position()] = buffer.get();
        
        return b;
    }
    
    
    
//////////////////////////////////////////////////////////////
//               CLIENT RECEPTIONS VERIFICATION
//////////////////////////////////////////////////////////////
    
    /**
     * Protocol command header reading.
     * 
     * @throws IOException
     * @throws utils.SyntaxErrorException
     * @throws utils.ProtocolErrorException
     * @return read header
     */
    public String readHeader() throws IOException, SyntaxErrorException, ProtocolErrorException {
        byte[] bytes = readBytes();
        for( byte b : bytes ) backup.add(b);
        if(backup.size() < 4) return null; /* 4 bytes (command) */
        
        List <Byte> sub = backup.subList(0, 4);
        backup.removeAll(sub);
        String header = getStringRepresentation( toByteArray(sub) ).toUpperCase();
        this.log.print("C: " + header);
        if(!isValidHeader(header)) throw new SyntaxErrorException();
        if(header.equals(Protocol.ERROR)) throw new ProtocolErrorException();
        
        return header;
    }
    
    private byte[] toByteArray( List<Byte> list){
        byte[] result = new byte[list.size()];
        for(int i = 0; i < list.size(); i++) result[i] = list.get(i);
        
        return result;
    }

    private String getStringRepresentation(byte[] list){    
        CharBuffer cBuffer = ByteBuffer.wrap(list).asCharBuffer();
        return cBuffer.toString();
    }
     
    
    /**
     * Start command reception.
     * 1. STRT header is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.SyntaxErrorException
     * @throws utils.ProtocolErrorException 
     */
    public void receiveStart() throws IOException, SyntaxErrorException, ProtocolErrorException {
        String cmd = readHeader();
        if(!cmd.equals(Protocol.START)) throw new SyntaxErrorException();
    }
    
    
    /**
     * Raise command reception. The Server receives the card as indicated by the defined communication protocol.
     * 1. ANTE header is expected.
     * 2. SP is expected (' ').
     * 3. NUMBER value representing the value of the raise is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.SyntaxErrorException
     * @return if an error occoured -1 is returned, otherwise returns raise value.
     */
    public int receiveRaise() throws IOException, SyntaxErrorException {
        byte[] bytes = readBytes();
        for( byte b : bytes ) backup.add(b);
        
        if(backup.size() > 1){
            byte b = backup.get(0);  
            if((char)b != ' ') throw new SyntaxErrorException();
            
            if(backup.size() >= 5){
                List <Byte> sub = backup.subList(1, 4);
                backup.remove(0);
                backup.removeAll(sub);
                return bytesToInt32(toByteArray(sub), "be");
            }
        }
        
        return -1;
    }

     /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], String endianess){
        int number;

        if("be".equals(endianess.toLowerCase())) number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        else number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);

        return number;
    }
    
    
    
    
    
    /**
     * Error command reception.
     * 1. STRT header is expected.
     * 
     * @throws java.io.IOException
     * @throws utils.SyntaxErrorException
     */ 
    public void receiveErrorDescription() throws IOException, SyntaxErrorException {
/*
        if(!(read_char() == ' ')) throw new SyntaxErrorException();
        
        String des = read_string_variable(2);
        this.log.println(" " + String.format("%02d", des.length()) + des);
*/ 
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
        switch (header) {
            case Protocol.START:
                lastState = Protocol.START;
                return true;
            case Protocol.DRAW:
                lastState = Protocol.DRAW;
                return true;
            case Protocol.ANTE:
                lastState = Protocol.ANTE;
                return true;
            case Protocol.PASS:
                lastState = Protocol.PASS;
                return true;
            case Protocol.ERROR:
                lastState = Protocol.ERROR;
                return true;
            default: return false;
        } 
    }
    
}
