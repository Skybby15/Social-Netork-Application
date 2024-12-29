package com.projectmap2.Controllers;

import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    Service service;
    Stage myStage;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;

    @FXML
    Label errorLabel;

    public LoginController() {}

    public void setService(Service service) {
        this.service = service;
    }
    public void setStage(Stage stage) {
        this.myStage = stage;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    public void handleLoginButton() throws Exception
    {
        try
        {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if(username.isEmpty() || password.isEmpty())
                throw new ValidationException("Username and Password are required!");
            else {
                Utilizator loggedUser = service.findUserByUsername(username.strip());
                if(loggedUser == null)
                    throw new ValidationException("Wrong username or password!");
                if(!loggedUser.getParola().equals(password.strip()))
                    throw new ValidationException("Wrong username or password!");

              MainController.loggedUser = loggedUser;
              WindowFactory.createMainWindow(service).show();
              myStage.close();
            }

        }catch (ValidationException error)
        {
            errorLabel.setVisible(true);
            errorLabel.setText(error.getMessage());
        }
    }

    public void handleSignUpButton() throws IOException {
        WindowFactory.createSignUpWindow(service).show();
    }
}
