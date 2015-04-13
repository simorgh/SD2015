/**
 * Intended Status: Proposed Standard  (University of Barcelona)
 * Expires: 12/03/2015
 *
 * A protocol for the 7 and a half game. (SELECTOR abstraction)
 * This protocol is used for the client/server version of the  popular
 * card game ’seven and a half’.
 */
package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
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
    
    private final ArrayList<Byte> backup;
    public String previousState = "";
    public String currentState = "";
    
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
     * return true if matches the lasTurn-header (previous command) played.
     * @param state
     * @return 
     */
    public boolean isLastState(String state){
        return previousState.equals(state);
    }
    
    /**
     * return true if matches the current-turn header.
     * @param state
     * @return 
     */
    public boolean isCurrentState(String state){
        return currentState.equals(state);
    }
    
    /**
     * step forward to new valid header.
     * @param newState 
     */
    private void updateStates(String newState){
        this.previousState = this.currentState;
        this.currentState = newState;
    }
    
    /**
     * Close PrintWriter stream to File & client Socket
     * @throws IOException 
     */
    public void close() throws IOException {
        if(this.log != null)    this.log.close();
        if(this.socket != null) this.socket.close();
    }
    
    /**
     * returns true if there is enough data to generate a new response, false otherwise.
     * @return 
     */
    public boolean isDataReady(){
        return ( ( currentState.equals(Protocol.ANTE) && backup.size() >= 5 ) 
                || ( !currentState.equals(Protocol.ANTE) && backup.size() >= 4 ) );
    }
    
    
//////////////////////////////////////////////////////////////
//                     SERVER MESSAGES
//////////////////////////////////////////////////////////////
   
    /**
     * 
     * @param str
     * @throws IOException 
     */
    private void sendHeader(String str) throws IOException {
        buffer.clear();
        buffer.put(str.getBytes());
        buffer.flip();
        socket.write(buffer);
    }
    
    
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
            buffer.flip();
            while(buffer.hasRemaining()) socket.write(buffer);
            System.out.println("\nS: " + Protocol.STARTING_BET + ' ' + bet);           
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
            buffer.flip();
            socket.write(buffer);
            System.out.println("\nS: " + Protocol.CARD + ' ' + Character.toLowerCase(D) + Character.toLowerCase(P));
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
            System.out.println("\nS: " + Protocol.BUSTING);
            this.log.println("S: " + Protocol.BUSTING);
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
            System.out.print("\nS: " + Protocol.BANK_SCORE + ' ' + number);
            this.log.print("S: " + Protocol.BANK_SCORE + ' ' + number);
            for (char[] c : cards) {
                System.out.print(c[0] + "" + c[1]);
                this.log.print(c[0] + "" + c[1]);
                buffer.put((byte) Character.toLowerCase(c[0]));
                buffer.put((byte) Character.toLowerCase(c[1]));
            }
            buffer.put((byte) ' ');
            buffer.put(customScoreFormat(score).getBytes());
            buffer.flip();
            socket.write(buffer);
            System.out.println(' ' + customScoreFormat(score));
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
            buffer.flip();
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
            buffer.flip();
            socket.write(buffer);
            //System.out.println("\nS: " + Protocol.ERROR + " " + String.format("%02d", err.length()) + err);
            this.log.println("\nS: " + Protocol.ERROR + " " + String.format("%02d", err.length()) + err);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    /**
     * Reads data throght the SocketChannel and saves it to the byte-backup stack.s
     * @return
     * @throws IOException 
     */
    private byte[] readBytes() throws IOException{
        buffer.clear();
        int numBytes = socket.read(buffer);
        if(numBytes == -1) throw new IOException();
        System.out.println("TOTAL READ BYTES " + numBytes);
        byte[] b = new byte[numBytes];
        buffer.flip();
        while(buffer.hasRemaining()){
            byte aux = buffer.get();
            System.out.println("\t>> buffer at position " + buffer.position() + "\tvalue " + aux + ";\tchar " + (char) aux);
            b[buffer.position()-1] = aux;
        }
                
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
        System.out.println("Entering readHeader()...");
        byte[] bytes = readBytes();
        for( byte b : bytes ) backup.add(b);
        if(backup.size() < 4) return null; /* 4 bytes (command) */
        
        List <Byte> sub = backup.subList(0, 4);
        String header = getStringRepresentation( toByteArray(sub) ).toUpperCase();
        
        //backup.removeAll(sub);
        for(int i = 0, size = sub.size(); i < size; i++) backup.remove(0);
        
        this.log.print("C: " + header);
        System.out.println("\t***** HEADER RECIEVED is " + header);
        showPendingData();
        
        if(!isValidHeader(header)) throw new SyntaxErrorException();
        else updateStates(header);
            
        if(header.equals(Protocol.ERROR)) throw new ProtocolErrorException();
        
        return header;
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
        System.out.println("Entering receiveRaise()...");
        byte[] bytes = readBytes();
        for( byte b : bytes ) backup.add(b);
        
        if(backup.size() > 1){
            byte b = backup.get(0);  
            if((char)b != ' ') throw new SyntaxErrorException();
            
            if(backup.size() >= 5){
                List <Byte> sub = backup.subList(1, 5);
                int raise = bytesToInt32( toByteArray(sub), "be");
                if (raise < 0) throw new SyntaxErrorException();
                
                this.log.println(" " + raise);
                System.out.println("\t***** RAISE RECIEVED is " + raise);
                updateStates(""); //step forward ANTE
                
                //backup.removeAll(sub);
                for(int i = 0, size = sub.size() + 1; i < size; i++) backup.remove(0);
                showPendingData();
                return raise;
            }
        }   
        return -1;
    }
    
    /**
     * Error command reception.
     * 1. STRT header is expected.
     * 
     * @return 
     * @throws java.io.IOException
     * @throws utils.SyntaxErrorException
     */ 
    public String receiveErrorDescription() throws IOException, SyntaxErrorException {
        System.out.println("Entering receiveErrorDescription()...");
        byte[] bytes = readBytes();
        for( byte b : bytes ) backup.add(b);
        
        /* description reconstruction */
        if(backup.size() < 3) throw new SyntaxErrorException();
        byte b = backup.get(0);
        if((char)b != ' ') throw new SyntaxErrorException();
        
        List sub1 = backup.subList(1, 3);
        int len = Integer.parseInt( sub1.get(0) + "" + sub1.get(1) );
        List sub2 = backup.subList(3, 3 + len);
        String des = getStringRepresentation( toByteArray(sub2) );
        this.log.println(" " + des );
        System.out.println("\t***** ERROR RECIEVED is " + des);
        
        //backup.removeAll(sub1);
        //backup.removeAll(sub2);
        for(int i = 0, size = sub1.size() + sub2.size(); i < size; i++) backup.remove(0);

        return des;
    }
    
//////////////////////////////////////////////////////////////
//                     SUPPORT METHODS
//////////////////////////////////////////////////////////////    
    
    
    /**
     * Customizes the format value to %2.1 as its String representation. 
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
     * Determines if a given header is valid or not.
     * @param header Receivend command-string to be checked
     * @return true if cmd is any of the expected command string, false otherwise.
     */
    private boolean isValidHeader(String header){
        switch (header) {
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
            default:
                currentState = "";
                return false;
        } 
    }
    
    /**
     * Serialize a given Byte Collection to its primitive byte[] array
     * @param list
     * @return 
     */
    private byte[] toByteArray( List<Byte> list){
        int size = list.size();
        byte[] result = new byte[size];
        for(int i = 0; i < size; i++) result[i] = list.get(i);

        return result;
    }
       
    /**
     * Parse byte array to its ASCII String represent.
     * @param list
     * @return 
     */
    private String getStringRepresentation(byte[] list){
        String result = "";
        for (byte b : list){
            result += (char) b;
        }
        return result;
    }
    
    /**
     * Debug porpuses only.
     * Print actual status on received backup stack. 
     */
    private void showPendingData(){
        if(backup.isEmpty()){
        System.out.println(
                "┌───────────────────────────────┐\n"
              + "│ Backup Stack EMPTY            │\n"
              + "└───────────────────────────────┘");
            return;
        }
        System.out.println(
                "┌───────────────────────────────┐\n"
              + "│ Backup Stack                  │\n"
              + "├───────────────────────────────┤");
        int i = 0; 
        for( byte bb : backup ){
             System.out.println("│  @" + i + "\tvalue " + bb + "\tchar " + (char) bb + "  │");
             i++;
        }
        System.out.println("└───────────────────────────────┘");
    }
    
    /**
     * Debug porpuses only.
     * Print actual status for CURRENT/PREVIOUS header states. 
     */
    public void showState(){
        System.out.println("┌────────────────────┐\n" +
                      "│ LastState:    " + previousState + " │\n" + 
                      "│ CurrentState: " + currentState +  " │\n" +
                      "└────────────────────┘");
    }
    
    /**
     * Parse byte array to Integer.
     * @param bytes
     * @param endianess
     * @return 
     */
    private int bytesToInt32(byte bytes[], String endianess){
        int number;

        if("be".equals(endianess.toLowerCase())) number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        else number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);

        return number;
    }
}