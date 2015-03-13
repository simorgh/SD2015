////////////////////////////////////////////////////
//  Distributed Computing Software (UB) [TASK 1]  //
// ---------------------------------------------- //
//         'seven and a half' - Client            //
////////////////////////////////////////////////////

import controller.ClientCLI;
import controller.ClientController;

/**
 *
 * @author simorgh & dzigor92
 */
public class Client {

    public static void main(String[] args){
        
       /* Command Line arguments threatment */
       ClientCLI cli = new ClientCLI(args);
       ClientController controller = new ClientController(cli.getServer(), cli.getPort(),  cli.getTopCard());
       controller.start();
    }
}
