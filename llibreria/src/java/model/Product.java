package model;

import com.google.gson.JsonObject;
import java.io.Serializable;

/**
 *
 * @author simorgh
 */
public class Product implements Serializable {
    public static enum FileType {AUDIO, BOOK, VIDEO, UNDEFINED};
    private short id;
    private FileType type;
    private String name; /* name must be unique */
    private String desc;
    private float price;
    private String path;
    private String thumb;

    public Product(){
        
    }
    
    public Product(short id, FileType type, String name, String desc, int price, String path, String thumb) {
        this.id = id;
	this.type = type;
	this.name = name;
	this.desc = desc;
	this.price = price;
	this.path = path;
        this.thumb = thumb;
    }
    
    public Product(JsonObject obj) {
        this.id = obj.get("id").getAsShort();
        this.name = obj.get("name").getAsString();
	this.type = getFileType(obj.get("type").getAsString());
	this.desc = obj.get("desc").getAsString();
	this.price = obj.get("price").getAsFloat();
	this.path = obj.get("path").getAsString();
        this.thumb = obj.get("thumb").getAsString();
        
        System.out.println(obj.toString());
    }

    private FileType getFileType(String type) {
        try {
            return FileType.valueOf(type);
        } catch(IllegalArgumentException e) {
            return FileType.UNDEFINED;
        }
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