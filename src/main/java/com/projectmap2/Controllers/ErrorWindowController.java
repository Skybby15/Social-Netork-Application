package com.projectmap2.Controllers;

import com.projectmap2.GraphicApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ErrorWindowController {
    Stage myStage;
    @FXML
    TextField message;

    public void setMessage(String message) {
        this.message.setText(message);
    }
    public void setStage(Stage stage) {
        myStage = stage;
        myStage.setResizable(false);
        myStage.setAlwaysOnTop(true);
    }

    public void handleExitButton(ActionEvent event) {
        myStage.close();
    }
}
