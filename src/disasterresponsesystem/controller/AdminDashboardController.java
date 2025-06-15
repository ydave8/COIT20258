//package disasterresponsesystem.controller;
//
//import disasterresponsesystem.model.Incident;
//import disasterresponsesystem.model.IncidentData;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class AdminDashboardController {
//@FXML private TableView<Incident> incidentTable;
//@FXML private TableColumn<Incident, String> typeColumn;
//@FXML private TableColumn<Incident, String> locationColumn;
//@FXML private TableColumn<Incident, String> severityColumn;
//@FXML private TableColumn<Incident, String> departmentColumn;
//@FXML private TableColumn<Incident, String> timeColumn;
//@FXML private ComboBox<String> responderComboBox;
//@FXML private ComboBox<String> severityFilterComboBox;
//@FXML private ComboBox<String> departmentFilterComboBox;
//
//private ObservableList<Incident> incidents;
//private ObservableList<Incident> allIncidents = FXCollections.observableArrayList();
//
//@FXML
//public void initialize() {
//    responderComboBox.getItems().addAll("Responder A", "Responder B", "Responder C");
//severityFilterComboBox.getItems().addAll("Low", "Medium", "High");
//departmentFilterComboBox.getItems().addAll("Fire Dept", "Medical", "Rescue");
//    incidents = IncidentData.getIncidents();
//
//    typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
//    locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
//    severityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeverity()));
//    departmentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
//    timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));
//
//    incidentTable.setItems(incidents);
//}
//
//@FXML
//private void handleAssign() {
//    Incident selected = incidentTable.getSelectionModel().getSelectedItem();
//    String responder = responderComboBox.getValue();
//
//    if (selected == null || responder == null) {
//        showAlert(Alert.AlertType.WARNING, "Please select an incident and a responder.");
//        return;
//    }
//
//    showAlert(Alert.AlertType.INFORMATION,
//        "Responder '" + responder + "' assigned to incident: " + selected.getType());
//}
//
//@FXML
//private void handleExport() {
//    try (BufferedWriter writer = new BufferedWriter(new FileWriter("admin_incident_report.txt"))) {
//        for (Incident i : incidents) {
//            writer.write(i.toString());
//            writer.newLine();
//        }
//        showAlert(Alert.AlertType.INFORMATION, "Exported to admin_incident_report.txt");
//    } catch (IOException e) {
//        showAlert(Alert.AlertType.ERROR, "Failed to export.");
//    }
//}
//
//@FXML
//private void handleLogout() {
//    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/disasterresponsesystem/view/LoginView.fxml"));
//        Parent root = loader.load();
//        Stage stage = (Stage) incidentTable.getScene().getWindow();
//        stage.setScene(new Scene(root));
//    } catch (IOException e) {
//        showAlert(Alert.AlertType.ERROR, "Error loading login.");
//    }
//}
//
//@FXML
//private void handleApplyFilters() {
//    String severity = severityFilterComboBox.getValue();
//    String department = departmentFilterComboBox.getValue();
//
//    ObservableList<Incident> filtered = FXCollections.observableArrayList();
//
//    for (Incident i : allIncidents) {
//        boolean match = true;
//        if (severity != null && !severity.equals(i.getSeverity())) match = false;
//        if (department != null && !department.equals(i.getDepartment())) match = false;
//        if (match) filtered.add(i);
//    }
//
//    incidentTable.setItems(filtered);
//}
//
//@FXML
//private void handleResetFilters() {
//    severityFilterComboBox.getSelectionModel().clearSelection();
//    departmentFilterComboBox.getSelectionModel().clearSelection();
//    incidentTable.setItems(allIncidents);  // show all again
//}
//
//private void showAlert(Alert.AlertType type, String msg) {
//    Alert alert = new Alert(type);
//    alert.setHeaderText(null);
//    alert.setContentText(msg);
//    alert.showAndWait();
//}
//
//}

package disasterresponsesystem.controller;

import disasterresponsesystem.model.Incident;
import disasterresponsesystem.network.DRSClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboardController {

    @FXML private TableView<Incident> incidentTable;
    @FXML private TableColumn<Incident, String> typeColumn;
    @FXML private TableColumn<Incident, String> locationColumn;
    @FXML private TableColumn<Incident, String> severityColumn;
    @FXML private TableColumn<Incident, String> departmentColumn;
    @FXML private TableColumn<Incident, String> timeColumn;
    @FXML private ComboBox<String> responderComboBox;
    @FXML private ComboBox<String> severityFilterComboBox;
    @FXML private ComboBox<String> departmentFilterComboBox;
    @FXML private TableColumn<Incident, String> responderColumn;

    private ObservableList<Incident> allIncidents = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
        severityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeverity()));
        departmentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));
	responderColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResponder()));	
        responderComboBox.getItems().addAll("RescueTeam", "FireUnit1", "MedicalTeamA");
        incidentTable.setItems(allIncidents);

        severityFilterComboBox.getItems().addAll("Low", "Medium", "High");
        departmentFilterComboBox.getItems().addAll("Fire Dept", "Medical", "Rescue");

        new Thread(this::loadIncidentsFromServer).start();
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
                Incident parsed = parseIncident(line);
                if (parsed != null) tempList.add(parsed);
            }

            Platform.runLater(() -> incidentTable.setItems(tempList));
        } else {
            Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Could not connect to server.")
            );
        }
    }).start();
}
    private void loadIncidentsFromServer() {
        DRSClient client = new DRSClient();
        ObservableList<Incident> tempList = FXCollections.observableArrayList();

        if (client.connect("localhost", 9090)) {
            client.sendMessage("GET_ALL_INCIDENTS");
            List<String> lines = client.readLinesUntilEnd();
            client.disconnect();

            for (String line : lines) {
                Incident parsed = parseIncident(line);
                if (parsed != null) tempList.add(parsed);
            }

            Platform.runLater(() -> {
                allIncidents.setAll(tempList);
                incidentTable.setItems(allIncidents);
            });

        } else {
            Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Could not connect to server.")
            );
        }
    }

@FXML
private void handleAssign() {
    Incident selected = incidentTable.getSelectionModel().getSelectedItem();
    String responder = responderComboBox.getValue();

    if (selected == null || responder == null || responder.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Please select an incident and a responder.");
        return;
    }

    DRSClient client = new DRSClient();
    if (client.connect("localhost", 9090)) {
        String command = String.format("ASSIGN_RESPONDER|%d|%s", selected.getId(), responder);
        client.sendMessage(command);
        client.disconnect();
        showAlert(Alert.AlertType.INFORMATION, "Assigned!");
        refreshIncidents(); // Refresh after update
    } else {
        showAlert(Alert.AlertType.ERROR, "Could not connect to server.");
    }
}

    private Incident parseIncident(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 8) return null;

            Incident i = new Incident();
            i.setId(Integer.parseInt(parts[0]));
            i.setType(parts[1]);
            i.setLocation(parts[2]);
            i.setSeverity(parts[3]);
            i.setDepartment(parts[4]);
            i.setStatus(parts[5]);
            i.setTimestamp(LocalDateTime.parse(parts[6]));
            i.setResponder(parts[7]);
            return i;
        } catch (Exception e) {
            System.err.println("Parse failed: " + e.getMessage());
            return null;
        }
    }

    
    @FXML
    private void handleApplyFilters() {
        String severity = severityFilterComboBox.getValue();
        String department = departmentFilterComboBox.getValue();

        ObservableList<Incident> filtered = FXCollections.observableArrayList();

        for (Incident i : allIncidents) {
            boolean match = true;
            if (severity != null && !severity.equals(i.getSeverity())) match = false;
            if (department != null && !department.equals(i.getDepartment())) match = false;
            if (match) filtered.add(i);
        }

        incidentTable.setItems(filtered);
    }

    @FXML
    private void handleResetFilters() {
        severityFilterComboBox.getSelectionModel().clearSelection();
        departmentFilterComboBox.getSelectionModel().clearSelection();
        incidentTable.setItems(allIncidents);
    }

    @FXML
    private void handleExport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("admin_incident_report.txt"))) {
            for (Incident i : allIncidents) {
                writer.write(i.toString());
                writer.newLine();
            }
            showAlert(Alert.AlertType.INFORMATION, "Exported to admin_incident_report.txt");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export failed.");
        }
    }

//    @FXML
//    private void handleAssign() {
//        // Placeholder — can be implemented based on how responders are handled
//        showAlert(Alert.AlertType.INFORMATION, "Responder assigned (dummy logic).");
//    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/disasterresponsesystem/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) incidentTable.getScene().getWindow();
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