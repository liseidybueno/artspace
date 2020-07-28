import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BuyerSearch extends Application{

	private Connection connection = null;
	
	//create artwork object
	ArtWork currentartwork = new ArtWork();
	
	public void start(Stage searchstage) {
		//initialize DB
		initializeDB();
						
		//Create Border Pane 
		BorderPane SearchPage = new BorderPane();
		SearchPage.setStyle("-fx-background-color: #FFFFFF");
						
		//TOP Border
		//Create VBox 
		VBox SearchPageTop = new VBox();

		Font peachSundress = Font.loadFont(getClass().getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
						
		//Create label for art pals
		Label textTop = new Label("search art");
		textTop.setFont(peachSundress);
		textTop.setTextFill(Color.BLUE);
					
		//create return button
		Button btReturn = new Button("Return");
		
		//create search by button
		Button btSearchBy = new Button("Search By");
		
		//search opens pop up to search using criteria
		btSearchBy.setOnAction(e -> {
			SearchBy secondwindow2 = new SearchBy();
			secondwindow2.start(searchstage);
		});
						
		//return returns to buyerpage
		btReturn.setOnAction(e -> {
			BuyerPage secondwindow2 = new BuyerPage();
			secondwindow2.start(searchstage);
		});
		
		HBox buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(btReturn, btSearchBy);
		
		SearchPageTop.getChildren().addAll(textTop, buttons);
				
		//set Vbox properties
		SearchPageTop.setStyle("-fx-background-color: #FFFFFF");
		SearchPageTop.setAlignment(Pos.CENTER);
		SearchPageTop.setPrefHeight(100);
		SearchPageTop.setPrefWidth(800);
						
		//Set Vbox as top
		SearchPage.setTop(SearchPageTop);
						
		//CENTER BORDERPANE
		//Create scroll pane
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setPannable(true);
		scrollpane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px");
		// Setting a horizontal scroll bar is always display
		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
					 
		// Setting vertical scroll bar is never displayed.
		scrollpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			
		//Create tile pane
		TilePane tile = new TilePane();
		tile.setStyle("-fx-background-color: #FFFFFF");
		tile.setAlignment(Pos.CENTER);
		tile.setPrefWidth(755);
		tile.setPrefHeight(530);
		tile.setPadding(new Insets(15, 15, 15, 15));	
		tile.setHgap(10);
		
		PreparedStatement psartwork;
		ResultSet rsartwork;
		
		try {
			
			String queryjointtable = "SELECT * FROM Artwork";
			psartwork = connection.prepareStatement(queryjointtable);
			
			rsartwork = psartwork.executeQuery();
			
			//create array list of arwork ID's
			ArrayList<String> artworkID = new ArrayList<String>();
			
			//add artwork ID to array list
			while(rsartwork.next()) {
				artworkID.add(rsartwork.getString("ArtworkID"));
			}
			
			//if if the arraylist is empty, display no images in gallery text
			if(artworkID.isEmpty()) {
				Label lblnoimages = new Label("No Images Found Matching Your Criteria");
				lblnoimages.setFont(Font.font(20));
				tile.getChildren().addAll(lblnoimages);
				tile.setAlignment(Pos.CENTER);
				}
			//if it is not empty, get filepaths, search in DB then get images from project folder and display as gallery
			//using image view
			else {
				//convert arraylist of artwork IDs to array
				String[] artIDarray = new String[artworkID.size()];
				for (int i =0; i < artworkID.size(); i++) {
					artIDarray[i] = artworkID.get(i); 
				}
				
				//put array into tile pane using for loop
				for(final String artwork:artIDarray) {
					
					String filepath;
					
					//search for particular artwork's filepath in Artwork table
					PreparedStatement psfilepath;
					ResultSet rsfilepath;
					
					try {
						String queryfilepath = "SELECT filepath FROM Artwork WHERE ArtworkID = ?";
						psfilepath = connection.prepareStatement(queryfilepath);
						psfilepath.setString(1, artwork);
						
						rsfilepath = psfilepath.executeQuery();
						
						//go through column
						while(rsfilepath.next()) {
							//get filepath
							filepath = rsfilepath.getString("filepath");
							
							File file = new File(filepath);
							
							//set current artwork's filepath
							currentartwork.setImagePath(filepath);
							
							//create imageview
							ImageView imageView;
							imageView = createImageView(file); 
							tile.getChildren().addAll(imageView);
							tile.setAlignment(Pos.TOP_CENTER);
							
						}
						
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
					
				}
			}
				
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		
		scrollpane.setContent(tile);
		
		SearchPage.setCenter(scrollpane);
		
		Scene scene = new Scene(SearchPage, 800, 600);
		searchstage.setScene(scene);
		searchstage.show();
		
	}
	
	private ImageView createImageView(final File imageFile) {
		//creates imageview for each artwork
		
		//set stage
		Stage newStage = new Stage();
        newStage.setWidth(620);
        
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
	                    String filepath = imageFile.getName();
	                    StringTokenizer st = new StringTokenizer(filepath, ".");
	                    String workname = st.nextToken();
	                    String medium = null;
	                    double price = 0;
	                    String description = null;
	                    String artist;
	                    String artistfirst = null;
	                    String artistlast = null;
	                    String artistemail = null;
	                    String artworkid = null;
	                    
	                    //set up buttons
	                   // Button btContactArtist = new Button();
	                    
	                    //add to gallery
	                    Button btAddToGallery = new Button("Add to Gallery");
	                    
	                    //combo box for galleries
	                    ComboBox<String> galleries = new ComboBox<>();
	                    galleries.setValue("Add to Gallery...");
	                    String galleryname;
	                    
	                    //search for buyer's galleries to put into combo box 
	                    PreparedStatement psgalleries;
	                    ResultSet rsgalleries;
	                    
	                    try {
	                    	String querygalleries = "SELECT * FROM Gallery WHERE BUser = ?";
	                    	psgalleries = connection.prepareStatement(querygalleries);
	                    	psgalleries.setString(1, landingPage.currentbuyer.getUsername());
	                    	rsgalleries = psgalleries.executeQuery();
	                    	
	                    	while(rsgalleries.next()) {
	                    		
	                    		//get name of gallery
	                    		galleryname = rsgalleries.getString("GalName");
	                    		
	                    		//show in combo box
	                    		galleries.getItems().addAll(galleryname);
	                    	}
	                    	
	                    } catch (SQLException ex){
	                    	ex.printStackTrace();
	                    }
	                    
	                    //hbox for add to gallery
	                    HBox addtogallery = new HBox();
	                    addtogallery.setSpacing(10);
	                    addtogallery.getChildren().addAll(galleries, btAddToGallery);
	                    addtogallery.setAlignment(Pos.CENTER);
	                    
	                   
	                    //look for artwork info in SQL database
	                    PreparedStatement psartworkinfo;
	                    ResultSet rsartworkinfo;
	                    PreparedStatement psartistinfo;
	                    ResultSet rsartistinfo;
	                    
	                    try {
	                    	String queryartworkinfo = "SELECT * FROM Artwork WHERE Name = ?";
	                    	psartworkinfo = connection.prepareStatement(queryartworkinfo);
							psartworkinfo.setString(1, workname);
							rsartworkinfo = psartworkinfo.executeQuery();
	                    
							if(rsartworkinfo.next()) {
								//set medium, price, and description to what is in the SQL database
								medium = rsartworkinfo.getString("Medium");
								price = rsartworkinfo.getDouble("Price");
								description = rsartworkinfo.getString("Descript");
								artist = rsartworkinfo.getString("AUser");
								artworkid = rsartworkinfo.getString("ArtworkID");
								currentartwork.setArtworkID(artworkid);
								
								//search for artist in Artist database
								String queryartistinfo = "SELECT * FROM Artist WHERE AUser = ?";
								
								try {
									psartistinfo = connection.prepareStatement(queryartistinfo);
									psartistinfo.setString(1, artist);
									rsartistinfo = psartistinfo.executeQuery();
									
									if(rsartistinfo.next()) {
										artistfirst = rsartistinfo.getString("FName");
										artistlast = rsartistinfo.getString("LName");
										artistemail = rsartistinfo.getString("Email");
									}
									
								} catch (SQLException ex) {
									ex.printStackTrace();
								}
								
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
	                    Label lblname = new Label(artistfirst + " " + artistlast);
	                    lblname.setFont(Font.font("Georgia", 18));
	                    Text textdescription = new Text(description);
	                    textdescription.setFont(Font.font("Georgia", 14));
	                    textdescription.setWrappingWidth(300);
	                    textdescription.setTextAlignment(TextAlignment.CENTER);
	                    Label lblemail = new Label(artistemail);
	                    lblemail.setFont(Font.font("Georgia", 18));
	                
	                    if(description.equals("")) {
	                    	labels.getChildren().addAll(lblworkname, lblname, lblmedium, lblprice, lblemail, addtogallery);
	                    	
	                    } else if(!description.equals("")) {
	                    	ScrollPane descriptionbox = new ScrollPane();
	                        descriptionbox.setContent(textdescription);
	                        descriptionbox.setMaxWidth(300);
	                        descriptionbox.setMaxHeight(200);
	                        descriptionbox.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
	                        descriptionbox.setHbarPolicy(ScrollBarPolicy.NEVER);
	                        descriptionbox.setStyle("-fx-background-color: #FFFFFF; -fx-border-width: 0px;");
	                        descriptionbox.setPadding(new Insets(10, 10, 10, 10));
	                    	labels.getChildren().addAll(lblworkname, lblname, lblmedium, lblprice, descriptionbox, lblemail, addtogallery);
	                    }
	                    
	                  //add to gallery button action: add button to gallery by storing in DB
	                    btAddToGallery.setOnAction(e ->{
	                    
	                    	//if "Add to Gallery" is chosen, pop up that says to choose gallery
	                    	//if a gallery is chosen, get the name of the gallery from the 
	                    	//combo box and insert into DB and pop up confirmation
	                    	String galleryselected;
	                    	String galleryid = "";
	                    	
	                    	galleryselected = (String) galleries.getValue();
	                    	
	                    	if(!galleryselected.equals("Add to Gallery..."))
	                    	{
	                    		//search gallery info
	                    		PreparedStatement psgalleryinfo;
	                    		ResultSet rsgalleryinfo;
	                    		
	                    		try {
	                    			String querygalleryinfo = "SELECT * FROM Gallery WHERE GalName = ? AND BUser = ?";
	                    			psgalleryinfo = connection.prepareStatement(querygalleryinfo);
	                    			psgalleryinfo.setString(1, galleryselected);
	                    			psgalleryinfo.setString(2, landingPage.currentbuyer.getUsername());
	                    			
	                    			rsgalleryinfo = psgalleryinfo.executeQuery();
	                    			
	                    			if(rsgalleryinfo.next()) {
	                    				galleryid = rsgalleryinfo.getString("GalID");
	                    			}
	                    			
	                    		}
	                    		catch(SQLException ex) {
	                    			ex.printStackTrace();
	                    		}
	                    		
	                    		//add to joint table
		                    	PreparedStatement psaddtojoint;
		                    	
		                    	try {
		                    		String queryaddtojoint = "INSERT INTO ArtGalJoin VALUES(?, ?)";
		                    		psaddtojoint = connection.prepareStatement(queryaddtojoint);
		                    		psaddtojoint.setString(1, galleryid);
		                    		psaddtojoint.setString(2, currentartwork.getArtworkID());
		                    		
		                    		psaddtojoint.executeUpdate();
		                    		
		                    		Inserted();
		                    		
		                    		newStage.close(); 
		                    	
		                    	} catch(SQLException ex) {
		                    		ex.printStackTrace();
		                    	}
	                    		
	                    	} else {
	                    		GalleryNotSelected();
	                    	}
	                    
	                    });
	                    
	                    
	                    borderPane.setBottom(labels);
	                    borderPane.setStyle("-fx-background-color: WHITE");
	                    scrollpane.setContent(borderPane);
	                                
	                    
	                    newStage.setTitle(workname);
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
	
	public static void Inserted() {
		
		//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Inserted into Gallery");      
    	Label label1= new Label("Artwork has been inserted into gallery.");
	      
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
	
	public static void GalleryNotSelected() {
		
		//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Gallery Not Selected");      
    	Label label1= new Label("Please select a gallery.");
	      
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


}
