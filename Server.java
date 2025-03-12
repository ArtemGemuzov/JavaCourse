import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9000;
    private static final ClientRegistry registry = new ClientRegistry();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, port = " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, registry)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
