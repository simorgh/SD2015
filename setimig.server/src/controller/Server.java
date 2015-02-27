package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import utils.ComUtils;

/**
 *
 * @author simorgh & dzigor92
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket serverSocket=null;
        Socket socket=null;
        ComUtils comUtils;

        int portServidor = 1234;
        int value;

        if (args.length > 1){
            System.out.println("Us: java Servidor [<numPort>]");
            System.exit(1);
        }

        if (args.length == 1)
            portServidor = Integer.parseInt(args[0]);

            try {
                serverSocket = new ServerSocket(portServidor);  /* Creem el servidor */
                System.out.println("Servidor socket preparat al port " + portServidor);

                while (true) {
                    System.out.println("Esperant una connexio d'un client...");
                    socket = serverSocket.accept(); /* Esperem a que un client es connecti amb el servidor */
                    System.out.println("Connexio acceptada d'un client.");
                    comUtils = new ComUtils(socket); /* Associem un flux d'entrada/sortida amb el client */ 
                    
                    //TODO: game logic for server comes here
                }
                
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } finally {
                /* Tanquem la comunicacio amb el client */
                try {
                    if(serverSocket != null) serverSocket.close();
                } catch (IOException ex) {
                    System.out.println("Els errors han de ser tractats correctament pel vostre programa");
                }
          }
    } // fi del main
    
    
}
