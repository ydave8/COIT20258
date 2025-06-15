package disasterresponsesystem.db;

import disasterresponsesystem.model.Incident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class IncidentDAO {

    public static boolean addIncident(Incident incident) {
        String sql = "INSERT INTO incidents (type, location, severity, department, status, reported_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, incident.getType());
            stmt.setString(2, incident.getLocation());
            stmt.setString(3, incident.getSeverity());
            stmt.setString(4, incident.getDepartment());
            stmt.setString(5, incident.getStatus());
            stmt.setString(6, incident.getResponder());

            // Convert LocalDateTime to java.sql.Timestamp
            Timestamp ts = Timestamp.valueOf(incident.getTimestamp());
            stmt.setTimestamp(6, ts);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ DB insert error: " + e.getMessage());
            return false;
        }
    }
    

public static boolean assignResponder(int incidentId, String responder) {
    String sql = "UPDATE incidents SET responder = ? WHERE id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, responder);
        stmt.setInt(2, incidentId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error assigning responder: " + e.getMessage());
        return false;
    }
}

public static List<Incident> getAllIncidents() {
    List<Incident> incidents = new ArrayList<>();
    String sql = "SELECT * FROM incidents ORDER BY reported_at DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Incident incident = new Incident();
            incident.setId(rs.getInt("id"));
            incident.setType(rs.getString("type"));
            incident.setLocation(rs.getString("location"));
            incident.setSeverity(rs.getString("severity"));
            incident.setDepartment(rs.getString("department"));
            incident.setStatus(rs.getString("status"));
	    incident.setResponder(rs.getString("responder"));
            // Convert SQL timestamp to LocalDateTime
            incident.setTimestamp(rs.getTimestamp("reported_at").toLocalDateTime());

            incidents.add(incident);
        }

    } catch (SQLException e) {
        System.err.println("❌ Error fetching incidents: " + e.getMessage());
    }

    return incidents;
}

public static List<Incident> getIncidentsByPriority() {
    List<Incident> incidents = new ArrayList<>();
    String sql = "SELECT * FROM incidents " +
                 "ORDER BY CASE " +
                 "  WHEN severity = 'High' THEN 1 " +
                 "  WHEN severity = 'Medium' THEN 2 " +
                 "  WHEN severity = 'Low' THEN 3 " +
                 "  ELSE 4 END, reported_at DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Incident incident = new Incident();
            incident.setId(rs.getInt("id"));
            incident.setType(rs.getString("type"));
            incident.setLocation(rs.getString("location"));
            incident.setSeverity(rs.getString("severity"));
            incident.setDepartment(rs.getString("department"));
            incident.setStatus(rs.getString("status"));
            incident.setTimestamp(rs.getTimestamp("reported_at").toLocalDateTime());
            incidents.add(incident);
        }

    } catch (SQLException e) {
        System.err.println("❌ Error fetching incidents by priority: " + e.getMessage());
    }

    return incidents;
}

public static List<Incident> getIncidentsByDepartment(String department) {
    List<Incident> incidents = new ArrayList<>();
    String sql = "SELECT * FROM incidents WHERE department = ? ORDER BY reported_at DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, department);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Incident incident = new Incident();
            incident.setId(rs.getInt("id"));
            incident.setType(rs.getString("type"));
            incident.setLocation(rs.getString("location"));
            incident.setSeverity(rs.getString("severity"));
            incident.setDepartment(rs.getString("department"));
            incident.setStatus(rs.getString("status"));
            incident.setTimestamp(rs.getTimestamp("reported_at").toLocalDateTime());
            incidents.add(incident);
        }

    } catch (SQLException e) {
        System.err.println("❌ Error filtering incidents by department: " + e.getMessage());
    }

    return incidents;
}

public static boolean updateIncidentStatus(int id, String status) {
    String sql = "UPDATE incidents SET status = ? WHERE id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, status);
        stmt.setInt(2, id);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
}