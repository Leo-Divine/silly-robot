package com.example;

import java.io.*;
import java.net.*;

enum ResponseCode {
  OK,
  DISCONNECT;
}

enum RobotCommand {
  MOVE_FORWARD,
  ROTATE_RIGHT,
  ROTATE_LEFT,
  SET_COLOR,
  GET_SENSOR_DATA;
}

public class Server {
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
    switch (command) {
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
      case GET_SENSOR_DATA:
        serverOutput.println("R_004");
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
