module com.project.libraryfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires dotenv.java;
    requires gson;

    opens com.project.libraryfx.entities to javafx.base;

    opens com.project.libraryfx to javafx.fxml;
    exports com.project.libraryfx;
    exports com.project.libraryfx.entities;
    exports com.project.libraryfx.controller;
    opens com.project.libraryfx.controller to javafx.fxml;
}

