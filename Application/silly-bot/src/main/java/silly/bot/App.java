package silly.bot;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    Editor canvas = new Editor(2000,2000);

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = new Pane();
        
        canvas.drawBackground();
        canvas.drawMenus();
        root.getChildren().add(canvas);

        stage.setTitle("silly-bot-ide");
        stage.setScene(new Scene(root, 1200, 800));

        stage.getScene().setOnMousePressed(event -> {
            canvas.mousePressed(event.getSceneX(), event.getSceneY());
        });
        stage.getScene().setOnMouseReleased(event -> {
            canvas.mouseReleased(event.getSceneX(), event.getSceneY());
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}