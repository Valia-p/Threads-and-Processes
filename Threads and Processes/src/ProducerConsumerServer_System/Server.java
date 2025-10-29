package ProducerConsumerServer_System;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

    private int storage;
    private final Object lock = new Object();

    private final ServerSocket producerSocket;
    private final ServerSocket consumerSocket;

    public Server(int producerPort, int consumerPort) throws IOException {
        this.producerSocket = new ServerSocket(producerPort);
        this.consumerSocket = new ServerSocket(consumerPort);
        this.storage = new Random().nextInt(1000) + 1;

        System.out.printf("Server started | Producer port: %d | Consumer port: %d | Initial storage: %d%n", producerPort, consumerPort, storage);
    }

    public void start() {
        Thread producerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listenProducers();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();

        Thread consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listenConsumers();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        consumerThread.start();
    }

    private void listenProducers() throws IOException {
        while (true) {
            Socket socket = producerSocket.accept();
            new Thread(() -> handleProducer(socket)).start();
        }
    }

    private void listenConsumers() throws IOException {
        while (true) {
            Socket socket = consumerSocket.accept();
            new Thread(() -> handleConsumer(socket)).start();
        }
    }

    private void handleProducer(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line = in.readLine();
            if (line == null) return;

            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2 || !parts[0].equalsIgnoreCase("ADD")) {
                out.println("INVALID COMMAND");
                return;
            }

            int amount = Integer.parseInt(parts[1]);
            synchronized (lock) {
                if (storage + amount > 1000) {
                    System.out.printf("%d REJECTED (limit exceeded, storage=%d)%n", amount, storage);
                    out.println("LIMIT EXCEEDED");
                }
                else {
                    storage += amount;
                    System.out.printf("%d OK (new storage=%d)%n", amount, storage);
                    out.println("OK " + storage);
                }
            }
        }
        catch (IOException | NumberFormatException e) {
            System.out.println("Producer handler error: " + e.getMessage());
        }
    }

    private void handleConsumer(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line = in.readLine();
            if (line == null) return;

            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2 || !parts[0].equalsIgnoreCase("REMOVE")) {
                out.println("INVALID COMMAND");
                return;
            }

            int amount = Integer.parseInt(parts[1]);
            synchronized (lock) {
                if (storage - amount < 1) {
                    System.out.printf("%d REJECTED (not enough stock, storage=%d)%n", amount, storage);
                    out.println("NOT ENOUGH STOCK");
                } else {
                    storage -= amount;
                    System.out.printf("%d OK (new storage=%d)%n", amount, storage);
                    out.println("OK " + storage);
                }
            }
        }
        catch (IOException | NumberFormatException e) {
            System.out.println("Consumer handler error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int producerPort = Integer.parseInt(args[0]);
        int consumerPort = Integer.parseInt(args[1]);

        try {
            Server server = new Server(producerPort, consumerPort);
            server.start();
        }
        catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
