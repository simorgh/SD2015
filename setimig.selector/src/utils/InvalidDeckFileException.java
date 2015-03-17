package utils;

/**
 *
 * @author simorgh & dzigor92
 */
public class InvalidDeckFileException extends Exception {
    
    public InvalidDeckFileException() {
        super("Deck file doesn't contain the deck or has an incorrect format");
    }
}
