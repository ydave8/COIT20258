package disasterresponsesystem.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple socket client to send commands to the DRS server.
 */
public class DRSClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String message) {
//    try {
//        out.println(message);
//        out.flush();
//        return in.readLine();
//    } catch (IOException e) {
//        return "⚠ Error: " + e.getMessage();
//    }
    out.println(message);
    out.flush();
    }

    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            // Ignore
        }
    }
public static void main(String[] args) {
    DRSClient client = new DRSClient();
    if (client.connect("localhost", 9090)) {
        client.out.println("GET_ALL_INCIDENTS");
        client.out.flush(); // 🔥 Force flush
        client.disconnect();
    }
}

public List<String> readLinesUntilEnd() {
    List<String> lines = new ArrayList<>();
    try {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("END_OF_LIST")) break;
            lines.add(line);
        }
    } catch (IOException e) {
        lines.add("⚠ Error reading: " + e.getMessage());
    }
    return lines;
}

}

