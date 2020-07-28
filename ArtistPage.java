import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ArtistPage extends Application {

	//create artwork object
	ArtWork currentartwork = new ArtWork();
	
	private Connection connection = null;
	
	public void start (Stage artiststage) throws FileNotFoundException {
		
		//initialize DB
		initializeDB();
		
		//Create Border Pane 
		BorderPane ArtistPage = new BorderPane();
		ArtistPage.setStyle("-fx-background-color: #FFFFFF");
		
		//TOP Border
		//Create VBox 
		VBox ArtistPageTop = new VBox();

		Font peachSundress = 
	            Font.loadFont(getClass()
	                .getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
		
		//Create label for art pals
		Label textTop = new Label(landingPage.currentartist.getUsername() + "'s Artist Space \n\n");
		textTop.setFont(peachSundress);
		textTop.setTextFill(Color.BLUE);
		
		//create HBox for buttons
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(10);
		
		//create upload button
		Button btUpload = new Button("Upload Art Work");
		Button btRefresh = new Button("Refresh");
		Button btLogout = new Button("Log Out");
		
		//put buttons in hbox
		buttons.getChildren().addAll(btUpload, btRefresh, btLogout);
		
		//set on action for upload button
		btUpload.setOnAction(e -> {
			
			UploadFile(artiststage); 
			
		});
		
		//set on action for log out
		btLogout.setOnAction(e -> {
			
			landingPage secondwindow = new landingPage();
			secondwindow.start(artiststage);
		});
		
		//add labels to Vbox
		ArtistPageTop.getChildren().addAll(textTop, buttons);
		
		//set Vbox properties
		ArtistPageTop.setStyle("-fx-background-color: #FFFFFF");
		ArtistPageTop.setAlignment(Pos.CENTER);
		ArtistPageTop.setPrefHeight(100);
		ArtistPageTop.setPrefWidth(800);
		
		//Set Vbox as top
		ArtistPage.setTop(ArtistPageTop);
		
		//CENTER BORDERPANE
		//Create scroll pane
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setPannable(true);
		scrollpane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px");
		// Setting a horizontal scroll bar is always display
		scrollpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
	 
		// Setting vertical scroll bar is never displayed.
		scrollpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		//Create tile pane
		TilePane tile = new TilePane();
		tile.setStyle("-fx-background-color: #FFFFFF");
		tile.setAlignment(Pos.CENTER);
		tile.setPrefWidth(780);
		tile.setPrefHeight(530);
		
		tile.setPadding(new Insets(15, 15, 15, 15));
		
		tile.setHgap(10);
		
		//create the path for the folder which is the artists'username
		String path = landingPage.currentartist.getUsername();
		
		//create folder object using filepath
		File folder = new File(path);
		//create array for the list of files in the folder 
		File[] listofFiles = folder.listFiles();
		
		//if the folder exists and there are files in the folder, display them
		if(folder.exists() && listofFiles.length != 0) {
		for(final File file:listofFiles) {
			ImageView imageView;
			imageView = createImageView(file);
			tile.getChildren().addAll(imageView);
			tile.setAlignment(Pos.TOP_CENTER);
		} 
		//f the folder does not exist (artist has not uploaded any images) display label
		} else {
			Label lblnoimages = new Label("No Artwork for this Artist");
			lblnoimages.setFont(Font.font(20));
			tile.getChildren().addAll(lblnoimages);
			tile.setAlignment(Pos.CENTER);
		}
		
		scrollpane.setContent(tile);
		
		ArtistPage.setCenter(scrollpane);
		
		//set on action for refresh. refresh page to show new artwork. 
		btRefresh.setOnAction(e -> {
			try {
				start(artiststage);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		
		Scene scene = new Scene(ArtistPage, 800, 600);
		artiststage.setScene(scene);
		artiststage.show();
		
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
	
	//method for showing each individual art work in new image view
	private ImageView createImageView(final File imageFile) {
	//creates imageview for each artwork
	
	//create imageview
    ImageView imageView = null;
    
    try {
    	//create image for selected imagefile
    	final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
        imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

        //create mouse event for when image is clicked on once
        @Override
        public void handle(MouseEvent mouseEvent) {
        	if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

            if(mouseEvent.getClickCount() == 1){
            	try {
            		//create scrollpane
            		ScrollPane scrollpane = new ScrollPane();
                    scrollpane.setFitToWidth(true);
                    scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
                    scrollpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
                    scrollpane.setStyle("-fx-background-color: #FFFFFF");
                  
                    //Create borderpane
                    BorderPane borderPane = new BorderPane();
                                
                    //create vbox
                    VBox vbox = new VBox();
                    vbox.setPadding(new Insets (15, 15, 15, 15));
                    vbox.setPrefHeight(400);
                    vbox.setAlignment(Pos.BOTTOM_CENTER);
                                
                    //Create imageview & set image into imageview
                    ImageView imageView = new ImageView();
                    Image image = new Image(new FileInputStream(imageFile));
                    imageView.setImage(image); imageView.setStyle("-fx-background-color: WHITE");
                    imageView.setFitHeight(390); 
                                
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setCache(true);
                    
                    //add imageview to vbox
                    vbox.getChildren().add(imageView);
                    
                    //add vbox to border pane
                    borderPane.setCenter(vbox);
                                
                    //create new vbox
                    VBox labels = new VBox();
                    labels.setAlignment(Pos.TOP_CENTER);
                    labels.setPadding(new Insets(15, 15, 15, 15));
                    
                    
                    //set up art work info
                    String workname = imageFile.getName();
                    StringTokenizer st = new StringTokenizer(workname, ".");
                    workname = st.nextToken();
                    String medium = null;
                    double price = 0;
                    String description = null;
                    String artist = landingPage.currentartist.getUsername();
                    
                    //look for artwork info in SQL database
                    PreparedStatement psartworkinfo;
                    ResultSet rsartworkinfo;
                    
                    try {
                    	String queryartworkinfo = "SELECT * FROM Artwork WHERE Name = ? AND AUser = ?";
                    	psartworkinfo = connection.prepareStatement(queryartworkinfo);
						psartworkinfo.setString(1, workname);
						psartworkinfo.setString(2, artist);
						rsartworkinfo = psartworkinfo.executeQuery();
                    
						if(rsartworkinfo.next()) {
							//set medium, price, and description to what is in the SQL database
							medium = rsartworkinfo.getString("Medium");
							price = rsartworkinfo.getDouble("Price");
							description = rsartworkinfo.getString("Descript");
						}
						
                    } catch (SQLException e) {
						e.printStackTrace();
					}
               
                    //display art info
                    //create string to display price with 2 decimal place
                    BigDecimal bdprice = new BigDecimal(price).setScale(2, RoundingMode.UNNECESSARY);
                    
                    Label lblworkname = new Label(workname);
                    lblworkname.setFont(Font.font("Georgia", 18));
                    Label lblmedium = new Label(medium);
                    lblmedium.setFont(Font.font("Georgia", 18));
                    Label lblprice = new Label("$" + bdprice);
                    lblprice.setFont(Font.font("Georgia", 18));
                    Text textdescription = new Text(description);
                    textdescription.setFont(Font.font("Georgia", 14));
                    textdescription.setWrappingWidth(300);
                    textdescription.setTextAlignment(TextAlignment.CENTER);
                
                    if(description.equals("")) {
                    	labels.getChildren().addAll(lblworkname, lblmedium, lblprice);
                    	
                    } else if(!description.equals("")) {
                    	ScrollPane descriptionbox = new ScrollPane();
                        descriptionbox.setContent(textdescription);
                        descriptionbox.setMaxWidth(300);
                        descriptionbox.setMaxHeight(200);
                        descriptionbox.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
                        descriptionbox.setHbarPolicy(ScrollBarPolicy.NEVER);
                        descriptionbox.setStyle("-fx-background-color: #FFFFFF; -fx-border-width: 0px;");
                    	labels.getChildren().addAll(lblworkname, lblmedium, lblprice, descriptionbox);
                    }
                    
                    borderPane.setBottom(labels);
                    borderPane.setStyle("-fx-background-color: WHITE");
                    scrollpane.setContent(borderPane);
                                
                    Stage newStage = new Stage();
                    newStage.setWidth(620);
                    newStage.setTitle(imageFile.getName());
                    Scene scene = new Scene(scrollpane);
                    newStage.setScene(scene);
                    newStage.show();
            	} catch (FileNotFoundException e) {
                     e.printStackTrace();
                  }

            }
        	}
        }
       	});
    } catch (FileNotFoundException ex) {
    	ex.printStackTrace();
    	}
    return imageView;
	}


	//method for uploading file and saving info to SQL database
	// @SuppressWarnings("null")
	private void UploadFile(Stage stage) {
		//create new stage for pop up window
		Stage popupwindow=new Stage();
    			
    	GridPane uploadlayout = new GridPane();
    			     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Upload");      
    			
    	//create labels and buttons and textfields 
    	Label lblartname= new Label("*Name of Work: ");
    	TextField tfartname = new TextField();
    	Label lblmedium = new Label("*Medium: ");
    	ComboBox<String> cbmedium = new ComboBox<>();
    	cbmedium.setValue("Select Medium");
    	cbmedium.getItems().addAll("Acrylic Painting", "Oil", "Pastel Painting", "Pencil", "Watercolor", "Sculpture",
    					"Colored Pencil", "Digital Art", "Mixed Media","Other");
    	Label lblop = new Label ("*Original or Print: ");
    	RadioButton original = new RadioButton("Original");
    	RadioButton print = new RadioButton("Print");
    	Label lblprice = new Label("*Price: ");
    	TextField tfprice = new TextField();
    	Label lbldescription = new Label("Description: ");
    	TextArea description = new TextArea();
    	description.setMaxWidth(400);
    	Label lblrequired = new Label("* Required Fields");
    	lblrequired.setFont(Font.font("Times", FontPosture.ITALIC, 12));
    			
    	//HBox for radio buttons
    	HBox radiobuttons = new HBox();
    	radiobuttons.getChildren().addAll(original, print);
    			
    	//set toggle group for radio buttons
    	ToggleGroup originalprint = new ToggleGroup();
    	original.setToggleGroup(originalprint);
    	print.setToggleGroup(originalprint);
    				
    	//add nodes to grid pane
    	uploadlayout.add(lblartname, 0, 0);
    	uploadlayout.add(tfartname, 1, 0);
    	uploadlayout.add(lblmedium, 0, 1);
    	uploadlayout.add(cbmedium, 1, 1);
    	uploadlayout.add(lblop, 0, 2);
    	uploadlayout.add(radiobuttons, 1, 2);
    	uploadlayout.add(lblprice, 0, 3);
    	uploadlayout.add(tfprice, 1, 3);
    	uploadlayout.add(lbldescription, 0, 4);
    	uploadlayout.add(description, 1, 4);
    		    
    	uploadlayout.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
    	uploadlayout.setHgap(5.5);
    	uploadlayout.setVgap(5.5);
    			      
    	//close pop up window    
    	Button cancel = new Button("Cancel");     
    	cancel.setOnAction(e -> popupwindow.close());
    			
    	//Upload button
    	Button upload = new Button("Upload");
    	
    	uploadlayout.add(cancel, 1, 5);
		uploadlayout.add(upload, 1, 5);
		uploadlayout.add(lblrequired, 1, 5);
		GridPane.setHalignment(cancel, HPos.LEFT);
		GridPane.setHalignment(upload, HPos.CENTER);
		GridPane.setHalignment(lblrequired, HPos.RIGHT);
		
		//upload action
    	upload.setOnAction(e -> {
    		
    		//create strings for artwork info
    		String workname;
    		String medium;
    		double price = 0;
    		String descript = null;
    		String origprint;
    		String artist = landingPage.currentartist.getUsername(); 
    		String artworkid = null;
    		
    		//if any of the required fields are blank, then generate fields blank popup
    		if(tfartname.getText().isEmpty() || cbmedium == null || tfprice.getText().isEmpty() || !original.isSelected() && !print.isSelected()) {
				FieldsBlankPopup();
			} else {
				
			//if none of the fields are blank, then make sure that price is numeric
				boolean numeric = true;
				try {
				    price = Double.parseDouble(tfprice.getText());
				} catch (NumberFormatException ex) {
				    numeric = false;
				}
				
				//if it is numeric, continue with program
				if(numeric) {
					//set strings to get info about artwork
					workname = tfartname.getText();
					medium = (String) cbmedium.getValue(); 
					descript = description.getText();
					RadioButton selectedRadioButton = (RadioButton) originalprint.getSelectedToggle();
					origprint = selectedRadioButton.getText();
					
				
					//check to make sure that workname is unique for this particular artist in database
					
					//Prepared statements for SQL query & result set
					PreparedStatement pscheckworkname;
					//PreparedStatement insertartwork;
					ResultSet rscheckworkname;
    		
					//see if the work exists in the database by searching by the name of the work
					try {
						String query = "SELECT * FROM Artwork WHERE Name = ? AND AUser = ?";
						pscheckworkname = connection.prepareStatement(query);
						pscheckworkname.setString(1, workname);
						pscheckworkname.setString(2, artist);
						rscheckworkname = pscheckworkname.executeQuery();
    			
					//if the work exists under the user, user has to give the workname a unique name
					if(rscheckworkname.next()) {
						WorkExistsPopup();
					
					//if the work does not exist, then save art info into artwork object
					} else {
					
						//set current artwork object info
			    		currentartwork.setWorkname(workname);
			    		currentartwork.setMedium(medium);
			    		currentartwork.setPrice(price);
			    		currentartwork.setOorP(origprint);
			    		currentartwork.setDescription(descript);
			    		currentartwork.setArtist(artist);
			    		
			    		//create and save the artwork id
			    		artworkid = artist + workname;
			    		currentartwork.setArtworkID(artworkid);
			    		
			    		//once the information is in the database, upload & save the image that will correspond with info
			    		UploadSave(stage);
			    		
			    		popupwindow.close();
						
					}
    			
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
    		
 
    		
    		//if price entered is NOT numeric, then show pop up
			} else {
				PriceNotNumericPopup();
			}
			}
    	});
    			
    	Scene scene= new Scene(uploadlayout);	      
    	popupwindow.setScene(scene);	      
    	popupwindow.showAndWait();
    			       
   }
    		
    	
    
    
public void UploadSave(Stage stage) {
    	
    	//create filechooser
    	FileChooser fileChooser = new FileChooser();
		
    	//open file chooser and get selected artwork
		File selectedArtwork = fileChooser.showOpenDialog(stage);
		
		//set extension filters to images
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
			     new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
			     new FileChooser.ExtensionFilter("PNG Files", "*.png"));
		
		//get artwork name 
		String artworkfullname = selectedArtwork.toURI().toString();
		
		
		//get the extension of the file name
		String ext = artworkfullname.substring(artworkfullname.lastIndexOf(".") + 1);
		
		//create buffered image from artwork image
		BufferedImage artworkimage = null;
		try {
			artworkimage = ImageIO.read(selectedArtwork);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//set work name
		String workname = currentartwork.getWorkname();
		
		
		//folder name should be artist userne
		String foldername = landingPage.currentartist.getUsername();
		
		//set folder to the artist id
		File folder = new File(foldername);
		
		//check if the folder already exists
		if(!folder.exists()) {
			//if it doesn't exist, then create the folder 
			folder.mkdir();
		}
		
	    //set filepath to folder workname
	    String filepath = folder + "/" + workname + "." + ext;
		
	    //create new file
		File file = new File(filepath);
		
		//save selected artwork image as the file name
		try {
			ImageIO.write(artworkimage, ext, file);
		} catch (IOException ex) {
			ex.printStackTrace();
			
		}
		
		//Create variables for art info
		String medium = currentartwork.getMedium();
		String origprint = currentartwork.getOorP();
		Double price = currentartwork.getPrice();
		String descript = currentartwork.getDescription();
		String artworkid = currentartwork.getArtworkID();
		
		
		//insert artwork info into database
		
		PreparedStatement insertartwork;
		
		try {String queryinsertartwork = "INSERT INTO Artwork VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		insertartwork = connection.prepareStatement(queryinsertartwork);
		insertartwork.setString(1, workname);
		insertartwork.setString(2, medium);
		insertartwork.setString(3, origprint);
		insertartwork.setDouble(4, price);
		insertartwork.setString(5, descript);
		insertartwork.setString(6, filepath);
		insertartwork.setString(7, foldername); //foldername = artist ID
		insertartwork.setString(8, artworkid);
		
		
		insertartwork.executeUpdate();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		
		
    }
    
    public void WorkExistsPopup() {
    	
    	//popup for if the work exists
    	//create new stage for pop up window
    	Stage popupwindow=new Stage();
    	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Work Exists");      
    	Label label1= new Label("You already have an artwork under this name. Please use another name.");
    	      
    	//close pop up window    
    	Button button1= new Button("Close");     
    	button1.setOnAction(e -> popupwindow.close());
    	
    	//layout of popup window
    	VBox layout= new VBox(10);      
    	layout.getChildren().addAll(label1, button1);
    	layout.setAlignment(Pos.CENTER);
    	      
    	Scene scene= new Scene(layout, 400, 100);	      
    	popupwindow.setScene(scene);	      
    	popupwindow.showAndWait();
    	
    }
    
    public static void FieldsBlankPopup()
	{
    	//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Fields Blank");      
    	Label label1= new Label("A field is blank. Please fill in all required fields.");
	      
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
    
    public static void PriceNotNumericPopup()
	{
    	//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Price");      
    	Label label1= new Label("Please enter a number for price.");
	      
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
    

}
