/**
 * Class <code>Product Bean</code>.
 */

package beans;

import java.io.Serializable;
import controller.DataManager.FileType;

/**
 * @author simorgh
 */
public class Product implements Serializable {
    private short id;
    private FileType type;
    private String name; /* name must be unique */
    private String desc;
    private float price;
    private String path;
    private String thumb;

    /** No-arg constructor (takes no arguments). */
    public Product(){    
    }
    
    
    public FileType getType() {
	return type;
    }

    public void setType(FileType mType) {
	this.type = mType;
    }

    public String getName() {
	return name;
    }

    public void setName(String mName) {
	this.name = mName;
    }

    public short getId(){
        return id;
    }
    
    public void setId(short mId){
        this.id = mId;
    }
    
    public String getDescription() {
	return desc;
    }

    public void setDescription(String mDescription) {
	this.desc = mDescription;
    }

    public float getPrice() {
	return price;
    }

    public void setPrice(float mPrice) {
	this.price = mPrice;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }
    
    public void setThumbnail(String thumb){
        this.thumb = thumb;
    }
        
    public String getThumbnail(){
        return this.thumb;
    }
}