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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SearchBy extends Application{
	
	private Connection connection = null;
	ArtWork currentartwork = new ArtWork();

public void start(Stage searchbystage) {
	
	//initialize DB
	initializeDB();
							
	//Create Border Pane 
	BorderPane searchByPage = new BorderPane();
	searchByPage.setStyle("-fx-background-color: #FFFFFF");
							
	//TOP Border
	//Create VBox 
	VBox searchByPageTop = new VBox();

	Font peachSundress = Font.loadFont(getClass().getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
							
	//Create label for art pals
	Label textTop = new Label("search art");
	textTop.setFont(peachSundress);
	textTop.setTextFill(Color.BLUE);
						
	//create return button
	Label textSearchBy = new Label("Search By: ");
	textSearchBy.setFont(Font.font("Georgia", 20));
	
	searchByPageTop.getChildren().addAll(textTop, textSearchBy);
	
	searchByPage.setTop(searchByPageTop);
					
	//set Vbox properties
	searchByPageTop.setStyle("-fx-background-color: #FFFFFF");
	searchByPageTop.setAlignment(Pos.CENTER);
	searchByPageTop.setPrefHeight(100);
	searchByPageTop.setPrefWidth(800);
							
	//Set Vbox as top
	searchByPage.setTop(searchByPageTop);
	
	//CENTER BORDER PANE
	VBox center = new VBox();
	
	GridPane searchByCenter = new GridPane();
	searchByCenter.setAlignment(Pos.TOP_CENTER);
	searchByCenter.setPadding(new Insets(10, 10, 10, 10));
	searchByCenter.setHgap(10);
	searchByCenter.setVgap(10);
	
	//create radiobuttons
	RadioButton workName = new RadioButton("Work Name");
	RadioButton artistFName = new RadioButton("Artist First Name");
	RadioButton artistLName = new RadioButton("Artist Last Name");
	RadioButton medium = new RadioButton("Medium");
	RadioButton origprint = new RadioButton("Original or Print");
	
	ToggleGroup searchcriteria = new ToggleGroup();
	workName.setToggleGroup(searchcriteria);
	artistFName.setToggleGroup(searchcriteria);
	artistLName.setToggleGroup(searchcriteria);
	medium.setToggleGroup(searchcriteria);
	origprint.setToggleGroup(searchcriteria);
	
	//origprint criteria
	ComboBox<String> origOrPrint = new ComboBox<>();
	origOrPrint.setValue("Type of work");
	origOrPrint.getItems().addAll("Original", "Print");
	
	//medium criteria
	ComboBox<String> cbmedium = new ComboBox<>();
	cbmedium.setValue("Medium is");
	cbmedium.getItems().addAll("Acrylic Painting", "Oil", "Pastel Painting", "Pencil", "Watercolor", "Sculpture",
			"Colored Pencil", "Digital Art", "Mixed Media","Other");

	
	//create textfields
	TextField tfWorkName = new TextField();
	TextField tfArtistFName = new TextField();
	TextField tfArtistLName = new TextField();
	
	searchByCenter.add(workName, 0, 0);
	searchByCenter.add(tfWorkName, 1, 0);
	searchByCenter.add(artistFName, 0, 1);
	searchByCenter.add(tfArtistFName, 1, 1);
	searchByCenter.add(artistLName, 0, 2);
	searchByCenter.add(tfArtistLName, 1, 2);
	searchByCenter.add(medium, 0, 3);
	searchByCenter.add(cbmedium, 1, 3);
	searchByCenter.add(origprint, 0, 4);
	searchByCenter.add(origOrPrint, 1, 4);

	
	//Buttons
	HBox buttons = new HBox();
	buttons.setSpacing(15);
	
	//create buttons
	Button btBack = new Button("Previous");
	Button btSearch = new Button("Search");
	buttons.getChildren().addAll(btBack, btSearch);
	buttons.setAlignment(Pos.TOP_CENTER);
	
	//add buttons and grid to vbox
	
	center.getChildren().addAll(searchByCenter, buttons);
	
	//set center
	searchByPage.setCenter(center);
	
	//button actions
	//cancel closes window
	btBack.setOnAction(e -> {
		BuyerSearch secondwindow2 = new BuyerSearch();
		secondwindow2.start(searchbystage);	
	});
	
	//searches database based on criteria
	btSearch.setOnAction(e -> {
		
		//if workname is selected
		if(workName.isSelected()) {
			String entered = tfWorkName.getText();
				//search exact name in database
				PreparedStatement psexactname;
				ResultSet rsexactname;
				
				try {
					String queryexactname = "SELECT * FROM Artwork WHERE Name = ?";
					psexactname = connection.prepareStatement(queryexactname);
					psexactname.setString(1, entered);
					
					rsexactname = psexactname.executeQuery();
					
					//create arraylist
					ArrayList<String> filepaths = new ArrayList<String>();
					
					
					while(rsexactname.next()) {
						filepaths.add(rsexactname.getString("filepath"));
					}
					
						SearchResults(filepaths, searchbystage);
						
						tfWorkName.clear();
					
				} catch(SQLException ex) {
					ex.printStackTrace();
				}
				
			}
			
			
			//if firstname is selected
			if(artistFName.isSelected()) {
				String enteredfname = tfArtistFName.getText();
				String auser;
					//search exact name in database
					PreparedStatement psexactname;
					ResultSet rsexactname;
					
					try {
						String queryexactname = "SELECT * FROM Artist WHERE FName = ?";
						psexactname = connection.prepareStatement(queryexactname);
						psexactname.setString(1, enteredfname);
						
						rsexactname = psexactname.executeQuery();
						
						//create array for usernames
						ArrayList<String>username = new ArrayList<String>();
						
						while(rsexactname.next()) {
							
							auser = rsexactname.getString("AUser");
							username.add(auser);
								}
								
						//convert to array
						String[] userarray = new String[username.size()];
						for (int i =0; i < username.size(); i++) {
							userarray[i] = username.get(i); 
						}
						
						PreparedStatement psartfilepath;
						ResultSet rsartfilepath;
						
						ArrayList<String> filepaths = new ArrayList<String>();
								
						for(final String artistusername:userarray)
						{ 	
							try {
								String queryartfilepath = "SELECT * FROM Artwork WHERE AUser = ?";
								psartfilepath = connection.prepareStatement(queryartfilepath);
								psartfilepath.setString(1, artistusername);
								
								rsartfilepath = psartfilepath.executeQuery();
									
								while(rsartfilepath.next()) {
										
									filepaths.add(rsartfilepath.getString("filepath"));
								}
									
							} catch(SQLException ex) {
									ex.printStackTrace();
							}
						}
							
						SearchResults(filepaths, searchbystage);
						
						tfArtistFName.clear();
							
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
					
				}
			
			//if lastname is selected
			if(artistLName.isSelected()) {
				String enteredlname = tfArtistLName.getText();
				String auser;
				
				//search exact name in database
				PreparedStatement psexactname;
				ResultSet rsexactname;
				
				try {
					String queryexactname = "SELECT * FROM Artist WHERE LName = ?";
					psexactname = connection.prepareStatement(queryexactname);
					psexactname.setString(1, enteredlname);
					
					rsexactname = psexactname.executeQuery();
					
					//create array for usernames
					ArrayList<String>username = new ArrayList<String>();
					
					while(rsexactname.next()) {
						
						auser = rsexactname.getString("AUser");
						username.add(auser);
							}
							
					//convert to array
					String[] userarray = new String[username.size()];
					for (int i =0; i < username.size(); i++) {
						userarray[i] = username.get(i); 
					}
					
					PreparedStatement psartfilepath;
					ResultSet rsartfilepath;
					
					ArrayList<String> filepaths = new ArrayList<String>();
							
					for(final String artistusername:userarray)
					{ 	
						try {
							String queryartfilepath = "SELECT * FROM Artwork WHERE AUser = ?";
							psartfilepath = connection.prepareStatement(queryartfilepath);
							psartfilepath.setString(1, artistusername);
							
							rsartfilepath = psartfilepath.executeQuery();
								
							while(rsartfilepath.next()) {
									
								filepaths.add(rsartfilepath.getString("filepath"));
							}
								
						} catch(SQLException ex) {
								ex.printStackTrace();
						}
					}
						
					SearchResults(filepaths, searchbystage);
					
					tfArtistLName.clear();
						
				} catch(SQLException ex) {
					ex.printStackTrace();
				}
						
			}
				
		//if medium is selected
			if(medium.isSelected()) {
				String enteredmedium = cbmedium.getValue();
				
				PreparedStatement psexactname;
				ResultSet rsexactname;
					
				try {
					String queryexactname = "SELECT * FROM Artwork WHERE Medium = ?";
					psexactname = connection.prepareStatement(queryexactname);
					psexactname.setString(1, enteredmedium);
					
					rsexactname = psexactname.executeQuery();
					
					ArrayList<String> filepaths = new ArrayList<String>();
					
					while(rsexactname.next()) {
						filepaths.add(rsexactname.getString("filepath"));
					}
					
					//SearchResults searchwindow = new SearchResults(filepaths);
					//searchwindow = 
					SearchResults(filepaths, searchbystage);
					
				} catch(SQLException ex) {
						ex.printStackTrace();
					}
					
				}
			
			//if origprint is selected
			if(origprint.isSelected()) {
				String enteredorigprint = origOrPrint.getValue();
				
				PreparedStatement psexactname;
				ResultSet rsexactname;
					
				try {
					String queryexactname = "SELECT * FROM Artwork WHERE OrigPrint = ?";
					psexactname = connection.prepareStatement(queryexactname);
					psexactname.setString(1, enteredorigprint);
					
					rsexactname = psexactname.executeQuery();
					
					ArrayList<String> filepaths = new ArrayList<String>();
					
					while(rsexactname.next()) {
						filepaths.add(rsexactname.getString("filepath"));
					}
					
					SearchResults(filepaths, searchbystage);
					
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
					
				}
			
			
	});
	
	Scene scene = new Scene(searchByPage, 600, 400);
	searchbystage.setScene(scene);
	searchbystage.show();

	
}

public void SearchResults(ArrayList<String> filepaths, Stage searchresultsstage) {
	
	//create borderpane
	BorderPane searchresults = new BorderPane();
	
	Font alightyNesia = Font.loadFont(getClass().getResourceAsStream("/fonts/Alighty Nesia.ttf"), 45);
	
	//set Vbox for top 
	VBox searchResultsTop = new VBox();
	searchResultsTop.setStyle("-fx-background-color: #FFFFFF");
	searchResultsTop.setSpacing(10);
	searchResultsTop.setAlignment(Pos.CENTER);
	
	//create labels for Vbox
	Label lblsearchresults = new Label("SEARCH RESULTS");
	lblsearchresults.setFont(alightyNesia);
	
	//create button for vbox
	Button btBack = new Button("Back to Search");
	btBack.setOnAction(e -> {
		SearchBy newwindow = new SearchBy();
		newwindow.start(searchresultsstage);
	});
	
	searchResultsTop.getChildren().addAll(lblsearchresults, btBack);
	
	searchresults.setTop(searchResultsTop);
	
	//create scrollpane
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
	tile.setPrefWidth(800);
	tile.setPrefHeight(600);
	tile.setPadding(new Insets(15, 15, 15, 15));	
	tile.setHgap(10);
			
	//if if the arraylist is empty, pop up no work found
	if(filepaths.isEmpty()) {
		NoWorkFound();
	}
	
	//if it is not empty, get filepaths, search in DB then get images from project folder and display as gallery
	//using image view
	else {
	//convert arraylist of artwork IDs to array
		String[] filepathsarray = new String[filepaths.size()];
			for (int i =0; i < filepaths.size(); i++) {
				filepathsarray[i] = filepaths.get(i); 
			}	
			
			//put array into tile pane using for loop
			for(final String filename:filepathsarray) {
				
				File file = new File(filename);
				ImageView imageView;
				imageView = createImageView(file);
				tile.getChildren().addAll(imageView);
				tile.setAlignment(Pos.TOP_CENTER);
						
			}
					
		}
	
	scrollpane.setContent(tile);
	
	searchresults.setCenter(scrollpane);
	
	Scene scene= new Scene(searchresults, 800, 600);	      
	searchresultsstage.setScene(scene);	      
	searchresultsstage.show();
}


private ImageView createImageView(final File imageFile) {
	//creates imageview for each artwork
	
	//set up stage
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
	                    		
	                    		BuyerSearch.Inserted();
	                    		
	                    		newStage.close();
	                    		
	                    	
	                    	} catch(SQLException ex) {
	                    		ex.printStackTrace();
	                    	}
                    		
                    	} else {
                    		BuyerSearch.GalleryNotSelected();
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

private void NoWorkFound() {
	
	//create new stage for pop up window
	Stage popupwindow=new Stage();
     
	popupwindow.initModality(Modality.APPLICATION_MODAL);
	popupwindow.setTitle("No Work Found");      
	Label label1= new Label("No work found that matches that criteria.");
      
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

public void main(String[] args) {
	launch(args);
}


}
