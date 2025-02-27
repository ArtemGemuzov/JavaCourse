import java.io.*;
import java.net.*;
import java.util.*;

public class SocketServer {
    private static final int PORT = 9000;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, port =  " + PORT);
            while (true) {
                try (Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("Login:");
                    String username = in.readLine();
                    clients.put(username, out);
                    out.println("Welcome, " + username);
                    System.out.println(username + " connected.");

                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.equals("/who")) {
                            out.println("Users: " + String.join(", ", clients.keySet()));
                        } else if (message.startsWith("@")) {
                            int spaceIndex = message.indexOf(' ');
                            if (spaceIndex != -1) {
                                String targetUser = message.substring(1, spaceIndex);
                                String privateMessage = message.substring(spaceIndex + 1);
                                sendPrivateMessage(username, targetUser, privateMessage);
                            }
                        } else {
                            broadcast(username + ": " + message);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendPrivateMessage(String sender, String recipient, String message) {
        PrintWriter recipientOut = clients.get(recipient);
        if (recipientOut != null) {
            recipientOut.println("Message from " + sender + ": " + message);
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter clientOut : clients.values()) {
            clientOut.println(message);
        }
    }
}

