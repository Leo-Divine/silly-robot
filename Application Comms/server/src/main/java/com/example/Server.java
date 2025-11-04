package com.example;

import java.io.*;
import java.net.*;

enum ResponseCode {
  OK,
  DISCONNECT;
}

enum RobotCommand {
  MOVE_FORWARD,
  MOVE_LEFT,
  MOVE_RIGHT,
  MOVE_BACKWARD,
  STOP_MOVING,
  SET_COLOR,
  PARTY_MODE;
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
        serverOutput.println("R_000" + String.format("%03d", parameters[0]));
        break;
      case MOVE_LEFT:
        serverOutput.println("R_001" + String.format("%03d", parameters[0]));
        break;
      case MOVE_BACKWARD:
        serverOutput.println("R_002" + String.format("%03d", parameters[0]));
        break;
      case MOVE_RIGHT:
        serverOutput.println("R_003" + String.format("%03d", parameters[0]));
        break;
      case STOP_MOVING:
        serverOutput.println("R_004");
        break;
      case SET_COLOR:
        serverOutput.println(
          "R_005" +
          String.format("%03d", parameters[0]) +
          String.format("%03d", parameters[1]) +
          String.format("%03d", parameters[2]) +
          String.format("%03d", parameters[3]) +
          String.format("%03d", parameters[4]) +
          String.format("%03d", parameters[5])
        );
        break;
      case PARTY_MODE:
        serverOutput.println("R_006");
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
