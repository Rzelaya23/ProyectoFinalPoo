package com.queuemanagementsystem.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Recoge y procesa datos estadísticos sobre la operación del servicio.
 */
public class ServiceStatistics {
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private int generatedTickets;
    private int attendedTickets;
    private double averageWaitingTime;
    private double averageServiceTime;
    private Map<String, Integer> ticketsByCategory;
    private Map<String, Double> employeePerformance;

    /**
     * Constructor por defecto.
     */
    public ServiceStatistics() {
        this.periodStart = LocalDateTime.now();
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Constructor con fechas de inicio y fin del período.
     *
     * @param periodStart Fecha y hora de inicio del período estadístico.
     * @param periodEnd Fecha y hora de fin del período estadístico.
     */
    public ServiceStatistics(LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Genera un informe estadístico diario.
     *
     * @return Una cadena que contiene el informe de estadísticas diarias.
     */
    public String generateDailyStatistics() {
        StringBuilder report = new StringBuilder();
        report.append("=== INFORME ESTADÍSTICO DIARIO ===\n");
        report.append("Período: ").append(periodStart.toLocalDate()).append("\n");
        report.append("Total de tickets generados: ").append(generatedTickets).append("\n");
        report.append("Total de tickets atendidos: ").append(attendedTickets).append("\n");
        report.append("Tiempo promedio de espera: ").append(String.format("%.2f", averageWaitingTime)).append(" minutos\n");
        report.append("Tiempo promedio de atención: ").append(String.format("%.2f", averageServiceTime)).append(" minutos\n");

        report.append("\nTickets por categoría:\n");
        for (Map.Entry<String, Integer> entry : ticketsByCategory.entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return report.toString();
    }

    /**
     * Calcula la productividad de un empleado.
     *
     * @param employee El empleado del cual se desea calcular la productividad.
     * @return Un valor de productividad (tickets por hora).
     */
    public double calculateEmployeeProductivity(Employee employee) {
        if (employee == null) {
            return 0.0;
        }

        String employeeId = employee.getId();
        return employeePerformance.getOrDefault(employeeId, 0.0);
    }

    /**
     * Actualiza las estadísticas con un nuevo ticket.
     *
     * @param ticket El ticket a agregar a las estadísticas.
     */
    public void updateWithTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        // Actualiza el conteo de tickets
        generatedTickets++;

        if ("COMPLETED".equals(ticket.getStatus())) {
            attendedTickets++;

            // Actualiza los tiempos de espera y atención
            long waitingTime = ticket.calculateWaitingTime();
            long serviceTime = ticket.calculateServiceTime();

            // Recalcula los promedios
            averageWaitingTime = ((averageWaitingTime * (attendedTickets - 1)) + waitingTime) / attendedTickets;
            averageServiceTime = ((averageServiceTime * (attendedTickets - 1)) + serviceTime) / attendedTickets;
        }

        // Actualiza las estadísticas por categoría
        if (ticket.getCategory() != null) {
            String categoryName = ticket.getCategory().getName();
            ticketsByCategory.put(categoryName, ticketsByCategory.getOrDefault(categoryName, 0) + 1);
        }
    }

    /**
     * Actualiza la estadística de productividad de un empleado.
     *
     * @param employeeId ID del empleado.
     * @param ticketsPerHour Medida de productividad (tickets por hora).
     */
    public void updateEmployeePerformance(String employeeId, double ticketsPerHour) {
        employeePerformance.put(employeeId, ticketsPerHour);
    }

    // Getters y Setters

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public int getGeneratedTickets() {
        return generatedTickets;
    }

    public void setGeneratedTickets(int generatedTickets) {
        this.generatedTickets = generatedTickets;
    }

    public int getAttendedTickets() {
        return attendedTickets;
    }

    public void setAttendedTickets(int attendedTickets) {
        this.attendedTickets = attendedTickets;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(double averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public Map<String, Integer> getTicketsByCategory() {
        return new HashMap<>(ticketsByCategory);  // Devuelve una copia para mantener la encapsulación
    }

    public Map<String, Double> getEmployeePerformance() {
        return new HashMap<>(employeePerformance);  // Devuelve una copia para mantener la encapsulación
    }

    /**
     * Devuelve una representación en cadena de este objeto ServiceStatistics.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "ServiceStatistics{" +
                "periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", generatedTickets=" + generatedTickets +
                ", attendedTickets=" + attendedTickets +
                ", averageWaitingTime=" + averageWaitingTime +
                ", averageServiceTime=" + averageServiceTime +
                '}';
    }
}