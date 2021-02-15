package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final int serverPort;
    private final List<ClientConnection> clients = new ArrayList<>();
    private int clientId;

    public ChatServer(int port) {
        serverPort = port;
    }

    public void start() {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                Socket socketForClient = serverSocket.accept();
                ClientConnection client = new ClientConnection(socketForClient, this);
                client.start();
            }
        } catch (IOException e) {
            System.out.println("Could not start the server service");
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(1234);
        server.start();
    }

    void addClient(ClientConnection clientConnection) {
        String msg = "User connected : " + clientConnection;
        clients.add(clientConnection);
        sendMessageToAll(clientConnection, msg);
        System.out.println(msg);
    }

    void removeClient(ClientConnection clientConnection) {
        String msg = "User disconnected :" + clientConnection;
        clients.remove(clientConnection);
        sendMessageToAll(clientConnection, msg);
        System.out.println(msg);
    }

    void sendMessageToAll(ClientConnection client, String msg) {
        for (ClientConnection conn : clients) {
            if (!conn.equals(client)) {
                conn.writeMessage(msg);
            }
        }
    }

    int generateUserId() {
        return clientId++;
    }

}
