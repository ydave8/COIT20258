package disasterresponsesystem.server;

import disasterresponsesystem.db.IncidentDAO;
import disasterresponsesystem.model.Incident;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles a single client connection.
 * Accepts commands from client, parses them,
 * and executes actions like saving incidents to database.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("← " + inputLine);

                if (inputLine.startsWith("ADD_INCIDENT")) {
                    String[] parts = inputLine.split("\\|");
                    if (parts.length == 6) {
                        String type = parts[1];
                        String location = parts[2];
                        String severity = parts[3];
                        String department = parts[4];
                        String reportedBy = parts[5];

                        // Create incident with current timestamp
                        Incident incident = new Incident(type, location, severity, department, LocalDateTime.now());

                        boolean success = IncidentDAO.addIncident(incident);
                        if (success) {
                            out.println("✅ Incident recorded successfully.");
                        } else {
                            out.println("❌ Error saving incident to database.");
                        }
                    } else {
                        out.println("❌ Invalid ADD_INCIDENT format. Expected:");
                        out.println("   ADD_INCIDENT|type|location|severity|department|reportedBy");
                    }

                } 
                else if (inputLine.equals("GET_ALL_INCIDENTS")) {
                    List<Incident> incidents = IncidentDAO.getAllIncidents();
                    System.out.print("fetcher: "+incidents.size());
                    for (Incident i : incidents){
                        System.out.println("sending" + i.toString());
                    }
                    
                        if (incidents.isEmpty()) {
                            out.println("No incidents found.");
                        } else {
                            for (Incident incident : incidents) {
                                System.out.println("Sending" + incident.toString());
//                                out.println(incident.toString());
                                  String formatted = String.join("|",
                            String.valueOf(incident.getId()),
                            incident.getType(),
                            incident.getLocation(),
                            incident.getSeverity(),
                            incident.getDepartment(),
                            incident.getStatus(),
                            incident.getTimestamp().toString(),
                            incident.getResponder()
                                    
                            );
                            out.println(formatted);
                            }
                            out.println("END_OF_LIST");
                        }
                    }

			else if (inputLine.equals("GET_BY_PRIORITY")) {
    List<Incident> incidents = IncidentDAO.getIncidentsByPriority();
        for (Incident incident : incidents) {
            out.println(incident.toString());
        }
        out.println("END_OF_LIST");
    
}

else if (inputLine.startsWith("GET_BY_DEPARTMENT|")) {
    String[] parts = inputLine.split("\\|");
    if (parts.length == 2) {
        String department = parts[1];
        List<Incident> incidents = IncidentDAO.getIncidentsByDepartment(department);

        if (incidents.isEmpty()) {
            out.println("No incidents found for department: " + department);
        } else {
            for (Incident incident : incidents) {
                // This must match your parsing format
                String formatted = String.join("|",
                    String.valueOf(incident.getId()),
                    incident.getType(),
                    incident.getLocation(),
                    incident.getSeverity(),
                    incident.getDepartment(),
                    incident.getStatus(),
                    incident.getTimestamp().toString(),
                    incident.getResponder()
                );
                out.println(formatted);
            }
            out.println("END_OF_LIST");
        }
    } else {
        out.println("❌ Invalid format. Use: GET_BY_DEPARTMENT|<department>");
    }
}


else if (inputLine.startsWith("UPDATE_STATUS|")) {
    String[] parts = inputLine.split("\\|");
    if (parts.length == 3) {
        int id = Integer.parseInt(parts[1]);
        String status = parts[2];

        boolean success = IncidentDAO.updateIncidentStatus(id, status);
        out.println(success ? "✅ Status updated." : "❌ Failed to update status.");
    } else {
        out.println("❌ Invalid format. Use: UPDATE_STATUS|<id>|<status>");
    }
}

else if (inputLine.startsWith("ASSIGN_RESPONDER|")) {
    String[] parts = inputLine.split("\\|");
    if (parts.length == 3) {
        int id = Integer.parseInt(parts[1]);
        String responder = parts[2];

        boolean updated = IncidentDAO.assignResponder(id, responder);
        if (updated) {
            out.println("✅ Responder assigned successfully.");
        } else {
            out.println("❌ Failed to assign responder.");
        }
    } else {
        out.println("❌ Invalid ASSIGN_RESPONDER format.");
    }
}

                
                else {
                    out.println("⚠️ Unrecognized command.");
                }
            }

        } catch (IOException ex) {
            System.err.println("Client handler exception: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException ex) {
                // Ignore
            }
        }
    }
}