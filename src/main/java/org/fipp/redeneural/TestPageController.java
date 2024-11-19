package org.fipp.redeneural;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fipp.redeneural.entidades.RedeNeural;
import org.fipp.redeneural.entidades.Util;

import java.io.*;
import java.net.URL;
import java.util.*;

public class TestPageController implements Initializable {
    public TableView tableViewTeste;
    public Button bttIniciarTournament;
    public TextField caminho_arquivo;
    public Button bttTeste;
    private double[] vetMaior, vetMenor;//utilizado para normalizar os valores das colunas
    private List<String> listaClasses;
    private RedeNeural redeNeural;
    private MainPageController mainPageController;
    private String caminho;
    private int porcentagem;
    private double tableWidth;
    private ObservableList<ObservableList<String>> dataTeste;

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;

        redeNeural = mainPageController.getRedeNeural();
        porcentagem = mainPageController.getPorcentagem();
        vetMaior = mainPageController.getVetMaior();
        vetMenor = mainPageController.getVetMenor();
        if(porcentagem!=100){
            tableWidth = mainPageController.getWidth();
            bttTeste.setVisible(false);
            bttTeste.setDisable(true);
            caminho_arquivo.setText(mainPageController.getCaminhoTreino());
            caminho_arquivo.setDisable(true);
            caminho = mainPageController.getCaminhoTeste();
            dataTeste = mainPageController.getDataTeste();
            loadTable(dataTeste,tableViewTeste);
        }
        else{
            bttTeste.setVisible(true);
            bttTeste.setDisable(false);
            caminho_arquivo.setDisable(false);
            caminho = mainPageController.getCaminhoTeste();
            if(caminho != null){
                File selectedFile = new File(caminho);
                carregarTabela(selectedFile);
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onChooseFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Downloads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        carregarTabela(selectedFile);
    }

    private void carregarTabela(File selectedFile){
        if (selectedFile != null) {
            loadCSVFile(selectedFile, tableViewTeste);
        }
        caminho_arquivo.setText(selectedFile.getAbsolutePath());
    }

    private void loadTable(ObservableList<ObservableList<String>> dataTeste, TableView<ObservableList<String>> tableView) {
        String[] headers = mainPageController.getHeaders();
        createColumns(headers, tableView);

        tableView.setItems(dataTeste);
        ajustaLarguraColunas2(tableView);
        normalizarTabela(tableView);
    }
    private void ajustaLarguraColunas2(TableView<ObservableList<String>> tableView) {
        int columnCount = tableView.getColumns().size();
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            column.setPrefWidth(tableWidth / columnCount);
        }
    }

    private void loadCSVFile(File file, TableView<ObservableList<String>> tableView) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                String[] headers = line.split(",");
                createColumns(headers, tableView);

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    ObservableList<String> row = FXCollections.observableArrayList(fields);
                    data.add(row);
                }
                tableView.setItems(data);
                ajustaLarguraColunas(tableView);
                normalizarTabela(tableView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void normalizarTabela(TableView<ObservableList<String>> tableView) {
        normalizarDadosVetor(tableView);
        normalizarClasse(tableView);
    }

    public void normalizarDadosVetor(TableView<ObservableList<String>> tableView) {
        //buscaVetorMaiorMenorValorTabela(tableView);
        for (ObservableList<String> row : tableView.getItems()) {
            for (int col = 0; col < row.size(); col++) {
                String cellData = row.get(col);
                if (cellData.matches("-?\\d+(\\.\\d+)?")) {
                    double valor = Double.parseDouble(cellData);
                    double valorNormalizado = (valor - vetMenor[col]) / (vetMaior[col] - vetMenor[col]);
                    row.set(col, String.format(Locale.US, "%.4f", valorNormalizado));//Locale.US para usar ponto
                }
            }
        }
    }

    public void normalizarClasse(TableView<ObservableList<String>> tableView) {
        Set<String> classSet = new HashSet<>();
        List<String> classValues = new ArrayList<>();

        for (ObservableList<String> row : tableView.getItems()) {
            String cellData = row.get(row.size() - 1);
            if (classSet.add(cellData)) {
                classValues.add(cellData);
            }
        }
        setListaClasses(classValues);

        for (ObservableList<String> row : tableView.getItems()) {
            String cellData = row.get(row.size() - 1);
            int newValue = classValues.indexOf(cellData) + 1;
            row.set(row.size() - 1, String.valueOf(newValue));
        }
    }
    /*
    private void buscaVetorMaiorMenorValorTabela(TableView<ObservableList<String>> tableView) {
        int columnCount = tableView.getColumns().size();
        double[] maioresValores = new double[columnCount];
        double[] menoresValores = new double[columnCount];

        //Atribuindo os primeiros valores
        ObservableList<String> firstRow = tableView.getItems().get(0);
        for (int col = 0; col < columnCount; col++) {
            String cellData = firstRow.get(col);
            if (cellData.matches("-?\\d+(\\.\\d+)?")) {
                double valor = Double.parseDouble(cellData);
                maioresValores[col] = valor;
                menoresValores[col] = valor;
            }
        }

        for (ObservableList<String> row : tableView.getItems()) {
            for (int col = 0; col < columnCount; col++) {
                String cellData = row.get(col);
                if (cellData.matches("-?\\d+(\\.\\d+)?")) {//somente se for numero
                    double valor = Double.parseDouble(cellData);
                    if (valor > maioresValores[col]) {
                        maioresValores[col] = valor;
                    }
                    if (valor < menoresValores[col]) {
                        menoresValores[col] = valor;
                    }
                }
            }
        }
        vetMaior = maioresValores;
        vetMenor = menoresValores;
        System.out.println("\n\n\n");
        for (int col = 0; col < columnCount; col++) {
            System.out.println("Coluna " + col + " - Maior: " + maioresValores[col] + " Menor: " + menoresValores[col]);
        }
    }
     */

    private void createColumns(String[] headers, TableView<ObservableList<String>> tableView) {
        tableView.getColumns().clear();
        for (int i = 0; i < headers.length; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
            final int colIndex = i;
            column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
            tableView.getColumns().add(column);
        }
    }

    private void ajustaLarguraColunas(TableView<ObservableList<String>> tableView) {
        double tableWidth = tableView.getWidth();
        int columnCount = tableView.getColumns().size();
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            column.setPrefWidth(tableWidth / columnCount);
        }
    }
    public void setListaClasses(List<String> listaClasses) {
        this.listaClasses = listaClasses;
    }

    public void onIniciarTreinamentoClick(ActionEvent actionEvent) {
        if (redeNeural == null) {
            Util.exibirMensagem("Erro", "Rede Neural não foi treinada!", Alert.AlertType.ERROR);
        }
        else{
            redeNeural.testar(tableViewTeste);
            mainPageController.setCaminhoTeste(caminho_arquivo.getText());
            mainPageController.matrizConfusao();
        }
    }

    public void btnReloadtable(ActionEvent actionEvent) {
        carregarTabela(new File(caminho));
    }

}
