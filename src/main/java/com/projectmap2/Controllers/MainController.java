package com.projectmap2.Controllers;

import com.projectmap2.DTOs.FriendDTO;
import com.projectmap2.Domain.*;
import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Service.Service;
import com.projectmap2.Utils.Events.Event;
import com.projectmap2.Utils.Events.PrietenieEvent;
import com.projectmap2.Utils.ObserverClasses.Observer;
import com.projectmap2.Utils.Paging.Page;
import com.projectmap2.Utils.Paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainController implements Observer<Event> {
    public static Utilizator loggedUser;
    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<FriendDTO> modelRequests = FXCollections.observableArrayList();

    private Stage myStage;
    private Service service;

    private int maxPageNumber;
    private int currentPageNumber = 0;
    private int pageSize = 17;

    @FXML
    Label welcomeLabel;
    @FXML
    Text requestInfoText;

    @FXML
    private TableView<FriendDTO> tableViewFriend;
    @FXML
    private TableColumn<FriendDTO,String> usernameFriendCol;

    @FXML
    private TableView<FriendDTO> tableViewRequest;
    @FXML
    private TableColumn<FriendDTO,String> usernameRequestCol;


    @FXML
    Button deleteButton;
    @FXML
    Button acceptButton;
    @FXML
    Button rejectButton;
    @FXML
    Button messageButton;

    @FXML
    TextField receiversField;
    @FXML
    TextArea multipleMessageField;
    @FXML
    Button sendButton;
    @FXML
    Button clearButton;

    @FXML
    Label pageLabel;

    @Override
    public void update(Event event) {
        if (event.getClass() == PrietenieEvent.class) {
            initModelPrietenie();
            initModelRequests();
        }
        checkForPendingRequests();
    }

    public void setStage(Stage stage) {
        myStage = stage;
    }

    public void setService(Service service) {
        this.service = service;
        maxPageNumber = service.findAllFriendsDTO(loggedUser.getId()).size() / pageSize - 1;
        if (maxPageNumber == -1)
            maxPageNumber = 0;
        service.addObserver(this);

        initModelPrietenie();
        initModelRequests();
        checkForPendingRequests();
    }

    private void initModelPrietenie() {
        Pageable pageable = new Pageable(currentPageNumber, pageSize);
        List<FriendDTO> friends = service.findAllFriendsDTOPage(pageable,loggedUser.getId());
        modelFriends.setAll(friends);

        pageLabel.setText("Page " + currentPageNumber + " out of " + maxPageNumber);
    }

    private void initModelRequests()
    {
        List<FriendDTO> requests = service.findAllRequestsDTO(loggedUser.getId());
        modelRequests.setAll(requests);
    }

    @FXML
    public void initialize()
    {

        tableViewFriend.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        receiversField.setEditable(false);
        requestInfoText.setVisible(false);
        deleteButton.setDisable(true);
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
        messageButton.setDisable(true);
        welcomeLabel.setText("Welcome, " + loggedUser.getUserName());

        usernameFriendCol.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("friendUsername"));
        tableViewFriend.setItems(modelFriends);

        usernameRequestCol.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("friendUsername"));
        tableViewRequest.setItems(modelRequests);

        tableViewFriend.getSelectionModel().selectedItemProperty().addListener(selection -> {
            deleteButton.setDisable(false);
            messageButton.setDisable(false);
        });

        tableViewRequest.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
            {
                rejectButton.setDisable(true);
                acceptButton.setDisable(true);
                return;
            }
            if (newValue.getStatus() == FriendshipStatus.PENDING)
            {
                rejectButton.setDisable(false);
                acceptButton.setDisable(false);
            } else if (newValue.getStatus() == FriendshipStatus.SENT) {
                rejectButton.setDisable(true);
                acceptButton.setDisable(true);
            }
        });
    }

    public void handleAddFriendButton(ActionEvent event) throws IOException
    {
        Stage stage = WindowFactory.createAddFriendWindow(service);
        stage.show();
    }

    public void handleShowUserDetails(MouseEvent event) throws IOException
    {
        if(tableViewFriend.getSelectionModel().getSelectedItem() == null || event.getClickCount() != 2)
            return;

        Utilizator user = tableViewFriend.getSelectionModel().getSelectedItem().getFriend();
        LocalDateTime date = tableViewFriend.getSelectionModel().getSelectedItem().getDate();
        if(Objects.equals(user.getId(), loggedUser.getId()))
            user = tableViewFriend.getSelectionModel().getSelectedItem().getFriend();
        Stage stage = WindowFactory.createUserDetailsWindow(user,date);
        stage.show();
    }

    public void handleShowRequestDetails(MouseEvent event) throws IOException
    {
        if(tableViewRequest.getSelectionModel().getSelectedItem() == null || event.getClickCount() != 2)
            return;

        Utilizator user = tableViewRequest.getSelectionModel().getSelectedItem().getFriend();
        LocalDateTime date = tableViewRequest.getSelectionModel().getSelectedItem().getDate();
        FriendshipStatus status = tableViewRequest.getSelectionModel().getSelectedItem().getStatus();
        if(Objects.equals(user.getId(), loggedUser.getId()))
            user = tableViewRequest.getSelectionModel().getSelectedItem().getFriend();
        Stage stage = WindowFactory.createRequestDetailsWindow(user,date,status);
        stage.show();
    }

    public void handleDeleteButton(ActionEvent event) throws IOException
    {
        FriendDTO friend = tableViewFriend.getSelectionModel().getSelectedItem();

        try {
            Long loggedId = loggedUser.getId();
            Long friendId = friend.getFriend().getId();
            service.StergePrietenie(new Tuple<>(loggedId,friendId));
        } catch (ValidationException error) {
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }

    }

    public void handleAcceptButton(ActionEvent event) throws IOException
    {
        FriendDTO request = tableViewRequest.getSelectionModel().getSelectedItem();
        try{
            Long loggedId = loggedUser.getId();
            Long requestId = request.getFriend().getId();
            service.actualizeazaPrietenie(loggedId,requestId,FriendshipStatus.ACCEPTED);

        } catch (ValidationException error) {
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }
    }

    public void handleRejectButton(ActionEvent event) throws IOException
    {
        FriendDTO request = tableViewRequest.getSelectionModel().getSelectedItem();
        try{
            Long loggedId = loggedUser.getId();
            Long requestId = request.getFriend().getId();
            service.actualizeazaPrietenie(loggedId,requestId,FriendshipStatus.REJECTED);

        } catch (ValidationException error) {
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }
    }

    public void handleChangeUserButton(ActionEvent event) throws IOException
    {
        myStage.close();
        WindowFactory.createLoginWindow(service).show();
    }

    public void handleMessageButton(ActionEvent event) throws IOException
    {
        var items = tableViewFriend.getSelectionModel().getSelectedItems();
        if(items.size() == 1) {
            Utilizator receiver = tableViewFriend.getSelectionModel().getSelectedItem().getFriend();
            WindowFactory.createMessageWindow(service, receiver).show();
        } else if (items.size() > 1) {
            receiversField.clear();
            items.forEach(friend ->{
                receiversField.setText(receiversField.getText() + friend.getFriendUsername() + " ");
            });
        }
    }

    private void checkForPendingRequests() {
        requestInfoText.setVisible(false);
        for (FriendDTO friend : modelRequests) {
            if (friend.getStatus() == FriendshipStatus.PENDING) {
                requestInfoText.setVisible(true);
                return;
            }
        }
    }

    public void handleClearButton(ActionEvent event)
    {
        receiversField.clear();
        multipleMessageField.clear();
    }

    public void handleSendButton(ActionEvent event) throws IOException
    {
        try{
            if(multipleMessageField.getText().isEmpty())
                throw new ValidationException("Message cannot be empty!");
            if(receiversField.getText().isEmpty())
                throw new ValidationException("You cannot send a message to nobody!");

            List<String> receivers = Arrays.stream(receiversField.getText().split(" ")).toList();
            List<Long> userReceivers = new ArrayList<>();
            receivers.forEach(rec->{
                Utilizator user = service.findUserByUsername(rec);
                userReceivers.add(user.getId());
            });

            Message multipleMessage = new Message(loggedUser.getId(),userReceivers,multipleMessageField.getText(), LocalDateTime.now());
            //salvare in baza de date
            service.saveMessage(multipleMessage);

            receiversField.clear();
            multipleMessageField.clear();

        }catch (ValidationException error){
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }
    }

    public void handlePreviousPageButton(ActionEvent event)
    {
        if(currentPageNumber == 0)
            return;
        else
            currentPageNumber--;

        initModelPrietenie();
    }

    public void handleNextPageButton(ActionEvent event)
    {
        if(currentPageNumber == maxPageNumber)
            return;
        else
            currentPageNumber++;

        initModelPrietenie();
    }

}
