package model;

import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author simorgh
 */
public class User {
    private String name; /* must be unique */
    private float credit;
    private HashMap<String, Product> products;

    public User(String name, float credit, HashMap<String, Product> products) {
	this.name = name;
	this.credit = credit;
	this.products = products;
    }
    
    /**
     * 
     * @param obj
     * @param totalproducts 
     */
    public User(JSONObject obj, HashMap<String, Product> totalproducts) {
	this.name = (String) obj.get("name");
	this.credit = (Float) obj.get("credit");
	this.products = new HashMap<String, Product>();
        
        JSONArray arr = (JSONArray) obj.get("products");
        Iterator i = arr.iterator();

        while (i.hasNext()) {
            JSONObject product = (JSONObject) i.next();
            String p = (String) product.get("title");
            
            if(totalproducts.containsKey(p)) {
		products.put(p, totalproducts.get(p));
	    }
        }	
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
     * 
     * @param obj 
     */
    public void save(JSONObject obj) {
	obj.put("name", name);
	obj.put("credit",credit);
	
	JSONArray arr = new JSONArray();
	for (Product p : products.values()) {
	    arr.add(p.getName());
	}
	obj.put("products", arr);
    }
}
