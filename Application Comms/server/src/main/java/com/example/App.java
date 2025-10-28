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

/**
 * JavaFX App
 */
public class App extends Application {
  private static Server server;
  private static Button btn_rotateRight;
  private static Button btn_rotateLeft;

  private static boolean isConnected = false;

  @Override
  public void start(Stage stage) throws IOException {
    Pane root = new Pane();

    AnimationTimer timer = new AnimationTimer() {
      @Override
      public void handle(long arg0) {
        try {
          // Find a Client if None is to be had;
          if(!isConnected) { server.findClient(); isConnected = true; }

          if(server.serverInput.ready()) {
            
            if ((server.inputLine = server.serverInput.readLine()) == null) {
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
          System.out.println("fuck: " + e.getMessage());
        }
      }
    };
    timer.start();

    btn_rotateLeft = new Button("Rotate Left");
    btn_rotateLeft.relocate(50, 25);
    btn_rotateLeft.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          server.sendMessage("000");
        } catch (IOException e) {
          System.out.println("fuck: " + e.getMessage());
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
          server.sendMessage("001");
        } catch (IOException e) {
          System.out.println("fuck: " + e.getMessage());
        }
      }
    });
    root.getChildren().add(btn_rotateRight);

    stage.setScene(new Scene(root, 1200, 800));
    stage.show();
  }

  public static void main(String[] args) throws IOException {
    server = new Server();
    launch();
  }
}

enum ResponseCode {
  OK,
  DISCONNECT;
}

class Server {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  public PrintWriter serverOutput;
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

  public void sendMessage(String message) throws IOException {
    serverOutput.println(message);
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