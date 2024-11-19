package org.fipp.redeneural;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fipp.redeneural.entidades.RedeNeural;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    public static BorderPane staticpane;
    public BorderPane BorderPane_MainPage;
    public Button btnTreining;
    public Button btnTest;
    public Text textOrientador;
    public Button btnVizualizarRedeNeural;

    private RedeNeural redeNeural;
    public String caminhoTreino;
    public String caminhoTeste;

    private String n;
    private String number_interacoes;
    private String valor_erro;
    private String number_oculta;
    private String fucaoTransferencia;
    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getNumber_interacoes() {
        return number_interacoes;
    }

    public void setNumber_interacoes(String number_interacoes) {
        this.number_interacoes = number_interacoes;
    }

    public String getValor_erro() {
        return valor_erro;
    }

    public void setValor_erro(String valor_erro) {
        this.valor_erro = valor_erro;
    }

    public String getNumber_oculta() {
        return number_oculta;
    }

    public void setNumber_oculta(String number_oculta) {
        this.number_oculta = number_oculta;
    }

    public String getFucaoTransferencia() {
        return fucaoTransferencia;
    }

    public void setFucaoTransferencia(String fucaoTransferencia) {
        this.fucaoTransferencia = fucaoTransferencia;
    }

    public String getCaminhoTreino() {
        return caminhoTreino;
    }

    public void setCaminhoTreino(String caminhoTreino) {
        this.caminhoTreino = caminhoTreino;
    }

    public String getCaminhoTeste() {
        return caminhoTeste;
    }

    public void setCaminhoTeste(String caminhoTeste) {
        this.caminhoTeste = caminhoTeste;
    }

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
        textOrientador.setText("TREINAMENTO");
    }

    public void onTest(ActionEvent actionEvent) {
        teste();
    }
    public void teste(){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("test_page.fxml"));
        loadPage(loader);
        TestPageController testController = loader.getController();
        testController.setMainPageController(this);
        textOrientador.setText("TESTE");
    }

    public void onVizualizarRedeNeutal(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("rede_neural_page.fxml"));
        loadPage(loader);
        RedeNeuralPageController redeNeuralPageController = loader.getController();
        redeNeuralPageController.setMainPageController(this);
        textOrientador.setText("REDE NEURAL");
    }

    public void onMatrizConfusao(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("matriz_confusao_page.fxml"));
        loadPage(loader);
        MatrizConfusaoPageController matrizConfusaoPageController = loader.getController();
        matrizConfusaoPageController.setMainPageController(this);
        textOrientador.setText("MATRIZ DE CONFUSÃO");
    }
}
