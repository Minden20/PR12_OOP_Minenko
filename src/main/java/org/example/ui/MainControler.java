package org.example.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.entity.User;
import org.example.service.UserService;

public class MainControler {

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Button addbuttonid;

  @FXML
  private Button getbuttonid;
  
  @FXML
  private TextField searchField;
  
  @FXML
  private Button searchButton;
  
  @FXML
  private ProgressBar progressBar;
  
  @FXML
  private Label statusLabel;

  @FXML
  private TableView<User> tableviewid;

  @FXML private TableColumn<User, Integer> idCol;
  @FXML private TableColumn<User, String> nameCol;
  @FXML private TableColumn<User, String> emailCol;
  @FXML private TableColumn<User, String> imageCol;

  private final UserService userService = new UserService();
  private final ObservableList<User> users = FXCollections.observableArrayList();

  private void updateStatus(String message, boolean loading) {
      statusLabel.setText(message);
      progressBar.setVisible(loading);
  }

  @FXML
  void onAdd(ActionEvent event) {
      Task<User> task = new Task<>() {
          @Override
          protected User call() throws Exception {
              String randomName = "User" + (int)(Math.random() * 1000);
              String randomEmail = "user" + (int)(Math.random() * 1000) + "@example.com";
              String randomImage = "https://picsum.photos/50?random=" + (int)(Math.random() * 1000);
              return userService.registerUser(randomName, randomEmail, "password", randomImage);
          }
      };
      
      task.setOnRunning(e -> updateStatus("Adding user...", true));
      
      task.setOnSucceeded(e -> {
          updateStatus("User added!", false);
          if (task.getValue() != null) {
              loadData(); 
          }
      });
      
      task.setOnFailed(e -> {
          updateStatus("Error adding user: " + task.getException().getMessage(), false);
          task.getException().printStackTrace();
      });
      
      new Thread(task).start();
  }

  @FXML
  void onEdit(ActionEvent event) {
      loadData();
  }
  
  @FXML
  void onSearch(ActionEvent event) {
      String query = searchField.getText();
      searchData(query);
  }

  @FXML
  void initialize() {
    assert addbuttonid != null : "fx:id=\"addbuttonid\" was not injected: check your FXML file 'Main.fxml'.";
    assert getbuttonid != null : "fx:id=\"getbuttonid\" was not injected: check your FXML file 'Main.fxml'.";
    
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    imageCol.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));
    
    imageCol.setCellFactory(column -> new TableCell<User, String>() {
        private final ImageView imageView = new ImageView();
        
        @Override
        protected void updateItem(String match, boolean empty) {
            super.updateItem(match, empty);
            if (empty || match == null) {
                setGraphic(null);
            } else {
                Image image = new Image(match, 50, 50, true, true, true); 
                imageView.setImage(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                setGraphic(imageView);
            }
        }
    });

    tableviewid.setItems(users);
    
    loadData();
  }
  
  private void loadData() {
      progressBar.progressProperty().unbind();
      
      Task<List<User>> task = new Task<>() {
          @Override
          protected List<User> call() throws Exception {
              updateProgress(0.1, 1.0);
              Thread.sleep(300); 
              updateProgress(0.5, 1.0);
              List<User> list = userService.getAllUsers();
              updateProgress(1.0, 1.0);
              return list;
          }
      };

      progressBar.progressProperty().bind(task.progressProperty());
      
      task.setOnRunning(e -> updateStatus("Loading data...", true));
      
      task.setOnSucceeded(e -> {
          progressBar.progressProperty().unbind(); // Clean up binding
          users.setAll(task.getValue());
          updateStatus("Data loaded.", false);
      });
      
      task.setOnFailed(e -> {
          progressBar.progressProperty().unbind(); // Clean up binding
          updateStatus("Error loading data.", false);
          task.getException().printStackTrace();
      });
      
      new Thread(task).start();
  }
  
  private void searchData(String query) {
      progressBar.progressProperty().unbind(); // Ensure no conflict
      progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
      
      Task<List<User>> task = new Task<>() {
          @Override
          protected List<User> call() throws Exception {
              return userService.searchUsers(query);
          }
      };
      
      task.setOnRunning(e -> updateStatus("Searching...", true));
      
      task.setOnSucceeded(e -> {
          users.setAll(task.getValue());
          updateStatus("Found " + users.size() + " results.", false);
          progressBar.setProgress(0); // Reset
      });
      
      task.setOnFailed(e -> {
          updateStatus("Search failed.", false);
          progressBar.setProgress(0);
          task.getException().printStackTrace();
      });
      
      new Thread(task).start();
  }
}
