/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author simorgh & dzigor92
 */
public class InvalidDeckFileException extends Exception
{
    public InvalidDeckFileException( ) 
    {
        super("Deck file doesn't contain the deck or has an incorrect format");
    }
}
