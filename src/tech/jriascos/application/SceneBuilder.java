package tech.jriascos.application;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class SceneBuilder {
    public static Scene buildDefaultScene() {
        GridPane grid = new GridPane();
        grid.setId("rootGrid");
        grid.setVgap(10);

        Scene defaultScene = new Scene(grid, 300, 275);
        defaultScene.getStylesheets().add(Window.class.getResource("/defaultScene.css").toExternalForm());

        VBox leftColumn = new VBox();
        leftColumn = buildLeftColumn(leftColumn, grid);
        leftColumn.setId("leftColumn");
        grid.add(leftColumn, 0, 0);

        ScrollPane definitionHousing = new ScrollPane();
        definitionHousing = buildDefinitionHousing();
        grid.add(definitionHousing, 1, 0);
        GridPane.setMargin(definitionHousing, new Insets(8, 8, 8, 8));
        definitionHousing.setId("definitionHousing");
        
        return defaultScene;
    }

    private static ScrollPane buildDefinitionHousing() {
        ScrollPane definitionHousing = new ScrollPane();
        return definitionHousing;
    }

    private static VBox buildLeftColumn(VBox leftColumn, GridPane grid) {
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
        CheckBox desc = new CheckBox("Desc");
        

        HBox checkboxHousing = new HBox();
        checkboxHousing.setId("checkboxHousing");
        checkboxHousing.getChildren().addAll(asc, desc);

        ScrollPane wordHousing = new ScrollPane();
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

        VBox.setVgrow(wordHousing, Priority.ALWAYS);
        wordHousing.setMaxHeight(Double.MAX_VALUE);

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


        asc.setPadding(new Insets(0, 0, 0, 30));
        desc.setPadding(new Insets(0, 0, 0, 30));

        return leftColumn;
    }
}