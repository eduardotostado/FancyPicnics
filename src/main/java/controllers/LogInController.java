package controllers;

import entities.Controller;
import entities.ControllerType;

import models.UserAccount;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

public class LogInController extends Controller implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        warningAlert = new Alert(Alert.AlertType.WARNING);
        warningAlert.setTitle("Warning");
    }

    public void enterButtonPressed(ActionEvent event) throws IOException {
        if (usernameField.getText().isEmpty()) {
            warningAlert.setHeaderText("Empty Username");
            warningAlert.setContentText("Please enter a username");
            warningAlert.showAndWait();
        } else if (passwordField.getText().isEmpty()) {
            warningAlert.setHeaderText("Empty Password");
            warningAlert.setContentText("Please enter a password");
            warningAlert.showAndWait();
        } else {
            String username = usernameField.getText();
            String password = passwordField.getText();

            UserAccount user = new UserAccount();
            user = user.findByUsername(username);
            if(user != null){
                if(user.getEmployee().getActive()){
                    if (UserAccount.checkPassword(password, user.getPassword())) {
                        SESSION_USER = user;
                        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAIN_MENU);
                    } else {
                        passwordField.setText("");
                        warningAlert.setHeaderText("Invalid Password ");
                        warningAlert.setContentText("Please enter a valid Password");
                        warningAlert.showAndWait();
                    }
                } else {
                    passwordField.setText("");
                    warningAlert.setHeaderText("Inactive User ");
                    warningAlert.setContentText("This employee has been deactivated.");
                    warningAlert.showAndWait();
                }
            } else {
                passwordField.setText("");
                warningAlert.setHeaderText("Invalid username ");
                warningAlert.setContentText("Please enter a valid username");
                warningAlert.showAndWait();
            }
        }
    }
}