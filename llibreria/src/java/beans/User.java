/**
 * Class <code>User Bean</code>.
 */

package beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author simorgh
 */
public class User implements Serializable{
    private String name; /* must be unique */
    private float credit;
    private ArrayList <Product>products;

    /** No-arg constructor (takes no arguments). */
    public User(){    
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

   
    public ArrayList<Product> getProducts() {
	return products;
    }

    public void setProducts(ArrayList <Product> mProducts) {
	this.products = mProducts;
    }
    
}
