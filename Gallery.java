//create gallery object
public class Gallery {
	private String galname;
	private String buyerid;
	private String galleryid;
	
	//default constructor
	public Gallery() {
		galname = "";
		buyerid = "";
		galleryid = "";
	}
	
	//Construct gallery with name, buyerid and galleryid
	public Gallery(String galname, String buyerid, String galleryid) {
		this.galname = galname;
		this.buyerid = buyerid;
		this.galleryid = galleryid;
	}
	
	//set gallery name
	public void setGalleryName(String galname) {
		this.galname = galname;
	}
	
	//get gallery name
	public String getGalleryName() {
		return galname;
	}
	
	//set buyer id
	public void setBuyerID(String buyerid) {
		this.buyerid = buyerid;
	}
		
	//get buyer id
	public String getBuyerID() {
		return buyerid;
	}
	
	
	//set gallery id
	public void setGalleryID(String galleryid) {
		this.galleryid = galleryid;
	}
		
	//get gallery name
	public String getGalleryID() {
		return galleryid;
	}
	
}
