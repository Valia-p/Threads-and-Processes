package KeyValueServer_ClientCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class KVServer {

    // Server socket
    private final ServerSocket serverSocket;

    // Hashtable
    private final Hashtable<Integer, Integer> hashtable;

    // Max size = 2^20
    private static final int tableSize = (int) Math.pow(2,20);

    // Constructor
    public KVServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.hashtable = new Hashtable<>(tableSize);
    }

    // Operations
    private String insert(int key, int value) {
        synchronized (hashtable) {
            if (hashtable.size() >= tableSize){
                return "0";
            }
            if (hashtable.containsKey(key)) {
                return "0";
            }
            hashtable.put(key, value);
            return "1";
        }
    }

    private String delete(int key) {
        synchronized (hashtable) {
            // Remove key if it exists
            if (hashtable.containsKey(key)) {
                hashtable.remove(key);
                return "1";
            } else {
                return "0";
            }
        }
    }

    private String search(int key) {
        synchronized (hashtable) {
            // Return the value if it exists, otherwise "0"
            Integer val = hashtable.get(key);
            if (val == null) {
                return "0";
            }
            return val.toString();
        }
    }

    // Command decoder
    private String handle(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0){
            return "0";
        }

        try {
            int operation = Integer.parseInt(parts[0]);

            if (operation == 0) {
                return "1"; // quit
            }
            else if (operation == 1 && parts.length >= 3) {
                int key = Integer.parseInt(parts[1]);
                int val = Integer.parseInt(parts[2]);
                return insert(key, val);
            }
            else if (operation == 2 && parts.length >= 2) {
                int key = Integer.parseInt(parts[1]);
                return delete(key);
            }
            else if (operation == 3 && parts.length >= 2) {
                int key = Integer.parseInt(parts[1]);
                return search(key);
            }
            else {
                return "Invalid Command"; // invalid command
            }
        } catch (NumberFormatException e) {
            return "Bad input format"; // bad input format
        }
    }

    // each client is handled by a dedicated thread
    public void serve() throws IOException {
        System.out.println("Server started. Waiting for connections...");

        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

            // Handle each client in a separate thread
            new Thread(() -> {
                try (Socket sock = clientSocket;
                     PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String response = handle(inputLine);
                        out.println(response);

                        // Close this client connection when "0 0" is received
                        if ("0 0".equals(inputLine.trim())) break;
                    }
                }
                catch (IOException ignored) {
                }
            }).start();
        }
    }

    // Entry point
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Use: java KVServer <PORT>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            new KVServer(serverSocket).serve();
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
    }
}
