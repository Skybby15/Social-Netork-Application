package com.projectmap2.Controllers;

import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.GraphicApp;
import com.projectmap2.Service.Service;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class WindowFactory {
    private WindowFactory(){}

    public static Stage createLoginWindow(Service service) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Graphic Social Network Application");
        stage.setScene(scene);
        LoginController controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setStage(stage);
        stage.setResizable(false);

        return stage;
    }

    ///Creaza o fereastra destinata afisarii erorii <message>
    public static Stage createErrorWindow(String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(GraphicApp.class.getResource("error_info.fxml"));
        Stage stage = new Stage();
        AnchorPane pane = loader.load();
        stage.setTitle("Error");
        stage.setScene(new Scene(pane));
        ErrorWindowController controller = loader.getController();
        controller.setMessage(message);
        controller.setStage(stage);
        return stage;
    }

    ///Creeaza o fereastra destinata adaugarii unui prieten
    public static Stage createAddFriendWindow(Service service) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("add-friend.fxml"));
        AnchorPane pane = fxmlLoader.load();
        Scene scene = new Scene(pane);
        stage.setTitle("Add Friend");
        stage.setScene(scene);
        AddFriendController controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setUser(MainController.loggedUser);
        controller.setStage(stage);
        stage.setResizable(false);

        return stage;
    }

    public static Stage createMainWindow(Service service) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Main Window");
        stage.setScene(scene);
        MainController controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setStage(stage);
        stage.setResizable(false);

        return stage;
    }

    public static Stage createSignUpWindow(Service service ) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("create-account.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign-up Window");
        stage.setScene(scene);
        CreateAccountController controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setStage(stage);
        stage.setResizable(false);

        return stage;
    }

    public static Stage createUserDetailsWindow(Utilizator user, LocalDateTime date) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("friend-details.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("User Details");
        stage.setScene(scene);
        UserDetailsController controller = fxmlLoader.getController();
        controller.setInfo(user,date);
        stage.setResizable(false);

        return stage;
    }

    public static Stage createRequestDetailsWindow(Utilizator user, LocalDateTime date, FriendshipStatus status) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("request-details.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Request Details");
        stage.setScene(scene);
        RequestDetailsController controller = fxmlLoader.getController();
        controller.setInfo(user,date,status);
        stage.setResizable(false);

        return stage;
    }

    public static Stage createMessageWindow(Service service ,Utilizator receiver) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("message-box.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Message Box");
        stage.setScene(scene);
        MessageBoxController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setService(service);
        controller.setLoggedUser(MainController.loggedUser);
        controller.setReceiver(receiver);
        controller.initLayout();
        stage.setResizable(false);

        return stage;
    }
}
