package com.projectmap2.Controllers;

import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddFriendController {
    private Utilizator selectedUser;
    private Stage myStage;
    private Service service;
    @FXML
    private TextField usernameField;

    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(Utilizator User) {
        this.selectedUser = User;
    }

    public void setStage(Stage stage) {
        myStage = stage;
    }

    public void handleAddFriendButton(ActionEvent event) throws IOException
    {
        String username = usernameField.getText();
        try {
            service.adaugaPrietenieId_Username(selectedUser.getId(), username);
        }catch (ValidationException exception)
        {
            Stage error = WindowFactory.createErrorWindow(exception.getMessage());
            error.show();
        }
        myStage.close();

    }

    public void handleCancelButton(ActionEvent event)
    {
        myStage.close();
    }

}
