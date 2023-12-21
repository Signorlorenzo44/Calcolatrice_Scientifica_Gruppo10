module com.example.calculatorapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.scripting;
    requires commons.math3;
    requires java.desktop;


    opens com.example.calculatorapp to javafx.fxml;
    exports com.example.calculatorapp;
}