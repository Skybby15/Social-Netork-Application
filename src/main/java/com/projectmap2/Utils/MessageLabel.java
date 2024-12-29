package com.projectmap2.Utils;

import com.projectmap2.Domain.Message;
import javafx.scene.control.Label;

public class MessageLabel extends Label {
    private final Message message;

    public MessageLabel(Message message) {
        String labelText = "";
        if(message.getReplyMessage() != null)
        {
            labelText = "Replying to:\n" + message.getReplyMessage().getText() + "\n\n";
        }
        labelText = labelText + message.getText();
        super.setText(labelText);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
