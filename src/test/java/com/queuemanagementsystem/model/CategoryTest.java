package com.queuemanagementsystem.model;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Ticket;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CategoryTest {

    private Category category;
    private Employee employee1;
    private Employee employee2;

    @Before
    public void setUp() {
        category = new Category(1, "Test Category", "Test Category Description", "TST", true);
        employee1 = new Employee("emp1", "John Doe", "password123");
        employee2 = new Employee("emp2", "Jane Smith", "password456");
    }

    // Caso normal (happy path): Activar una categoría
    @Test
    public void testActivate_ShouldSetActiveToTrue() {
        // Arrange
        category.setActive(false);

        // Act
        boolean result = category.activate();

        // Assert
        assertTrue(result);
        assertTrue(category.isActive());
    }

    // Caso normal (happy path): Desactivar una categoría
    @Test
    public void testDeactivate_ShouldSetActiveToFalse() {
        // Arrange
        category.setActive(true);

        // Act
        boolean result = category.deactivate();

        // Assert
        assertTrue(result);
        assertFalse(category.isActive());
    }

    // Caso normal: Asignar un empleado a la categoría
    @Test
    public void testAssignEmployee_ShouldAddEmployeeToList() {
        // Act
        boolean result = category.assignEmployee(employee1);

        // Assert
        assertTrue(result);
        assertTrue(category.getAssignedEmployees().contains(employee1));
        assertEquals(1, category.getAssignedEmployees().size());
    }

    // Caso de error: Asignar un empleado null
    @Test
    public void testAssignEmployee_WithNullEmployee_ShouldReturnFalse() {
        // Act
        boolean result = category.assignEmployee(null);

        // Assert
        assertFalse(result);
        assertEquals(0, category.getAssignedEmployees().size());
    }

    // Caso de error: Asignar el mismo empleado dos veces
    @Test
    public void testAssignEmployee_SameEmployeeTwice_ShouldReturnFalse() {
        // Arrange
        category.assignEmployee(employee1);

        // Act
        boolean result = category.assignEmployee(employee1);

        // Assert
        assertFalse(result);
        assertEquals(1, category.getAssignedEmployees().size());
    }

    // Caso normal: Remover un empleado de la categoría
    @Test
    public void testRemoveEmployee_ShouldRemoveEmployeeFromList() {
        // Arrange
        category.assignEmployee(employee1);

        // Act
        boolean result = category.removeEmployee(employee1);

        // Assert
        assertTrue(result);
        assertFalse(category.getAssignedEmployees().contains(employee1));
        assertEquals(0, category.getAssignedEmployees().size());
    }

    // Caso normal: Remover un empleado que no está asignado
    @Test
    public void testRemoveEmployee_NotAssigned_ShouldReturnFalse() {
        // Act
        boolean result = category.removeEmployee(employee1);

        // Assert
        assertFalse(result);
    }

    // Caso normal: Agregar un ticket a la cola
    @Test
    public void testAddTicketToQueue_ShouldAddTicket() {
        // Arrange
        Ticket ticket = new Ticket(category, "CLIENT1");

        // Act
        boolean result = category.addTicketToQueue(ticket);

        // Assert
        assertTrue(result);
        assertEquals(1, category.countPendingTickets());
        assertTrue(category.peekTicketQueue().contains(ticket));
    }

    // Caso de error: Agregar un ticket null
    @Test
    public void testAddTicketToQueue_WithNullTicket_ShouldReturnFalse() {
        // Act
        boolean result = category.addTicketToQueue(null);

        // Assert
        assertFalse(result);
        assertEquals(0, category.countPendingTickets());
    }

    // Caso de error: Agregar un ticket cuando la categoría está inactiva
    @Test
    public void testAddTicketToQueue_WhenCategoryInactive_ShouldReturnFalse() {
        // Arrange
        category.setActive(false);
        Ticket ticket = new Ticket(category, "CLIENT1");

        // Act
        boolean result = category.addTicketToQueue(ticket);

        // Assert
        assertFalse(result);
        assertEquals(0, category.countPendingTickets());
    }

    // Caso normal: Obtener el siguiente ticket de la cola
    @Test
    public void testGetNextTicket_ShouldReturnAndRemoveFirstTicket() {
        // Arrange
        Ticket ticket1 = new Ticket(category, "CLIENT1");
        Ticket ticket2 = new Ticket(category, "CLIENT2");
        category.addTicketToQueue(ticket1);
        category.addTicketToQueue(ticket2);

        // Act
        Ticket result = category.getNextTicket();

        // Assert
        assertEquals(ticket1, result);
        assertEquals(1, category.countPendingTickets());
        assertFalse(category.peekTicketQueue().contains(ticket1));
        assertTrue(category.peekTicketQueue().contains(ticket2));
    }

    // Caso límite: Obtener el siguiente ticket de una cola vacía
    @Test
    public void testGetNextTicket_FromEmptyQueue_ShouldReturnNull() {
        // Act
        Ticket result = category.getNextTicket();

        // Assert
        assertNull(result);
    }

    // Caso normal: Contar tickets pendientes
    @Test
    public void testCountPendingTickets_ShouldReturnCorrectCount() {
        // Arrange
        Ticket ticket1 = new Ticket(category, "CLIENT1");
        Ticket ticket2 = new Ticket(category, "CLIENT2");
        Ticket ticket3 = new Ticket(category, "CLIENT3");

        category.addTicketToQueue(ticket1);
        category.addTicketToQueue(ticket2);
        category.addTicketToQueue(ticket3);

        // Act
        int count = category.countPendingTickets();

        // Assert
        assertEquals(3, count);
    }

    // Caso límite: Contar tickets pendientes en una cola vacía
    @Test
    public void testCountPendingTickets_EmptyQueue_ShouldReturnZero() {
        // Act
        int count = category.countPendingTickets();

        // Assert
        assertEquals(0, count);
    }

    // Caso normal: Verificar que getAssignedEmployees devuelve una copia
    @Test
    public void testGetAssignedEmployees_ShouldReturnACopy() {
        // Arrange
        category.assignEmployee(employee1);

        // Act
        List<Employee> employees = category.getAssignedEmployees();
        employees.add(employee2); // Intento modificar la lista devuelta

        // Assert
        assertEquals(1, category.getAssignedEmployees().size());
        assertFalse(category.getAssignedEmployees().contains(employee2));
    }

    // Caso normal: Verificar que peekTicketQueue devuelve una copia
    @Test
    public void testPeekTicketQueue_ShouldReturnACopy() {
        // Arrange
        Ticket ticket1 = new Ticket(category, "CLIENT1");
        category.addTicketToQueue(ticket1);

        // Act
        List<Ticket> queue = category.peekTicketQueue();
        Ticket ticket2 = new Ticket(category, "CLIENT2");
        queue.add(ticket2); // Intento modificar la lista devuelta

        // Assert
        assertEquals(1, category.countPendingTickets());
        assertEquals(2, queue.size());
    }

    // Caso normal: Verificar igualdad basada en ID
    @Test
    public void testEquals_WithSameId_ShouldReturnTrue() {
        // Arrange
        Category sameCategory = new Category(1, "Different Name", "Different Description", "DIF", false);
        Category differentCategory = new Category(2, "Test Category", "Test Category Description", "TST", true);

        // Assert
        assertEquals(category, sameCategory);
        assertNotEquals(category, differentCategory);
    }

    // Caso de error: Verificar igualdad con null u otro tipo
    @Test
    public void testEquals_WithNullOrDifferentType_ShouldReturnFalse() {
        // Assert
        assertFalse(category.equals(null));
        assertFalse(category.equals("Not a Category"));
    }
}
