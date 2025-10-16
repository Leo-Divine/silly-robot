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
        System.out.println("SERVER: Check this out imma send a message...");
        serverOutput.println("5");
        while (true) {
            String response = serverInput.readLine();
            if(response == null) { continue; }
            if (!"OK".equals(response)) {
                System.out.println("SERVER: Client is being a bit autistic.");
                return;
            }
            break;
        }
        
        serverOutput.println("Hello");
        while (true) {
            String response = serverInput.readLine();
            if(response == null) { continue; }
            if (!"OK".equals(response)) {
                System.out.println("SERVER: Client is being a bit autistic.");
                return;
            }
            break;
        }
    }

    public void stopServer() throws IOException {
        serverOutput.println("MSG_1");
        String response = serverInput.readLine();
        if (!"OK".equals(response)) {
            System.out.println("SERVER: Client refuses to die.");
            return;
        }

        System.out.println("SERVER: Shutting down server.");
        serverOutput.close();
        serverInput.close();
        clientSocket.close();
        serverSocket.close();
    }
}