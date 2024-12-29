package com.projectmap2.Controllers;

import com.projectmap2.Domain.Message;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Service.Service;
import com.projectmap2.Utils.Events.Event;
import com.projectmap2.Utils.MessageLabel;
import com.projectmap2.Utils.ObserverClasses.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class MessageBoxController implements Observer<Event> {
    Stage myStage;
    Service service;
    Utilizator loggedUser;
    Utilizator receiver;
    MessageLabel selectedMessage;
    int maxNumberOfCharacters = 40;

    @FXML
    Label receiverLabel;
    @FXML
    AnchorPane anchorLayout;
    @FXML
    VBox VBoxLayout;
    @FXML
    TextArea textArea;
    @FXML
    TextArea replyArea;
    @FXML
    Button sendButton;
    @FXML
    Button cancelReplyButton;



    @Override
    public void update(Event event)
    {
        initLayout();
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    public void setLoggedUser(Utilizator loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setReceiver(Utilizator receiver) {
        this.receiver = receiver;
        receiverLabel.setText("Conversation with " + receiver.getUserName());
    }

    public void setStage(Stage stage) {
        myStage = stage;
    }


    @FXML
    public void initialize() {
        replyArea.setEditable(false);
        replyArea.setVisible(false);
        cancelReplyButton.setDisable(true);
        cancelReplyButton.setVisible(false);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty())
                sendButton.setDisable(true);
            else
                sendButton.setDisable(false);

            //some constraints for maximum number of characters on a line
            maxNumberOfCharacters += oldValue.length() - newValue.length();

            if(newValue.endsWith("\n"))
                maxNumberOfCharacters = 40;

            if(maxNumberOfCharacters <= 0)
                textArea.setText(oldValue);

        });

        VBoxLayout.prefHeightProperty().bind(anchorLayout.prefHeightProperty());

    }

    public void initLayout()
    {
        double prefHeight = 0;
        VBoxLayout.getChildren().clear();
        for (Message message : service.getUserMessages(loggedUser.getId(), receiver.getId())) {
            MessageLabel messageLabel;
            if(message.getSender().equals(loggedUser.getId()))
                messageLabel = createMessageLabel(Pos.CENTER_RIGHT, message);
            else
                messageLabel = createMessageLabel(Pos.CENTER_LEFT, message);

            prefHeight += messageLabel.getPrefHeight();
            VBoxLayout.getChildren().add(messageLabel);
        }

        anchorLayout.setPrefHeight(prefHeight);

    }

    public void handleSendButton(ActionEvent event)
    {

        String text = textArea.getText();
        Message message;
        if(replyArea.getText().isEmpty())
            message = new Message(loggedUser.getId(), receiver.getId(), text, LocalDateTime.now());
        else
            message = new Message(loggedUser.getId(),receiver.getId(),text,selectedMessage.getMessage(), LocalDateTime.now());

        service.saveMessage(message);
        textArea.clear();
        sendButton.setDisable(true);

        handleCancelButton(event);
    }

    private MessageLabel createMessageLabel(Pos pos,Message message)
    {
        MessageLabel label = new MessageLabel(message);
        label.setAlignment(pos);
        label.prefWidthProperty().bind(VBoxLayout.prefWidthProperty());

        //make it so u can reply
        label.setOnMouseClicked(item -> {
            replyArea.setText(label.getMessage().getText());
            replyArea.setVisible(true);
            cancelReplyButton.setDisable(false);
            cancelReplyButton.setVisible(true);
            selectedMessage = label;
        });
        //17.6
        double numberOfLines = 1D;

        for(char c : label.getText().toCharArray())
            if(c == '\n')
                numberOfLines++;

        label.setPrefHeight(numberOfLines * 18D);

        return label;
    }


    public void handleCancelButton(ActionEvent event)
    {
        replyArea.clear();
        selectedMessage = null;
        replyArea.setVisible(false);
        cancelReplyButton.setVisible(false);
        cancelReplyButton.setDisable(true);
    }
}
