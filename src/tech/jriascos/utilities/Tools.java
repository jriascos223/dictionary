package tech.jriascos.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import tech.jriascos.application.SceneBuilder;
import tech.jriascos.model.Definitions;
import tech.jriascos.model.Words;
import tech.jriascos.utilities.Tools;

public class Tools {
    /**
     * Needed to obtain resources (such as words.json) from the classpath's
     * directory
     * 
     * @return string that returns the directory in the classpath with required
     *         resources
     */
    public static String getClasspathDir() {
        String classpath = System.getProperty("java.class.path", ".");
        boolean windows = false;
        if (classpath.matches(".*\\\\.*")) {
            windows = true;
        }
        if (windows) {
            String[] splitClasspathDir = classpath.split(";");
            String classpathDirectory = "";
            for (String s : splitClasspathDir) {
                if (s.matches(".*lib\\\\.*")) {
                    classpathDirectory = s;
                }
            }
            return classpathDirectory;
        } else {
            String[] splitClasspathDir = classpath.split(":");
            String classpathDirectory = "";
            for (String s : splitClasspathDir) {
                if (s.matches(".*lib/.*")) {
                    classpathDirectory = s;
                }
            }
            return classpathDirectory;
        }
    }

    /**
     * Does, as you'd imagine, sorting. Turns words into ascii, sorts those numbers to two letters, 
     * and then puts the words back together.
     * @param words array of words for the dictionary
     * @param asciiArray array of words in ascii
     */
    public static Words[] sortAscending(Words[] words, ArrayList<byte[]> asciiArray) {
        for (int i = 0; i < asciiArray.size() - 1; i++) {
            for (int j = 0; j < asciiArray.size() - 1 - i; j++) {
                if (asciiArray.get(j)[0] > asciiArray.get(j + 1)[0]) {
                    byte[] temp = asciiArray.get(j);
                    asciiArray.set(j, asciiArray.get(j + 1));
                    asciiArray.set(j + 1, temp);
                    Words temp2 = words[j];
                    words[j] = words[j + 1];
                    words[j + 1] = temp2;
                    // 99% sure it will stop working if I add a one letter word
                } else if (asciiArray.get(j)[0] == asciiArray.get(j + 1)[0]) {
                    if (asciiArray.get(j)[1] > asciiArray.get(j + 1)[1]) {
                        byte[] temp = asciiArray.get(j);
                        asciiArray.set(j, asciiArray.get(j + 1));
                        asciiArray.set(j + 1, temp);
                        Words temp2 = words[j];
                        words[j] = words[j + 1];
                        words[j + 1] = temp2;
                    }
                }
            }
        }
        return words;
    }

    /**
     * Reverses array.
     * @param a array to reverse
     * @param n length of array (almost always)
     * @return reversed array
     */
    public static Words[] reverse(Words a[], int n) {
        Words[] b = new Words[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }
        return b;
    }

    /**
     * Saves the word passed through into the json file. Yay.
     * @param words array of words used by dictionary
     * @param wordObj instance of Words object to add to dictionary
     * @throws IOException
     */
    public static Words[] saveWordJson(Words[] words, Words wordObj) throws IOException {
        String classpathDirectory = getClasspathDir();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter fw = new FileWriter(classpathDirectory + "words.json");

        Words[] newWords = new Words[words.length + 1];
        System.arraycopy(words, 0, newWords, 0, words.length);
        
        newWords[words.length] = wordObj;

        String json = gson.toJson(newWords, Words[].class);

        fw.write(json);
        fw.flush();
        fw.close();

        return newWords;
    }

    private static Words[] sortWordsAscending(Words[] words, int isSorted) {
        if (isSorted == 1) {
            return words;
        }
        ArrayList<String> wordStrings = new ArrayList<String>();
        ArrayList<byte[]> asciiArray = new ArrayList<byte[]>();
        for (Words word : words) {
            wordStrings.add((word.getWord().toLowerCase()));
        }

        for (String s : wordStrings) {
            byte[] asciiWord = new byte[s.length()];
            for(int i = 0; i < s.length(); i++) {
                asciiWord[i] = (byte) s.charAt(i);
            }
            asciiArray.add(asciiWord);
        }

        words = Tools.sortAscending(words, asciiArray);
        return words;
    }

    private static Words[] sortWordsDescending(Words[] words, int isSorted) {
        if (isSorted == 2) {
            return words;
        }
        words = sortWordsAscending(words, 0);
        int n = words.length;
        int j = n;
        Words[] b = new Words[n]; 
        for (int i = 0; i < n; i++) { 
            b[j - 1] = words[i]; 
            j = j - 1; 
        } 
        return b;
    }

    public static void leftColumnListeners(Scene scene, Words[] words, Stage stage, int isSorted) {
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
            if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                desc.setSelected(false);
                Words[] wordsInner = sortWordsAscending(words, isSorted);
                List<String> wordStringsInner = new ArrayList<String>();
                for (int i = 0; i < wordsInner.length; i++) {
                    wordStringsInner.add(wordsInner[i].getWord());
                }
                wordHousing.setItems(FXCollections.observableArrayList(wordStringsInner));
            }
        });

        desc.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                asc.setSelected(false);
                if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                    asc.setSelected(false);
                    Words[] wordsInner = sortWordsDescending(words, isSorted);
                    List<String> wordStringsInner = new ArrayList<String>();
                    for (int i = 0; i < wordsInner.length; i++) {
                        wordStringsInner.add(wordsInner[i].getWord());
                    }
                    wordHousing.setItems(FXCollections.observableArrayList(wordStringsInner));
                }
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
                stage.getScene().setRoot(SceneBuilder.buildAddGrid(scene, words, stage));
                leftColumnListeners(scene, words, stage, 0);
                addScreenListeners(scene, words, stage);
            }
        };
        addButton.setOnAction(showAddScreen);

        EventHandler<ActionEvent> showDeleteScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.getScene().setRoot(SceneBuilder.buildDeleteGrid(words));
                leftColumnListeners(scene, words, stage, 0);
            }
        };
        removeButton.setOnAction(showDeleteScreen);
        wordHousing.setItems(filteredWords);

        wordHousing.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number index) {
                SceneBuilder.buildDefinitionHousing(words, (Integer) index, scene);
            }
          
        });
    }

    public static void addScreenListeners(Scene scene, Words[] words, Stage stage) {
        ScrollPane addScroll = (ScrollPane) scene.lookup("#addScroll");
        Button back = (Button) addScroll.getContent().lookup("#close");
        Button addDefinition = (Button) addScroll.getContent().lookup("#addDButton");
        VBox addDSection = (VBox) addScroll.getContent().lookup("#addDSection");
        VBox addSynSection = (VBox) addScroll.getContent().lookup("#addSynSection");
        VBox addAntSection = (VBox) addScroll.getContent().lookup("#addAntSection");
        Button addSynonymButton = (Button) addScroll.getContent().lookup("#addSynonymButton");
        Button addAntonymButton = (Button) addScroll.getContent().lookup("#addAntonymButton");

        System.out.println(addDSection);
        System.out.println(addSynSection);

        EventHandler<ActionEvent> showDefaultScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.getScene().setRoot(SceneBuilder.buildDefaultScene(words));
                leftColumnListeners(scene, words, stage, 0);
            }
        };

        back.setOnAction(showDefaultScreen);

        EventHandler<ActionEvent> addDInputs = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                addDSection.getChildren().add(createDefSpeechPairInput());
            }
        };

        addDefinition.setOnAction(addDInputs);

        EventHandler<ActionEvent> addSInputs = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                addSynSection.getChildren().add(createSynonymInput());
            }
        };

        addSynonymButton.setOnAction(addSInputs);

        EventHandler<ActionEvent> addAInputs = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                addAntSection.getChildren().add(createAntonymInput());
            }
        };

        addAntonymButton.setOnAction(addAInputs);
    }

    public static VBox createDefSpeechPairInput() {
        VBox defSpeechPair = new VBox();
        defSpeechPair.setSpacing(10);
        defSpeechPair.setId("defSpeechPair");
        TextField definition = new TextField();
        definition.setPromptText("Enter definition here.");
        TextField partOfSpeech = new TextField();
        partOfSpeech.setPromptText("Enter part of speech here:");

        defSpeechPair.getChildren().addAll(definition, partOfSpeech);

        return defSpeechPair;
    }

    public static VBox createSynonymInput() {
        VBox synonymInput = new VBox();
        synonymInput.setSpacing(10);
        synonymInput.setId("synonymInput");
        TextField synonym = new TextField();
        synonym.setPromptText("Enter synonym here.");
        synonymInput.getChildren().addAll(synonym);
        return synonymInput;
    }

    public static VBox createAntonymInput() {
        VBox antonymInput = new VBox();
        antonymInput.setSpacing(10);
        antonymInput.setId("antonymInput");
        TextField antonym = new TextField();
        antonym.setPromptText("Enter antonym here.");
        antonymInput.getChildren().addAll(antonym);
        return antonymInput;
    }

    
}