package utils;

/**
 * Customized Exception.
 * ---------------------
 * 
 * Used when 'ERRO' message header has been recived.
 * The treatment MUST involve a call of receiveErrorDescription() in
 * order to release arguments from it's socket socket and a proper closing.
 * 
 * @author simorgh & dzigor92
 */
public class ProtocolErrorException extends Exception {
    
    public ProtocolErrorException() {
        super("Error message received.");
    }
}
