package KeyValueServer_ClientCommunication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class KVClient {

    // Initialize socket and input/output streams
    private Socket socket = null;
    private BufferedReader in = null; // Read messages from the server
    private PrintWriter out = null; // Send messages to the server
    private BufferedReader keyboard = null; // Read user input from the keyboard

    // Constructor
    public KVClient(String address, int port)
    {
        // Establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected with server in port " + port);

            // Initialize streams
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // User instructions
            System.out.println("Give the corresponding command:");
            System.out.println("  1 <key> <value>  for insert");
            System.out.println("  2 <key>          for delete");
            System.out.println("  3 <key>          for search");
            System.out.println("  0 0              for quit");

            // Keep reading until (0,0) is input
            while (true) {
                System.out.print("> ");
                String message = keyboard.readLine();
                if (message == null || message.trim().isEmpty()) continue; // Ignore empty input

                // Send one line to the server
                out.println(message);

                if (message.trim().equals("0 0")) {
                    System.out.println("Connection is terminated");
                    break;
                }

                // Read answer from server
                String response = in.readLine();
                System.out.println("< " + response);
            }

            in.close();
            out.close();
            socket.close();
        }
        catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Input/Output error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Use: java KVClient <IP> <PORT>");
            return;
        }
        String address = args[0]; // IP of the server
        int port = Integer.parseInt(args[1]); // Port number

        new KVClient(address, port);
    }
}