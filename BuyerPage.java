import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BuyerPage extends Application {
	
	//create gallery object
	public static Gallery currentgallery = new Gallery();
	
	
	private Connection connection = null;
	
	public void start (Stage buyerstage) {
		
		initializeDB();
		
		//Create Border Pane 
		BorderPane BuyerPage = new BorderPane();
		BuyerPage.setStyle("-fx-background-color: #FFFFFF");
				
		//TOP Border
		//Create VBox 
		VBox BuyerPageTop = new VBox();
		BuyerPageTop.setPadding(new Insets(15, 15, 15, 15));

		Font peachSundress = Font.loadFont(getClass().getResourceAsStream("/fonts/peach-sundress.ttf"), 45);
				
		//Create label for art pals
		Label textTop = new Label(landingPage.currentbuyer.getFname() + "'s Galleries \n\n");
		textTop.setFont(peachSundress);
		textTop.setTextFill(Color.BLUE);
				
		//create HBox for buttons
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(10);
				
		//create upload button
		Button btSearch = new Button("Search");
		Button btRefresh = new Button("Refresh");
		Button btCreateNew = new Button("Create New Gallery");
		Button btLogout = new Button("Logout");
				
		//put buttons in hbox
		buttons.getChildren().addAll(btSearch, btCreateNew, btRefresh, btLogout);
		
		//set on action for log out
				btLogout.setOnAction(e -> {
					
					landingPage secondwindow = new landingPage();
					secondwindow.start(buyerstage);
				});
				
		//set on action for search button: open search page
		btSearch.setOnAction(e -> {
			BuyerSearch secondWindow = new BuyerSearch();
	    	secondWindow.start(buyerstage);
		});
		
		//set on action for refresh: update scene
		btRefresh.setOnAction(e -> {
			start(buyerstage);
		});
		
		//set on action for Create New
		btCreateNew.setOnAction(e -> {
			CreateGallery();
		});
				
		//add labels to Vbox
		BuyerPageTop.getChildren().addAll(textTop, buttons);
				
		//set Vbox properties
		BuyerPageTop.setStyle("-fx-background-color: #FFFFFF");
		BuyerPageTop.setAlignment(Pos.CENTER);
		BuyerPageTop.setPrefHeight(100);
		BuyerPageTop.setPrefWidth(800);
				
		//Set Vbox as top
		BuyerPage.setTop(BuyerPageTop);
				
		//CENTER BORDERPANE
		//Create scroll pane
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setPannable(true);
		scrollpane.setPadding(new Insets(15, 15, 15, 15));
		scrollpane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px");
		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
			
		//Create tile pane
		TilePane tile = new TilePane();
		tile.setStyle("-fx-background-color: #FFFFFF");
		tile.setAlignment(Pos.CENTER);
		tile.setPrefWidth(755);
		tile.setPrefHeight(530);
		tile.setPadding(new Insets(15, 15, 15, 15));
		tile.setHgap(10);
				
		//search the gallery table for the buyerid to get all the buyer's galleries
		PreparedStatement psgetgalleries;
		ResultSet rsgetgalleries;
		String buyerid = landingPage.currentbuyer.getUsername();
		String galleryid = null;
		
		String querygetgalleries = "SELECT * FROM Gallery WHERE BUser = ?";
		try {
			psgetgalleries = connection.prepareStatement(querygetgalleries);
			psgetgalleries.setString(1, buyerid);
			rsgetgalleries = psgetgalleries.executeQuery();
			
			//create arraylist 
			ArrayList<String> galleries = new ArrayList<String>();
			
			//while there are values in the column, add them to arraylist
			while(rsgetgalleries.next()) {
				
				//get the text
				galleries.add(rsgetgalleries.getString("GalName"));
				galleryid = rsgetgalleries.getString("GalID");
							
			}
			
			//if if the arraylist is empty, display no galleries created text
			if(galleries.isEmpty()) {
				Label lblnoimages = new Label("No Galleries Created");
				lblnoimages.setFont(Font.font(20));
				tile.getChildren().addAll(lblnoimages);
				tile.setAlignment(Pos.CENTER);
				} 
						
			//if the arraylist is not empty, display in tile as gallery
			else {
				//convert arraylist to array
				String[] galnamesarray = new String[galleries.size()];
				for (int i =0; i < galleries.size(); i++) {
					galnamesarray[i] = galleries.get(i); 
				}
						
				//put array into tile pane on rectangle
				for(final String galleryname:galnamesarray) {				
					//create stack pane to hold rectangle
					StackPane stack = new StackPane();
					stack.setPadding(new Insets(10, 10, 10, 10));
					
					//search gallery table for galleryid
					String galid = "";
					PreparedStatement psgalleryid;
					ResultSet rsgalleryid;
					
					try {
						String querygalleryid = "SELECT * FROM Gallery WHERE GalName = ?";
						psgalleryid = connection.prepareStatement(querygalleryid);
						psgalleryid.setString(1, galleryname);
					
						rsgalleryid = psgalleryid.executeQuery();
					
						while(rsgalleryid.next()) {
						
							//create variable for gallery id
							galid = rsgalleryid.getString("GalID");
						
							//search joint table for artwork id
							String artid = "";
							PreparedStatement psjoint;
							ResultSet rsjoint;
							
							try {
								String queryjoint = "SELECT * FROM ArtGalJoin WHERE GalID = ?";
								psjoint = connection.prepareStatement(queryjoint);
								psjoint.setString(1, galid);
								
								rsjoint = psjoint.executeQuery();
								
								//create arraylist of art ids with particular gallery id
								ArrayList<String> artids = new ArrayList<>();
								
								while(rsjoint.next()) {
									//add values from artwork id column to array list
									artids.add(rsjoint.getString("ArtworkID"));
								}
								
								//if this arraylist is empty, then create rectangle
								if(artids.isEmpty()) {
									//create rectangle and set properties
									Rectangle galrectangle = new Rectangle(150, 150, 150, 150);
									galrectangle.setFill(Color.HONEYDEW);
									galrectangle.setStroke(Color.DARKGRAY);
												
									//create label and set properties
									Label lblgalname = new Label(galleryname);
									lblgalname.setFont(Font.font("Times", FontWeight.BOLD, 13));
									lblgalname.setStyle("-fx-font-color: #FFFFFF");
								
									//set galleryid
									String galleryID = galid;
									
									//put rectangle and name on stack
									stack.getChildren().addAll(galrectangle, lblgalname);
								
											
									//when rectangle is clicked, open gallery page
									galrectangle.setOnMousePressed(e -> {
											

										currentgallery.setGalleryName(galleryname);
										currentgallery.setGalleryID(galleryID);
										
									//open gallery page
									GalleryPage secondwindow = new GalleryPage();
									secondwindow.start(buyerstage);
									
											
									});
								} else {
								
									//Get filepath for artwork 
									String filepath;
									PreparedStatement psfile;
									ResultSet rsfile;
									
									//select a random artid
									Random random = new Random();
									String randartid = artids.get(random.nextInt(artids.size()));
									
									try {
										String queryfile = "SELECT * FROM Artwork WHERE ArtworkID = ?";
										psfile = connection.prepareStatement(queryfile);
										psfile.setString(1, randartid);
										
										rsfile = psfile.executeQuery();
										
										while(rsfile.next()) {
											
											//create image 
											
											filepath = rsfile.getString("filepath");
											
											try {
												Image image = new Image(new FileInputStream(filepath), 150, 0, true, true);
												ImageView imageView = new ImageView(image);
												imageView.setFitWidth(150);
												imageView.setOpacity(.5);
													
												//create label for gallery name
												Label lblgalname = new Label(galleryname);
												lblgalname.setFont(Font.font("Times", FontWeight.BOLD, 13));
												lblgalname.setStyle("-fx-font-color: #FFFFFF");
												
										
												//put rectangle and name on stack
												stack.getChildren().addAll(imageView, lblgalname);
												
												String galleryID = galid;

												
												//when rectangle is clicked, open gallery page
												imageView.setOnMousePressed(e -> {
													
													//set gallery name and id
													currentgallery.setGalleryName(galleryname);
													currentgallery.setGalleryID(galleryID);
													
													
													//open gallery page
													GalleryPage secondwindow = new GalleryPage();
													secondwindow.start(buyerstage);
														
												});
													
											} catch (FileNotFoundException e1) {
												e1.printStackTrace();
											}
										
											
										}
										
									} catch(SQLException ex) {
										ex.printStackTrace();
									}
								}
							} catch(SQLException ex) {
								ex.printStackTrace();
							}
							
						}
						
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
					
					//put stack in tile
					tile.getChildren().addAll(stack);
					tile.setAlignment(Pos.TOP_CENTER);
			
			}
					
	}
		} catch (SQLException e) {
		e.printStackTrace();
	}
				
	scrollpane.setContent(tile);
				
	BuyerPage.setCenter(scrollpane);
				
	Scene scene = new Scene(BuyerPage, 800, 600);
	buyerstage.setScene(scene);
	buyerstage.show();
		
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
	
	private void CreateGallery() {
		
		//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Create Gallery");      
    	
    	//create hbox for label and textfield
    	HBox creategal = new HBox();
    	Label label1= new Label("Gallery Name: ");
    	TextField tfGalName = new TextField();
    	creategal.getChildren().addAll(label1, tfGalName);
    	
 
    	
    	
    	//create hbox for buttons
    	HBox buttons = new HBox();
    	buttons.setAlignment(Pos.CENTER);
    	buttons.setSpacing(15);
    	
    	//close pop up window    
    	Button btClose= new Button("Close");     
    	btClose.setOnAction(e -> popupwindow.close());
    	
    	//create gallery button
    	Button btCreate = new Button("Create Gallery");
    	btCreate.setOnAction(e -> {
    		
    		//get create all info for galleryname 
    		String galleryname = tfGalName.getText();
        	String user = landingPage.currentbuyer.getUsername();
        	String galleryid = galleryname + user;
    		
    		//statements to search DB
    		PreparedStatement pscheckgalname;
    		ResultSet rscheckgalname;
    		
    		//statement to insert into DB
    		PreparedStatement psinsertgallery;
    		
    		//check if gallery name exists for user
    		String querycheckgalname = "SELECT * FROM Gallery WHERE GalName = ? AND BUser = ?";
    		
    		try {
    		pscheckgalname = connection.prepareStatement(querycheckgalname);
    		pscheckgalname.setString(1, galleryname);
    		pscheckgalname.setString(2, user);
    		
    		rscheckgalname = pscheckgalname.executeQuery();
    		
    		//if it exists, display pop up
    		if(rscheckgalname.next()) {
    			GalleryExists();
    		} else {
    			
    			//if it doesn't exist, then insert it into DB and close
    			String queryinsertgallery = "INSERT INTO Gallery VALUES (?, ?, ?)";
    			
    			try {
    				psinsertgallery = connection.prepareStatement(queryinsertgallery);
    				psinsertgallery.setString(1, galleryname);
    				psinsertgallery.setString(2, user);
    				psinsertgallery.setString(3, galleryid);
    				
    				psinsertgallery.executeUpdate();
    				
    				System.out.println(galleryname + " entered into database");
    				
    			} catch(SQLException ex) {
    				ex.printStackTrace();
    			}
    			
    			popupwindow.close();
    			
    		}
    		
    		} catch(SQLException ex) {
    			ex.printStackTrace();
    		}
    		
    	});
    	
    	//set buttons into hbox
    	buttons.getChildren().addAll(btClose, btCreate);
	
    	//layout of popup window
    	VBox layout= new VBox(10);      
    	layout.getChildren().addAll(creategal, buttons);
    	layout.setAlignment(Pos.CENTER);
	      
    	Scene scene= new Scene(layout, 300, 100);	      
    	popupwindow.setScene(scene);	      
    	popupwindow.showAndWait();
    	
		
	}
	
	public static void GalleryExists() {
		
		//create new stage for pop up window
    	Stage popupwindow=new Stage();
	     
    	popupwindow.initModality(Modality.APPLICATION_MODAL);
    	popupwindow.setTitle("Existing Gallery");      
    	Label label1= new Label("Gallery name already exists for this user. \n Please enter new name. ");
	      
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
	
	//DELETE WHEN DONE
	public static void main(String[] args) { 
		   launch(args);
		  }



}