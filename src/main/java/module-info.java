module com.example.roborally {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;


    opens client to javafx.fxml;
    opens server to com.google.gson; // updated this
    exports client;
    opens server.message to com.google.gson;
}