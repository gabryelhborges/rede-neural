module org.fipp.redeneural {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fipp.redeneural to javafx.fxml;
    exports org.fipp.redeneural;
}