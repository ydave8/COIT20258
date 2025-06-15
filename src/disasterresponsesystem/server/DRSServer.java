package disasterresponsesystem.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import disasterresponsesystem.db.DBInitializer;

/**
 * Multi-threaded socket server for DRS-Enhanced.
 * Listens on port 9090 and spins up a new ClientHandler
 * thread for every incoming client connection.
 */
public class DRSServer {

    private static final int PORT = 9090;

    public static void main(String[] args) {
        DBInitializer.initialize();
        
        System.out.println("Starting DRS Server on port " + PORT + " …");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Keep accepting clients indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected from "
                                   + clientSocket.getInetAddress().getHostAddress());

                // Each client served by its own handler thread
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}