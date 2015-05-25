package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
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
     * @param file
     * @return 
     */
    private HashMap<String, Product> loadProducts(String file) {
	String text = getStringFile(file);
	HashMap<String, Product> out = new HashMap<String, Product>();
	if(text == null) return out;

        /* Decode products from JSON string */   
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(text);
        JsonObject json = (JsonObject) obj;
        JsonArray array = json.getAsJsonArray("products");
       
        System.out.println("> Loading items from json @products.json");
        for (JsonElement e : array) {
            Product p = new Product((JsonObject) e);
            out.put(p.getName(), p);
        }
        
        return out;
    }
    
    /**
     * @param file
     * @param products
     * @return 
     */
    private HashMap<String, User> loadUsers(String file, HashMap<String, Product> products) {
	String text = getStringFile(file);
	HashMap<String, User> out = new HashMap<String, User>();
	if(text == null) return out;
        
        /* Decode products from JSON string */
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(text);
        JsonObject json = (JsonObject) obj;  
        JsonArray array = json.get("users").getAsJsonArray();
  
        System.out.println("> Loading users from json @users.json");
        for (JsonElement e : array) {
            User u = new User((JsonObject) e, products);
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
