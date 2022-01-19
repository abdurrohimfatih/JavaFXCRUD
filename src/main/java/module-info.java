module com.abdurrohim.javafxdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.abdurrohim.javafxdb to javafx.fxml;
    exports com.abdurrohim.javafxdb;
    exports com.abdurrohim.javafxdb.controllers;
    opens com.abdurrohim.javafxdb.controllers to javafx.fxml;
}