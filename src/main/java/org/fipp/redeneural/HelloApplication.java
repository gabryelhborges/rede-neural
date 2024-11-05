package org.fipp.redeneural;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start_page.fxml"));
        Scene startPage = new Scene(fxmlLoader.load());

        // Configura a janela em tela cheia inicialmente
        stage.setScene(startPage);
        stage.setFullScreen(true);
        stage.show();

        // Adiciona um listener para detectar a tecla Enter pressionada
        startPage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    // Carrega novamente o FXML, caso necessário, para a nova cena
                    FXMLLoader newLoader = new FXMLLoader(HelloApplication.class.getResource("main_page.fxml"));
                    Scene newScene = new Scene(newLoader.load());

                    // Define o título e a nova cena
                    stage.setTitle("Rede Neural!");
                    stage.setScene(newScene);
                    stage.setFullScreen(true);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // o gay esteve aqui
    public static void main(String[] args) {
        launch();
    }
}