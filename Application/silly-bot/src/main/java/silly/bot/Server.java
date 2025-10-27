package silly.bot;

import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter serverOutput;
    private BufferedReader serverInput;

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(9090);
        System.out.println("SERVER: Server waiting for client...");
        clientSocket = serverSocket.accept();
        System.out.println("SERVER: A client has been connected (" + clientSocket.getInetAddress().getHostName() + ")!");
        
        // Set up Communication
        serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage() throws IOException {
        serverOutput.flush();

        String inputLine;
        while(true) {
            if((inputLine = serverInput.readLine()) != null) {
                if (".".equals(inputLine)) {
                   serverOutput.println("good bye");
                   break;
                }
                System.out.println(inputLine);
                serverOutput.println("Recieved");
                continue;
            }
            serverOutput.close();
            serverInput.close();
            clientSocket.close();
            clientSocket = serverSocket.accept();
            serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
    }

    public void stopServer() throws IOException {
        System.out.println("SERVER: Shutting down server.");
        serverOutput.close();
        serverInput.close();
        clientSocket.close();
        serverSocket.close();
    }
}