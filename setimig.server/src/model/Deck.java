/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author simorgh & dzigor92
 */

public class Deck {
    private ArrayList cards;
    
    /**
     * Class constructor
     * @param file
     * @throws IOException 
     */
    public Deck(File file) throws IOException{
        this.cards = readDeckFile(file);
    } 
    
    /**
     * Method that reads the deck file and saves the values on the "cards" parameter
     * @param fin
     * @return
     * @throws IOException 
     */
    private ArrayList readDeckFile(File fin) throws IOException {
        ArrayList deck = new ArrayList();
	FileInputStream fis = new FileInputStream(fin);
 
	//Construct BufferedReader from InputStreamReader
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
 
	String line = null;
	while ((line = br.readLine()) != null) {
		System.out.println("Added card "+line);
                deck.add(line);
	}
        
	br.close();
        return deck; 
    }
    
    /**
     * Shuffles the deck
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }
    
    /**
     * Draws a card from indicated position
     * @param position
     * @return 
     */
    public String drawCard(int position){
        return (String) this.cards.get(position);
    }
}
