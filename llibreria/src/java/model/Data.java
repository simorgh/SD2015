package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author simorgh
 */
public class Data {
    private final HashMap<String, User> users;
    private final HashMap<String, Product> products;
    
    public Data(String users, String products) {
	this.products = loadProducts(products);
	this.users = loadUsers(users, this.products);
    }
    
    public HashMap<String, Product> getProducts() {
	return products;
    }

    public HashMap<String, User> getUsers() {
	return users;
    }
    
    
    /**
     * 
     * @param file
     * @return 
     */
    private HashMap<String, Product> loadProducts(String file) {
	String text = getStringFile(file);
	HashMap<String, Product> out = new HashMap<String, Product>();
	if(text == null) return out;

        /* Decode products from JSON string */
        JSONArray array = null;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(text);
            JSONObject json = (JSONObject) obj;
            array = (JSONArray) json.get("products");
        } catch (ParseException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        for (Object e : array) {
            Product p = new Product((JSONObject) e);
            out.put(p.getName(), p);
        }
        
        return out;
    }
    
    /**
     * 
     * @param file
     * @param products
     * @return 
     */
    private HashMap<String, User> loadUsers(String file, HashMap<String, Product> products) {
	String text = getStringFile(file);
	HashMap<String, User> out = new HashMap<String, User>();
	if(text == null) return out;
        //JSONObject obj = new JSONObject(text);
        //JSONArray array = obj.getJSONArray("users");
        
        /* Decode products from JSON string */
        JSONArray array = null;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(text);
            JSONObject json = (JSONObject) obj;
            array = (JSONArray) json.get("users");
        } catch (ParseException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Object e : array) {
            User u = new User((JSONObject) e, products);
            out.put(u.getName(), u);
        }
        
        return out;
    }
    
    /**
     * 
     * @param file
     * @return 
     */
    private String getStringFile(String file) {
	BufferedReader reader = null;
        
	try {
 
	    reader = new BufferedReader(new FileReader(file));
	    String line;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    while( ( line = reader.readLine() ) != null ) {
		stringBuilder.append(line);
		stringBuilder.append(ls);
	    }

	    return stringBuilder.toString();
            
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    try {
		if(reader != null)
		    reader.close();
	    } catch (IOException ex) {
		Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
        
	return null;
    }
    
    
    
}
