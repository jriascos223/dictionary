package tech.jriascos.application;

import java.security.spec.DSAPrivateKeySpec;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tech.jriascos.model.Definitions;
import tech.jriascos.model.Words;
import tech.jriascos.utilities.Tools;

public class SceneBuilder {
    /**
     * Builds the default scene of showing words that are clicked from the ListView
     * on the left, alongside its definitions
     * 
     * @param words array of words in the dictionary
     * @return constructed scene JavaFX has to show
     */
    public static GridPane buildDefaultScene(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("rootGrid");
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(80);
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

        return grid;
    }

    public static void buildDefinitionHousing(Words[] words, int index, Scene scene) {
        if (index == -1) {
            return;
        }
        VBox definitionHousing = (VBox) scene.lookup("#definitionHousing");
        if (definitionHousing == null) {
            return;
        }
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        definitionHousing.getChildren().clear();
        TextField searchbar = (TextField) scene.lookup("#searchbar");
        List<String> filteredStrings = wordHousing.getItems();
        Words[] filteredWords = new Words[filteredStrings.size()];

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < filteredStrings.size(); j++) {
                if (words[i].getWord().equals(filteredStrings.get(j))) {
                    filteredWords[j] = words[i];
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

        for (int i = 0; i < filteredWords[index].getSynonyms().length; i++) {
            if (i == 0) {
                HBox synonymHeading = new HBox(new Text("Synonyms"));
                synonymHeading.setId("synonymHeading");
                definitionHousing.getChildren().addAll(synonymHeading);
            }
            HBox synonymString = new HBox(new Text(((Integer) (i+1)).toString() + ". " + filteredWords[index].getSynonyms()[i]));
            synonymString.getStyleClass().add("synonyms");
            definitionHousing.getChildren().addAll(synonymString);
        }

        for (int i = 0; i < filteredWords[index].getAntonyms().length; i++) {
            if (i == 0) {
                HBox antonymHeading = new HBox(new Text("Antonym"));
                antonymHeading.setId("antonymHeading");
                definitionHousing.getChildren().addAll(antonymHeading);
            }
            HBox antonymString = new HBox(new Text(((Integer) (i+1)).toString() + ". " + filteredWords[index].getAntonyms()[i]));
            antonymString.getStyleClass().add("antonyms");
            definitionHousing.getChildren().addAll(antonymString);
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

    public static GridPane buildAddGrid(Scene scene, Words[] words, Stage stage) {
        GridPane grid = new GridPane();
        grid.setId("addGrid");
        grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(80);
        grid.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        grid.getRowConstraints().addAll(row1);
        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid, words);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        //Right hand side of screen
        VBox addHousing = new VBox();
        addHousing.setSpacing(10);
        addHousing.setId("addHousing");
        //Heading that says "Add word" and has button "back to dictionary"
        HBox addHeading = new HBox();
        addHeading.setId("addHeading");
        Text addHeadingText = new Text("Add Word");
        Button close = new Button("Back to Dictionary");
        close.setId("close");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        //Adding heading to right hand side of screen
        addHeading.getChildren().addAll(addHeadingText, spacer, close);
        addHousing.getChildren().add(addHeading);

        //New heading for definition section
        HBox addDefinitionHeading = new HBox();
        addDefinitionHeading.setId("addDefinitionHeading");
        //VBox to add to later in event listeners (for extra fields)
        VBox addDSection = new VBox();
        addDSection.setSpacing(10);
        addDSection.setId("addDSection");
        //header text and button
        Text addDefinitions = new Text("Add Definition & Part of Speech");
        Button addDButton = new Button("+");
        addDButton.setId("addDButton");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        //adding header text and button to right hand side of screen
        addDefinitionHeading.getChildren().addAll(addDefinitions, spacer2, addDButton);
        addDSection.getChildren().add(addDefinitionHeading);
        addHousing.getChildren().add(addDSection);

        //input boxes for definition and part of speech (also model for container that will be added to sections)
        VBox defSpeechPair = new VBox();
        defSpeechPair.setSpacing(10);
        defSpeechPair.setId("defSpeechPair");
        TextField definition = new TextField();
        definition.setPromptText("Enter definition here.");
        TextField partOfSpeech = new TextField();
        partOfSpeech.setPromptText("Enter part of speech here.");
        //adding definition and part of speech input boxes into container
        defSpeechPair.getChildren().addAll(definition, partOfSpeech);
        //adding input boxes to right hand side of screen
        addHousing.getChildren().add(defSpeechPair);

        HBox addSynonymHeading = new HBox();
        addSynonymHeading.setId("addSynonymHeading");
        Text addSynonyms = new Text("Add Synonyms");
        Button addSynonymButton = new Button("+");
        addSynonymButton.setId("addSynonymButton");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        addSynonymHeading.getChildren().addAll(addSynonyms, spacer3, addSynonymButton);
        addHousing.getChildren().add(addSynonymHeading);

        VBox synonymInput = new VBox();
        synonymInput.setSpacing(10);
        synonymInput.setId("synonymInput");
        TextField synonym = new TextField();
        synonym.setPromptText("Enter synonym here.");

        synonymInput.getChildren().addAll(synonym);

        addHousing.getChildren().add(synonymInput);

        HBox addAntonymHeading = new HBox();
        addAntonymHeading.setId("addAntonymHeading");
        Text addAntonym = new Text("Add Antonyms");
        Button addAntonymButton = new Button("+");
        addAntonymButton.setId("addAntonymButton");
        Region spacer4 = new Region();
        HBox.setHgrow(spacer4, Priority.ALWAYS);

        addAntonymHeading.getChildren().addAll(addAntonym, spacer4, addAntonymButton);
        addHousing.getChildren().add(addAntonymHeading);

        VBox antonymInput = new VBox();
        antonymInput.setSpacing(10);
        antonymInput.setId("antonymInput");
        TextField antonym = new TextField();
        antonym.setPromptText("Enter antonym here.");

        antonymInput.getChildren().addAll(antonym);

        addHousing.getChildren().add(antonymInput);
        
        grid.add(addHousing, 1, 0);
        GridPane.setMargin(addHousing, new Insets(8, 8, 8, 8));

        return grid;
    }

    public static GridPane buildDeleteGrid(Words[] words) {
        GridPane grid = new GridPane();
        grid.setId("deleteGrid");
        grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(80);
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

    
}