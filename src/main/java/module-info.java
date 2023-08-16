module com.example.pinochleagain {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pinochle to javafx.fxml;
    exports com.example.pinochle;
}