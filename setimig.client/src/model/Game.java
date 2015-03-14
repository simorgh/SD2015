/**
 * Game.
 * This class provides a layered abstraction of the "Seven and a half" game. It contains all
 * the necessary information for the Client side to understand the state of game that is being played.
 */

package model;

import java.util.ArrayList;

/**
 *
 * @author simorgh & dzigor92
 */

public class Game {
    private float playerScore;
    private final ArrayList handPlayer;
    
    /**
     * Class constructor. 
     * 
     */
    public Game(){
        this.playerScore = 0.0f;
        this.handPlayer = new ArrayList();
    }
    
    /**
     * Calculates the value of the card.
     * @param D The drawn card 
     * @return The value of the card
     */
    private float getCardValue(char D){
        if (Character.isDigit(D)) return (float)Character.getNumericValue(D);
        else{
            return (float)0.5;
        }      
    }
    
    /**
     * Adds the value of the drawn card to player's score.
     * @param D The representation of  the card to add.
     */
    public void updatePlayerScore(char D){
        float value = this.getCardValue(D);
        this.playerScore += value;
    }

    /**
     * Gets the Player's score in  the game.
     * @return The player's score.
     */
    public float getPlayerScore() {
        return playerScore;
    }
    
    /**
     * Player hand getter.
     * @return the handPlayer
     */
    public ArrayList getHandPlayer() {
        return handPlayer;
    }
    
    public void updateHandPlayer(String card){
        this.handPlayer.add(card);
    }

    /**
     * Method to check if player has overcome the maximum allowed score. 
     * @return True if the score is not in allowed range [0, 7.5]. Returns False otherwise.
     */
    public boolean isBusted(){
        return this.playerScore > 7.5f;
    }


}
    


