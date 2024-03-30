package com.example.banterbox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    private Stage stage;
    private Scene scene;
    @FXML
    private Label appName, errorText;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton, registerButton;

    public void onClickRegisterButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("BanterBox");
        stage.setScene(scene);
        stage.show();
    }

    public void onClickLoginButton(ActionEvent event) {
        try {
            if (passwordField.getText().isEmpty() || usernameField.getText().isEmpty()) {
                errorText.setStyle("-fx-text-fill: red;");
                errorText.setText("Please fill in all fields");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/banterbox",
                        "root", "");

                String sql = "SELECT * FROM users WHERE username = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, usernameField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (StringHasher.verifyString(passwordField.getText(), storedPassword)) {
                        errorText.setStyle("-fx-text-fill: yellow;");
                        errorText.setText("Login success!");
                    } else {
                        errorText.setStyle("-fx-text-fill: red;");
                        errorText.setText("Invalid username or password");
                    }
                } else {
                    errorText.setStyle("-fx-text-fill: red;");
                    errorText.setText("Invalid username or password");
                }

                stmt.close();
                connection.close();
            }
        }catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}