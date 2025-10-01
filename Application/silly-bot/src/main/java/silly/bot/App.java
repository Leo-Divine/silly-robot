package silly.bot;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    Editor canvas = new Editor(250,250);

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = new Pane();
        
        canvas.drawBackground(10, 10);
        canvas.drawBlocks();
        root.getChildren().add(canvas);

        stage.setTitle("silly-bot-ide");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}