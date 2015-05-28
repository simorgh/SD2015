package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import beans.Product;
import beans.User;


/**
 * @author simorgh
 */
public class DataManager {
    private static DataManager instance = null;
    public static enum FileType {AUDIO, BOOK, VIDEO, UNDEFINED};
    private final HashMap<String, User> users;
    private final HashMap<String, Product> products;
    
    protected DataManager(String users, String products) {
	this.products = loadProducts(products);
	this.users = loadUsers(users, this.products);
    }
    /**
     * Singleton pattern
     * @param users
     * @param products
     * @return 
     */
    public static DataManager getInstance(String users, String products) {
        if(instance == null) {
            instance = new DataManager( users, products);
        }
        return instance;
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
        JsonObject json= parser.parse(text).getAsJsonObject();
        JsonArray array = json.getAsJsonArray("products");
       
        System.out.println("> Loading items from json @products.json");
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            System.out.println(obj.toString());
                        
            Product p = new Product();
            p.setId(obj.get("id").getAsShort());
            p.setName(obj.get("name").getAsString());
            p.setType(getFileType(obj.get("type").getAsString()));
            p.setDescription(obj.get("desc").getAsString());
            p.setPrice(obj.get("price").getAsFloat());
            p.setPath(obj.get("path").getAsString());
            p.setThumbnail(obj.get("thumb").getAsString());
            
            out.put(Short.toString(p.getId()), p);
        }
        
        return out;
    }
    
    /**
     * 
     * @param type
     * @return 
     */
    private FileType getFileType(String type) {
        try {
            return FileType.valueOf(type);
        } catch(IllegalArgumentException e) {
            return FileType.UNDEFINED;
        }
    }
    
    /**
     * @param file
     * @param products
     * @return 
     */
    private HashMap<String, User> loadUsers(String file, HashMap<String, Product> products) {
	String text = getStringFile(file);
	HashMap<String, User> out = new HashMap();
	if(text == null) return out;
        
        /* Decode products from JSON string */
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(text).getAsJsonObject(); 
        JsonArray array = json.get("users").getAsJsonArray();
  
        System.out.println("> Loading users from json @users.json");
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            
            /* load previosly owned Products from json */
            ArrayList <Product> owned = new ArrayList();
            JsonArray arr = obj.get("products").getAsJsonArray();
            for(int i = 0; i < arr.size(); i++){
                String pid = arr.get(i).getAsString();
                if(products.containsKey(pid) ) {
                    owned.add(products.get(pid));
                }
            }
            
            User u = new User();
            u.setName(obj.get("name").getAsString());
            u.setCredits(obj.get("credit").getAsFloat());
            u.setProducts(owned);
            System.out.println(obj.toString());
            
            out.put(u.getName(), u); //update user hashmap
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
	    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    try {
		if(reader != null)
		    reader.close();
	    } catch (IOException ex) {
		Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
        
	return null;
    }
        
    
    
    
    
    ///////////////////////////////
    //  Persistence Methods
    ///////////////////////////////
  
    /**
     * 
     * @param users 
     */
    public void saveUsers(HashMap<String, User> users) {
        
        for(User u : users.values()){
            
            JsonObject obj = new JsonObject();
            obj.addProperty("name", u.getName());
            obj.addProperty("credit", u.getCredits());
            
            JsonArray arr = new JsonArray();
            for (Product p : u.getProducts()) {
                arr.add(new JsonPrimitive(p.getId()));
            }
            obj.add("products", arr); 
        }
	
    }
}
