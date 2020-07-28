//create artwork object

public class ArtWork {
  private String workname;
  private String medium;
  private String OorP;
  private double price;
  private String description;
  private String imagepath;
  private String artist;
  private String artworkid;
  

  /** Default constructor */
  public ArtWork() {
    this.workname = "";
    this.medium = "";
    this.OorP = "";
    this.price = 0;
    this.description = "";
    this.imagepath = "";
    this.artist = "";
    this.artworkid = "";
  }

  /** Construct an artwork with work name, medium, original or print, price, description, image path, artist ID, ID
    */
  public ArtWork(String workname, String medium, String OorP, double price, String description, String imagepath, String artist, String artworkid) {
	  this.workname = workname;
	    this.medium = medium;
	    this.OorP = OorP;
	    this.price = price;
	    this.description = description;
	    this.imagepath = imagepath;
	    this.artist = artist;
	    this.artworkid = artworkid;
  }

  /** Return work name */
  public String getWorkname() {
    return workname;
  }

  /** Set a new first name */
  public void setWorkname(String workname) {
    this.workname = workname;
  }
  
  /** Return medium */
  public String getMedium() {
    return medium;
  }

  /** Set a new medium */
  public void setMedium(String medium) {
    this.medium= medium;
  }
  
  /** Return Original or print*/
  public String getOorP() {
    return OorP;
  }

  /** Set a Original or print */
  public void setOorP(String OorP) {
    this.OorP = OorP;
  }
  
  /** Return price */
  public double getPrice() {
    return price;
  }

  /** Set a new price */
  public void setPrice(double price) {
    this.price = price;
  }

  /** Return description */
  public String getDescription() {
    return description;
  }

  /** Set a new  description */
  public void setDescription(String description) {
    this.description = description;
  }
  
  /** Return artist ID */
  public String getArtist() {
    return artist;
  }

  /** Set a new artist A_ID */
  public void setArtist(String artist) {
    this.artist = artist;
  }
  
  public String getImagepath() {
	  return imagepath;
  }
  
  public void setImagePath(String imagepath) {
	  this.imagepath = imagepath;
  }
  
  public String getArtworkID() {
	  return artworkid;
  }
  
  public void setArtworkID(String artworkid) {
	  this.artworkid = artworkid;
  }

}