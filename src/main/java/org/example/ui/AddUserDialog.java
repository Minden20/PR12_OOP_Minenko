package org.example.ui;

import java.util.Set;
import java.util.stream.Collectors;

import org.example.entity.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Діалог для додавання нового користувача з валідацією.
 */
public class AddUserDialog extends Dialog<User> {

    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField passwordField = new TextField();
    private final TextField imageUrlField = new TextField();
    private final Label errorLabel = new Label();

    public AddUserDialog() {
        setTitle("Add New User");
        setHeaderText("Enter the data for the new user");

        // Кнопки
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Форма
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        nameField.setPromptText("Name");
        emailField.setPromptText("email@example.com");
        passwordField.setPromptText("Minimum 4 characters");
        imageUrlField.setPromptText("https://...");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Image URL:"), 0, 3);
        grid.add(imageUrlField, 1, 3);

        // Мітка помилок валідації
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(300);

        VBox content = new VBox(10, grid, errorLabel);
        getDialogPane().setContent(content);

        // Перехоплюємо натискання кнопки "Додати" для валідації
        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            User user = buildUser();
            String errors = validateUser(user);
            if (!errors.isEmpty()) {
                errorLabel.setText(errors);
                event.consume(); // Не закриваємо діалог
            }
        });

        // Конвертер результату
        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return buildUser();
            }
            return null;
        });
    }

    private User buildUser() {
        User user = new User();
        user.setName(nameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setHashedPassword(passwordField.getText().trim());
        user.setImageUrl(imageUrlField.getText().trim().isEmpty() ? null : imageUrlField.getText().trim());
        return user;
    }

    private String validateUser(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        factory.close();

        if (violations.isEmpty()) {
            return "";
        }

        return violations.stream()
                .map(v -> "• " + v.getMessage())
                .collect(Collectors.joining("\n"));
    }
}
