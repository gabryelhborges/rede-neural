package org.fipp.redeneural;

import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.Console;
import java.net.URL;
import java.util.ResourceBundle;

public class TestPageController implements Initializable {
    public Text test;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        test.setText("TEste");
    }
}
