package tech.jriascos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tech.jriascos.model.Definitions;
import tech.jriascos.model.Words;

public class SceneBuilder {
    /**
     * Builds the default scene of showing words that are clicked from the ListView
     * on the left, alongside its definitions
     * 
     * @param words array of words in the dictionary
     * @return constructed scene JavaFX has to show
     */
    public static Scene buildDefaultScene(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("rootGrid");
        grid.setVgap(10);

        Scene defaultScene = new Scene(grid, 300, 275);
        defaultScene.getStylesheets().add(Window.class.getResource("/styles/style.css").toExternalForm());
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        grid.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        grid.getRowConstraints().addAll(row1);

        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        // Will probably have to be a VBox with dynamically added labels
        VBox definitionHousing = new VBox();
        ScrollPane definitionScroll = new ScrollPane();
        definitionScroll.setContent(definitionHousing);
        grid.add(definitionScroll, 1, 0);
        GridPane.setMargin(definitionScroll, new Insets(8, 8, 8, 8));
        definitionHousing.setId("definitionHousing");
        definitionScroll.setId("definitionScroll");

        return defaultScene;
    }

    private static void buildDefinitionHousing(Words[] words, int index, Scene scene) {
        if (index == -1) {
            return;
        }
        VBox definitionHousing = (VBox) scene.lookup("#definitionHousing");
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        definitionHousing.getChildren().clear();
        TextField searchbar = (TextField) scene.lookup("#searchbar");
        List<String> filteredStrings = wordHousing.getItems();
        Words[] filteredWords = new Words[filteredStrings.size()];

        if (searchbar.getText() != null || searchbar.getText().length() != 0) {
            for (int i = 0; i < words.length; i++) {
                for (int j = 0; j < filteredStrings.size(); j++) {
                    if (words[i].getWord().equals(filteredStrings.get(j))) {
                        filteredWords[j] = words[i];
                    }
                }
            }   
        }
        Text word = new Text(filteredWords[index].getWord());
        word.setId("word");
        definitionHousing.getChildren().addAll(word);

        HBox heading = new HBox(new Text("Definitions"));
        heading.setId("definitionHeading");
        definitionHousing.getChildren().addAll(heading);

        for (int i = 0; i < filteredWords[index].getDefinitions().length; i++) {
            HBox definitionString = new HBox(new Text(filteredWords[index].getDefinitions()[i].getDefinition()));
            HBox partOfSpeech = new HBox(new Text(((Integer) (i+1)).toString() + ". " + filteredWords[index].getWord() + " (" + filteredWords[index].getDefinitions()[i].getPartOfSpeech() + ")"));
            
            definitionString.getStyleClass().add("definitions");
            partOfSpeech.getStyleClass().add("partOfSpeech");
            definitionHousing.getChildren().addAll(partOfSpeech);
            definitionHousing.getChildren().addAll(definitionString);
        }


        

    }

    /**
     * Builds the left hand side of the GUI, this being the buttons, checkboxes,
     * searchbar, and word list the dictionary requires.
     * 
     * @param leftColumn VBox that will house the contents
     * @param grid       GridPane that is the root node of the scene
     * @param words      array of words in the dictionary
     * @return leftColumn VBox populated with new elements
     */
    private static VBox buildLeftColumn(VBox leftColumn, GridPane grid, Words[] words) {
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

    public static GridPane buildAddGrid(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("addGrid");
        grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        grid.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        grid.getRowConstraints().addAll(row1);
        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        return grid;
    }

    public static GridPane buildDeleteGrid(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("deleteGrid");
        grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        grid.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        grid.getRowConstraints().addAll(row1);
        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        return grid;
    }

    public static void leftColumnListeners(Scene scene, Words[] words, Stage stage) {
        CheckBox asc = (CheckBox) scene.lookup("#asc");
        CheckBox desc = (CheckBox) scene.lookup("#desc");
        TextField searchbar = (TextField) scene.lookup("#searchbar");
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        Button addButton = (Button) scene.lookup("#addButton");
        Button removeButton = (Button) scene.lookup("#removeButton");
        GridPane rootGrid = (GridPane) scene.lookup("#rootGrid");
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

        searchbar.textProperty().addListener(obs -> {
            String filter = searchbar.getText().toLowerCase();
            if (filter == null || filter.length() == 0 || filter.matches("\\s*")) {
                filteredWords.setPredicate(s -> true);
            } else {
                filteredWords.setPredicate(s -> s.matches(filter + ".*"));
            }
        });

        EventHandler<ActionEvent> showAddScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.getScene().setRoot(buildAddGrid(words));
                SceneBuilder.leftColumnListeners(scene, words, stage);
            }
        };
        addButton.setOnAction(showAddScreen);

        EventHandler<ActionEvent> showDeleteScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.getScene().setRoot(buildDeleteGrid(words));
                SceneBuilder.leftColumnListeners(scene, words, stage);
            }
        };
        removeButton.setOnAction(showDeleteScreen);
        wordHousing.setItems(filteredWords);

        wordHousing.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number index) {
                buildDefinitionHousing(words, (Integer) index, scene);
            }
          
        });
    }
}