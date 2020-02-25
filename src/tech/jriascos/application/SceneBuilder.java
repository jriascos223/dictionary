package tech.jriascos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import tech.jriascos.model.Words;

public class SceneBuilder {
    public static Scene buildDefaultScene(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("rootGrid");
        grid.setVgap(10);

        Scene defaultScene = new Scene(grid, 300, 275);
        defaultScene.getStylesheets().add(Window.class.getResource("/styles/defaultScene.css").toExternalForm());

        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words, defaultScene);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        ListView<String> definitionHousing = new ListView<String>();
        definitionHousing = buildDefinitionHousing(words);
        grid.add(definitionHousing, 1, 0);
        GridPane.setMargin(definitionHousing, new Insets(8, 8, 8, 8));
        definitionHousing.setId("definitionHousing");
        
        return defaultScene;
    }

    private static ListView<String> buildDefinitionHousing(Words[] words) {
        ListView<String> definitionHousing = new ListView<String>();

        return definitionHousing;
    }

    private static VBox buildLeftColumn(VBox leftColumn, GridPane grid, Words[] words, Scene scene) {
        HBox buttonHousing = new HBox();
        buttonHousing.setId("buttonHousing");
        
        Button addButton = new Button("Add");
        addButton.setId("addButton");
        buttonHousing.getChildren().add(addButton);
        
        Button removeButton = new Button("Remove");
        removeButton.setId("removeButton");
        buttonHousing.getChildren().add(removeButton);

        TextField searchbar = new TextField();
        searchbar.setId("searchbar");
        searchbar.setPromptText("Search");

        CheckBox asc = new CheckBox("Asc");
        asc.setId("asc");
        CheckBox desc = new CheckBox("Desc");
        desc.setId("desc");
        

        HBox checkboxHousing = new HBox();
        checkboxHousing.setId("checkboxHousing");
        checkboxHousing.getChildren().addAll(asc, desc);

        ListView<String> wordHousing = new ListView<String>();
        wordHousing.setId("wordHousing");
        
        leftColumn.getChildren().add(buttonHousing);
        leftColumn.getChildren().add(searchbar);
        leftColumn.getChildren().add(checkboxHousing);
        leftColumn.getChildren().add(wordHousing);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        grid.getColumnConstraints().addAll(col1,col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        grid.getRowConstraints().addAll(row1);

        addButton.prefWidthProperty().bind(Bindings.divide(buttonHousing.widthProperty(), 2.0));
        removeButton.prefWidthProperty().bind(Bindings.divide(buttonHousing.widthProperty(), 2.0));
        asc.prefWidthProperty().bind(Bindings.divide(checkboxHousing.widthProperty(), 2.0));
        desc.prefWidthProperty().bind(Bindings.divide(checkboxHousing.widthProperty(), 2.0));
        asc.setPadding(new Insets(0, 0, 0, 30));
        desc.setPadding(new Insets(0, 0, 0, 30));

        VBox.setVgrow(wordHousing, Priority.ALWAYS);
        wordHousing.setMaxHeight(Double.MAX_VALUE);
        return leftColumn;
    }

    public static Scene buildAddScene(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("addGrid");
        grid.setVgap(10);

        Scene addScene = new Scene(grid, 300, 275);
        addScene.getStylesheets().add(Window.class.getResource("/styles/addScene.css").toExternalForm());

        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words, addScene);


        return null;
    }

    public static void leftColumnListeners(Scene scene, Words[] words) {
        CheckBox asc = (CheckBox) scene.lookup("#asc");
        CheckBox desc = (CheckBox) scene.lookup("#desc");
        TextField searchbar = (TextField) scene.lookup("#searchbar");
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        List<String> wordStrings = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            wordStrings.add(words[i].getWord());
        }
        ObservableList<String> wordObserv = FXCollections.observableArrayList(wordStrings);

        asc.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                desc.setSelected(false);
            }
        });

        desc.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                asc.setSelected(false);
            }
        });
        FilteredList<String> filteredWords = new FilteredList<String>(wordObserv, s -> true);

        searchbar.textProperty().addListener(obs->{
            String filter = searchbar.getText(); 
            if(filter == null || filter.length() == 0) {
                filteredWords.setPredicate(s -> true);
            }
            else {
                filteredWords.setPredicate(s -> s.contains(filter));
            }
        });

        wordHousing.setItems(filteredWords);
    }
}