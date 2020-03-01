package tech.jriascos.application;

import tech.jriascos.utilities.Tools;
import tech.jriascos.model.Words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends Application {

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        Gson gson = new Gson();
        String classpathDirectory = Tools.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "words.json"));
        Words[] words = gson.fromJson(br, Words[].class);

        primaryStage.setTitle("Dictionary Application");
        primaryStage.setMaximized(true);

        Scene defaultScene = SceneBuilder.buildDefaultScene(words);

        primaryStage.setScene(defaultScene);
        primaryStage.show();
        //Event listeners are after stage.show() since it depends on looking up elements with ids, which only works
        //after the stage has been built and shown
        SceneBuilder.leftColumnListeners(defaultScene, words, primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    

}
