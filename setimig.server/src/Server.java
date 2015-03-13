////////////////////////////////////////////////////
//  Distributed Computing Software (UB) [TASK 1]  //
// ---------------------------------------------- //
//   'seven and a half' - Multithreading Server   //
////////////////////////////////////////////////////

import controller.ServerCLI;
import controller.ServerController;

/**
 *
 * @author simorgh & dzigor92
 */
public class Server {

        public static void main(String[] args){
            
           /* Command Line arguments threatment */
           ServerCLI cli = new ServerCLI(args);
           ServerController controller = new ServerController(cli.getStartingBet(), cli.getPort(),  cli.getDeckfile());
           controller.start();
        }
}
