/**
 * Class <code>User Bean</code>.
 */

package model;

import beans.Product;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author simorgh
 */
public class User {
    private String name; //user identifier
    private float credit;
    
    private ArrayList <Product> purchased;
    private ArrayList <Product> cart;

    /**
     * Constructor for persistence recovery
     * @param obj
     * @param products 
     */
    public User(JsonObject obj, ConcurrentHashMap <String, Product> products){
        this.name = obj.get("name").getAsString();
        this.credit = obj.get("credit").getAsFloat();
        
        this.purchased = new ArrayList();
                this.cart = new ArrayList();
                
        // load previosly owned Products from json
        JsonArray arr = obj.get("products").getAsJsonArray();
        for(int i=0; i < arr.size(); i++){
            String pid = arr.get(i).getAsString();
            if(products.containsKey(pid)) this.purchased.add(products.get(pid)); 
        }
    }
    
    /**
     * Constructor for register a new user into DataManager
     * @param name
     * @param credit 
     */
    public User(String name, float credit){
        this.name = name;
        this.credit = credit;
        
        purchased = new ArrayList();
        cart = new ArrayList();        
    }

    public String getName() {
	return name;
    }

    public float getCredits() {
	return credit;
    }
    
    public void setCredits(float c) {
        this.credit = c;
    }

    public ArrayList<Product> getProducts() {
	return purchased;
    }
    
    public ArrayList<Product> getCart() {
	return cart;
    }
    
    public void addToCart(Product item) {
        this.cart.add(item);
    }
    
   public void addToPurchased(Product item) {
        this.purchased.add(item);
    }
   
   public void removeFromCart(Product item) {
        this.cart.remove(item);
    }
   
    
}
