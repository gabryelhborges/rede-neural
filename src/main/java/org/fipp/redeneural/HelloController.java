package org.fipp.redeneural;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fipp.redeneural.entidades.RedeNeural;
import org.fipp.redeneural.entidades.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HelloController {
    public Button bttIniciarTreinamento;
    public Button bttIniciarTeste;
    public TableView<ObservableList<String>> tableViewTestes;
    @FXML
    private TableView<ObservableList<String>> tableView;//tabela treinamento
    private double[] vetMaior, vetMenor;//utilizado para normalizar os valores das colunas
    private List<String> listaClasses;
    private RedeNeural redeNeural;

    private void criaRedeNeural() {
        //-1 pra nao incluir a coluna da classe
        int qtdeEntrada = tableView.getColumns().size() - 1;
        int qtdeSaida = listaClasses.size();

        //TODO: pegar valores dos campos da tela, se nao houver, enviar valores padrao
        //exemplo padrao: new RedeNeural(qtdeEntrada, qtdeSaida, 0, 0.001, 2000, "linear", 1, true, true);//exemplo pdf

        redeNeural = new RedeNeural(qtdeEntrada, qtdeSaida, 0, 0.00001, 2000, "linear", 1, true, false);
    }

    public void onChooseFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Downloads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            loadCSVFile(selectedFile, tableView);
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
        buscaVetorMaiorMenorValorTabela(tableView);
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
        criaRedeNeural();
        redeNeural.treinar(tableView);
        Util.exibirMensagem("Treinamento", "Treinamento concluído com sucesso!", javafx.scene.control.Alert.AlertType.INFORMATION);
    }

    public void onIniciarTestesClick(ActionEvent actionEvent) {
        if (redeNeural == null) {
            Util.exibirMensagem("Erro", "Rede Neural não foi treinada!", javafx.scene.control.Alert.AlertType.ERROR);
        }
        else{
            redeNeural.testar(tableViewTestes);
        }
    }

    public void onSelecionarArquivoTestes(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Downloads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            loadCSVFile(selectedFile, tableViewTestes);
        }
    }
}