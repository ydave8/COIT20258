package disasterresponsesystem;

import disasterresponsesystem.model.Incident;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartFeatureController {

    @FXML
    private PieChart disasterPieChart;

    private List<Incident> incidentList;

    public void setIncidentData(List<Incident> incidents) {
        this.incidentList = incidents;
        updateChart();
    }

    private void updateChart() {
        Map<String, Integer> typeCount = new HashMap<>();
        for (Incident incident : incidentList) {
            String type = incident.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        disasterPieChart.setData(pieChartData);
    }
}
