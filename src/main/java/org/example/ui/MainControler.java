package org.example.ui;

import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.util.DatabaseInit;

public class MainControler {

    @FXML private Button addbuttonid;
    @FXML private Button addManualbuttonid;
    @FXML private Button getbuttonid;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private TableView<User> tableviewid;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> imageCol;

    private final UserService userService;
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public MainControler() {
        DatabaseInit.Init();
        userService = new UserService();
    }

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        imageCol.setCellFactory(column -> new TableCell<User, String>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(url, 50, 50, true, true, true));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });

        tableviewid.setItems(users);
        loadData();
    }

    @FXML
    void onAdd(ActionEvent event) {
        Task<User> task = new Task<>() {
            @Override
            protected User call() {
                String name = "User" + (int)(Math.random() * 1000);
                String email = "user" + (int)(Math.random() * 1000) + "@example.com";
                String image = "https://picsum.photos/50?random=" + (int)(Math.random() * 1000);
                return userService.registerUser(name, email, "password", image);
            }
        };
        task.setOnRunning(e -> updateStatus("Adding user...", true));
        task.setOnSucceeded(e -> { updateStatus("User added!", false); loadData(); });
        task.setOnFailed(e -> updateStatus("Error: " + task.getException().getMessage(), false));
        new Thread(task).start();
    }

    @FXML
    void onAddManual(ActionEvent event) {
        AddUserDialog dialog = new AddUserDialog();
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            userService.registerUser(
                    user.getName(), user.getEmail(),
                    user.getHashedPassword(), user.getImageUrl()
            );
            updateStatus("Користувача додано!", false);
            loadData();
        });
    }

    @FXML
    void onEdit(ActionEvent event) { loadData(); }

    @FXML
    void onSearch(ActionEvent event) { loadData(); }

    private void loadData() {
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() { return userService.getAllUsers(); }
        };
        task.setOnRunning(e -> updateStatus("Loading...", true));
        task.setOnSucceeded(e -> { users.setAll(task.getValue()); updateStatus("Loaded.", false); });
        task.setOnFailed(e -> updateStatus("Error loading.", false));
        new Thread(task).start();
    }

    private void updateStatus(String msg, boolean loading) {
        statusLabel.setText(msg);
        progressBar.setVisible(loading);
    }
}
