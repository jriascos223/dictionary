package tech.jriascos.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
     * Does, as you'd imagine, sorting. Turns words into ascii, sorts those numbers, 
     * and then puts the words back together.
     * @param words array of words for the dictionary
     * @param asciiArray array of words in ascii
     */
    public static Words[] sortAscending(Words[] words, ArrayList<String> wordStrings) {
        int n = wordStrings.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (wordStrings.get(i).toLowerCase().compareTo(wordStrings.get(j).toLowerCase())>0) {
                    Words temp = words[i];
                    words[i] = words[j];
                    words[j] = temp;
                    String temp2 = wordStrings.get(i);
                    wordStrings.set(i, wordStrings.get(j));
                    wordStrings.set(j, temp2);
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
        if (wordObj == null) {
            String classpathDirectory = getClasspathDir();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(classpathDirectory + "words.json");

            String json = gson.toJson(words, Words[].class);

            fw.write(json);
            fw.flush();
            fw.close();

            return words;
        }
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

    public static Words[] sortWordsAscending(Words[] words, int isSorted) {
        if (isSorted == 1) {
            return words;
        }
        ArrayList<String> wordStrings = new ArrayList<String>();
        for (Words word : words) {
            wordStrings.add((word.getWord().toLowerCase()));
        }
        words = Tools.sortAscending(words, wordStrings);
        return words;
    }

    public static Words[] sortWordsDescending(Words[] words, int isSorted) {
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

    public static void leftColumnListeners(Scene scene, Stage stage, int isSorted) throws FileNotFoundException {
        Words[] words = getWords();
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
            searchbar.clear();
            if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                desc.setSelected(false);
                Words[] wordsInner;
                try {
                    wordsInner = sortWordsAscending(getWords(), isSorted);
                    List<String> wordStringsInner = new ArrayList<String>();
                    for (int i = 0; i < wordsInner.length; i++) {
                        wordStringsInner.add(wordsInner[i].getWord());
                    }
                    wordHousing.setItems(FXCollections.observableArrayList(wordStringsInner));
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        });

        desc.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            searchbar.clear();
            if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                asc.setSelected(false);
                if (isNowSelected && (isSorted != 1 || isSorted != 2)) {
                    asc.setSelected(false);
                    Words[] wordsInner;
                    try {
                        wordsInner = sortWordsDescending(getWords(), isSorted);
                        List<String> wordStringsInner = new ArrayList<String>();
                        for (int i = 0; i < wordsInner.length; i++) {
                            wordStringsInner.add(wordsInner[i].getWord());
                        }
                        wordHousing.setItems(FXCollections.observableArrayList(wordStringsInner));
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                }
            }
        });
        FilteredList<String> filteredWords = new FilteredList<String>(wordObserv, s -> true);

        searchbar.textProperty().addListener(obs -> {
            //Since these lambda expressions can't modify outside variables, gotta check if array is sorted in ascending order or not
            FilteredList<String> innerFilteredWords = new FilteredList<String>(wordObserv);
            Words[] innerWords;
            try {
                innerWords = getWords();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //should default to ascending if none of the boxes are selected
            if (asc.isSelected() || (!asc.isSelected() && !desc.isSelected())) {
                List<String> innerWordStrings = new ArrayList<String>();
                innerWords = Tools.sortWordsAscending(words, 0);
                for (int i = 0; i < words.length; i++) {
                    innerWordStrings.add(innerWords[i].getWord());
                }
                ObservableList<String> innerWordObserv = FXCollections.observableArrayList(innerWordStrings);
                innerFilteredWords = new FilteredList<String>(innerWordObserv, s -> true);
            }else if (desc.isSelected()) {
                List<String> innerWordStrings = new ArrayList<String>();
                innerWords = Tools.sortWordsDescending(words, 0);
                for (int i = 0; i < words.length; i++) {
                    innerWordStrings.add(innerWords[i].getWord());
                }
                ObservableList<String> innerWordObserv = FXCollections.observableArrayList(innerWordStrings);
                innerFilteredWords = new FilteredList<String>(innerWordObserv, s -> true);
            }
            String filter = searchbar.getText().toLowerCase();
            if (filter == null || filter.length() == 0 || filter.matches("\\s*")) {
                innerFilteredWords.setPredicate(s -> true);
                wordHousing.setItems(innerFilteredWords);

            } else {
                innerFilteredWords.setPredicate(s -> s.matches(filter + ".*"));
                wordHousing.setItems(innerFilteredWords);
            }
        });

        EventHandler<ActionEvent> showAddScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.getScene().setRoot(SceneBuilder.buildAddGrid(scene, words, stage));
                try {
                    leftColumnListeners(scene, stage, 0);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    addScreenListeners(scene, stage);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        };
        addButton.setOnAction(showAddScreen);

        EventHandler<ActionEvent> deleteWords = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    deleteWords(scene, stage);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    leftColumnListeners(scene, stage, 0);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        };
        removeButton.setOnAction(deleteWords);
        wordHousing.setItems(filteredWords);

        
        wordHousing.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number index) {
                SceneBuilder.buildDefinitionHousing(words, (Integer) index, scene);
            }
          
        });
    }

    private static Words[] getWords() throws FileNotFoundException {
        Gson gson = new Gson();
        String classpathDirectory = Tools.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "words.json"));
        Words[] words = gson.fromJson(br, Words[].class);
        words = Tools.sortWordsAscending(words, 0);
        return words;
    }

    public static void addScreenListeners(Scene scene, Stage stage) throws FileNotFoundException {
        Words[] words = getWords();
        ScrollPane addScroll = (ScrollPane) scene.lookup("#addScroll");
        Button back = (Button) addScroll.getContent().lookup("#close");
        Button submit = (Button) addScroll.getContent().lookup("#submit");
        Button addDefinition = (Button) addScroll.getContent().lookup("#addDButton");
        VBox addHousing = (VBox) addScroll.getContent().lookup("#addHousing");
        VBox addDSection = (VBox) addScroll.getContent().lookup("#addDSection");
        VBox addSynSection = (VBox) addScroll.getContent().lookup("#addSynSection");
        VBox addAntSection = (VBox) addScroll.getContent().lookup("#addAntSection");
        Button addSynonymButton = (Button) addScroll.getContent().lookup("#addSynonymButton");
        Button addAntonymButton = (Button) addScroll.getContent().lookup("#addAntonymButton");
        

        EventHandler<ActionEvent> showDefaultScreen = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    Words[] innerWords = getWords();
                    stage.getScene().setRoot(SceneBuilder.buildDefaultScene(innerWords));
                    leftColumnListeners(scene, stage, 0);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        };

        EventHandler<ActionEvent> addWordToDictAlert = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to add this word?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        addWordToDict(scene, stage);
                    }
                });
                
            }
        };

        submit.setOnAction(addWordToDictAlert);

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

    private static void deleteWords(Scene scene, Stage stage) throws IOException {
        Words[] words = getWords();
        System.out.println(words.length);
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        ObservableList<String> strings = wordHousing.getSelectionModel().getSelectedItems();
        for (int j = 0; j < strings.size(); j++) {
            for (int i = 0; i < words.length; i++) {
                if (strings.get(j).equals(words[i].getWord())) {
                    words = removeTheElement(words, i);
                }
            }
        }
        System.out.println(words.length);
        Words[] outputWords = saveWordJson(words, null);
        updateWords(outputWords, scene);
    }

    public static Words[] removeTheElement(Words[] arr, 
                                          int index) 
    { 
  
        // If the array is empty 
        // or the index is not in array range 
        // return the original array 
        if (arr == null
            || index < 0
            || index >= arr.length) { 
  
            return arr; 
        } 
  
        // Create another array of size one less 
        Words[] anotherArray = new Words[arr.length - 1]; 
  
        // Copy the elements except the index 
        // from original array to the other array 
        for (int i = 0, k = 0; i < arr.length; i++) { 
  
            // if the index is 
            // the removal element index 
            if (i == index) { 
                continue; 
            } 
  
            // if the index is not 
            // the removal element index 
            anotherArray[k++] = arr[i]; 
        } 
  
        // return the resultant array 
        return anotherArray; 
    } 

    private static void addWordToDict(Scene scene, Stage stage) {
        ArrayList<String> definitionStrings = new ArrayList<String>();
        ArrayList<String> partOfSpeechStrings = new ArrayList<String>();
        ArrayList<String> synonyms = new ArrayList<String>();
        ArrayList<String> antonyms = new ArrayList<String>();
        ScrollPane addScrollInternal = (ScrollPane) scene.lookup("#addScroll");
        VBox addDSectionInternal = (VBox) addScrollInternal.getContent().lookup("#addDSection");
        VBox addSynSectionInternal = (VBox) addScrollInternal.getContent().lookup("#addSynSection");
        VBox addAntSectionInternal = (VBox) addScrollInternal.getContent().lookup("#addAntSection");
        TextField wordInputInternal = (TextField) addScrollInternal.getContent().lookup("#wordInput");
        String word = wordInputInternal.getText();
        boolean whitespace = false;
        whitespace = (word.contains(" ")) ? true : false;
        ObservableList<Node> defArr = addDSectionInternal.getChildren();
        for (Node n : defArr) {
            if (n instanceof VBox) {
                VBox container = (VBox) n;
                ObservableList<Node> array = container.getChildren();
                TextField container2 = (TextField) array.get(0);
                String text = container2.getText();
                whitespace = text.contains(" ") || text.matches("\\s*") ? true : false;
                definitionStrings.add(text);
                TextField container3 = (TextField) array.get(1);
                String text2 = container3.getText();
                whitespace = text.matches("\\s*") ? true : false;
                partOfSpeechStrings.add(text2);
            }
        }
        ObservableList<Node> synArr = addSynSectionInternal.getChildren();
        for (Node n : synArr) {
            if (n instanceof VBox) {
                VBox container = (VBox) n;
                ObservableList<Node> array = container.getChildren();
                if (array.get(0) instanceof TextField) {
                    TextField container2 = (TextField) array.get(0);
                    String text = container2.getText();
                    synonyms.add(text);
                }
            }
        }
        ObservableList<Node> antArr = addAntSectionInternal.getChildren();
        for (Node n : antArr) {
            if (n instanceof VBox) {
                VBox container = (VBox) n;
                ObservableList<Node> array = container.getChildren();
                if (array.get(0) instanceof TextField) {
                    TextField container2 = (TextField) array.get(0);
                    String text = container2.getText();
                    antonyms.add(text);
                }
            }
        }

        if (whitespace) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Something was incorrect, please try again.");
            alert.show();
        }else {
            Object[] definitions =  definitionStrings.toArray();
            Object[] partOfSpeeches = partOfSpeechStrings.toArray();
            Definitions[] defProper = new Definitions[definitions.length];
            Object[] synonymArray = synonyms.toArray();
            Object[] antonymArray = antonyms.toArray();
            if (definitions.length == partOfSpeeches.length) {
                for (int j = 0; j < definitions.length; j++)  {
                    defProper[j] = new Definitions((String) definitions[j], (String) partOfSpeeches[j]);
                }
            }
            if (synonymArray[0].equals("")) {
                synonymArray = new String[0];
            }
            if (antonymArray[0].equals("")) {
                antonymArray = new String[0];
            }

            Words newWord = new Words(word, defProper, Arrays.copyOf(synonymArray, synonymArray.length, String[].class), Arrays.copyOf(antonymArray, antonymArray.length, String[].class));
            try {
                Words[] innerWords = getWords();
                innerWords = saveWordJson(innerWords, newWord);
                updateWords(innerWords, scene);
                leftColumnListeners(scene, stage, 0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } 
    }

    private static void updateWords(Words[] words, Scene scene) {
        words = sortWordsAscending(words, 0);
        ListView<String> wordHousing = (ListView<String>) scene.lookup("#wordHousing");
        List<String> wordStrings = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            wordStrings.add(words[i].getWord());
        }
        ObservableList<String> wordObserv = FXCollections.observableArrayList(wordStrings);
        FilteredList<String> filteredWords = new FilteredList<String>(wordObserv, s -> true);
        wordHousing.setItems(filteredWords);
        
    }

    public static VBox createDefSpeechPairInput() {
        VBox defSpeechPair = new VBox();
        defSpeechPair.setSpacing(10);
        defSpeechPair.getStyleClass().add("defSpeechPair");
        TextField definition = new TextField();
        definition.setPromptText("Enter definition here.");
        definition.getStyleClass().add("definitionInput");
        TextField partOfSpeech = new TextField();
        partOfSpeech.getStyleClass().add("partOfSpeechInput");
        partOfSpeech.setPromptText("Enter part of speech here.");

        defSpeechPair.getChildren().addAll(definition, partOfSpeech);

        return defSpeechPair;
    }

    public static VBox createSynonymInput() {
        VBox synonymInput = new VBox();
        synonymInput.setSpacing(10);
        synonymInput.getStyleClass().add("synonymInput");
        TextField synonym = new TextField();
        synonym.setPromptText("Enter synonym here.");
        synonymInput.getChildren().addAll(synonym);
        return synonymInput;
    }

    public static VBox createAntonymInput() {
        VBox antonymInput = new VBox();
        antonymInput.setSpacing(10);
        antonymInput.getStyleClass().add("antonymInput");
        TextField antonym = new TextField();
        antonym.setPromptText("Enter antonym here.");
        antonymInput.getChildren().addAll(antonym);
        return antonymInput;
    }

    
}