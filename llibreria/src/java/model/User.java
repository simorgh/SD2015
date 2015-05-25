package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author simorgh
 */
public class User implements Serializable{
    private String name; /* must be unique */
    private float credit;
    private HashMap<String, Product> products;

    public User(){
        
    }
    
    public User(String name, float credit, HashMap<String, Product> products) {
	this.name = name;
	this.credit = credit;
	this.products = products;
    }
    
    /**
     * @param obj
     * @param totalproducts 
     */
    public User(JsonObject obj, HashMap<String, Product> totalproducts) {
	this.name = obj.get("name").getAsString();
	this.credit = obj.get("credit").getAsFloat();
	this.products = new HashMap<String, Product>();
  
        JsonArray arr = obj.get("products").getAsJsonArray();
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < arr.size(); i++){
            String pid = arr.get(i).getAsString();
            list.add(pid);
            if(totalproducts.containsKey(pid) ) {
                products.put(pid, totalproducts.get(pid));
            }
        }
        
        System.out.println(obj.toString());
    }

    public String getName() {
	return name;
    }

    public void setName(String mName) {
	this.name = mName;
    }

    public float getCredits() {
	return credit;
    }

    public void setCredits(float mCredits) {
	this.credit = mCredits;
    }

    public HashMap<String, Product> getProducts() {
	return products;
    }

    public void setProducts(HashMap<String, Product> mProducts) {
	this.products = mProducts;
    }
    
    /**
     * @param obj 
     */
    public void save(JsonObject obj) {
	obj.addProperty("name", name);
        obj.addProperty("credit", credit);

	JsonArray arr = new JsonArray();
	for (Product p : products.values()) {
	    arr.add(new JsonPrimitive(p.getId()));
	}
	obj.add("products", arr);
    }
}
