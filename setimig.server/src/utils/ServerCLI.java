/**
 * SERVER Command Line Input.
 * -------------------------------------------------------------
 * Expected:
 * servidor> java Server -h
 * Us: java Server -p <port> -b <starting_bet> -f <deckfile>
 * 
 */

package utils;

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
    
    /* variables to be returned */
    private File deckfile;
    private int port;
    private int bet;

    public ServerCLI (String[] args) {
        this.options = new Options();
        this.args = args;
        options.addOption("h", "help", false, "show help.");
        options.addOption("p", "port", true, "connection 'port' to establish connection at" );
        options.addOption("b", "bet", true, "'starting_bet' it's an integer acting as minimum initial bet established by the server.");
        options.addOption("f", "bet", true, "'deckfile' it's the absolute path to a file containing the deck definition.");
        
        parse(); // starts parsing args into inner HashTable
    }
    
    /**
     * 
     */
    private void parse() {
        CommandLineParser parser = new BasicParser();
        
        // parse the command line arguments
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) help();
            
            /* validate that 'port' has been set */
            if (cmd.hasOption("p")) {
                this.port = Integer.parseInt(cmd.getOptionValue("p"));
            } else {
                log.log(Level.SEVERE, "Missing 'port' option");
                help();
            }
            
            /* validate that 'starting bet' has been set */
            if(cmd.hasOption("b")) {
                this.bet = Integer.parseInt(cmd.getOptionValue("b"));
            } else {
                log.log(Level.SEVERE, "Missing 'starting_bet' option");
                help();
            }
            
            /* validate that 'deckfile' has been set */
            if(cmd.hasOption("f")) {
                
                deckfile = new File(cmd.getOptionValue("f"));
                if(!deckfile.exists() || deckfile.isDirectory()) {
                    log.log(Level.SEVERE, "Wrong filename path: {0}", cmd.getOptionValue("f"));
                }
                
            } else {
                log.log(Level.SEVERE, "Missing 'deckfile' option");
                help();
            }
            
        } catch(ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }
    }

    /**
     * Prints out some help (Usage)
     */
    private void help() {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Server", options);
        System.exit(0);
    }
    
    
    public File getDeckfile(){
        return this.deckfile;
    }
    
    public int getPort(){
        return this.port;
    }
    
    public int getStartingBet(){
        return this.bet;
    }
    
    
}
