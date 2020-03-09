package tech.jriascos.application;

import tech.jriascos.utilities.Tools;
import tech.jriascos.model.Definitions;
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
    public void start(final Stage primaryStage) throws IOException {
        final Gson gson = new Gson();
        final String classpathDirectory = Tools.getClasspathDir();
        final BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "words.json"));
        Words[] words = gson.fromJson(br, Words[].class);
        words = Tools.sortWordsAscending(words, 0);
        
        primaryStage.setTitle("Dictionary Application");
        primaryStage.setMaximized(true);

        final Scene defaultScene = new Scene(SceneBuilder.buildDefaultScene(words), 300, 275);
        defaultScene.getStylesheets().add(Window.class.getResource("/styles/style.css").toExternalForm());

        primaryStage.setScene(defaultScene);
        primaryStage.show();
        // Event listeners are after stage.show() since it depends on looking up
        // elements with ids, which only works
        // after the stage has been built and shown
        Tools.leftColumnListeners(defaultScene, primaryStage, 0);
    }

    public static void main(final String[] args) {
        launch(args);
    }

    

}
