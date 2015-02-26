/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

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

/*    
    public static enum CMD { START, DRAW, ANTE, PASS, 
    STARTING_BET, CARD, BUSTING, BANK_SCORE, GAINS, ERROR };
    
    private Map<String, String> dictionary = new HashMap<String, String>() {
        
    };
*/
    public Protocol(File file) throws IOException {
        super(file);
        
    }
    
    public Protocol(Socket socket) throws IOException {
        super(socket);
    }
    
    
    //
    // Client Commands
    //
    public void sendHeader(String str) throws IOException{ 
        write_string_variable(str);
    }
    
    public void sendAnte(int raise) throws IOException{
        // ANTE <SP><NUMBER>
        sendHeader(this.ANTE);
        write_char(' ');
        write_int32(raise);
        
    }

    
    //
    // Server Commands verf.
    //               
    public void recieveSTARTING_BET(){
        //TODO
    }
    
    public void recieveCard(){
        //TODO
    }
    
    public void recieveBusting(){
        //TODO
    }
    
    public void recieveBankScore(){
        //TODO
    }
    
    public void recieveGains(){
        //TODO
    }
    
    public void recieveError(){
        //TODO
    }



}
