package disasterresponsesystem;

import disasterresponsesystem.db.IncidentDAO;
import disasterresponsesystem.model.Incident;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.Assert.*;

public class IncidentDAOTest {
    @Test
    public void testAddAndFetchIncident() {
        Incident incident = new Incident("Fire", "Zone B", "Medium", "Fire Dept", LocalDateTime.now());
        IncidentDAO.addIncident(incident);

        List<Incident> incidents = IncidentDAO.getAllIncidents();
        assertTrue(incidents.size() > 0);
    }
}