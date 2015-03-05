/**
 * CLIENT Command Line Input.
 * -------------------------------------------------------------
 * Expected:
 * client> java Client -h
 * Us: java Client -s <maquina_servidora> -p <port> [-a topcard]
 * 
 */

package utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ClientCLI  {
    private static final Logger log = Logger.getLogger(ClientCLI.class.getName());
    private String[] args = null;
    private final Options options;
    
    /* variables to be returned */
    private float topcard;
    private int port;
    private String server;
    
    /* IPv4 arg validation constants */
    private final String IPADDRESS_PATTERN = 
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    private Matcher matcher;
    
    public ClientCLI (String[] args) {
        this.options = new Options();
        this.args = args;
        options.addOption("h", "help", false, "show help.");
        options.addOption("s", "server", true, "Ipv4 of the server to establish connection at" );
        options.addOption("p", "port", true, "specifies the 'port' for the connection");
        options.addOption("a", "auto", true, "optional param which specifies client-autoplay goal score");
        
        parse(); // starts parsing args into inner HashTable
    }
    
    /**
     * 
     */
    private void parse() {
        CommandLineParser parser = new BasicParser();
        
        /* parse the command line arguments */
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) help();
            
            /* validate that 'port' has been set */
            if (cmd.hasOption("s")) {

                this.matcher = pattern.matcher(cmd.getOptionValue("s")); // IPv4 verification
                if(!matcher.matches()){
                    log.log(Level.SEVERE, "Introduced Server IPv4 is not a valid address");
                } else {
                    this.server = cmd.getOptionValue("s");
                }
                
            } else {
                log.log(Level.SEVERE, "Missing 'port' option");
                help();
            }
            
            /* validate that 'starting bet' has been set */
            if(cmd.hasOption("p")) {
                this.port = Integer.parseInt(cmd.getOptionValue("p"));
            } else {
                log.log(Level.SEVERE, "Missing 'port' option");
                help();
            }
            
            /* validate that 'starting bet' has been set */
            if(cmd.hasOption("a")) {
                this.topcard = Float.parseFloat(cmd.getOptionValue("a"));
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

        formater.printHelp("Client", options);
        System.exit(0);
    }
    
    
    public String getServer(){
        return this.server;
    }
    
    public int getPort(){
        return this.port;
    }
    
    public float topCard(){
        return this.topcard;
    }
    
}
