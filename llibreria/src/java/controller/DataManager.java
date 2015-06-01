/**
 * PERSISTENCE DATA CONTROLLER.
 * 
 * Acts as a handler for JSON formatted Data (Users/Products).
 * Provides methods to load/save (JSON parsed) structured data.
 * Implements static methods to get both ConcurrentHashMap.
 */

package controller;

import model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import beans.Product;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author simorgh
 */
public class DataManager {
    private static DataManager instance = null;
    public static enum FileType {AUDIO, BOOK, VIDEO, UNDEFINED};
    private static ConcurrentHashMap<String, User> users;
    private static ConcurrentHashMap<String, Product> products;
    
    protected DataManager(String users, String products) {
	DataManager.products = loadProducts(products); //Note that products MUST be loaded first
	DataManager.users = loadUsers(users, DataManager.products);
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
    

    public static ConcurrentHashMap<String, Product> getProducts() {
	return products;
    }

    public static ConcurrentHashMap<String, User> getUsers() {
	return users;
    }
    
    
    /**
     * @param file
     * @return 
     */
    private ConcurrentHashMap<String, Product> loadProducts(String file) {
	String text = getStringFile(file);
	ConcurrentHashMap<String, Product> out = new ConcurrentHashMap<String, Product>();
	if(text == null) return out;

        /* Decode products from JSON string */   
        JsonParser parser = new JsonParser();
        JsonObject json= parser.parse(text).getAsJsonObject();
        JsonArray array = json.getAsJsonArray("products");
       
        System.out.println("> Loading items from json @products.json");
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            
            Gson gson = new Gson();
            Product p = gson.fromJson(obj, Product.class);
            out.put(Short.toString(p.getPid()), p);
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
    private ConcurrentHashMap<String, User> loadUsers(String file, ConcurrentHashMap<String, Product> products) {
	String text = getStringFile(file);
	ConcurrentHashMap<String, User> out = new ConcurrentHashMap();
	if(text == null) return out;
        
        /* Decode products from JSON string */
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(text).getAsJsonObject(); 
        JsonArray array = json.get("users").getAsJsonArray();
  
        System.out.println("> Loading users from json @users.json");
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            System.out.println(obj); //debug print
                        
            User u = new User(obj, DataManager.getProducts());
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
        
    /**
     * 
     * @param u 
     */
    public void addUser(User u){
        if(users.containsKey(u.getName())) return;
        DataManager.users.put(u.getName(), u);
    }
    
    
    
    ///////////////////////////////
    //  Persistence Methods
    ///////////////////////////////
    
    /**
     * 
     * @param filename
     * @throws FileNotFoundException 
     */
    public void saveUsers(String filename) throws FileNotFoundException {
        JsonArray users = new JsonArray();
        for(User u : DataManager.users.values()){
            
            JsonObject obj = new JsonObject();
            obj.addProperty("name", u.getName());
            obj.addProperty("credit", u.getCredits());
            
            JsonArray products = new JsonArray();
            for (Product p : u.getProducts()) {
                products.add(new JsonPrimitive(p.getPid()));
            }
            obj.add("products", products); 
            users.add(obj);
        }
        
        JsonObject root = new JsonObject();
        root.add("users", users);
        
        PrintWriter out = new PrintWriter(filename);
        out.write(root.toString());
        out.close();
    }
}
