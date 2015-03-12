import controller.ClientCLI;
import controller.ClientController;


/**
 *
 * @author simorgh
 */
public class Client {

    public static void main(String[] args){
        
       /* Command Line arguments threatment */
       ClientCLI cli = new ClientCLI(args);
       ClientController controller = new ClientController(cli.getServer(), cli.getPort(),  cli.getTopCard());
       controller.start();
    }
}
