package model;

import java.util.ArrayList;

/**
 *
 * @author simorgh & dzigor92
 */
public class Game {
    private Deck deck;
    
    private final ArrayList handBank;
    private final ArrayList handPlayer;
    private float playerScore;
    private float bankScore;
    private int bet;
    
    private int cont;
    private boolean end;
    
    /**
     * Class constructor 
     * 
     * @param deck deep copy from Deck
     * @param strt_bet 
     */
    public Game(Deck deck, int strt_bet){
        this.deck = deepCopy(deck);
        this.bet = strt_bet;
        this.end = false;

        /* init game */
        this.handBank = new ArrayList();
        this.handPlayer = new ArrayList();
        this.cont = 0;
        //this.deck.shuffle(); /* unused due to testing issues */
    }

    /**
     * Bank Hand getter.
     * @return the handBank
     */
    public ArrayList getHandBank() {
        return handBank;
    }

    /**
     * Player hand getter.
     * @return the handPlayer
     */
    public ArrayList getHandPlayer() {
        return handPlayer;
    }

    /**
     * Starting bet getter.
     * @return the bet
     */
    public int getBet() {
        return bet;
    }
    
    /**
     * Calculates the value of the card.
     * @param D The draw card 
     * @return The value of the card
     */
    private float getCardValue(char D){
        if (Character.isDigit(D)) return (float)Character.getNumericValue(D);
        else return (float)0.5;  
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
     * Adds the value of the drawn card to bank's score.
     * @param D 
     */
    public void updateBankScore(char D){
        float value = this.getCardValue(D);
        this.bankScore+=value;
    }
    
    /**
     * Raises the bet.
     * @param value 
     */
    public void raiseBet(int value){
        this.bet += value;
    }

    /**
     * Player score getter.
     * @return the playerScore
     */
    public float getPlayerScore() {
        return playerScore;
    }

    /**
     * Bank score getter.
     * @return the bankScore
     */
    public float getBankScore() {
        return bankScore;
    }
    
    /**
     * End-Game getter
     * @return end state
     */
    public boolean isFinished(){
        return end;
    }
    
    /**
     * End-Game setter
     * @param end
     */
    public void setFinished(boolean end){
        this.end = end;
    }
    
    /**
     * Bank IA. The method plays the game, trying to beat the score achieved 
     * by the player. 
     * 
     */
    public void playBank(){
        char[] card;
        
        /* Player hand busted
         * Bank will draw once and pass. */
        if (this.playerScore > 7.5f){
            card = this.drawCard();
            this.handBank.add(card);
            this.updateBankScore(card[0]);
        }
        
        /* Player reached 7.5 score
         * Bank will draw until he reaches 7.5 or gets busted. */
        else if(this.playerScore == 7.5f) {
            while(this.bankScore < this.playerScore){
                card = this.drawCard();
                this.handBank.add(card);
                this.updateBankScore(card[0]);
            }
        }
        
        /* Player drew less than 7.5
         * Bank will draw until he gets a higher score (or gets busted) */
        else {
            while(this.bankScore <= this.playerScore){
                card = this.drawCard();
                this.handBank.add(card);
                this.updateBankScore(card[0]);
            }
        }
    }
    
    /**
     * Draws a card from the deck.
     * @return The card drawn from the deck.
     */
    public char[] drawCard(){
        char[] card = new char[2];
        card[0] =  this.deck.getCards().get(cont).charAt(0);
        card[1] =  this.deck.getCards().get(cont).charAt(1);
        this.cont++;
        return card;
    }

    /**
     * Method that compares the bank's  and the player's score and calculates players earnings.
     * @return The player's gains.
     */
    public int computeGains(){
        Integer boost = null ;
        
        /* Player wins */
        if(this.playerScore == 7.5f && this.playerScore != this.bankScore) boost = 2;   // 7half reached!
        else if(this.playerScore < 7.5f && (this.playerScore > this.bankScore || this.bankScore > 7.5f)) boost = 1; // regular success
        
        else if (this.playerScore == this.bankScore) boost = 0; // tie
 
        /* Player loses */
        else if (this.playerScore > 7.5|| (this.playerScore < 7.5 && this.playerScore < this.bankScore)) boost = -1;

        return boost*this.getBet();
    }
    
    
   /**
    * Clones Deck ArrayList and also clone its contents.
    * We will need to iterate on the items, and clone them one by one,
    * putting the clones in a result array as we go.
    *
    * @param source Deck to copy.
    * @return full deep copy of the source.
    */  
    private Deck deepCopy(Deck source) {
        ArrayList <String> clone = new ArrayList();
        for (String card : source.getCards()) clone.add(card);
        return new Deck(clone);
    }
    
}