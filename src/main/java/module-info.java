module com.example.roborally {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jfr;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires java.desktop;
    requires org.testng;
    requires java.smartcardio;


    opens client to javafx.fxml;
    // updated this
    exports client;
    opens server.message to com.google.gson;
    exports server;
    opens server to com.google.gson, javafx.fxml;
    exports client.controller;
    opens client.controller to javafx.fxml;
}