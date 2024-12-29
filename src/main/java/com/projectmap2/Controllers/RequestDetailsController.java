package com.projectmap2.Controllers;

import com.projectmap2.DTOs.FriendDTO;
import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Utilizator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDateTime;

public class RequestDetailsController {
    private Utilizator utilizator;

    @FXML
    Label usernameLabel;
    @FXML
    Label firstnameLabel;
    @FXML
    Label lastnameLabel;
    @FXML
    Label dateLabel;
    @FXML
    Label statusLabel;

    public void setInfo(Utilizator utilizator, LocalDateTime date , FriendshipStatus status) {
        usernameLabel.setText(utilizator.getUserName());
        firstnameLabel.setText(utilizator.getFirstName());
        lastnameLabel.setText(utilizator.getLastName());
        dateLabel.setText(date.format(FriendDTO.formatter));

        if(status == FriendshipStatus.SENT)
            statusLabel.setText("Cererea asteapta raspunsul utilizatorului! (SENT)");
        if(status == FriendshipStatus.PENDING)
            statusLabel.setText("Cererea asteapta raspunsul tau ! (PENDING)");
    }
}
