/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author simorgh & dzigor92
 */
public class Console {
    private static Console instance = null; /* SingleTone pattern */
    Scanner in;
    
    public Console() {
      // Exists only to defeat instantiation.
       in = new Scanner(System.in);
    }
    
    public static Console getInstance() {
        if(instance == null) {
            instance = new Console();
        }
        return instance;
    }
    
    public void printWelcome(){
        System.out.println(".------.------.------.     .------.     .------.------.------.\n" +
            "|S.--. |E.--. |T.--. |.-.  |I.--. |.-.  |M.--. |I.--. |G.--. |\n" +
            "| :/\\: | (\\/) | :/\\: ((5)) | (\\/) ((5)) | (\\/) | (\\/) | :/\\: |\n" +
            "| :\\/: | :\\/: | (__) |'-.-.| :\\/: |'-.-.| :\\/: | :\\/: | :\\/: |\n" +
            "| '--'S| '--'E| '--'T| ((1)| '--'I| ((1)| '--'M| '--'I| '--'G|\n" +
            "`------`------`------'  '-'`------'  '-'`------`------`------'");
    }
    
    public char printInGameOptions(float score){
        System.out.println(
            " SCORE: " + String.format("%2.1f", score) + "\n" +
            "╔════════════════════╦═══════════╦═══════════╦═══════════╗\n" +
            "║░░░Player Actions░░░║  1. Draw  ║  2. Ante  ║  3. Pass  ║\n" +
            "╚════════════════════╩═══════════╩═══════════╩═══════════╝\n");
        System.out.print("► Select action number: ");
        return in.next().charAt(0);
    }
    
    public void printStartingBet(int bet){
        System.out.println("► Connection Established: STARTING BET is " + bet);
    }
    
    
    public void printError(String error){
        System.out.println(error);
    }
    
    public int enterRaise(){
        System.out.print("► How much do you want to raise?: ");
        int raise = 0;
        while(!in.hasNextInt()){
            in.next();
            System.out.print("\t► Please enter a valid numeric value: ");
        }
        raise = in.nextInt();

        return raise;
       
    }
    
    public void printNewCard(char[] card){
        System.out.println("         _____\n" +
            "        |" + card[0] + "    |\n" +
            "        |     |\n" +
            "        |  " + card[1] + "  |\n" +
            "        |     |\n" +
            "        |____" + card[0] + "|");
    }
    
    public void printBankScore(ArrayList <String> game){
        System.out.println("░░░░░░░░░░░░ BANK GAME RESUME ░░░░░░░░░░░░");
        for(int i=0; i < game.size()-1; i++){
            char [] card = game.get(i).toCharArray();
            printNewCard(card);
        }
        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░SCORE░░" + game.get(game.size()-1));
    }
    
    public void printGains(int gains){
        //System.out.println("@printGains -> gains = " + gains);
        String feedback = "";
        if(gains>0) feedback = "You win!";
        else if (gains<0) feedback = "You lose...";
        else if (gains==0) feedback = "Tie!!";
            
        System.out.println("► GAINS: " + gains + " "+ feedback);
    }
}
