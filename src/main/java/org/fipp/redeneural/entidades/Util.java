package org.fipp.redeneural.entidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import org.fipp.redeneural.HelloApplication;

public class Util {
    public static void exibirMensagem(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
