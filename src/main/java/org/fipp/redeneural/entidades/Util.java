package org.fipp.redeneural.entidades;

import javafx.scene.control.Alert;

public class Util {
    public static void exibirMensagem(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
