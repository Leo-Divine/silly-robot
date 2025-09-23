package silly.bot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Pane root = new Pane();

        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}