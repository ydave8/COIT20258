package disasterresponsesystem;

import disasterresponsesystem.model.Incident;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class IncidentTest {
private Incident incident;

@Before
public void setUp() {
    incident = new Incident("Fire", "Main Street", "High", "Fire", LocalDateTime.of(2025, 5, 17, 12, 0));
}

@Test
public void testGetType() {
    assertEquals("Fire", incident.getType());
}

@Test
public void testGetLocation() {
    assertEquals("Main Street", incident.getLocation());
}

@Test
public void testGetSeverity() {
    assertEquals("High", incident.getSeverity());
}

@Test
public void testGetDepartment() {
    assertEquals("Fire", incident.getDepartment());
}

@Test
public void testGetTimestamp() {
    assertEquals(LocalDateTime.of(2025, 5, 17, 12, 0), incident.getTimestamp());
}

@Test
public void testGetStatus() {
    assertEquals("Pending", incident.getStatus());
}

@Test
public void testSetStatus() {
    incident.setStatus("Resolved");
    assertEquals("Resolved", incident.getStatus());
}

}