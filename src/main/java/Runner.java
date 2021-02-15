import client.ClientApp;
import server.ChatServer;

/**
 * Console application chat, which start on localhost on 1234 port
 * <p>
 * If u want to start server, start application with 1 argument.
 * Else, if u need to run client - 0;
 *
 * @author Meshchaninov Aleksey
 */
public class Runner {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1";
        int serverPort = 1234;
        if (args.length == 1 && args[0].matches("[-+]?\\d+")) {
            int value = Integer.parseInt(args[0]);
            switch (value) {
                case 0:
                    ClientApp app = new ClientApp(serverIp, serverPort);
                    app.connect();
                    break;
                case 1:
                    ChatServer server = new ChatServer(serverPort);
                    server.start();
                default:
                    System.out.println("Неверно выбрана опция запуска");
            }
        } else {
            System.out.println("Ошибка парсинга входных аргументов");
        }
    }
}
