package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {
  private Server server;
  private boolean isConnected = false;
  private int speed = 64;
  private boolean isPartyModeOn = false;
  private KeyCode moveForwardKey = KeyCode.W;
  private KeyCode moveLeftKey = KeyCode.A;
  private KeyCode moveBackwardKey = KeyCode.S;
  private KeyCode moveRightKey = KeyCode.D;
  private KeyCode partyBotToggleKey = KeyCode.P;
  private KeyTracker keyTracker = new KeyTracker();

  private Pane root = new Pane();
  private Button btn_moveForward;
  private Button btn_setColor;

  @Override
  public void start(Stage stage) throws IOException {
    server = new Server();

    AnimationTimer timer = new AnimationTimer() {
      @Override
      public void handle(long arg0) {
        // Handle Server Connection and Messages
        try { handleServerConnection(); }
        catch (IOException e) { System.out.println("SERVER ERROR: " + e.getMessage()); }
      }
    };
    timer.start();
    
    stage.setTitle("silly-bot-ide");
    stage.setScene(new Scene(root, 1200, 800));

    // Create UI and Event Handlers
    createInterface();
    createEventHandlers(stage);

    stage.show();
  }

  private void createInterface() {
    btn_moveForward = new Button("Move Forward");
    btn_moveForward.relocate(50, 125);
    btn_moveForward.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.MOVE_FORWARD, null);
        } catch (IOException e) {
          System.out.println("SERVER ERROR: " + e.getMessage());
        }
      }
    });
    root.getChildren().add(btn_moveForward);

    btn_setColor = new Button("Set Color");
    btn_setColor.relocate(50, 175);
    btn_setColor.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.SET_COLOR, new int[]{255, 0, 0, 255, 0, 0});
        } catch (IOException e) {
          System.out.println("SERVER ERROR: " + e.getMessage());
        }
      }
    });
    root.getChildren().add(btn_setColor);
  }

  private void createEventHandlers(Stage stage) {
    stage.getScene().setOnKeyPressed(event -> {
      if(event.getCode() == partyBotToggleKey && !keyTracker.isKeyPressed(partyBotToggleKey)) {
        isPartyModeOn = !isPartyModeOn;
      }
      keyTracker.handleKeyPress(event.getCode());
    });

    stage.getScene().setOnKeyReleased(event -> {
      keyTracker.handleKeyRelease(event.getCode());
    });

    root.setOnScroll(event -> {
      int scroll = (int)(event.getDeltaY() / 10);
      speed = Math.max(Math.min(speed + scroll, 255), 30);
      System.out.println(speed);
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

    handleKeyInput();
  }

  private void handleKeyInput() throws IOException {
    // Party Mode
    if(isPartyModeOn) {
      server.sendCommand(RobotCommand.PARTY_MODE, null);
      return;
    }

    // Move Forward
    if(keyTracker.isKeyPressed(moveForwardKey)
      && !keyTracker.isKeyPressed(moveLeftKey)
      && !keyTracker.isKeyPressed(moveBackwardKey)
      && !keyTracker.isKeyPressed(moveRightKey)) {
      server.sendCommand(RobotCommand.MOVE_FORWARD, new int[]{speed});
    }

    // Move Left
    if(!keyTracker.isKeyPressed(moveForwardKey)
      && keyTracker.isKeyPressed(moveLeftKey)
      && !keyTracker.isKeyPressed(moveBackwardKey)
      && !keyTracker.isKeyPressed(moveRightKey)) {
      server.sendCommand(RobotCommand.MOVE_LEFT, new int[]{speed});
    }

    // Move Backward
    if(!keyTracker.isKeyPressed(moveForwardKey)
      && !keyTracker.isKeyPressed(moveLeftKey)
      && keyTracker.isKeyPressed(moveBackwardKey)
      && !keyTracker.isKeyPressed(moveRightKey)) {
      server.sendCommand(RobotCommand.MOVE_BACKWARD, new int[]{speed});
    }

    // Move Right
    if(!keyTracker.isKeyPressed(moveForwardKey)
      && !keyTracker.isKeyPressed(moveLeftKey)
      && !keyTracker.isKeyPressed(moveBackwardKey)
      && keyTracker.isKeyPressed(moveRightKey)) {
      server.sendCommand(RobotCommand.MOVE_RIGHT, new int[]{speed});
    }

    // Stop Moving
    if(!keyTracker.isKeyPressed(moveForwardKey)
      && !keyTracker.isKeyPressed(moveLeftKey)
      && !keyTracker.isKeyPressed(moveBackwardKey)
      && !keyTracker.isKeyPressed(moveRightKey)) {
      server.sendCommand(RobotCommand.STOP_MOVING, null);
    }
  }

  public static void main(String[] args) throws IOException {
    launch();
  }
}

class KeyTracker {
  private final Map<KeyCode, Boolean> keyPressedMap = new HashMap<>();

  public KeyTracker() {
    for (KeyCode code : KeyCode.values()) {
      keyPressedMap.put(code, false);
    }
  }

  public void handleKeyPress(KeyCode code) {
    keyPressedMap.put(code, true);
  }

  public void handleKeyRelease(KeyCode code) {
    keyPressedMap.put(code, false);
  }

  public boolean isKeyPressed(KeyCode code) {
    return keyPressedMap.getOrDefault(code, false);
  }
}