
package disasterresponsesystem.controller;

import disasterresponsesystem.model.Incident;
import disasterresponsesystem.network.DRSClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class CitizenDashboardController {

    @FXML private TextField typeField, locationField;
    @FXML private ComboBox<String> severityComboBox, departmentComboBox;
    @FXML private TableView<Incident> incidentTable;
    @FXML private TableColumn<Incident, String> typeColumn;
    @FXML private TableColumn<Incident, String> locationColumn;
    @FXML private TableColumn<Incident, String> severityColumn;
    @FXML private TableColumn<Incident, String> departmentColumn;
    @FXML private TableColumn<Incident, String> timeColumn;

    private final ObservableList<Incident> incidentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        severityComboBox.getItems().addAll("Low", "Medium", "High");
        departmentComboBox.getItems().addAll("Fire Dept", "Medical", "Rescue");

        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
        severityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeverity()));
        departmentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));

        incidentTable.setItems(incidentList);

        // Load incidents from server
        new Thread(this::refreshIncidents).start();
    }

    @FXML
    private void handleAddIncident() {
        String type = typeField.getText().trim();
        String location = locationField.getText().trim();
        String severity = severityComboBox.getValue();
        String department = departmentComboBox.getValue();

        if (type.isEmpty() || location.isEmpty() || severity == null || department == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all fields.");
            return;
        }

        String command = String.format("ADD_INCIDENT|%s|%s|%s|%s|CitizenGUI", type, location, severity, department);
        DRSClient client = new DRSClient();
        if (client.connect("localhost", 9090)) {
//            String response = client.sendMessage(command);
            client.sendMessage(command);
            client.disconnect();
            showAlert(Alert.AlertType.INFORMATION, "Added!");
            refreshIncidents();
        } else {
            showAlert(Alert.AlertType.ERROR, "Could not connect to server.");
        }

        // Clear form
        typeField.clear();
        locationField.clear();
        severityComboBox.getSelectionModel().clearSelection();
        departmentComboBox.getSelectionModel().clearSelection();
    }




private Incident parseIncident(String line) {
    try {
        String[] parts = line.split("\\|");
        if (parts.length < 7) return null;

        Incident i = new Incident();
        i.setId(Integer.parseInt(parts[0]));
        i.setType(parts[1]);
        i.setLocation(parts[2]);
        i.setSeverity(parts[3]);
        i.setDepartment(parts[4]);
        i.setStatus(parts[5]);
        i.setTimestamp(LocalDateTime.parse(parts[6]));
        return i;
    } catch (Exception e) {
        System.err.println("Parse failed: " + e.getMessage());
        return null;
    }
}


private void refreshIncidents() {
    new Thread(() -> {
        DRSClient client = new DRSClient();
        ObservableList<Incident> tempList = FXCollections.observableArrayList();

        if (client.connect("localhost", 9090)) {
            client.sendMessage("GET_ALL_INCIDENTS");
            List<String> lines = client.readLinesUntilEnd();
            client.disconnect();

            for (String line : lines) {
                System.out.println("📥 Received: " + line);  // Debugging
                Incident parsed = parseIncident(line);
                if (parsed != null) {
                    tempList.add(parsed);
                }
            }

            Platform.runLater(() -> incidentList.setAll(tempList));

        } else {
            Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Could not connect to server.")
            );
        }
    }).start();
}


//private void refreshIncidents() {
//    DRSClient client = new DRSClient();
//    if (client.connect("localhost", 9090)) {
//        client.sendMessage("GET_ALL_INCIDENTS");
//        new Thread(() -> {
//            ObservableList<Incident> tempList = FXCollections.observableArrayList();
//            for (String line : client.readLinesUntilEnd()) {
//                Incident parsed = parseIncident(line);
//                if (parsed != null) {
//                    tempList.add(parsed);
//                }
//            }
//            client.disconnect();
//
//            // Now update the UI thread
//            Platform.runLater(() -> {
//                incidentList.setAll(tempList);
//            });
//        }).start();
//    } else {
//        showAlert(Alert.AlertType.ERROR, "Could not connect to server.");
//    }
//}

//    private void refreshIncidents() {
//        DRSClient client = new DRSClient();
//        if (client.connect("localhost", 9090)) {
//            client.sendMessage("GET_ALL_INCIDENTS");
//            try {
//                String line;
//                incidentList.clear();
//                while ((line = client.in.readLine()) != null) {
//                    if (line.equals("END_OF_LIST")) break;
//                    // Parse each line into an Incident object (format must match server toString)
//                    Incident parsed = parseIncident(line);
//                    if (parsed != null) incidentList.add(parsed);
//                }
//            } catch (IOException e) {
//                showAlert(Alert.AlertType.ERROR, "Error reading incidents.");
//            }
//            client.disconnect();
//        }
//    }
//
//    
    
    
//    private void refreshIncidents() {
//    DRSClient client = new DRSClient();
//    if (client.connect("localhost", 9090)) {
//        client.sendMessage("GET_ALL_INCIDENTS");
//        incidentList.clear();
//        for (String line : client.readLinesUntilEnd()) {
//            Incident parsed = parseIncident(line);
//            if (parsed != null) incidentList.add(parsed);
//        }
//        client.disconnect();
//    } else {
//        showAlert(Alert.AlertType.ERROR, "Could not connect to server.");
//    }
//}
    
    
//    private Incident parseIncident(String line) {
//        try {
//            // Sample format: Incident{id=1, type='Flood', location='Riverside', severity='High', department='Rescue', status='Pending', timestamp=2025-06-14T20:00}
//            String[] parts = line.split(", ");
//            String type = parts[1].split("=")[1].replace("'", "");
//            String location = parts[2].split("=")[1].replace("'", "");
//            String severity = parts[3].split("=")[1].replace("'", "");
//            String dept = parts[4].split("=")[1].replace("'", "");
//            String timestamp = parts[6].split("=")[1].replace("}", "");
//
//            Incident i = new Incident();
//            i.setType(type);
//            i.setLocation(location);
//            i.setSeverity(severity);
//            i.setDepartment(dept);
//            i.setTimestamp(LocalDateTime.parse(timestamp));
//            return i;
//
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @FXML
    private void handleExport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("citizen_incident_report.txt"))) {
            for (Incident i : incidentList) {
                writer.write(i.toString());
                writer.newLine();
            }
            showAlert(Alert.AlertType.INFORMATION, "Exported to citizen_incident_report.txt");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export failed.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/disasterresponsesystem/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) typeField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Logout failed.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}