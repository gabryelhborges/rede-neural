package org.fipp.redeneural;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.fipp.redeneural.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    public VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadInitialContent();
        vbox.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    // Adiciona o EventHandler para substituir o conteúdo ao pressionar Enter
                    newScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                        if (event.getCode().toString().equals("ENTER")) {
                            replaceContent();
                        }
                    });
                }
            }
        });
    }

    private void loadInitialContent() {
        try {
            FXMLLoader presentation = new FXMLLoader(HelloApplication.class.getResource("presentation_page.fxml"));
            presentation.load();
            vbox.getChildren().clear();
            vbox.getChildren().add(presentation.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceContent() {
        try {
            FXMLLoader presentation = new FXMLLoader(MainPageController.class.getResource("training_page.fxml"));
            presentation.load();
            vbox.getChildren().clear();
            vbox.getChildren().add(presentation.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
