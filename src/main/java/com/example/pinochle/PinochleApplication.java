package com.example.pinochle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public class PinochleApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PinochleApplication.class.getResource("Pinochle.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Pinochle Game");
        stage.setScene(scene);
        stage.show();

        SizeChangeListener sizeListener = new SizeChangeListener(scene, scene.getWidth(), scene.getHeight());
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
    }

    public static void main(String[] args) {
        launch();
    }

    private record SizeChangeListener(Scene scene, double width, double height) implements ChangeListener<Number> {
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            scene.getRoot().getTransforms().setAll(new Scale(scene.getWidth() / width, scene.getHeight() / height, 0, 0));
        }
    }
}