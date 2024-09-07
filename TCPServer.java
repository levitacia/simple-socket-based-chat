import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    private static final int PORT = 6969; // Укажите нужный порт
    private static HashSet<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    public static synchronized void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static synchronized void sendToUser(String message, String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                client.sendMessage(message);
                break;
            }
        }
    }

    public static synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username:");
            username = in.readLine();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("@senduser ")) {
                    String[] parts = inputLine.split(" ", 3);
                    if (parts.length >= 3) {
                        String targetUser = parts[1];
                        String message = parts[2];
                        TCPServer.sendToUser(username + " (private): " + message, targetUser);
                    }
                } else {
                    TCPServer.broadcast(username + ": " + inputLine, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            TCPServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}