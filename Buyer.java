public class Buyer {
  private String fname;
  private String lname;
  private String username;
  private String email;
  private String password;
  

  /** Default constructor */
  public Buyer() {
    this.fname = "";
    this.lname = "";
    this.email = "";
    this.username = "";
    this.password = "";
  }

  /** Construct an artist with first name last name, username, email, password 
    */
  public Buyer(String fname, String lname, String username, String email, String password) {
    this.fname = fname;
    this.lname = lname;
    this.email = email;
    this.username = username;
    this.password = password;
  }

  /** Return first name */
  public String getFname() {
    return fname;
  }

  /** Set a new first name */
  public void setfname(String fname) {
    this.fname = fname;
  }
  
  /** Return last name */
  public String getLname() {
    return lname;
  }

  /** Set a new last name */
  public void setlname(String lname) {
    this.lname = lname;
  }
  
  /** Return user name */
  public String getUsername() {
    return username;
  }

  /** Set a new user name */
  public void setusername(String username) {
    this.username = username;
  }
  
  /** Return password */
  public String getPassword() {
    return password;
  }

  /** Set a new password */
  public void setpassword(String password) {
    this.password = password;
  }

  /** Return email */
  public String getEmail() {
    return email;
  }

  /** Set a new  email */
  public void setemail(String email) {
    this.email = email;
  }


}