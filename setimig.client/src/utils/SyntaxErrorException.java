package utils;

/**
 * Customized Exception.
 * ---------------------
 * 
 * Used when any part of the datastream is not following the RFC Protocol.
 * The treatment MUST involve a call of sendError specifying ERR_SYNTAX
 * and a proper closing.
 * 
 * @author simorgh & dzigor92
 */
public class SyntaxErrorException extends Exception {
    
    public SyntaxErrorException() {
        super("Invalid Protocol syntax detected.");
    }
}
