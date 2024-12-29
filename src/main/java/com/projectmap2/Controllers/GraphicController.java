package com.projectmap2.Controllers;

import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Prietenie;
import com.projectmap2.Domain.Tuple;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Utils.Events.Event;
import com.projectmap2.Utils.Events.PrietenieEvent;
import com.projectmap2.Utils.Events.UtilizatorEvent;
import com.projectmap2.Utils.ObserverClasses.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.projectmap2.Service.Service;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GraphicController implements Observer<Event> {
    private Service service;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Prietenie> modelFriendships = FXCollections.observableArrayList();
    public static Long searchedId = -1L;

    @FXML
    private TableView<Utilizator> tableViewUser;
    @FXML
    private TableColumn<Utilizator,String> firstNameUserCol;
    @FXML
    private TableColumn<Utilizator,String> lastNameUserCol;

    @FXML
    private TableView<Prietenie> tableViewFriend;
    @FXML
    private TableColumn<Prietenie,String> firstNameFriendCol;
    @FXML
    private TableColumn<Prietenie,String> lastNameFriendCol;
    @FXML
    private TableColumn<Prietenie,String> statusFriendCol;
    @FXML
    private TableColumn<Prietenie,String> friendShipDateCol;

    @FXML
    private Button AddFriendButton;
    @FXML
    private Button RemoveFriendButton;
    @FXML
    private Button AcceptFriendButton;
    @FXML
    private Button RejectFriendButton;

    //observer implementation


    @Override
    public void update(Event event) {
        if (event.getClass() == UtilizatorEvent.class) {
            initModelUtilizator();
        }
        if (event.getClass() == PrietenieEvent.class) {
            initModelPrietenie(searchedId);
        }
    }

    //observer implementation end

    private void initModelUtilizator() {
        Iterable<Utilizator> messages = service.GetUseri();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        modelUsers.setAll(users);
    }

    private void initModelPrietenie(Long searchedId) {
        GraphicController.searchedId = searchedId;
        //List<Prietenie> friends = service.findAllFriends(searchedId);
        //modelFriendships.setAll(friends);
    }

    public void setService(Service newService)
    {
        service = newService;
        service.addObserver(this);
        initModelUtilizator();
        initModelPrietenie(-1L);
    }

    @FXML
    public void initialize() {
        firstNameUserCol.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        lastNameUserCol.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableViewUser.setItems(modelUsers);

        searchedId = -1L;
        firstNameFriendCol.setCellValueFactory(new PropertyValueFactory<Prietenie, String>("friendFirstName"));
        lastNameFriendCol.setCellValueFactory(new PropertyValueFactory<Prietenie, String>("friendLastName"));
        statusFriendCol.setCellValueFactory(new PropertyValueFactory<Prietenie, String>("status"));
        friendShipDateCol.setCellValueFactory(new PropertyValueFactory<Prietenie, String>("date"));
        tableViewFriend.setItems(modelFriendships);

        tableViewUser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            initModelPrietenie(newValue.getId());
            AddFriendButton.setDisable(false);
        });

        tableViewFriend.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            RemoveFriendButton.setDisable(newValue == null);

            if(newValue != null && newValue.getStatus() == FriendshipStatus.SENT)
            {
                AcceptFriendButton.setDisable(false);
                RejectFriendButton.setDisable(false);
            }else
            {
                AcceptFriendButton.setDisable(true);
                RejectFriendButton.setDisable(true);
            }
        });

        AddFriendButton.setDisable(true);
        RemoveFriendButton.setDisable(true);
        AcceptFriendButton.setDisable(true);
        RejectFriendButton.setDisable(true);
    }

    public void handleAddFriendButton(ActionEvent event) throws IOException
    {
        Stage stage = WindowFactory.createAddFriendWindow(service);
        stage.show();
    }

    public void handleRemoveFriendButton(ActionEvent event) throws IOException
    {
        Long removerId = tableViewUser.getSelectionModel().getSelectedItem().getId();
        Long removedId = tableViewFriend.getSelectionModel().getSelectedItem().getFriendId();

        try {
            service.StergePrietenie(new Tuple<>(removerId, removedId));
        }catch (ValidationException error)
        {
            Stage errorStage = WindowFactory.createErrorWindow(error.getMessage());
            errorStage.show();
        }
    }

    public void handleAcceptButton(ActionEvent event) throws IOException {
        Long selectedId = tableViewFriend.getSelectionModel().getSelectedItem().getFriendId();
        try {
            service.actualizeazaPrietenie(searchedId, selectedId, FriendshipStatus.ACCEPTED);
        }catch (ValidationException error)
        {
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }
    }

    public void handleRejectButton(ActionEvent event) throws IOException {
        Long selectedId = tableViewFriend.getSelectionModel().getSelectedItem().getFriendId();
        try {
            service.actualizeazaPrietenie(searchedId, selectedId, FriendshipStatus.REJECTED);
        }catch (ValidationException error)
        {
            WindowFactory.createErrorWindow(error.getMessage()).show();
        }
    }

}
