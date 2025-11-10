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
  GET_SENSOR_DATA,
  PLAY_NOTE,
  WAIT;
}

class Note {
  public Notes pitch;
  public int duration;

  public Note(Notes pitch, int duration) {
    this.pitch = pitch;
    this.duration = duration;
  }
}

enum Notes {
  NOTE_B0(31),
  NOTE_C1(33),
  NOTE_CS1(35),
  NOTE_D1(37),
  NOTE_DS1(39),
  NOTE_E1(41),
  NOTE_F1(44),
  NOTE_FS1(46),
  NOTE_G1(49),
  NOTE_GS1(52),
  NOTE_A1(55),
  NOTE_AS1(58),
  NOTE_B1(62),
  NOTE_C2(65),
  NOTE_CS2(69),
  NOTE_D2(73),
  NOTE_DS2(78),
  NOTE_E2(82),
  NOTE_F2(87),
  NOTE_FS2(93),
  NOTE_G2(98),
  NOTE_GS2(104),
  NOTE_A2(110),
  NOTE_AS2(117),
  NOTE_B2(123),
  NOTE_C3(131),
  NOTE_CS3(139),
  NOTE_D3(147),
  NOTE_DS3(156),
  NOTE_E3(165),
  NOTE_F3(175),
  NOTE_FS3(185),
  NOTE_G3(196),
  NOTE_GS3(208),
  NOTE_A3(220),
  NOTE_AS3(233),
  NOTE_B3(247),
  NOTE_C4(262),
  NOTE_CS4(277),
  NOTE_D4(294),
  NOTE_DS4(311),
  NOTE_E4(330),
  NOTE_F4(349),
  NOTE_FS4(370),
  NOTE_G4(392),
  NOTE_GS4(415),
  NOTE_A4(440),
  NOTE_AS4(466),
  NOTE_B4(494),
  NOTE_C5(523),
  NOTE_CS5(554),
  NOTE_D5(587),
  NOTE_DS5(622),
  NOTE_E5(659),
  NOTE_F5(698),
  NOTE_FS5(740),
  NOTE_G5(784),
  NOTE_GS5(831),
  NOTE_A5(880),
  NOTE_AS5(932),
  NOTE_B5(988),
  NOTE_C6(1047),
  NOTE_CS6(1109),
  NOTE_D6(1175),
  NOTE_DS6(1245),
  NOTE_E6(1319),
  NOTE_F6(1397),
  NOTE_FS6(1480),
  NOTE_G6(1568),
  NOTE_GS6(1661),
  NOTE_A6(1760),
  NOTE_AS6(1865),
  NOTE_B6(1976),
  NOTE_C7(2093),
  NOTE_CS7(2217),
  NOTE_D7(2349),
  NOTE_DS7(2489),
  NOTE_E7(2637),
  NOTE_F7(2794),
  NOTE_FS7(2960),
  NOTE_G7(3136),
  NOTE_GS7(3322),
  NOTE_A7(3520),
  NOTE_AS7(3729),
  NOTE_B7(3951),
  NOTE_C8(4186),
  NOTE_CS8(4435),
  NOTE_D8(4699),
  NOTE_DS8(4978);
  
  final int frequency;
  private Notes(int frequency) {
    this.frequency = frequency;
  }
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
      case PLAY_NOTE:
        serverOutput.println("R_005" +
          String.format("%04d", parameters[0]) +
          String.format("%04d", parameters[1])
        );
        break;
      case WAIT:
        serverOutput.println("R_006" + 
          String.format("%03d", parameters[0])
        );
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
