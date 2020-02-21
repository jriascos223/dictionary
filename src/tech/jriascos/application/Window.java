package tech.jriascos.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Dictionary Application");
        primaryStage.setMaximized(true);

        Scene defaultScene = SceneBuilder.buildDefaultScene();
        
        primaryStage.setScene(defaultScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    

}
