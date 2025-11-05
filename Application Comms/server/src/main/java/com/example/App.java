package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {
  private final static int FONT_SIZE = 24;
  private Server server;
  private boolean isConnected = false;
  private int speed = 64;
  private boolean isPartyModeOn = false;
  private KeyCode moveForwardKey = KeyCode.W;
  private KeyCode moveLeftKey = KeyCode.A;
  private KeyCode moveBackwardKey = KeyCode.S;
  private KeyCode moveRightKey = KeyCode.D;
  private KeyTracker keyTracker = new KeyTracker();

  private Label lbl_title = new Label("Control Panel");
  private GridPane root = new GridPane();

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
    //timer.start();
    
    stage.setTitle("silly-bot-ide");
    stage.setScene(new Scene(root, 1200, 800));

    // Create UI and Event Handlers
    createInterface();
    createEventHandlers(stage);

    stage.show();
  }

  private void createInterface() {
    ObservableList<KeyCode> options = FXCollections.observableArrayList();
    for (KeyCode code : KeyCode.values()) {
      options.add(code);
    }

    root.setHgap(25);
    root.setVgap(25);

    ColumnConstraints col = new ColumnConstraints();
    col.setPercentWidth(33);
    col.setFillWidth(true);
    root.getColumnConstraints().add(col);
    root.getColumnConstraints().add(col);
    root.getColumnConstraints().add(col);

    RowConstraints row = new RowConstraints();
    row.setPercentHeight(25);
    row.setFillHeight(true);
    root.getRowConstraints().add(row);
    root.getRowConstraints().add(row);
    root.getRowConstraints().add(row);
    root.getRowConstraints().add(row);

    lbl_title.setFont(Font.font("Arial", 30));
    GridPane.setHalignment(lbl_title, HPos.CENTER);
    root.add(lbl_title, 0, 0, 3, 1);

    ComboBox<KeyCode> cmb_moveForward = new ComboBox<>(options);
    cmb_moveForward.setValue(moveForwardKey);
    cmb_moveForward.setMaxWidth(Double.MAX_VALUE);
    cmb_moveForward.setMaxHeight(Double.MAX_VALUE);
    cmb_moveForward.setOnMouseEntered(event -> {
      lbl_title.setText("Set the Key to Move Forward");
    });
    cmb_moveForward.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        KeyCode item = cmb_moveForward.getSelectionModel().getSelectedItem();
        if(item == moveLeftKey || item == moveBackwardKey || item == moveRightKey) {
          cmb_moveForward.getSelectionModel().select(moveForwardKey);
          return;
        }
        moveForwardKey = item;
      }
    });
    GridPane.setHalignment(cmb_moveForward, HPos.CENTER);
    root.add(cmb_moveForward, 1, 1);

    ComboBox<KeyCode> cmb_moveLeft = new ComboBox<>(options);
    cmb_moveLeft.setValue(moveLeftKey);
    cmb_moveLeft.setMaxWidth(Double.MAX_VALUE);
    cmb_moveLeft.setMaxHeight(Double.MAX_VALUE);
    cmb_moveLeft.setOnMouseEntered(event -> {
      lbl_title.setText("Set the Key to Move Left");
    });
    cmb_moveLeft.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        KeyCode item = cmb_moveLeft.getSelectionModel().getSelectedItem();
        if(item == moveForwardKey || item == moveBackwardKey || item == moveRightKey) {
          cmb_moveLeft.getSelectionModel().select(moveLeftKey);
          return;
        }
        moveLeftKey = item;
      }
    });
    GridPane.setHalignment(cmb_moveLeft, HPos.CENTER);
    root.add(cmb_moveLeft, 0, 2);

    Button btn_PartyMode = new Button("Party Mode");
    btn_PartyMode.setFont(Font.font("Arial", FONT_SIZE));
    btn_PartyMode.setMaxWidth(Double.MAX_VALUE);
    btn_PartyMode.setMaxHeight(Double.MAX_VALUE);
    btn_PartyMode.setOnMouseEntered(event -> {
      lbl_title.setText("Activate Party Mode (" + (isPartyModeOn ? "On" : "Off") + ")");
    });
    btn_PartyMode.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        isPartyModeOn = !isPartyModeOn;
        lbl_title.setText("Activate Party Mode (" + (isPartyModeOn ? "On" : "Off") + ")");
      }
    });
    GridPane.setHalignment(btn_PartyMode, HPos.CENTER);
    root.add(btn_PartyMode, 1, 2);

    ComboBox<KeyCode> cmb_moveRight = new ComboBox<>(options);
    cmb_moveRight.setValue(moveRightKey);
    cmb_moveRight.setMaxWidth(Double.MAX_VALUE);
    cmb_moveRight.setMaxHeight(Double.MAX_VALUE);
    cmb_moveRight.setOnMouseEntered(event -> {
      lbl_title.setText("Set the Key to Move Right");
    });
    cmb_moveRight.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        KeyCode item = cmb_moveRight.getSelectionModel().getSelectedItem();
        if(item == moveForwardKey || item == moveLeftKey || item == moveBackwardKey) {
          cmb_moveRight.getSelectionModel().select(moveRightKey);
          return;
        }
        moveRightKey = item;
      }
    });
    GridPane.setHalignment(cmb_moveRight, HPos.CENTER);
    root.add(cmb_moveRight, 2, 2);

    Button btn_speedUp = new Button("Speed Up");
    btn_speedUp.setFont(Font.font("Arial", FONT_SIZE));
    btn_speedUp.setMaxWidth(Double.MAX_VALUE);
    btn_speedUp.setMaxHeight(Double.MAX_VALUE);
    btn_speedUp.setOnMouseEntered(event -> {
      lbl_title.setText("Speed up the Robot (Speed: " + speed + ")");
    });
    btn_speedUp.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        speed = Math.min(speed + 10, 255);
        lbl_title.setText("Speed up the Robot (Speed: " + speed + ")");
      }
    });
    GridPane.setHalignment(btn_speedUp, HPos.CENTER);
    root.add(btn_speedUp, 0, 3);

    ComboBox<KeyCode> cmb_moveBackward = new ComboBox<>(options);
    cmb_moveBackward.setValue(moveBackwardKey);
    cmb_moveBackward.setMaxWidth(Double.MAX_VALUE);
    cmb_moveBackward.setMaxHeight(Double.MAX_VALUE);
    cmb_moveBackward.setOnMouseEntered(event -> {
      lbl_title.setText("Set the Key to Move Backward");
    });
    cmb_moveBackward.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        KeyCode item = cmb_moveBackward.getSelectionModel().getSelectedItem();
        if(item == moveForwardKey || item == moveLeftKey || item == moveRightKey) {
          cmb_moveBackward.getSelectionModel().select(moveBackwardKey);
          return;
        }
        moveBackwardKey = item;
      }
    });
    GridPane.setHalignment(cmb_moveBackward, HPos.CENTER);
    root.add(cmb_moveBackward, 1, 3);

    Button btn_slowDown = new Button("Slow Down");
    btn_slowDown.setFont(Font.font("Arial", FONT_SIZE));
    btn_slowDown.setMaxWidth(Double.MAX_VALUE);
    btn_slowDown.setMaxHeight(Double.MAX_VALUE);
    btn_slowDown.setOnMouseEntered(event -> {
      lbl_title.setText("Slow down the Robot (Speed: " + speed + ")");
    });
    btn_slowDown.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        speed = Math.max(speed - 10, 30);
        lbl_title.setText("Slow down the Robot (Speed: " + speed + ")");
      }
    });
    GridPane.setHalignment(btn_slowDown, HPos.CENTER);
    root.add(btn_slowDown, 2, 3);
  }

  private void createEventHandlers(Stage stage) {
    stage.getScene().setOnKeyPressed(event -> {
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