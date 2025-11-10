package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.*;

public class App extends Application {
  private Server server;
  private boolean isConnected = false;

  private Pane root = new Pane();
  private Button btn_rotateRight;
  private Button btn_rotateLeft;
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

    // Create UI and Event Handlers
    createInterface();
    createEventHandlers();
    
    stage.setTitle("silly-bot-ifff");
    stage.setScene(new Scene(root, 1200, 800));
    stage.show();
  }

  private void createInterface() {
    btn_rotateLeft = new Button("Get Sensor Data");
    btn_rotateLeft.relocate(50, 25);
    btn_rotateLeft.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.GET_SENSOR_DATA, null);
          System.out.println(server.getMessage());
        } catch (IOException e) {
          System.out.println("SERVER ERROR: " + e.getMessage());
        }
      }
    });
    root.getChildren().add(btn_rotateLeft);

    btn_rotateRight = new Button("Rotate Right");
    btn_rotateRight.relocate(50, 75);
    btn_rotateRight.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.ROTATE_RIGHT, null);
        } catch (IOException e) {
          System.out.println("SERVER ERROR: " + e.getMessage());
        }
      }
    });
    root.getChildren().add(btn_rotateRight);

    btn_moveForward = new Button("Move Forward");
    btn_moveForward.relocate(50, 125);
    btn_moveForward.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.MOVE_FORWARD, new int[]{128, 1});
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

  private void createEventHandlers() {

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

  public static void main(String[] args) throws IOException {
    launch();
  }
}