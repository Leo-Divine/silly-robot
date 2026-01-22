package silly.bot;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

enum PopupType {
    FINDING_CLIENT,
    PROGRAM_RUNNING;
}

public class App extends Application {
    private Server server;
    private boolean isConnected = false;
    private boolean isFindingClient = false;
    private Thread findClient;
    private Thread runCode;

    private Color leftColor = Color.RED;
    private Color rightColor = Color.RED;

    private Pane root = new Pane();
    public Editor canvas = new Editor(2000,2000);

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
                canvas.drawStartButton(stage.getScene().getWidth());

                if(isFindingClient) { canvas.drawPopup(PopupType.FINDING_CLIENT); }
                if(canvas.getIsProgramRunning()) { canvas.drawPopup(PopupType.PROGRAM_RUNNING); }

                // Handle Server Connection and Messages
                try { handleServerConnection(); }
                catch (IOException e) { System.out.println("SERVER ERROR: " + e.getMessage()); }
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
        stage.getScene().setOnKeyPressed(event -> {
            canvas.keyPressed(event);
        });
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

            if(canvas.hasProgramStarted(event.getSceneX(), event.getSceneY(), stage.getScene().getWidth())) {
                if(canvas.getIsProgramRunning() || !isConnected) { return; }
                canvas.setIsProgramRunning(true);
                runCode = new Thread(new RunCodeTask(this), "Run-Code");
                runCode.start();
            }
        });
        root.setOnScroll(event -> {
            canvas.mouseScroll(event.getSceneX(), event.getDeltaY());
        });
    }

    private void handleServerConnection() throws IOException {
        if(!findClient()) {
            return;
        }

        // Check if a Message has Been Recieved
        if(server.isMessageAvailable()) {
            // Restart Connection if NULL Messages are Being Recieved or if Client Disconnects
            String message = server.getMessage();
            if (message == null || message.equals("FCKOFF")) {
              server.disconnectClient();
              isConnected = false;
              findClient();
            }
        }

        if(canvas.getIsProgramRunning() && !runCode.isAlive()) { canvas.setIsProgramRunning(false); }
    }

    private boolean findClient() throws IOException {
        if(!isConnected && !isFindingClient) {
            findClient = new Thread(new FindClientTask(server), "Find-Client");
            findClient.start();
            isFindingClient = true;
            return false;
        } else if (isFindingClient) {
            if(!findClient.isAlive()) {
                isFindingClient = false;
                isConnected = true;
                return true;
            }
            return false;
        }
        return true;
    }

    public String runBlockCode(Block block) {
        if(block.blockType == BlockType.Start && block.belowBlock != null) {
            runBlockCode(block.belowBlock);
            return "";
        }

        // Handle Parameters
        String[] parameters = new String[3];
        for(int i = 0; i < block.parameters.length; i++) {
            Parameter param = block.parameters[i];

            // If There's a Value Block Use It's Value, Otherwise Get The Normal Value
            if(param.childBlock != null) {
                parameters[i] = runBlockCode(param.childBlock);
            } else if(param.value == null) {
                parameters[i] = "0";
            } else if(param.value.getClass() == Integer.class) {
                parameters[i] = String.format("%03d", param.value);
            } else if(param.value.getClass() == Color.class) {
                parameters[i] = colorToString((Color)param.value);
            } else if(param.value.getClass() == Notes.class) {
                parameters[i] = String.format("%04d", ((Notes)param.value).frequency);
            }
        }

        try {
            switch(block.blockType) {
                case MoveForward: server.sendCommand(RobotCommand.MOVE_FORWARD, parameters); server.getMessage(); break;
                case RotateLeft: server.sendCommand(RobotCommand.ROTATE_LEFT, parameters); server.getMessage(); break;
                case RotateRight: server.sendCommand(RobotCommand.ROTATE_RIGHT, parameters); server.getMessage(); break;
                case Wait: server.sendCommand(RobotCommand.WAIT, parameters); server.getMessage(); break;
                case PlayNote: server.sendCommand(RobotCommand.PLAY_NOTE, parameters); server.getMessage(); break;
                case StopPlaying: server.sendCommand(RobotCommand.STOP_PLAYING, parameters); server.getMessage(); break;
                case SetColor: 
                leftColor = (Color)block.parameters[0].value; rightColor = (Color)block.parameters[0].value; server.sendCommand(RobotCommand.SET_COLOR, new String[]{colorToString(leftColor), colorToString(rightColor)}); server.getMessage(); break;
                case SetLeftColor: leftColor = (Color)block.parameters[0].value; server.sendCommand(RobotCommand.SET_COLOR, new String[]{colorToString(leftColor), colorToString(rightColor)}); server.getMessage(); break;
                case SetRightColor: rightColor = (Color)block.parameters[0].value; server.sendCommand(RobotCommand.SET_COLOR, new String[]{colorToString(leftColor), colorToString(rightColor)}); server.getMessage(); break;
                case GetDistanceValue: server.sendCommand(RobotCommand.GET_SENSOR_DATA, parameters); String test = server.getMessage(); System.out.println(test); return test;
                case Equal: return Integer.parseInt(parameters[0]) == Integer.parseInt(parameters[1]) ? "1" : "0";
                case Greater: return Integer.parseInt(parameters[0]) > Integer.parseInt(parameters[1]) ? "1" : "0";
                case Less: return Integer.parseInt(parameters[0]) < Integer.parseInt(parameters[1]) ? "1" : "0";
                case If:
                    if(parameters[0] == "1" && ((NestingBlock)(block)).nestedBlock != null) {
                        Thread.sleep(500);
                        runBlockCode(((NestingBlock)(block)).nestedBlock);
                    }
                    break;
                case IfEl: 
                    if(parameters[0] == "1" && ((DoubleNestingBlock)(block)).nestedBlock != null) {
                        Thread.sleep(500);
                        runBlockCode(((DoubleNestingBlock)(block)).nestedBlock);
                    } else if(((DoubleNestingBlock)(block)).secondNestedBlock != null) {
                        Thread.sleep(500);
                        runBlockCode(((DoubleNestingBlock)(block)).secondNestedBlock);
                    }
                    break;
                case Loop:
                    for(int i = 0; i < Integer.parseInt(parameters[0]); i++) {
                        if(((NestingBlock)(block)).nestedBlock == null) { break; }
                        runBlockCode(((NestingBlock)(block)).nestedBlock);
                    }
                    break;
                case WaitUntil: break;
                default: break;
            }
        } catch (IOException e) {}
          catch (InterruptedException e) {}

        if(block.belowBlock != null) {
            if(block.blockType == BlockType.WaitUntil && parameters[0] == "0") {
                runBlockCode(block);
            } else {
                runBlockCode(block.belowBlock);
            }
        }

        return "";
    }

    private String colorToString(Color color) {
        int red = (int)(color.getRed() * 255);
        int green = (int)(color.getGreen() * 255);
        int blue = (int)(color.getBlue() * 255);
        return String.format("%03d", red) + String.format("%03d", green) + String.format("%03d", blue);
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

class FindClientTask implements Runnable {
  private Server server;

  public FindClientTask(Server server) {
    this.server = server;
  }

  @Override
  public void run() {
    try {
      server.findClient();
    } catch (IOException e) {
      System.out.println("Couldn't find client");
    }
  }
}

class RunCodeTask implements Runnable {
  private App app;

  public RunCodeTask(App app) {
    this.app = app;
  }

  @Override
  public void run() {
    app.runBlockCode(app.canvas.getStartBlock());
  }
}