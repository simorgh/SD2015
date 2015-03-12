import controller.ServerCLI;
import controller.ServerController;

/**
 *
 * @author simorgh
 */
public class Server {

        public static void main(String[] args){
            
           /* Command Line arguments threatment */
           ServerCLI cli = new ServerCLI(args);
           ServerController controller = new ServerController(cli.getStartingBet(), cli.getPort(),  cli.getDeckfile());
           controller.start();
        }
}
