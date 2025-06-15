package disasterresponsesystem;

import disasterresponsesystem.model.Incident;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class MainController {

    @FXML
    private TextField typeField;

    @FXML
    private TextField locationField;

    @FXML
    private ComboBox<String> severityBox;

    @FXML
    private ComboBox<String> departmentBox;

    @FXML
    private TableView<Incident> incidentTable;

    @FXML
    private TableColumn<Incident, String> typeColumn, locationColumn, severityColumn, departmentColumn;

    @FXML
    private TableColumn<Incident, String> timeColumn;

    @FXML
    private PieChart disasterPieChart;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Incident> searchTable;

    @FXML
    private TableColumn<Incident, String> searchTypeColumn, searchLocationColumn, searchSeverityColumn, searchDepartmentColumn;

    private ObservableList<Incident> incidents;

    @FXML
    public void initialize() {
        severityBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
        departmentBox.setItems(FXCollections.observableArrayList("Fire", "Police", "Medical", "Electricity", "Rescue"));

        incidents = FXCollections.observableArrayList();

        // Incident table setup
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));
        incidentTable.setItems(incidents);

        // Search result table setup
        searchTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        searchLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        searchSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        searchDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Initialize chart
        updatePieChart();
    }

    @FXML
    private void handleAddIncident() {
        String type = typeField.getText();
        String location = locationField.getText();
        String severity = severityBox.getValue();
        String department = departmentBox.getValue();

        if (type == null || type.trim().isEmpty()
                || location == null || location.trim().isEmpty()
                || severity == null || department == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
            return;
        }

        Incident incident = new Incident(type, location, severity, department, LocalDateTime.now());
        incidents.add(incident);

        typeField.clear();
        locationField.clear();
        severityBox.setValue(null);
        departmentBox.setValue(null);

        updatePieChart(); // Update chart on new incident
    }

    @FXML
    private void handleExport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("incident_report.txt"))) {
            for (Incident inc : incidents) {
                writer.write("Type: " + inc.getType()
                        + ", Location: " + inc.getLocation()
                        + ", Severity: " + inc.getSeverity()
                        + ", Department: " + inc.getDepartment()
                        + ", Time: " + inc.getTimestamp());
                writer.newLine();
            }
            showAlert(Alert.AlertType.INFORMATION, "Report exported to incident_report.txt");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to export report.");
        }
    }

    @FXML
    private void handleRefreshChart() {
        updatePieChart();
    }

    private void updatePieChart() {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        long fire = incidents.stream().filter(i -> "Fire".equals(i.getDepartment())).count();
        long police = incidents.stream().filter(i -> "Police".equals(i.getDepartment())).count();
        long medical = incidents.stream().filter(i -> "Medical".equals(i.getDepartment())).count();
        long electricity = incidents.stream().filter(i -> "Electricity".equals(i.getDepartment())).count();
        long rescue = incidents.stream().filter(i -> "Rescue".equals(i.getDepartment())).count();

        if (fire > 0) chartData.add(new PieChart.Data("Fire", fire));
        if (police > 0) chartData.add(new PieChart.Data("Police", police));
        if (medical > 0) chartData.add(new PieChart.Data("Medical", medical));
        if (electricity > 0) chartData.add(new PieChart.Data("Electricity", electricity));
        if (rescue > 0) chartData.add(new PieChart.Data("Rescue", rescue));

        disasterPieChart.setData(chartData);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a search query.");
            return;
        }

        ObservableList<Incident> filtered = FXCollections.observableArrayList();
        for (Incident inc : incidents) {
            if (inc.getType().toLowerCase().contains(query)
                    || inc.getLocation().toLowerCase().contains(query)) {
                filtered.add(inc);
            }
        }

        searchTable.setItems(filtered);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
