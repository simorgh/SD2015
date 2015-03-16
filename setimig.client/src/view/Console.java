/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author simorgh & dzigor92
 */
public class Console {
    
    /* View error messages */
    public static final String ERR_00 = "ERROR: Remote host timed out during read operation";
    public static final String ERR_01 = "ERROR: Wrong option. Please enter a valid action.";
    public static final String ERR_02 = "ERROR: There was a problem trying to close the socket connection.";
    public static final String ERR_03 = "ERROR: The last card has been received before. The game is now aborted.";
    public static final String ERR_04 = "ERROR: A problem with the server appeared - Closing now the connection.";
    public static final String ERR_05 = "ERROR: Cannot communicate the message to the server. Connection interrupted.";        
    public static final String ERR_06 = "ERROR: Syntax error detected - Proceding to comunicate to server.";
    
    /* SingleTone pattern */
    private static Console instance = null; 
    Scanner in;
    
    /**
     * Class constructor.
     */
    public Console() {
      // Exists only to defeat instantiation.
       in = new Scanner(System.in);
    }
    
    /**
     * Class constructor. Used to follow the Singleton pattern.
     * @return 
     */
    public static Console getInstance() {
        if(instance == null) {
            instance = new Console();
        }
        return instance;
    }  
    
    /**
     * Method to print the predefined welcome message.
     */
    public void printWelcome(){
        System.out.println("    ╔═╗┌─┐┌┬┐  ╦  ╔╦╗┬┌─┐\n"
                        +  "    ╚═╗├┤  │   ║  ║║║││ ┬\n"
                        +  "    ╚═╝└─┘ ┴   ╩  ╩ ╩┴└─┘");
    }
    
    /**
     * Print of the socket.
     * @param socket 
     */
    public void showConnection(Socket socket){
        System.out.println("socket received: " + socket.getLocalSocketAddress().toString());
    }
    
    /**
     * Print of the player in game options and to introduce the option.
     * @param score Player's current score.
     * @return the option introduced by user. 
     */
    public char printInGameOptions(float score){
        System.out.println(
            "╔════════════════════╦═══════════╦═══════════╦═══════════╗\n" +
            "║░  Player Actions  ░║  1. Draw  ║  2. Ante  ║  3. Pass  ║\n" +
            "╟────────────────────╨───────────╨───────────╨───────────╢\n" +
            "║░░░               Player Score: " + customFormat(score) + "                 ░░░║\n" +        
            "╚════════════════════════════════════════════════════════╝\n");
        System.out.print("► Select action number: ");
        return in.next().charAt(0);
    }
    
    /**
     * Print of the starting bet.
     * @param bet Value to show.
     */
    public void printStartingBet(int bet){
        System.out.println("► Connection Established: STARTING BET is " + bet);
    }
    
    /**
     * Print of the error message.
     * @param error Message to print.
     */
    public void printError(String error){
        System.out.println(error);
    }
    
    /**
     * Method  to receive a value of the raise from user.
     * @return Value of the raise introduced by user.
     */
    public int enterRaise(){
        System.out.print("► How much do you want to raise?: ");
        int raise = 0;
        boolean end = false;
        do {
            while(!in.hasNextInt()){
                in.next();
                System.out.print("\t► Please enter a valid numeric value: ");
            }
            raise = in.nextInt();
            if (raise <= 0) System.out.print("\t► Raise must be greater than 0: ");
            else  end=true;
            
        } while (!end);
        
        return raise;
       
    }
    
    /**
     * Print of the card.
     * @param card 
     */
    public void printNewCard(char[] card){
        System.out.println("         _____\n" +
            "        |" + card[0] + "    |\n" +
            "        |     |\n" +
            "        |  " + card[1] + "  |\n" +
            "        |     |\n" +
            "        |____" + card[0] + "|");
    }
    
    /**
     * Print of the bank score.
     * @param game Array of values to print. Contains the cards of the bank and the bank's score.
     */
    public void printBankScore(ArrayList <String> game){
        System.out.println("░░░░░░░░░░░░ BANK GAME RESUME ░░░░░░░░░░░░");
        for(int i=0; i < game.size()-1; i++){
            char [] card = game.get(i).toCharArray();
            printNewCard(card);
        }
        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░SCORE░░" + game.get(game.size()-1));
    }
    
    /**
     * Print the gains.
     * @param gains Value to print.
     */
    public void printGains(int gains){
        //System.out.println("@printGains -> gains = " + gains);
        String feedback = "";
        if(gains>0) feedback = "You win!";
        else if (gains<0) feedback = "You lose...";
        else if (gains==0) feedback = "Tie!!";
            
        System.out.println("► GAINS: " + gains + " "+ feedback);
    }
    
    /**
     * Desc. Customizes the format of the float to %2.1 
     * @param value
     * @return Formatted float as a String
     */
    private String customFormat( float value ) {
        DecimalFormat myFormatter = new DecimalFormat("00.0");
        String output = myFormatter.format(value);
        return output;
    }
}