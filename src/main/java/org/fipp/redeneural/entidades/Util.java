package org.fipp.redeneural.entidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.fipp.redeneural.HelloApplication;

import java.util.List;

public class Util {
    public static void exibirMensagem(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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

        // Criar a janela para exibir o gráfico
        Stage stage = new Stage();
        stage.setTitle("Gráfico de Erros");
        stage.setScene(new javafx.scene.Scene(grafico, 800, 600));
        stage.show();
    }
}
