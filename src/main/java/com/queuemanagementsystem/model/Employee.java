package com.queuemanagementsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un empleado que atiende a los clientes según su turno.
 * Extiende la clase base User.
 */
public class Employee extends User {
    private String availabilityStatus; // "AVAILABLE", "BUSY", "PAUSED", "OFFLINE"
    private transient Station assignedStation; // Se marca como transient
    private List<Ticket> attendedTickets;

    /**
     * Constructor por defecto.
     */
    public Employee() {
        super();
        this.availabilityStatus = "OFFLINE";
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Constructor parametrizado.
     *
     * @param id Identificador único del empleado.
     * @param name Nombre completo del empleado.
     * @param password Contraseña de autenticación del empleado.
     */
    public Employee(String id, String name, String password) {
        super(id, name, password);
        this.availabilityStatus = "OFFLINE";
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Constructor completo con todos los campos.
     *
     * @param id Identificador único del empleado.
     * @param name Nombre completo del empleado.
     * @param password Contraseña de autenticación del empleado.
     * @param availabilityStatus Estado actual de disponibilidad.
     * @param assignedStation Estación donde trabaja el empleado.
     */
    public Employee(String id, String name, String password, String availabilityStatus, Station assignedStation) {
        super(id, name, password);
        this.availabilityStatus = availabilityStatus;
        this.assignedStation = assignedStation;
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Marca el ticket actual como completado.
     *
     * @param ticket El ticket que se está atendiendo.
     * @return true si fue exitoso, false en caso contrario.
     */
    public boolean markTicketAsCompleted(Ticket ticket) {
        if (ticket != null && "IN_PROGRESS".equals(ticket.getStatus())) {
            ticket.setStatus("COMPLETED");
            ticket.setCompletionTime(LocalDateTime.now());
            this.attendedTickets.add(ticket);
            this.availabilityStatus = "AVAILABLE";
            return true;
        }
        return false;
    }

    /**
     * Atiende al siguiente cliente en la cola.
     *
     * @param ticket El siguiente ticket a atender.
     * @return true si el empleado comenzó la atención, false en caso contrario.
     */
    public boolean attendNextClient(Ticket ticket) {
        if (ticket != null && "WAITING".equals(ticket.getStatus()) && "AVAILABLE".equals(this.availabilityStatus)) {
            ticket.setStatus("IN_PROGRESS");
            ticket.setAttentionTime(LocalDateTime.now());
            this.availabilityStatus = "BUSY";
            return true;
        }
        return false;
    }

    /**
     * Pausa la asignación de nuevos tickets a este empleado.
     *
     * @return true si el estado fue cambiado a PAUSED, false en caso contrario.
     */
    public boolean pauseAssignment() {
        if (!"BUSY".equals(this.availabilityStatus)) {
            this.availabilityStatus = "PAUSED";
            return true;
        }
        return false;
    }

    /**
     * Reanuda la asignación de tickets a este empleado.
     *
     * @return true si el estado fue cambiado a AVAILABLE, false en caso contrario.
     */
    public boolean resumeAttention() {
        if ("PAUSED".equals(this.availabilityStatus) || "OFFLINE".equals(this.availabilityStatus)) {
            this.availabilityStatus = "AVAILABLE";
            return true;
        }
        return false;
    }

    /**
     * Recupera la información del ticket de un cliente.
     *
     * @param ticket El ticket del cual obtener información.
     * @return Una cadena con la información del cliente o null si el ticket es inválido.
     */
    public String getClientInformation(Ticket ticket) {
        if (ticket != null) {
            return "Ticket: " + ticket.getCode() +
                    "\nCategoría: " + (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A") +
                    "\nHora de generación: " + ticket.getGenerationTime() +
                    "\nTiempo de espera: " + ticket.calculateWaitingTime() + " minutos";
        }
        return null;
    }

    /**
     * Genera un resumen de los tickets atendidos por este empleado durante la sesión actual.
     *
     * @return Una cadena con el resumen de atención.
     */
    public String getAttentionSummary() {
        StringBuilder summary = new StringBuilder("Resumen de atención para " + getName() + ":\n");
        summary.append("Total de tickets atendidos: ").append(attendedTickets.size()).append("\n");

        double totalServiceTime = 0;
        for (Ticket ticket : attendedTickets) {
            if (ticket.getAttentionTime() != null && ticket.getCompletionTime() != null) {
                long serviceTimeMinutes = java.time.Duration.between(
                        ticket.getAttentionTime(), ticket.getCompletionTime()).toMinutes();
                totalServiceTime += serviceTimeMinutes;
            }
        }

        if (!attendedTickets.isEmpty()) {
            summary.append("Tiempo promedio de atención: ")
                    .append(String.format("%.2f", totalServiceTime / attendedTickets.size()))
                    .append(" minutos\n");
        }

        return summary.toString();
    }

    // Getters y Setters

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Station getAssignedStation() {
        return assignedStation;
    }

    public void setAssignedStation(Station assignedStation) {
        this.assignedStation = assignedStation;
    }

    public List<Ticket> getAttendedTickets() {
        return new ArrayList<>(attendedTickets);  // Devuelve una copia para mantener la encapsulación
    }

    /**
     * Devuelve una representación en cadena de este empleado.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "Employee{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", station=" + (assignedStation != null ? assignedStation.getNumber() : "None") +
                ", attendedTickets=" + attendedTickets.size() +
                '}';
    }
}