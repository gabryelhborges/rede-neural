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
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("presentation_page.fxml"));
            loadPage(loader);
        }
    }

    // Função genérica para carregar uma página e substituir o conteúdo do VBox
    private void loadPage(FXMLLoader loader) {
        try {
            VBox page = loader.load();
            staticpane.setCenter(page);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a página: " +  e);
        }
    }
    public void onTrainig(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("training_page.fxml"));
        loadPage(loader);
        TrainingPageController trainingController = loader.getController();
        trainingController.setMainPageController(this);
    }

    public void onTest(ActionEvent actionEvent) {
        teste();
    }
    public void teste(){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("test_page.fxml"));
        loadPage(loader);
        TestPageController testController = loader.getController();
        testController.setMainPageController(this);
    }
}
