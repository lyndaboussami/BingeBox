module groupna.projectNetflix {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    opens groupna.projectNetflix.controllers to javafx.fxml;

    exports groupna.projectNetflix;
}