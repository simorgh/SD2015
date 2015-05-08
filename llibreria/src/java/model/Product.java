package model;

import org.json.simple.JSONObject;

/**
 *
 * @author simorgh
 */
public class Product {
    public static enum FileType {AUDIO, BOOK, VIDEO, UNDEFINED};
         
    private FileType type;
    private String name; /* name must be unique */
    private String desc;
    private float price;
    private String path;
    private String thumb;

    public Product(FileType type, String name, String desc, int price, String path, String thumb) {
	this.type = type;
	this.name = name;
	this.desc = desc;
	this.price = price;
	this.path = path;
        this.thumb = thumb;
    }
    
    public Product(JSONObject obj) {
	this.name = (String) obj.get("name");
	this.type = getFileType((String) obj.get("type"));
	this.desc = (String) obj.get("desc");
	this.price = (Float) obj.get("price");
	this.path = (String) obj.get("path");
        this.thumb = (String) obj.get("thumb");
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