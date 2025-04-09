package com.queuemanagementsystem.model;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Ticket;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TicketTest {

    private Ticket ticket;
    private Category category;
    private String clientId;

    @Before
    public void setUp() {
        category = new Category(1, "Test Category", "Test Category Description", "TST", true);
        clientId = "TESTCLIENT";
        ticket = new Ticket(category, clientId);
    }

    // Caso normal (happy path): Cambio de estado válido de WAITING a IN_PROGRESS
    @Test
    public void testChangeStatus_FromWaitingToInProgress_ShouldSucceed() {
        // Act
        boolean result = ticket.changeStatus("IN_PROGRESS");

        // Assert
        assertTrue(result);
        assertEquals("IN_PROGRESS", ticket.getStatus());
        assertNotNull(ticket.getAttentionTime());
    }

    // Caso normal (happy path): Cambio de estado válido de IN_PROGRESS a COMPLETED
    @Test
    public void testChangeStatus_FromInProgressToCompleted_ShouldSucceed() {
        // Arrange
        ticket.changeStatus("IN_PROGRESS");

        // Act
        boolean result = ticket.changeStatus("COMPLETED");

        // Assert
        assertTrue(result);
        assertEquals("COMPLETED", ticket.getStatus());
        assertNotNull(ticket.getCompletionTime());
    }

    // Caso de error: Cambio de estado inválido de WAITING a COMPLETED
    @Test
    public void testChangeStatus_FromWaitingToCompleted_ShouldFail() {
        // Act
        boolean result = ticket.changeStatus("COMPLETED");

        // Assert
        assertFalse(result);
        assertEquals("WAITING", ticket.getStatus());
        assertNull(ticket.getCompletionTime());
    }

    // Caso de error: Cambio de estado con valor null
    @Test
    public void testChangeStatus_WithNullStatus_ShouldFail() {
        // Act
        boolean result = ticket.changeStatus(null);

        // Assert
        assertFalse(result);
        assertEquals("WAITING", ticket.getStatus());
    }

    // Caso de error: Cambio de estado desde un estado terminal
    @Test
    public void testChangeStatus_FromCompletedToAnyOther_ShouldFail() {
        // Arrange
        ticket.changeStatus("IN_PROGRESS");
        ticket.changeStatus("COMPLETED");

        // Act
        boolean result = ticket.changeStatus("WAITING");

        // Assert
        assertFalse(result);
        assertEquals("COMPLETED", ticket.getStatus());
    }

    // Caso normal: Cálculo de tiempo de espera con ticket en espera
    @Test
    public void testCalculateWaitingTime_ForWaitingTicket_ShouldCalculateFromNow() {
        // Arrange
        LocalDateTime generationTime = LocalDateTime.now().minusMinutes(30);
        ticket.setGenerationTime(generationTime);

        // Act
        long waitingTime = ticket.calculateWaitingTime();

        // Assert
        assertTrue(waitingTime >= 29 && waitingTime <= 31); // Permitir un pequeño margen de error
    }

    // Caso normal: Cálculo de tiempo de espera con ticket en progreso
    @Test
    public void testCalculateWaitingTime_ForInProgressTicket_ShouldCalculateFromAttentionTime() {
        // Arrange
        LocalDateTime generationTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime attentionTime = LocalDateTime.now().minusMinutes(15);

        ticket.setGenerationTime(generationTime);
        ticket.setAttentionTime(attentionTime);
        ticket.setStatus("IN_PROGRESS");

        // Act
        long waitingTime = ticket.calculateWaitingTime();

        // Assert
        assertEquals(30, waitingTime); // 45 - 15 = 30 minutos
    }

    // Caso normal: Cálculo de tiempo de servicio con ticket completado
    @Test
    public void testCalculateServiceTime_ForCompletedTicket_ShouldCalculateCorrectly() {
        // Arrange
        LocalDateTime attentionTime = LocalDateTime.now().minusMinutes(20);
        LocalDateTime completionTime = LocalDateTime.now().minusMinutes(5);

        ticket.setAttentionTime(attentionTime);
        ticket.setCompletionTime(completionTime);
        ticket.setStatus("COMPLETED");

        // Act
        long serviceTime = ticket.calculateServiceTime();

        // Assert
        assertEquals(15, serviceTime); // 20 - 5 = 15 minutos
    }

    // Caso normal: Cálculo de tiempo de servicio con ticket en progreso
    @Test
    public void testCalculateServiceTime_ForInProgressTicket_ShouldCalculateFromNow() {
        // Arrange
        LocalDateTime attentionTime = LocalDateTime.now().minusMinutes(10);

        ticket.setAttentionTime(attentionTime);
        ticket.setStatus("IN_PROGRESS");

        // Act
        long serviceTime = ticket.calculateServiceTime();

        // Assert
        assertTrue(serviceTime >= 9 && serviceTime <= 11); // Permitir un pequeño margen de error
    }

    // Caso límite: Cálculo de tiempo de servicio sin tiempo de atención
    @Test
    public void testCalculateServiceTime_WithoutAttentionTime_ShouldReturnZero() {
        // Act
        long serviceTime = ticket.calculateServiceTime();

        // Assert
        assertEquals(0, serviceTime);
    }



    // Caso normal: Verificar que el código generado tiene el formato correcto
    @Test
    public void testTicketCodeFormat_ShouldContainCategoryPrefix() {
        // Assert
        assertNotNull(ticket.getCode());
        assertTrue(ticket.getCode().startsWith(category.getPrefix()));
    }
}
