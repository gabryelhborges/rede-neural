package org.fipp.redeneural;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fipp.redeneural.entidades.RedeNeural;
import org.fipp.redeneural.entidades.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class TrainingPageController extends MainPageController{
    public Button bttIniciarTreinamento;
    public TextField caminho_arquivo;
    public TextField textField_number_entrada;
    public TextField textField_number_saida;
    public TextField textField_number_oculta;
    public TextField textField_valor_erro;
    public TextField textField_number_interacoes;
    public TextField textField_n;
    public CheckBox checkbox_linear;
    public CheckBox checkbox_logistica;
    public CheckBox checkbox_hiperbolica;
    public CheckBox checkBox_erro;
    public CheckBox checkbox_interact;
    @FXML
    private TableView<ObservableList<String>> tableView;//tabela treinamento
    private double[] vetMaior, vetMenor;//utilizado para normalizar os valores das colunas
    private List<String> listaClasses;
    private String caminho;
    private RedeNeural redeNeural;
    public String funcaTransferencia;
    public boolean criterioParad;
    private MainPageController mainPageController;

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;

        redeNeural = mainPageController.getRedeNeural();
        caminho = mainPageController.getCaminho();
        if(caminho != null){
            textField_number_entrada.setDisable(false);
            textField_number_saida.setDisable(false);
            File selectedFile = new File(caminho);
            carregarTabela(selectedFile);}

    }

    public void setRedeNeural(RedeNeural redeNeural) {
        this.redeNeural = redeNeural;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textField_n.setText("1");
        textField_number_interacoes.setText("2000");
        textField_valor_erro.setText("0.00001");
        textField_number_oculta.setText("8");
        checkbox_linear.setSelected(true);
        funcaTransferencia = "linear";
        checkBox_erro.setSelected(true);
        criterioParad = true;
    }

    private void criaRedeNeural() {
        //-1 pra nao incluir a coluna da classe

        //int qtdeEntrada = Integer.parseInt(textField_number_entrada.getText());
        //int qtdeSaida = listaClasses.size();

        //TODO: pegar valores dos campos da tela, se nao houver, enviar valores padrao
        //exemplo padrao: new RedeNeural(qtdeEntrada, qtdeSaida, 0, 0.001, 2000, "linear", 1, true, true);//exemplo pdf

        redeNeural = new RedeNeural(  Integer.parseInt(textField_number_entrada.getText()),
                Integer.parseInt(textField_number_saida.getText()),
                Integer.parseInt(textField_number_oculta.getText()),
                Double.parseDouble(textField_valor_erro.getText()),
                Integer.parseInt(textField_number_interacoes.getText()),
                funcaTransferencia, Integer.parseInt(textField_n.getText()), true, criterioParad);
    }

    public void onChooseFileButtonClick(ActionEvent actionEvent) {
        textField_number_entrada.setDisable(false);
        textField_number_saida.setDisable(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Downloads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        carregarTabela(selectedFile);
    }

    private void carregarTabela(File selectedFile){
        if (selectedFile != null) {
            loadCSVFile(selectedFile, tableView);
            int qtdeEntrada = tableView.getColumns().size() - 1;
            int qtdeSaida = listaClasses.size();
            textField_number_entrada.setText(String.valueOf(qtdeEntrada));
            textField_number_saida.setText(String.valueOf(qtdeSaida));
            textField_number_entrada.setDisable(true);
            textField_number_saida.setDisable(true);
        }
        caminho_arquivo.setText(selectedFile.getAbsolutePath());
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
        Util.exibirMensagem("Treinamento", "Treinamento concluído com sucesso!", Alert.AlertType.INFORMATION);
        mainPageController.setCaminho(caminho_arquivo.getText());
        mainPageController.setRedeNeural(redeNeural);
    }

    public void onChangeFuncaoTransferencia(ActionEvent actionEvent) {
        if (checkbox_hiperbolica.isSelected()) {
            checkbox_logistica.setSelected(false);
            checkbox_linear.setSelected(false);
            funcaTransferencia = "hiperbolica";
        } else if (checkbox_logistica.isSelected()) {
            checkbox_hiperbolica.setSelected(false);
            checkbox_linear.setSelected(false);
            funcaTransferencia = "logistica";
        } else if (checkbox_linear.isSelected()) {
            checkbox_hiperbolica.setSelected(false);
            checkbox_logistica.setSelected(false);
            funcaTransferencia = "linear";
        }
    }

    public void onChangeCreterioParada(ActionEvent actionEvent) {
        if(checkBox_erro.isSelected()){
            checkbox_interact.setSelected(false);
            criterioParad = false;
        }else{
            checkBox_erro.setSelected(false);
            criterioParad = true;
        }
    }

    public void btnReloadtable(ActionEvent actionEvent) {
        carregarTabela(new File(caminho));
    }
}
