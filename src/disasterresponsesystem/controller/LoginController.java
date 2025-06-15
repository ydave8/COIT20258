package disasterresponsesystem.controller;

import disasterresponsesystem.model.User;
import disasterresponsesystem.model.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("CITIZEN", "RESPONDER", "ADMIN");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String roleText = roleComboBox.getValue();

        if (username.isEmpty() || roleText == null) {
            showAlert(Alert.AlertType.ERROR, "Please enter a username and select a role.");
            return;
        }

        try {
            UserRole role = UserRole.valueOf(roleText);
            String fxmlFile = "";

            switch (role) {
                case CITIZEN:
                    fxmlFile = "/disasterresponsesystem/view/CitizenDashboard.fxml";
                    break;
                case RESPONDER:
                    fxmlFile = "/disasterresponsesystem/view/ResponderDashboard.fxml";
                    break;
                case ADMIN:
                    fxmlFile = "/disasterresponsesystem/view/AdminDashboard.fxml";
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent dashboardRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle(role + " Dashboard");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error loading dashboard: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
