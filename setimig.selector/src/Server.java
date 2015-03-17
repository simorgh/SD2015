////////////////////////////////////////////////////
//  Distributed Computing Software (UB) [TASK 1]  //
// ---------------------------------------------- //
//   'seven and a half' - Multithreading Server   //
////////////////////////////////////////////////////

import controller.SelectorCLI;
import controller.SelectorController;

/**
 *
 * @author simorgh & dzigor92
 */
public class Server {

        public static void main(String[] args){
            
           /* Command Line arguments threatment */
           SelectorCLI cli = new SelectorCLI(args);
           SelectorController controller = new SelectorController(cli.getStartingBet(), cli.getPort(),  cli.getDeckfile());
           controller.start();
        }
}
