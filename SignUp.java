import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SignUp extends Application{
	
	//create textfield variables
	public TextField tfFName = new TextField();
	public TextField tfLName = new TextField();
	public TextField tfEmail = new TextField();
	public TextField tfUserName = new TextField();
	public PasswordField tfPassword = new PasswordField();
	public RadioButton artist = new RadioButton("Artist");
	public RadioButton buyer = new RadioButton("Buyer");
	public Connection connection = null;
	FlowPane SignUpBottom;
	public int A_ID;
	public int U_ID;
	
	
	public void start(Stage signupstage)
	{
	//initialize DB
		initializeDB();
		
	//Create border pane
	BorderPane SignUpPage = new BorderPane();
	
	//TOP Border
	//Create VBox 
	VBox SignUpTop = new VBox();

	Font peachSundress = 
            Font.loadFont(getClass()
                .getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
	
	//Create label for art pals
	Label textTop = new Label("ArtPals\n\n");
	textTop.setFont(peachSundress);
	textTop.setTextFill(Color.BLUE);
	
	//create label for sign up
	Label textSignUp = new Label("Sign Up");
	textSignUp.setFont(Font.font("Georgia", 20));
	
	//add labels to Vbox
	SignUpTop.getChildren().addAll(textTop, textSignUp);
	
	//set Vbox properties
	SignUpTop.setStyle("-fx-background-color: #FFFFFF");
	SignUpTop.setAlignment(Pos.CENTER);
	SignUpTop.setPrefHeight(100);
	SignUpTop.setPrefWidth(600);
	
	//Set Vbox as top
	SignUpPage.setTop(SignUpTop);
	
	//CENTER Border 
	//Create grid pane
	GridPane SignUpCenter = new GridPane();
	SignUpCenter.setAlignment(Pos.TOP_CENTER);
    SignUpCenter.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
    SignUpCenter.setHgap(5.5);
    SignUpCenter.setVgap(5.5);
    
    //Create toggle group
    ToggleGroup artistbuyer = new ToggleGroup();
    artist.setToggleGroup(artistbuyer);
    buyer.setToggleGroup(artistbuyer);
    
    //Place text fields & radio buttons in the pane
    SignUpCenter.add(new Label("First Name: "), 0, 1);
    SignUpCenter.add(tfFName, 1, 1);
    SignUpCenter.add(new Label("Last Name: "), 0, 2);
    SignUpCenter.add(tfLName, 1, 2);
    SignUpCenter.add(new Label("Email: "), 0, 3);
    SignUpCenter.add(tfEmail, 1, 3);
    SignUpCenter.add(new Label("Username: "), 0, 4);
    SignUpCenter.add(tfUserName, 1, 4);
    SignUpCenter.add(new Label("Password: "), 0, 5);
    SignUpCenter.add(tfPassword, 1, 5);
    SignUpCenter.add(new Label("Are you a Buyer or an Artist? "), 0, 6);
    SignUpCenter.add(artist, 1, 6);
    SignUpCenter.add(buyer, 1, 6);
    GridPane.setHalignment(artist, HPos.LEFT);
    GridPane.setHalignment(buyer, HPos.RIGHT);
	
    //set GridPane properties
  	SignUpCenter.setStyle("-fx-background-color: #FFFFFF");
  	SignUpCenter.setAlignment(Pos.CENTER);
  	SignUpCenter.setPrefHeight(200);
  	SignUpCenter.setPrefWidth(600);
  	
  	//Set Gridpane as Center
  	SignUpPage.setCenter(SignUpCenter);
  	
  	//BOTTOM of Border
  	//Create Flow Pane
  	SignUpBottom = new FlowPane();
  	SignUpBottom.setHgap(15);
  	
  	//Create Sign Up & Log In Buttons
  	Button btCreate = new Button("Create Account");
  	Button btLogin = new Button("Log In");
  	
  	
  	//Add button to pane
  	SignUpBottom.getChildren().addAll(btLogin, btCreate);
  	
  	//set FlowPane properties
  	SignUpBottom.setStyle("-fx-background-color: #FFFFFF");
  	SignUpBottom.setAlignment(Pos.TOP_CENTER);
  	SignUpBottom.setPrefHeight(100);
  	
  	//set FlowPane as bottom
  	SignUpPage.setBottom(SignUpBottom);
  	
  	//Create Account Button event
	//Create Sign Up button action
	btCreate.setOnAction(new EventHandler<ActionEvent>(){
		
		public void handle(ActionEvent event) {
		//Create Result Sets & Prepared Statements
		  ResultSet uniqueUserArtist;
		  ResultSet uniqueUserBuyer;
		  ResultSet uniqueEmailArtist;
		  ResultSet uniqueEmailBuyer;
		  PreparedStatement pscheckuserartist;
		  PreparedStatement pscheckemailartist;
		  PreparedStatement pscheckuserbuyer;
		  PreparedStatement pscheckemailbuyer;
		  String username = tfUserName.getText();
		  String email = tfEmail.getText();
			  
		  try {
			//if any of the text fields are empty, create pop up error
			if(tfFName.getText().isEmpty() ||
		  		tfLName.getText().isEmpty() || 
		  		tfEmail.getText().isEmpty() || 
		  		tfUserName.getText().isEmpty() || 
		  		tfPassword.getText().isEmpty() ||
		  		!artist.isSelected() && !buyer.isSelected())
			{
				FieldsBlank();
			}//if text fields are not empty, check database to make sure username & email are unique
			else 
			{
				//check user in artist
				String queryCheckUserArtist = "SELECT * FROM Artist WHERE AUser = ?";
				pscheckuserartist = connection.prepareStatement(queryCheckUserArtist);	  
				pscheckuserartist.setString(1, username);				  
				uniqueUserArtist = pscheckuserartist.executeQuery();
				
				String queryEmailArtist = "SELECT * FROM Artist WHERE Email = ?";
				pscheckemailartist = connection.prepareStatement(queryEmailArtist);
				pscheckemailartist.setString(1,  email);
				uniqueEmailArtist = pscheckemailartist.executeQuery();	
				
				//check user in buyer
				String queryCheckUserBuyer = "SELECT * FROM Buyer WHERE BUser = ?";
				pscheckuserbuyer = connection.prepareStatement(queryCheckUserBuyer);	  
				pscheckuserbuyer.setString(1, username);				  
				uniqueUserBuyer = pscheckuserbuyer.executeQuery();
				
				String queryEmailBuyer = "SELECT * FROM Buyer WHERE Email = ?";
				pscheckemailbuyer = connection.prepareStatement(queryEmailBuyer);
				pscheckemailbuyer.setString(1,  email);
				uniqueEmailBuyer = pscheckemailbuyer.executeQuery();	
				
				if(uniqueEmailArtist.next() || uniqueEmailBuyer.next()) {
					EmailNotUnique();
				} else if (uniqueUserArtist.next() || uniqueUserBuyer.next()) {
					UsernameNotUnique();
				} else {
					InsertIntoDB(signupstage);
				}

			}
				  
		} catch (SQLException ex) {
			ex.printStackTrace();
			}
			  
		}
		
	});
	
	//Log In Button Action Event
	btLogin.setOnAction( e -> {
    	landingPage secondWindow = new landingPage();
    	secondWindow.start(signupstage);
    });
  		
 
  	
	Scene scene = new Scene(SignUpPage, 600, 400);
	signupstage.setScene(scene);
	signupstage.show();

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
	
	
	private void InsertIntoDB(Stage stage) {
		//variables to search
		String fname = tfFName.getText();
		String lname = tfLName.getText();
		String email = tfEmail.getText();
		String user = tfUserName.getText();
		String pw = tfPassword.getText();
		
		//Create Prepared Statements
		PreparedStatement insertartist;
		PreparedStatement insertbuyer;
		
		try{
			//if artist is selected, insert new user into Artist table
			if(artist.isSelected()) {
				String queryInsertArtist = "INSERT INTO Artist VALUES (?, ?, ?, ?, ?)";
				insertartist = connection.prepareStatement(queryInsertArtist);
				insertartist.setString(1, fname);
				insertartist.setString(2, lname);
				insertartist.setString(3, email);
				insertartist.setString(4, user);
				insertartist.setString(5, pw);
			
				insertartist.executeUpdate();
				
				//set artist info in artist object
				landingPage.currentartist.setusername(user);
	    		landingPage.currentartist.setpassword(pw); 
	    		landingPage.currentartist.setfname(fname);
	    		landingPage.currentartist.setlname(lname);
	    		landingPage.currentartist.setemail(email);
		    	
				//go to artist page
				ArtistPage secondWindow2 = new ArtistPage();
		    	secondWindow2.start(stage);
		    
		    	
				//if buyer is selected, insert new user into Buyer table
			} else if(buyer.isSelected()) {
				String queryInsertBuyer = "INSERT INTO Buyer VALUES (?, ?, ?, ?, ?)";
				insertbuyer = connection.prepareStatement(queryInsertBuyer);
				insertbuyer.setString(1, fname);
				insertbuyer.setString(2, lname);
				insertbuyer.setString(3, email);
				insertbuyer.setString(4, user);
				insertbuyer.setString(5, pw);
				
				insertbuyer.executeUpdate();	
				
				//set username
				
				landingPage.currentbuyer.setusername(user);
	    		landingPage.currentbuyer.setpassword(pw); 
	    		landingPage.currentbuyer.setfname(fname);
	    		landingPage.currentbuyer.setlname(lname);
	    		landingPage.currentbuyer.setemail(email);
				
				//go to buyer page
				BuyerPage secondWindow2 = new BuyerPage();
		    	secondWindow2.start(stage);
				
			}
			
			
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	//popup windows
	
	//method for if any fields are blank
		public static void FieldsBlank()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
		     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Fields Blank");      
		Label label1= new Label("Please make sure to fill in all fields.");
		      
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
		
		//method for if username is not unique
		public static void UsernameNotUnique()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
			     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Username Used");      
		Label label1= new Label("This Username is already in use. Please log in if you have an account.");
			      
		//close pop up window    
		Button button1= new Button("Close");     
		button1.setOnAction(e -> popupwindow.close());
			
		//layout of popup window
		VBox layout= new VBox(10);      
		layout.getChildren().addAll(label1, button1);
		layout.setAlignment(Pos.CENTER);
			      
		Scene scene= new Scene(layout, 500, 100);	      
		popupwindow.setScene(scene);	      
		popupwindow.showAndWait();
			       
		}
		
		//method for if email is not unique
		public static void EmailNotUnique()
		{
		//create new stage for pop up window
		Stage popupwindow=new Stage();
			     
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Email Used");      
		Label label1= new Label("This Email is already in use. Please log in if you have an account.");
			      
		//close pop up window    
		Button button1= new Button("Close");     
		button1.setOnAction(e -> popupwindow.close());
			
		//layout of popup window
		VBox layout= new VBox(10);      
		layout.getChildren().addAll(label1, button1);
		layout.setAlignment(Pos.CENTER);
			      
		Scene scene= new Scene(layout, 450, 100);	      
		popupwindow.setScene(scene);	      
		popupwindow.showAndWait();
			       
		}
	
}
	
