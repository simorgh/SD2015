package utils;

/**
 * @author simorgh & dzigor92
 */
public class DuplicatedCardException extends Exception {
    
    public DuplicatedCardException() {
        super("The card has been already registered. An error occured.");
    }
}
