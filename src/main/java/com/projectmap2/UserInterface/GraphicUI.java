package com.projectmap2.UserInterface;

import com.projectmap2.Controllers.AddFriendController;
import com.projectmap2.Controllers.GraphicController;
import com.projectmap2.Controllers.LoginController;
import com.projectmap2.Domain.Message;
import com.projectmap2.Domain.Prietenie;
import com.projectmap2.Domain.Tuple;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.GraphicApp;
import com.projectmap2.Repository.DbRepos.MessageDbRepo;
import com.projectmap2.Repository.DbRepos.PrietenieDbRepo;
import com.projectmap2.Repository.DbRepos.UtilizatorDbRepo;
import com.projectmap2.Repository.Repository;
import com.projectmap2.Service.Service;
import com.projectmap2.Utils.Passes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicUI extends Application {
    private Service service;
    private Repository<Long,Utilizator> repoUser;
    private PrietenieDbRepo repoFriends;
    private MessageDbRepo repoMessage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        String url = "jdbc:postgresql://localhost:5432/ProjectMAP";
        String userName = "postgres";
        String password = Passes.postgresPass;

        repoUser = new UtilizatorDbRepo(url,userName,password);
        repoFriends = new PrietenieDbRepo(url,userName,password);
        repoMessage = new MessageDbRepo(url,userName,password);
        service = new Service(repoUser,repoFriends, repoMessage);
        initView(stage);
        stage.show();
    }

    public void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GraphicApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Graphic Social Network Application");
        primaryStage.setScene(scene);
        LoginController controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setStage(primaryStage);
        primaryStage.setResizable(false);

    }


}
