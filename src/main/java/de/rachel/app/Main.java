package de.rachel.app;

import java.io.IOException;
import java.util.Locale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage loginStage) throws IOException{
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));

      Scene loginScene = new Scene(fxmlLoader.load());

      loginStage.setResizable(false);
      loginStage.setTitle("Login to BigOne");
      loginStage.setScene(loginScene);
      loginStage.show();
    }

    public static void main(String[] args) {
      Locale.setDefault(Locale.GERMAN);
      launch(args);
    }
}
