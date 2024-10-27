package org.fipp.redeneural;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HelloController {
    @FXML
    private TableView<ObservableList<String>> tableView;
    private double[] vetMaior, vetMenor;//utilizado para normalizar os valores das colunas
    private List<String> listaClasses;

    public void onChooseFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Downloads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            loadCSVFile(selectedFile);
        }
    }

    private void loadCSVFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                String[] headers = line.split(",");
                createColumns(headers);

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    ObservableList<String> row = FXCollections.observableArrayList(fields);
                    data.add(row);
                }
                tableView.setItems(data);
                ajustaLarguraColunas();
                normalizarTabela();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void normalizarTabela() {
        normalizarDadosVetor();
        normalizarClasse();
    }

    public void normalizarDadosVetor() {
        buscaVetorMaiorMenorValorTabela();
        for (ObservableList<String> row : tableView.getItems()) {
            for (int col = 0; col < row.size(); col++) {
                String cellData = row.get(col);
                if (cellData.matches("-?\\d+(\\.\\d+)?")) {
                    double valor = Double.parseDouble(cellData);
                    double valorNormalizado = (valor - vetMenor[col]) / (vetMaior[col] - vetMenor[col]);
                    row.set(col, String.format("%.4f", valorNormalizado));
                }
            }
        }
    }

    public void normalizarClasse() {
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

    private void buscaVetorMaiorMenorValorTabela() {
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
        for (int col = 0; col < columnCount; col++) {
            System.out.println("Coluna " + col + " - Maior: " + maioresValores[col] + " Menor: " + menoresValores[col]);
        }
    }

    private void createColumns(String[] headers) {
        tableView.getColumns().clear();
        for (int i = 0; i < headers.length; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
            final int colIndex = i;
            column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
            tableView.getColumns().add(column);
        }
    }

    private void ajustaLarguraColunas() {
        double tableWidth = tableView.getWidth();
        int columnCount = tableView.getColumns().size();
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            column.setPrefWidth(tableWidth / columnCount);
        }
    }

    public List<String> getListaClasses() {
        return listaClasses;
    }

    public void setListaClasses(List<String> listaClasses) {
        this.listaClasses = listaClasses;
    }
}