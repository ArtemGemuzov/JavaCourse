import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

interface Command {
    void execute(String sender, String message, PrintWriter out);
}

class ClientRegistry {
    private final Map<String, PrintWriter> clients = new HashMap<>();

    public synchronized void addClient(String username, PrintWriter out) {
        clients.put(username, out);
    }

    public synchronized void removeClient(String username) {
        clients.remove(username);
    }

    public synchronized Set<String> getClients() {
        return clients.keySet();
    }

    public synchronized PrintWriter getClientOutput(String username) {
        return clients.get(username);
    }
}

class WhoCommand implements Command {
    private final ClientRegistry registry;

    public WhoCommand(ClientRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(String sender, String message, PrintWriter out) {
        out.println("Users: " + String.join(", ", registry.getClients()));
    }
}

class PrivateMessageCommand implements Command {
    private final ClientRegistry registry;

    public PrivateMessageCommand(ClientRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(String sender, String message, PrintWriter out) {
        int spaceIndex = message.indexOf(' ');
        if (spaceIndex != -1) {
            String targetUser = message.substring(1, spaceIndex);
            String privateMessage = message.substring(spaceIndex + 1);
            PrintWriter recipientOut = registry.getClientOutput(targetUser);
            if (recipientOut != null) {
                recipientOut.println("Message from " + sender + ": " + privateMessage);
            } else {
                out.println("User not found: " + targetUser);
            }
        }
    }
}

class BroadcastCommand implements Command {
    private final ClientRegistry registry;

    public BroadcastCommand(ClientRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(String sender, String message, PrintWriter out) {
        for (String client : registry.getClients()) {
            PrintWriter clientOut = registry.getClientOutput(client);
            if (clientOut != null) {
                clientOut.println(sender + ": " + message);
            }
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final ClientRegistry registry;
    private final Map<String, Command> commands;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ClientRegistry registry) {
        this.socket = socket;
        this.registry = registry;
        this.commands = new HashMap<>();
        this.commands.put("/who", new WhoCommand(registry));
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Login:");
            username = in.readLine();
            registry.addClient(username, out);
            out.println("Welcome, " + username);
            System.out.println(username + " connected.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/")) {
                    Command command = commands.getOrDefault(message.split(" ")[0], new PrivateMessageCommand(registry));
                    command.execute(username, message, out);
                } else {
                    new BroadcastCommand(registry).execute(username, message, out);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client " + username + ": " + e.getMessage());
        } finally {
            registry.removeClient(username);
            System.out.println(username + " disconnected.");
        }
    }
}

