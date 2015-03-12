/**
 * SERVER Command Line Input.
 * -------------------------------------------------------------
 * Expected:
 * servidor> java Server -h
 * Us: java Server -p <port> -b <starting_bet> -f <deckfile>
 * 
 */

package controller;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/* CLI specific imports */
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * @author simorgh & dzigor92
 */
public class ServerCLI {
    private static final Logger log = Logger.getLogger(ServerCLI.class.getName());
    private String[] args = null;
    private final Options options;
    
    private final int MINPORT = 1024;
    private final int MAXPORT = 65535;
    
    /* variables to be returned */
    private File deckfile;
    private int port;
    private int bet;

    /**
     * Class constructor.
     * Here we add all the CLI required arguments as a keys for the hash map. After that the parse
     * is automatically executed to verify all the associated argument values.  
     * @param args 
     */
    public ServerCLI (String[] args) {
        //this.port = 1212; /* As protocol specefies let's assume by default we'll be working on 1212 port */
        
        this.options = new Options();
        this.args = args;
        options.addOption("h", "help", false, "show help.");
        options.addOption("p", "port", true, "connection 'port' to establish connection at" );
        options.addOption("b", "bet", true, "'starting_bet' it's an integer acting as minimum initial bet established by the server.");
        options.addOption("f", "bet", true, "'deckfile' it's the absolute path to a file containing the deck definition.");
        
        parse(); // starts parsing args into inner HashTable
    }
    
    /**
     * Command line argument parser from org.apache.commons.
     * Used  to patch each value as hash key-value.
     * It allows us to put arguments in any order.
     */
    private void parse() {
        CommandLineParser parser = new BasicParser();
        
        // parse the command line arguments
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) help();
            
            /* validate that 'port' has been set */
            if (cmd.hasOption("p")) {
                this.port = Integer.parseInt(cmd.getOptionValue("p"));
                if(this.port < MINPORT ||this.port > MAXPORT){
                    log.log(Level.SEVERE, "Port value out of range: {0}", this.port);
                    System.exit(4);   
                }
            } else {
                log.log(Level.SEVERE, "Missing 'port' option");
                help();
            }
            
            /* validate that 'starting bet' has been set */
            if(cmd.hasOption("b")) {
                this.bet = Integer.parseInt(cmd.getOptionValue("b"));
                if(this.bet < 0){
                     log.log(Level.SEVERE, "Starting bet must be a positive value");
                     System.exit(3);
                }
            } else {
                log.log(Level.SEVERE, "Missing 'starting_bet' option");
                help();
            }
            
            /* validate that 'deckfile' has been set */
            if(cmd.hasOption("f")) {
                
                deckfile = new File(cmd.getOptionValue("f"));
                if(!deckfile.exists() || deckfile.isDirectory()) {
                    log.log(Level.SEVERE, "Wrong filename path: {0}", cmd.getOptionValue("f"));
                    System.exit(2);
                }
                
            } else {
                log.log(Level.SEVERE, "Missing 'deckfile' option");
                help();
            }
        
        } catch (NumberFormatException e){
            log.log(Level.SEVERE, "Failed to convert arg to Number. Make sure your input is correct.");
            System.exit(1);    
        } catch(ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties");
            help();
        }
    }

    /**
     * Prints out some help (Usage).
     */
    private void help() {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Server", options);
        System.exit(0);
    }
    
    /**
     * Deckfile getter.
     * 
     * @return Returns a file, the deck is read from. 
     */
    public File getDeckfile(){
        return this.deckfile;
    }
    
    /**
     * Connection port getter.
     * @return Returns the port to establish connection at.
     */
    public int getPort(){
        return this.port;
    }
    
    /**
     * Starting bet getter.
     * @return Returns the value of the starting bet binded to the server. 
     */
    public int getStartingBet(){
        return this.bet;
    }
    
    
}
