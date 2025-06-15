package disasterresponsesystem.controller;

import disasterresponsesystem.model.Incident;
import disasterresponsesystem.model.IncidentData;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.List;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import java.time.LocalDateTime;
import disasterresponsesystem.network.DRSClient;
import javafx.collections.FXCollections;

public class ResponderDashboardController {

@FXML private TableView<Incident> incidentTable;
@FXML private TableColumn<Incident, String> typeColumn;
@FXML private TableColumn<Incident, String> locationColumn;
@FXML private TableColumn<Incident, String> severityColumn;
@FXML private TableColumn<Incident, String> departmentColumn;
@FXML private TableColumn<Incident, String> statusColumn;
@FXML private TableColumn<Incident, String> timeColumn;
@FXML private ComboBox<String> statusComboBox;

private final ObservableList<Incident> incidentList = FXCollections.observableArrayList();

@FXML
public void initialize() {
    statusComboBox.getItems().addAll("Pending", "In Progress", "Resolved");

    typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
    locationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
    statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    severityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeverity()));
    departmentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
    timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));

    
    incidentTable.setItems(IncidentData.getIncidents());

    new Thread(this::loadIncidentsFromServer).start();
    
    
}

@FXML
private void handleUpdateStatus() {
    Incident selected = incidentTable.getSelectionModel().getSelectedItem();
    String newStatus = statusComboBox.getValue();
String command = String.format("UPDATE_STATUS|%d|%s", selected.getId(), newStatus);
DRSClient client = new DRSClient();
if (client.connect("localhost", 9090)) {
    client.sendMessage(command);
    client.disconnect();
    showAlert(Alert.AlertType.INFORMATION, "Added!");
    refreshIncidents(); // reload list
} else {
    showAlert(Alert.AlertType.ERROR, "Failed to connect to server.");
}


    if (selected == null || newStatus == null) {
        showAlert(Alert.AlertType.WARNING, "Select an incident and status.");
        return;
    }

    selected.setStatus(newStatus);
    incidentTable.refresh();
    showAlert(Alert.AlertType.INFORMATION, "Status updated to: " + newStatus);
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


@FXML
private void handleLogout() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/disasterresponsesystem/view/LoginView.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) incidentTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Could not return to login.");
    }
}

private void showAlert(Alert.AlertType type, String message) {
    Alert alert = new Alert(type);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

private void loadIncidentsFromServer() {
    DRSClient client = new DRSClient();
    ObservableList<Incident> tempList = FXCollections.observableArrayList();
    String department = "Rescue"; // TODO: Replace with dynamic department if login tracks it

    if (client.connect("localhost", 9090)) {
        client.sendMessage("GET_BY_DEPARTMENT|" + department);
        List<String> lines = client.readLinesUntilEnd();
        client.disconnect();

        for (String line : lines) {
            Incident parsed = parseIncident(line);
            if (parsed != null) tempList.add(parsed);
        }

        Platform.runLater(() -> incidentTable.setItems(tempList));
    } else {
        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Failed to connect to server"));
    }
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


}