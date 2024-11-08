module org.fipp.redeneural {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens org.fipp.redeneural to javafx.fxml;
    exports org.fipp.redeneural;
    exports org.fipp.redeneural.entidades;
    opens org.fipp.redeneural.entidades to javafx.fxml;
}