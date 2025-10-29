package ProducerConsumerServer_System;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Producer {

    private final String host;
    private final int[] ports;
    private final Random rnd = new Random();

    public Producer(String host, int[] ports) {
        this.host = host;
        this.ports = ports;
    }

    // Sleep random seconds in [1, 10]
    private void randomSleep() {
        try {
            int secs = 1 + rnd.nextInt(10);
            Thread.sleep(secs * 1000L);
        }
        catch (InterruptedException ignored) {}
    }

    public void runForever() {
        while (true) {
            int port = ports[rnd.nextInt(ports.length)];
            int x = 10 + rnd.nextInt(91); // Random X in [10, 100]

            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String cmd = "ADD " + x;
                out.println(cmd);
                String response = in.readLine();
                String printResponse;
                if (response == null) {
                    printResponse = "<no response>";
                }
                else {
                    printResponse = response;
                }

                System.out.printf("PRODUCER -> port %d :: %s | response: %s%n", port, cmd, printResponse);
            }
            catch (IOException e) {
                System.out.printf("PRODUCER -> port %d :: connection error: %s%n", port, e.getMessage());
            }

            randomSleep();
        }
    }

    public static void main(String[] args) {
        // Expecting arguments already set via Configurations
        String host = args[0];  // e.g. "127.0.0.1"
        String[] parts = args[1].split(","); // e.g. "8881,8882,8883"
        int[] ports = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            ports[i] = Integer.parseInt(parts[i].trim());
        }

        new Producer(host, ports).runForever();
    }
}
