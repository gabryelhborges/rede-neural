package org.fipp.redeneural.entidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.fipp.redeneural.HelloApplication;

import java.util.List;
import java.util.Optional;

public class Util {
    public static void exibirMensagem(String titulo, String conteudo, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    public static boolean exibirMensagemConfirmacao(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static double solicitarNovaTaxaAprendizagem(double taxaAtual) {
        TextInputDialog dialog = new TextInputDialog(Double.toString(taxaAtual));
        dialog.setTitle("NOVA TAXA DE APRENDIZAGEM");
        dialog.setHeaderText("Alterar Taxa de Aprendizagem");
        dialog.setContentText("Por favor, insira a nova taxa de aprendizagem:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double novaTaxa = Double.parseDouble(result.get());
                if(novaTaxa <= 0){
                    novaTaxa = 0.2;
                }
                if(novaTaxa > 1){
                    novaTaxa = 1;
                }
                return novaTaxa;
            } catch (NumberFormatException e) {
                exibirMensagem("Erro", "Valor inválido para a taxa de aprendizagem.", Alert.AlertType.ERROR);
            }
        }
        return taxaAtual;
    }

    public static void exibirGraficoErros(List<Double> errosRede) {
        // Criar os eixos X e Y para o gráfico
        NumberAxis eixoX = new NumberAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoX.setLabel("Épocas");
        eixoY.setLabel("Erro da Rede");

        // Criar o gráfico
        LineChart<Number, Number> grafico = new LineChart<>(eixoX, eixoY);
        grafico.setTitle("Evolução do Erro da Rede Neural");

        // Adicionar os dados ao gráfico
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Erro por Época");

        // Adicionar os dados de erro, um para cada época
        for (int i = 0; i < errosRede.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, errosRede.get(i))); // i+1 para representar a época 1, 2, 3, ...
        }

        // Adicionar a série ao gráfico
        grafico.getData().add(series);

        // Remover as bolinhas
        grafico.setCreateSymbols(false);

        // Configurar a cor da linha para cinza escuro
        series.getNode().setStyle("-fx-stroke: #444444; -fx-stroke-width: 2;");
        grafico.setLegendVisible(false);
        // Criar a janela para exibir o gráfico
        Stage stage = new Stage();
        stage.setTitle("Gráfico de Erros");
        stage.setScene(new javafx.scene.Scene(grafico, 400, 300));
        stage.show();
    }

}
