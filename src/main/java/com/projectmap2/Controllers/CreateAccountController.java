package com.projectmap2.Controllers;

import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class CreateAccountController {
    private Service service;
    private Stage myStage;

    @FXML
    Label errorLabel;
    @FXML
    TextField usernameField;
    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField passwordField;
    @FXML
    TextField retypeField;

    public void setService(Service service) {
        this.service = service;
    }
    public void setStage(Stage stage) {
        this.myStage = stage;
    }

    @FXML
    public void initialize(){
        errorLabel.setVisible(false);
        retypeField.setDisable(true);

        passwordField.textProperty().addListener(text->{
            if(passwordField.getText().isEmpty()){
                retypeField.setText("");
                retypeField.setDisable(true);
            }else
                retypeField.setDisable(false);
        });

        usernameField.textProperty().addListener(text->checkUsername());
        passwordField.textProperty().addListener(text->checkPassword());
        retypeField.textProperty().addListener(text->checkPassword());
    }

    public void checkUsername()
    {
        if(service.findUserByUsername(usernameField.getText())!=null)
        {
            errorLabel.setText("Username already exists!");
            errorLabel.setVisible(true);
        }else
        {
            errorLabel.setVisible(false);
        }
    }

    public void checkPassword()
    {
        if(!Objects.equals(passwordField.getText(), retypeField.getText()))
        {
            errorLabel.setText("Passwords do not match!");
            errorLabel.setVisible(true);
        }else
        {
            errorLabel.setText("");
        }

    }

    public void onCreateAccount(ActionEvent actionEvent) throws Exception
    {
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();
        String retype = retypeField.getText();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || retype.isEmpty())
        {
            errorLabel.setText("Please fill all fields!");
            errorLabel.setVisible(true);
            return;
        }

        if (password.equals(retype)) {
            try {
                service.AdaugaUser(firstName, lastName, username, password);
                myStage.close();
            } catch (ValidationException e) {
                WindowFactory.createErrorWindow(e.getMessage()).show();
            }
        }
    }

}
