package com.example.banterbox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController {
    private Stage stage;
    private Scene scene;
    @FXML
    private Label appName, errorText;
    @FXML
    private TextField usernameField, emailField;
    @FXML
    private PasswordField passwordField, rpasswordField;
    @FXML
    private Button registerButton, backButton;

    public void onClickBackButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("BanterBox");
        stage.setScene(scene);
        stage.show();
    }

    public void onClickRegisterButton(ActionEvent event) {
        try {
            if (passwordField.getText().isEmpty() || rpasswordField.getText().isEmpty() ||
                    usernameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Please fill in all fields");
            } else if (isUsernameUnique(usernameField.getText())) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Username already exists");
            } else if (passwordField.getText().length() < 8) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Password should contain at least 8 characters");
            } else if (!passwordField.getText().equals(rpasswordField.getText())) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Passwords do not match");
            } else if (!isValidEmail(emailField.getText())) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Invalid email format");
            } else if (isEmailUnique(emailField.getText())) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Email is already associated with another account");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/banterbox",
                        "root", "");

                String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                String hashedPassword = StringHasher.createHash(passwordField.getText());
                stmt.setString(1, usernameField.getText());
                stmt.setString(2, hashedPassword);
                stmt.setString(3, emailField.getText());

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    errorText.setStyle("-fx-text-fill: yellow;");
                    errorText.setText("User registered successfully!");
                } else {
                    errorText.setStyle("-fx-text-fill: red;");
                    errorText.setText("Failed to register user");
                }

                stmt.close();
                connection.close();
            }
        }catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean isUsernameUnique(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banterbox",
                    "root", "");

            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    stmt.close();
                    connection.close();
                    return true;
                }
            }
            stmt.close();
            connection.close();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmailUnique(String email) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banterbox",
                    "root", "");

            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    stmt.close();
                    connection.close();
                    return true;
                }
            }
            stmt.close();
            connection.close();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
