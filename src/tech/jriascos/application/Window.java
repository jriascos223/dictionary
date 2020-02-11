package tech.jriascos.application;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Window extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dictionary Application");
        primaryStage.setMaximized(true);
        GridPane grid = new GridPane();
        grid.setId("rootGrid");
        grid.setVgap(10);
        grid.setGridLinesVisible(true);

        HBox buttonHousing = new HBox();
        buttonHousing.setId("buttonHousing");

        VBox leftColumn = new VBox();
        leftColumn.setId("leftColumn");

        Button addButton = new Button("Add");
        addButton.setId("addButton");
        buttonHousing.getChildren().add(addButton);
        
        Button removeButton = new Button("Remove");
        removeButton.setId("removeButton");
        buttonHousing.getChildren().add(removeButton);

        TextField searchbar = new TextField();
        searchbar.setId("searchbar");
        searchbar.setPromptText("Search");

        leftColumn.getChildren().add(buttonHousing);
        leftColumn.getChildren().add(searchbar);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        grid.getColumnConstraints().addAll(col1,col2);

        addButton.prefWidthProperty().bind(Bindings.divide(buttonHousing.widthProperty(), 2.0));
        removeButton.prefWidthProperty().bind(Bindings.divide(buttonHousing.widthProperty(), 2.0));


        grid.add(leftColumn, 0, 0);
        Scene scene = new Scene(grid, 300, 275);
        scene.getStylesheets().add(Window.class.getResource("/window.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    

}
