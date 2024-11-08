package org.fipp.redeneural;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main_page.fxml"));
        Scene mainPage = new Scene(fxmlLoader.load());

        stage.setTitle("Rede Neural");
        stage.setScene(mainPage);
        stage.setMaximized(true);

        stage.show();



        // Adiciona um listener para detectar a tecla Enter pressionada
//        mainPage.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                try {
//                    // Carrega novamente o FXML, caso necessário, para a nova cena
//                    FXMLLoader presentation = new FXMLLoader(HelloApplication.class.getResource("presentation_page.fxml"));
//                    presentation.load();
//                    vbox.getChildren().add(presentation.getRoot());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    // o gay esteve aqui
    public static void main(String[] args) {
        launch();
    }
}