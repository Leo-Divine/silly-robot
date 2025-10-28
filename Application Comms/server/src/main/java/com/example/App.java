package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.*;
import java.io.*;

enum ResponseCode {
  OK,
  DISCONNECT;
}

enum RobotCommand {
  MOVE_FORWARD,
  ROTATE_RIGHT,
  ROTATE_LEFT,
  SET_COLOR;
}

public class App extends Application {
  private Server server;
  private boolean isConnected = false;

  private static Pane root = new Pane();;
  private static Button btn_rotateRight;
  private static Button btn_rotateLeft;
  private static Button btn_moveForward;
  private static Button btn_setColor;

  @Override
  public void start(Stage stage) throws IOException {
    server = new Server();

    AnimationTimer timer = new AnimationTimer() {
      @Override
      public void handle(long arg0) {
        try {
          if(!isConnected) { server.findClient(); isConnected = true; }

          if(server.isMessageAvailable()) {
            if (server.getMessage() == null) {
              server.disconnectClient();
              server.findClient();
              return;
            } 

            if(server.readMessage() == ResponseCode.DISCONNECT) {
              server.disconnectClient();
              server.findClient();
            }
          }
        } catch (IOException e) {
          System.out.println("SERVER ERROR: " + e.getMessage());
        }
      }
    };
    timer.start();

    createInterface();
    createEventHandlers();
    stage.setScene(new Scene(root, 1200, 800));
    stage.show();
  }

  private void createInterface() {
    btn_rotateLeft = new Button("Rotate Left");
    btn_rotateLeft.relocate(50, 25);
    btn_rotateLeft.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendCommand(RobotCommand.ROTATE_LEFT, null);
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

  public static void main(String[] args) throws IOException {
    launch();
  }
}

class Server {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter serverOutput;
  public BufferedReader serverInput;
  public String inputLine;

  public Server() throws IOException {
    serverSocket = new ServerSocket(9090);
  }

  public void findClient() throws IOException {
    System.out.println("SERVER: Server waiting for client...");
    clientSocket = serverSocket.accept();
    System.out.println("SERVER: A client has been connected (" + clientSocket.getInetAddress().getHostName() + ")!");

    // Set up Communication
    serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);
    serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  public void disconnectClient() throws IOException {
    System.out.println("Disconnecting");
    serverOutput.close();
    serverInput.close();
    clientSocket.close();
  }

  public void sendCommand(RobotCommand command, int parameters[]) throws IOException {
    switch(command) {
      case MOVE_FORWARD:
        serverOutput.println("R_000" + String.format("%03d", parameters[0]) + String.format("%03d", parameters[1]));
        break;
      case ROTATE_LEFT:
        serverOutput.println("R_001");
        break;
      case ROTATE_RIGHT:
        serverOutput.println("R_002");
        break;
      case SET_COLOR:
        serverOutput.println(
          "R_003" +
          String.format("%03d", parameters[0]) +
          String.format("%03d", parameters[1]) +
          String.format("%03d", parameters[2]) +
          String.format("%03d", parameters[3]) +
          String.format("%03d", parameters[4]) +
          String.format("%03d", parameters[5])
          );
        break;
    }
  }

  public boolean isMessageAvailable() throws IOException {
    return serverInput.ready();
  }

  public String getMessage() throws IOException {
    inputLine = serverInput.readLine();
    return inputLine;
  }

  public ResponseCode readMessage() throws IOException {
    if ("FCKOFF".equals(inputLine)) {
      return ResponseCode.DISCONNECT;
    }
    System.out.println(inputLine);
    return ResponseCode.OK;
  }

  public void stopServer() throws IOException {
    System.out.println("SERVER: Shutting down server.");
    disconnectClient();
    serverSocket.close();
  }
}