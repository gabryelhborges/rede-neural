package org.fipp.redeneural;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.fipp.redeneural.MainPageController;
import org.fipp.redeneural.entidades.Neuronio;
import org.fipp.redeneural.entidades.RedeNeural;
import java.util.ArrayList;
import java.util.List;

public class RedeNeuralPageController {
    public VBox vboxMain;
    private MainPageController mainPageController;

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
        RedeNeural redeNeural = mainPageController.getRedeNeural();
        if(redeNeural != null){
            DesenharNeuronio(redeNeural);
        }
    }

    public void DesenharNeuronio(RedeNeural redeNeural){
        vboxMain.getChildren().clear(); // Limpa o conteúdo anterior

        // Painel principal onde desenhamos os neurônios e as conexões
        Pane pane = new Pane();

        // Obtém cada camada de neurônios da rede neural
        List<Neuronio> neuroniosEntrada = redeNeural.getNeuroniosEntrada();
        List<Neuronio> neuroniosOcultos = redeNeural.getNeuroniosOcultos();
        List<Neuronio> neuroniosSaida = redeNeural.getNeuroniosSaida();

        // Cria visualmente cada camada de neurônios e armazena os círculos para conectar
        List<Circle> circulosEntrada = criarCamadaVisual(pane, neuroniosEntrada, "Entrada", 100);
        List<Circle> circulosOculta = criarCamadaVisual(pane, neuroniosOcultos, "Oculta", 300);
        List<Circle> circulosSaida = criarCamadaVisual(pane, neuroniosSaida, "Saída", 500);

        // Desenha as conexões entre os neurônios das camadas
        desenharConexoes(pane, circulosEntrada, circulosOculta);
        desenharConexoes(pane, circulosOculta, circulosSaida);

        vboxMain.getChildren().add(pane);
    }

    private List<Circle> criarCamadaVisual(Pane pane, List<Neuronio> neuronios, String tipoCamada, double xPosition) {
        VBox vboxCamada = new VBox(10); // Espaçamento entre os neurônios
        vboxCamada.setAlignment(Pos.CENTER);

        // Posição inicial para cada camada
        List<Circle> circulos = new ArrayList<>();

        // Desenha cada neurônio da camada
        double yPosition = 100;
        for (Neuronio neuronio : neuronios) {
            Circle circuloNeuronio = new Circle(xPosition, yPosition, 20, Color.LIGHTBLUE); // Neurônio visual
            pane.getChildren().add(circuloNeuronio);
            circulos.add(circuloNeuronio);
            yPosition += 50; // Ajuste a posição vertical para o próximo neurônio
        }

        // Adiciona um título para a camada
        Text camadaTitulo = new Text(xPosition - 20, 50, tipoCamada);
        pane.getChildren().add(camadaTitulo);

        return circulos;
    }

    private void desenharConexoes(Pane pane, List<Circle> camadaAnterior, List<Circle> camadaProxima) {
        for (Circle neuronioAnterior : camadaAnterior) {
            for (Circle neuronioProximo : camadaProxima) {
                Line linha = new Line();
                linha.setStartX(neuronioAnterior.getCenterX());
                linha.setStartY(neuronioAnterior.getCenterY());
                linha.setEndX(neuronioProximo.getCenterX());
                linha.setEndY(neuronioProximo.getCenterY());
                linha.setStroke(Color.WHITE);
                pane.getChildren().add(linha);
                linha.toBack();
            }
        }
    }
}
