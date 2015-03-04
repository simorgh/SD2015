package model;

/**
 *
 * @author simorgh & dzigor92
 */

public class Game {
    private float playerScore;
    
    /**
     * Class constructor 
     * 
     */
    public Game(){
        this.playerScore = 0.0f;
    }
    
    /**
     * Calculates the value of the card 
     * @param D The draw card 
     * @return The value of the card
     */
    private float getCardValue(char D){
        if (Character.isDigit(D)) return (float)Character.getNumericValue(D);
        else{
            return (float)0.5;
        }      
    }
    
    /**
     * Adds the value of the drawn card to player's score
     * @param D
     */
    public void updatePlayerScore(char D){
        float value = this.getCardValue(D);
        this.playerScore += value;
    }

    /**
     * @return the playerScore
     */
    public float getPlayerScore() {
        return playerScore;
    }

    /**
     * 
     * @return 
     */
    public boolean isBusted(){
        return this.playerScore > 7.5f;
    }


}
    


