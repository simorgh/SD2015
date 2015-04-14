////////////////////////////////////////////////////
//  Distributed Computing Software (UB) [TASK 1]  //
// ---------------------------------------------- //
//      'seven and a half' - Selector Server      //
////////////////////////////////////////////////////

import controller.SelectorCLI;
import controller.Controller;

/**
 *
 * @author simorgh & dzigor92
 */
public class Server {

        public static void main(String[] args){
            
           /* Command Line arguments threatment */
           SelectorCLI cli = new SelectorCLI(args);
           Controller controller = new Controller(cli.getStartingBet(), cli.getPort(),  cli.getDeckfile());
           controller.start();
        }
}
