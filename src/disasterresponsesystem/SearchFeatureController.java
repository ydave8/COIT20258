package disasterresponsesystem;

import disasterresponsesystem.model.Incident;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class SearchFeatureController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Incident> tableView;

    @FXML
    private TableColumn<Incident, String> typeColumn;

    @FXML
    private TableColumn<Incident, String> locationColumn;

    @FXML
    private TableColumn<Incident, String> severityColumn;

    @FXML
    private TableColumn<Incident, String> departmentColumn;

    private ObservableList<Incident> masterList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Example data
        masterList.add(new Incident("Fire", "Zone A", "High", "Fire Dept", java.time.LocalDateTime.now()));
        masterList.add(new Incident("Flood", "Zone B", "Moderate", "Water Supply", java.time.LocalDateTime.now()));

        tableView.setItems(masterList);
    }

    @FXML
    public void onSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<Incident> filtered = FXCollections.observableArrayList();

        for (Incident incident : masterList) {
            if (incident.getType().toLowerCase().contains(keyword) ||
                incident.getLocation().toLowerCase().contains(keyword)) {
                filtered.add(incident);
            }
        }

        tableView.setItems(filtered);
    }
}
