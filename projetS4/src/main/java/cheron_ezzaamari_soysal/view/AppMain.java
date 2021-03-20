package cheron_ezzaamari_soysal.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AppMain extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        GridPane gridPaneRoot = FXMLLoader.load(getClass().getResource("SceneBuilder.fxml"));
        Scene scene = new Scene(gridPaneRoot,1000,600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}