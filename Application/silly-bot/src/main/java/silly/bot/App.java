package silly.bot;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    private Server server;
    private boolean isConnected = false;

    private Pane root = new Pane();
    private Editor canvas = new Editor(2000,2000);

    @Override
    public void start(Stage stage) throws IOException {
        server = new Server();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long arg0) {
                // Draw Editor
                canvas.clearCanvas();
                canvas.drawBackground();
                canvas.drawMenus();
                canvas.drawBlocks();

                // Handle Server Connection and Messages
                /* 
                try { handleServerConnection(); }
                catch (IOException e) { System.out.println("SERVER ERROR: " + e.getMessage()); }
                */
            }
        };
        timer.start();

        // Create UI and Event Handlers
        createInterface(stage);
        createEventHandlers(stage);
        stage.show();
    }

    private void createInterface(Stage stage) {
        root.getChildren().add(canvas);

        stage.setTitle("silly-bot-ide");
        stage.setScene(new Scene(root, 1200, 800));
    }

    private void createEventHandlers(Stage stage) {
        stage.getScene().setOnMouseDragged(event -> {
            canvas.mouseMoved(event.getSceneX(), event.getSceneY());
        });
        stage.getScene().setOnMousePressed(event -> {
            canvas.mousePressed(event.getSceneX(), event.getSceneY());
        });
        stage.getScene().setOnMouseReleased(event -> {
            canvas.mouseReleased(event.getSceneX(), event.getSceneY());
        });
        stage.getScene().setOnMouseClicked(event -> {
            canvas.mouseClicked(event.getSceneX(), event.getSceneY());
        });
        root.setOnScroll(event -> {
            canvas.mouseScroll(event.getSceneX(), event.getDeltaY());
        });
    }

    private void handleServerConnection() throws IOException {
      // Connect if not Already
      if(!isConnected) {
        server.findClient();
        isConnected = true;
      }

      // Check if a Message has Been Recieved
      if(server.isMessageAvailable()) {
        // Restart Connection if NULL Messages are Being Recieved
        if (server.getMessage() == null) {
          server.disconnectClient();
          isConnected = false;
          server.findClient();
          isConnected = true;
          return;
        } 

        // Restart Connection if Client Disconnects
        if(server.readMessage() == ResponseCode.DISCONNECT) {
          server.disconnectClient();
          isConnected = false;
          server.findClient();
          isConnected = true;
        }
      }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Position {
    public double x;
    public double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}