package client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Console application for chat client
 *
 * @author Meshchaninov Aleksey
 */
public class ClientApp {
    private final String serverIpAddress;
    private final int serverPort;
    Socket socket;

    public ClientApp(String ip, int port) {
        serverPort = port;
        serverIpAddress = ip;
    }

    /**
     * Connect to server
     */
    public void connect() {
        try {
            socket = new Socket(serverIpAddress, serverPort);
            // Thread to read messages from console input and write to socket
            Thread outputMsgWriterThread = new Thread(() -> {
                try (Scanner console = new Scanner(System.in);
                     OutputStreamWriter bw = new OutputStreamWriter(ClientApp.this.socket.getOutputStream())) {
                    String msg = "";
                    while (!msg.equals("exit")) {
                        System.out.println("Enter message : ");
                        msg = console.nextLine();
                        bw.write(msg + "\n");
                        bw.flush();
                    }
                } catch (IOException e) {
                    System.out.println("Error in console thread: " + e.getMessage());
                }
            });
            //Thread to read messages from the server
            Thread inputMsgReaderThread = new Thread(() -> {
                try (Scanner br = new Scanner(ClientApp.this.socket.getInputStream())) {
                    while (br.hasNextLine()) {
                        System.out.println(br.nextLine());
                    }
                } catch (IOException e) {
                    System.out.println("Error in reader msg thread: " + e.getMessage());
                }
            });
            inputMsgReaderThread.start();
            outputMsgWriterThread.start();
            inputMsgReaderThread.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Server " + serverIpAddress + " on port " + serverPort + " is not available");
            System.out.println("Error: " + e.getLocalizedMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error while closing socket:" + e.getLocalizedMessage());
            }
        }
    }
}
