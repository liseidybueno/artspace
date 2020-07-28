import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.sql.*; 



public class landingPage extends Application{
	
	public TextField tfUSERNAME = new TextField();
    public PasswordField tfPASSWORD = new PasswordField();
    public Connection connection = null;
    private FlowPane logincheck = new FlowPane();
    public RadioButton artist = new RadioButton("Artist");
    public RadioButton buyer = new RadioButton("Buyer");
    public Stage primaryStage = new Stage();
    public String username;
	public String password;
	public String fname;
	public String lname;
	public String email;
	
    
	//create artist and buyer objects
	public static Artist currentartist = new Artist();
	public static Buyer currentbuyer = new Buyer();
	
	
	public void start(Stage primaryStage) {
		//initialize database
		initializeDB();
		 
		BorderPane LogInPane = new BorderPane();
		
		Font peachSundress = 
	            Font.loadFont(getClass()
	                .getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
		
		
		FlowPane LogIntop = new FlowPane();
		
		//Top Area 
		Text textTop = new Text("Welcome to ArtPals");
		textTop.setFill(Color.BLUE);
		textTop.setFont(peachSundress);
		
		LogIntop.getChildren().addAll(textTop);
		
		LogIntop.setStyle("-fx-background-color: #FFFFFF");
		
		LogIntop.setAlignment(Pos.CENTER);
		
		LogIntop.setPrefHeight(150);
		
		LogInPane.setTop(LogIntop);
		
		//Center Area
		
		GridPane LogInCenter = new GridPane();
	    LogInCenter.setAlignment(Pos.TOP_CENTER);
	    LogInCenter.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
	    LogInCenter.setHgap(5.5);
	    LogInCenter.setVgap(5.5);
	    
	    ToggleGroup artistbuyer = new ToggleGroup();
	    artist.setToggleGroup(artistbuyer);
	    buyer.setToggleGroup(artistbuyer);
	    
	    // Place radio button nodes in the pane
	    LogInCenter.add(artist, 0, 0);
	    LogInCenter.add(buyer, 1, 0);
	    GridPane.setHalignment(artist, HPos.LEFT);
	    GridPane.setHalignment(buyer, HPos.RIGHT);
	    
	    //Create labels and set up text fields
	    LogInCenter.add(new Label("Username: "), 0, 1);
	    LogInCenter.add(tfUSERNAME, 1, 1);
	    LogInCenter.add(new Label("Password: "), 0, 2);
	    LogInCenter.add(tfPASSWORD, 1, 2);
	    Button btLogIn = new Button("Log In");
	    Button btSignUpLogin = new Button("Sign Up");
	    LogInCenter.add(btLogIn, 1, 4);
	    LogInCenter.add(btSignUpLogin, 0, 4);
	    GridPane.setHalignment(btLogIn, HPos.RIGHT);
	    GridPane.setHalignment(btSignUpLogin, HPos.LEFT);
	   
	    LogInCenter.setPrefHeight(200);
	    
	    LogInCenter.setStyle("-fx-background-color: #FFFFFF");
	    
	    
	    LogInPane.setCenter(LogInCenter);
	    
	    //Bottom Area
	   
	    logincheck.setAlignment(Pos.TOP_CENTER);
	    logincheck.setPrefHeight(100);
	    logincheck.setStyle("-fx-background-color: #FFFFFF");
	    
	    
	    LogInPane.setBottom(logincheck);
	    
	    //LOG IN BUTTON ACTION
	    
	    btLogIn.setOnAction(e -> {
	    	logincheck.getChildren().clear();
			
			//set variables for username and password entered by user
			username = tfUSERNAME.getText();
			password = tfPASSWORD.getText();
			PreparedStatement pscheckpw; //create prepared statement
			Label notfound = new Label(); //create label to output error statement
			try {
				//create result set
			     ResultSet rsetcheckPW;
			   
			     //RETRIEVE AND STORE USER ID AS STATIC VARIABLE
			
			    //if buyer is selected, search in Buyer table:
			    if(buyer.isSelected()) {
			    	//create query - search for a column with the username and password entered by user
			    	String queryCheckPW = "SELECT * FROM Buyer WHERE BUser = ? AND PW= ?";
			    	//connect using prepared statement
			    	pscheckpw = connection.prepareStatement(queryCheckPW);
			    	//set prepared statement variables to username & password and execute query
			    	pscheckpw.setString(1, username);
			    	pscheckpw.setString(2, password);
			    	rsetcheckPW = pscheckpw.executeQuery();	
			    	 
			    	//if the column exists, then log in. else, output error messages 
			    	if(rsetcheckPW.next()) {
			    		logincheck.getChildren().clear();
			    		username = rsetcheckPW.getString("BUser");
			    		currentbuyer.setusername(username); 
			    		fname = rsetcheckPW.getString("FName");
			    		lname = rsetcheckPW.getString("LName");
			    		email = rsetcheckPW.getString("Email");
			    		password = rsetcheckPW.getString("PW");
			    		currentbuyer.setusername(username); 
			    		currentbuyer.setfname(fname);
			    		currentbuyer.setlname(lname);
			    		currentbuyer.setemail(email);
			    		BuyerPage secondWindow2 = new BuyerPage();
				    	secondWindow2.start(primaryStage);
			    		tfUSERNAME.clear();
			    		tfPASSWORD.clear();
			    	} else {
			    		logincheck.getChildren().clear();
			    		BuyerNotFound();
			    		tfPASSWORD.clear();
			    	}
			    }//if artist is selected, search in Artist table:
			    else if(artist.isSelected()){
			    	//create query - search for a column with the username and password entered by user
			    	String queryCheckPW = "SELECT * FROM Artist WHERE AUSer = ? AND PW = ?";
			    	//connect using prepared statement
			    	pscheckpw = connection.prepareStatement(queryCheckPW);
			    	//set prepared statement variables to username & password and execute query
			    	pscheckpw.setString(1, username);
			    	pscheckpw.setString(2, password);
			    	rsetcheckPW = pscheckpw.executeQuery();	
			    	 
			    	//if the column exists, then log in. else, output error messages 
			    	if(rsetcheckPW.next()) {
			    		logincheck.getChildren().clear();
			    		username = rsetcheckPW.getString("AUser");
			    		fname = rsetcheckPW.getString("FName");
			    		lname = rsetcheckPW.getString("LName");
			    		email = rsetcheckPW.getString("Email");
			    		password = rsetcheckPW.getString("PW");
			    		currentartist.setusername(username); 
			    		currentartist.setfname(fname);
			    		currentartist.setlname(lname);
			    		currentartist.setemail(email);
			    		currentartist.setpassword(password);
			    		ArtistPage secondWindow2 = new ArtistPage();
				    	secondWindow2.start(primaryStage);
			    		tfUSERNAME.clear();
			    		tfPASSWORD.clear();
			    	} else {
			    		logincheck.getChildren().clear();
			    		ArtistNotFound();
			    		tfPASSWORD.clear();
			    	}
			    } else {
			    	UserType();
			    }
			    
			    //add the label to the login check pane
			    logincheck.getChildren().add(notfound);
			    
			}
			    catch (SQLException ex) {
			      ex.printStackTrace();
			    } catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
	    
	    });	
	    
	    
	    //SIGN UP BUTTON ACTION
	    
	    btSignUpLogin.setOnAction( e -> {
	    	SignUp secondWindow = new SignUp();
	    	secondWindow.start(primaryStage);
	    });
	
	
	 // Create a scene and place it in the stage
    Scene scene = new Scene(LogInPane, 600, 400);
    primaryStage.setTitle("Art Pals"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
	}
	
	private void initializeDB() {
	    try {

	      // Establish a connection
	      connection = DriverManager.getConnection
	        ("jdbc:mysql://localhost:3306/artgallery?useTimezone=true&serverTimezone=UTC", "artgal", "xyz321");
	      System.out.println("Database connected");
	     
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }
	
	//popuwindows
	
	//buyer user/password not found 
		public static void BuyerNotFound()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
		     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Fields Blank");      
		Label label1= new Label("Buyer User or Password invalid.");
		      
		//close pop up window    
		Button button1= new Button("Close");     
		button1.setOnAction(e -> popupwindow.close());
		
		//layout of popup window
		VBox layout= new VBox(10);      
		layout.getChildren().addAll(label1, button1);
		layout.setAlignment(Pos.CENTER);
		      
		Scene scene= new Scene(layout, 300, 100);	      
		popupwindow.setScene(scene);	      
		popupwindow.showAndWait();
		       
		}
		
		//artist user/password not found
		public static void ArtistNotFound()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
		     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Fields Blank");      
		Label label1= new Label("Artist User or Password invalid.");
		      
		//close pop up window    
		Button button1= new Button("Close");     
		button1.setOnAction(e -> popupwindow.close());
		
		//layout of popup window
		VBox layout= new VBox(10);      
		layout.getChildren().addAll(label1, button1);
		layout.setAlignment(Pos.CENTER);
		      
		Scene scene= new Scene(layout, 300, 100);	      
		popupwindow.setScene(scene);	      
		popupwindow.showAndWait();
		       
		}
		
		//Select User Type
		public static void UserType()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
		     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Fields Blank");      
		Label label1= new Label("Please select a User Type.");
		      
		//close pop up window    
		Button button1= new Button("Close");     
		button1.setOnAction(e -> popupwindow.close());
		
		//layout of popup window
		VBox layout= new VBox(10);      
		layout.getChildren().addAll(label1, button1);
		layout.setAlignment(Pos.CENTER);
		      
		Scene scene= new Scene(layout, 300, 100);	      
		popupwindow.setScene(scene);	      
		popupwindow.showAndWait();
		       
		}
	
	
	public static void main(String[] args) { 
	    launch(args);
	  }
}
