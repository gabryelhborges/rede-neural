package org.fipp.redeneural;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fipp.redeneural.entidades.RedeNeural;

import java.util.List;

import static org.fipp.redeneural.entidades.Util.exibirMensagem;

public class MatrizConfusaoPageController {

    public VBox vboxMain;
    private MainPageController mainPageController;

    private RedeNeural redeNeural;

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
        redeNeural = mainPageController.getRedeNeural();
        if(redeNeural != null){
            matriz(redeNeural.getConfusionMatrix());
            Exibir(redeNeural.getAcuracia(),redeNeural.getListaClasses());
        }
    }
    private void Exibir(List<Double> Acuracia, List<String> classes) {
        if (Acuracia.isEmpty() && classes.isEmpty()) {
            exibirMensagem("Erro", "Dados inválidos fornecidos para exibição.", Alert.AlertType.ERROR);
        }else{
                // Construção da mensagem de acurácia
                StringBuilder acuracias = new StringBuilder();
                acuracias.append("Acurácia Global: ").append(Acuracia.remove(0)+"%").append("\n");

                // Iteração pelas listas
                for (int i = 0; i < classes.size(); i++) {
                    acuracias.append("Acurácia da classe ").append(classes.get(i))
                            .append(": ").append(Acuracia.get(i)+"%").append("\n");
                }

                // Exibe a mensagem formatada
                exibirMensagem("Acurácia", acuracias.toString(), Alert.AlertType.INFORMATION);
        }
    }

    private void matriz(int[][] matrizConfusao) {
        // Define dimensões das células e do Canvas
        double cellWidth = 80;
        double cellHeight = 40;
        double startX = 10;
        double startY = 10;
        double canvasWidth = (matrizConfusao[0].length + 1) * cellWidth + startX * 2;
        double canvasHeight = (matrizConfusao.length + 1) * cellHeight + startY * 2;

        // Cria o Canvas
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Define a cor do fundo do Canvas
        gc.setFill(Color.LIGHTGRAY); // Cor do fundo do Canvas
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Obtém a lista de classes
        List<String> listaClasses = mainPageController.getRedeNeural().getListaClasses();

        // Desenha a matriz de confusão no Canvas
        for (int i = 0; i <= matrizConfusao.length; i++) {
            for (int j = 0; j <= matrizConfusao[0].length; j++) {
                // Calcula a posição da célula
                double x = startX + j * cellWidth;
                double y = startY + i * cellHeight;

                if (i == 0 || j == 0) {
                    // Células de cabeçalho
                    gc.setFill(Color.DARKGRAY); // Cor de fundo do cabeçalho
                    gc.fillRect(x, y, cellWidth, cellHeight);
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(x, y, cellWidth, cellHeight);

                    // Adiciona os textos do cabeçalho
                    if (i == 0 && j > 0) {
                        // Cabeçalho da primeira linha
                        gc.setFill(Color.BLACK);
                        gc.fillText(listaClasses.get(j - 1), x + 10, y + cellHeight / 2);
                    } else if (j == 0 && i > 0) {
                        // Cabeçalho da primeira coluna
                        gc.setFill(Color.BLACK);
                        gc.fillText(listaClasses.get(i - 1), x + 10, y + cellHeight / 2);
                    }
                } else {
                    // Células internas
                    gc.setFill(Color.WHITE); // Cor de fundo da célula
                    gc.fillRect(x, y, cellWidth, cellHeight);

                    // Desenha a borda da célula
                    gc.setStroke(Color.BLACK); // Cor da borda
                    gc.strokeRect(x, y, cellWidth, cellHeight);

                    // Desenha o valor dentro da célula
                    gc.setFill(Color.BLACK); // Cor do texto
                    gc.fillText(String.valueOf(matrizConfusao[i - 1][j - 1]), x + 10, y + cellHeight / 2);
                }
            }
        }

        // Limpa o VBox antes de adicionar o Canvas
        vboxMain.getChildren().clear();
        // Adiciona o Canvas ao VBox
        vboxMain.getChildren().add(canvas);
    }


}
