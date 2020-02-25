package tech.jriascos.application;

import tech.jriascos.utilities.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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

    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();
        String classpathDirectory = Tools.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "words.json"));
        Words[] words = gson.fromJson(br, Words[].class);
        launch(args);
    }

    

}
