package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Client connection class.
 * Starts on new Tread.
 *
 * @author Meshchaninov Aleksey
 */
class ClientConnection extends Thread {

    private final Socket socket;
    private final ChatServer server;
    private final int id;
    private BufferedReader reader;
    private BufferedWriter writer;

    ClientConnection(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        id = server.generateUserId();

    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            server.addClient(this);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            String msg;
            while (true) {
                msg = reader.readLine();
                if (msg == null || msg.equals("exit")) {
                    break;
                } else {
                    msg = String.format("User %s : %s", id, msg);
                    server.sendMessageToAll(this, msg);
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error on IO in socket :" + e.getLocalizedMessage());
        } finally {
            disconnectClient();
        }
    }

    /**
     * Message writer to output stream
     *
     * @param msg client message
     */
    synchronized void writeMessage(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error IO when writing msg: " + e.getLocalizedMessage());
        }

    }

    /**
     * Disconnect client from server
     */
    private synchronized void disconnectClient() {
        try {
            socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            System.out.println("Error :" + e.getLocalizedMessage());
        } finally {
            System.out.println(socket.getInetAddress() + "  " + socket.getPort() + " disconnected");
        }
    }

    @Override
    public String toString() {
        return "Connection :" + socket.getInetAddress() + " on " + socket.getPort() + " port.";
    }
}
