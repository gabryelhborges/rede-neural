package org.fipp.redeneural;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fipp.redeneural.entidades.RedeNeural;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    public static BorderPane staticpane;
    public BorderPane BorderPane_MainPage;
    public Button btnTreining;
    public Button btnTest;

    private RedeNeural redeNeural;

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho;


    public RedeNeural getRedeNeural() {
        return redeNeural;
    }

    public void setRedeNeural(RedeNeural redeNeural) {
        this.redeNeural = redeNeural;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        staticpane = BorderPane_MainPage;
        if(redeNeural==null){
            loadPage("presentation_page.fxml");
        }
    }

    // Função genérica para carregar uma página e substituir o conteúdo do VBox
    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFileName));
            VBox page = loader.load();

            if ("training_page.fxml".equals(fxmlFileName)) {
                TrainingPageController trainingController = loader.getController();
                trainingController.setMainPageController(this); // Passa a si próprio (this)
            }

            staticpane.setCenter(page);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a página: " + fxmlFileName, e);
        }
    }
    public void onTrainig(ActionEvent actionEvent) {
        loadPage("training_page.fxml");
    }

    public void onTest(ActionEvent actionEvent) {
        loadPage("test_page.fxml");
    }
    public void teste(){
        loadPage("test_page.fxml");
    }
}
