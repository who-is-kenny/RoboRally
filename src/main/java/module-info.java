module com.example.roborally {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.roborally to javafx.fxml;
    exports com.example.roborally;
}