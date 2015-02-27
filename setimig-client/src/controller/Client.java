package controller;

import utils.ComUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author simorgh & dzigor92
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String nomMaquina, str;
        int numPort, value;

        InetAddress maquinaServidora;
        Socket socket = null;
        ComUtils comUtils;
/*
        if (args.length != 2){
            System.out.println("Us: java Client <maquina_servidora> <port>");
            System.exit(1);
        }

        nomMaquina = args[0];
        numPort    = Integer.parseInt(args[1]); 
*/
        nomMaquina = "localhost";
        numPort = 1234;
        
        try{
            
            maquinaServidora = InetAddress.getByName(nomMaquina); /* Obtenim la IP de la maquina servidora */
            socket = new Socket(maquinaServidora, numPort); /* Obrim una connexio amb el servidor */
            comUtils = new ComUtils(socket); /* Obrim un flux d'entrada/sortida amb el servidor */

            //TODO: game logic for player comes here:

           
        } catch (IOException e) {
            System.out.println("Els errors han de ser tractats correctament en el vostre programa.");
        } finally {
            try {
                if(socket != null) socket.close();
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch    
        }
    } // fi del main
    
}
