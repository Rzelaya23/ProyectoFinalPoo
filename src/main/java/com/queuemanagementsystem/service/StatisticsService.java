package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.ServiceStatistics;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.TicketRepository;
import com.queuemanagementsystem.repository.UserRepository;
import com.queuemanagementsystem.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase de servicio para generar y gestionar estadísticas del sistema de atención.
 */
public class StatisticsService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DateTimeUtil dateTimeUtil;
    private ServiceStatistics currentDayStatistics;

    /**
     * Constructor con inyección de repositorios y utilidades.
     *
     * @param ticketRepository Repositorio de tickets.
     * @param userRepository Repositorio de usuarios.
     * @param categoryRepository Repositorio de categorías.
     * @param dateTimeUtil Utilidad para operaciones con fechas y horas.
     */
    public StatisticsService(TicketRepository ticketRepository, UserRepository userRepository,
                             CategoryRepository categoryRepository, DateTimeUtil dateTimeUtil) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.dateTimeUtil = dateTimeUtil;
        this.currentDayStatistics = new ServiceStatistics(
                dateTimeUtil.getStartOfDay(LocalDateTime.now()),
                dateTimeUtil.getEndOfDay(LocalDateTime.now())
        );

        initializeCurrentDayStatistics();
    }

    /**
     * Inicializa las estadísticas del día actual a partir de los datos existentes.
     */
    private void initializeCurrentDayStatistics() {
        LocalDateTime startOfDay = dateTimeUtil.getStartOfDay(LocalDateTime.now());
        LocalDateTime endOfDay = dateTimeUtil.getEndOfDay(LocalDateTime.now());

        List<Ticket> todayTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getGenerationTime() != null &&
                        !ticket.getGenerationTime().isBefore(startOfDay) &&
                        !ticket.getGenerationTime().isAfter(endOfDay))
                .collect(Collectors.toList());

        for (Ticket ticket : todayTickets) {
            currentDayStatistics.updateWithTicket(ticket);
        }

        updateEmployeePerformanceStatistics();
    }

    /**
     * Actualiza las estadísticas de productividad de los empleados.
     */
    private void updateEmployeePerformanceStatistics() {
        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            double ticketsPerHour = calculateEmployeeTicketsPerHour(employee);
            currentDayStatistics.updateEmployeePerformance(employee.getId(), ticketsPerHour);
        }
    }

    /**
     * Calcula el número de tickets atendidos por hora de un empleado.
     *
     * @param employee El empleado.
     * @return Número de tickets por hora.
     */
    private double calculateEmployeeTicketsPerHour(Employee employee) {
        if (employee == null) {
            return 0.0;
        }

        List<Ticket> completedTickets = employee.getAttendedTickets().stream()
                .filter(ticket -> "COMPLETED".equals(ticket.getStatus()))
                .collect(Collectors.toList());

        if (completedTickets.isEmpty()) {
            return 0.0;
        }

        double totalServiceHours = completedTickets.stream()
                .mapToDouble(ticket -> ticket.calculateServiceTime() / 60.0)
                .sum();

        if (totalServiceHours <= 0) {
            return 0.0;
        }

        return completedTickets.size() / totalServiceHours;
    }

    /**
     * Actualiza las estadísticas con un nuevo ticket.
     *
     * @param ticket El ticket a registrar.
     */
    public void updateStatistics(Ticket ticket) {
        if (ticket == null) return;

        currentDayStatistics.updateWithTicket(ticket);

        if ("COMPLETED".equals(ticket.getStatus())) {
            updateEmployeePerformanceStatistics();
        }
    }

    /**
     * Genera el reporte de estadísticas diarias.
     *
     * @return Reporte de estadísticas del día.
     */
    public String generateDailyStatistics() {
        return currentDayStatistics.generateDailyStatistics();
    }

    /**
     * Genera el reporte de estadísticas semanales.
     *
     * @return Reporte de estadísticas de la semana.
     */
    public String generateWeeklyStatistics() {
        LocalDateTime weekStart = dateTimeUtil.getStartOfWeek(LocalDateTime.now());
        LocalDateTime weekEnd = dateTimeUtil.getEndOfWeek(LocalDateTime.now());
        return generateStatisticsForPeriod(weekStart, weekEnd, "WEEKLY");
    }

    /**
     * Genera el reporte de estadísticas mensuales.
     *
     * @return Reporte de estadísticas del mes.
     */
    public String generateMonthlyStatistics() {
        LocalDateTime monthStart = dateTimeUtil.getStartOfMonth(LocalDateTime.now());
        LocalDateTime monthEnd = dateTimeUtil.getEndOfMonth(LocalDateTime.now());
        return generateStatisticsForPeriod(monthStart, monthEnd, "MONTHLY");
    }

    /**
     * Genera un reporte de estadísticas para un período específico.
     *
     * @param startDate Fecha de inicio.
     * @param endDate Fecha de fin.
     * @param periodType Tipo de período (por ejemplo, "WEEKLY", "MONTHLY").
     * @return Reporte de estadísticas del período.
     */
    private String generateStatisticsForPeriod(LocalDateTime startDate, LocalDateTime endDate, String periodType) {
        ServiceStatistics periodStats = new ServiceStatistics(startDate, endDate);

        List<Ticket> periodTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getGenerationTime() != null &&
                        !ticket.getGenerationTime().isBefore(startDate) &&
                        !ticket.getGenerationTime().isAfter(endDate))
                .collect(Collectors.toList());

        for (Ticket ticket : periodTickets) {
            periodStats.updateWithTicket(ticket);
        }

        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            List<Ticket> employeeTickets = employee.getAttendedTickets().stream()
                    .filter(ticket -> "COMPLETED".equals(ticket.getStatus()) &&
                            ticket.getCompletionTime() != null &&
                            !ticket.getCompletionTime().isBefore(startDate) &&
                            !ticket.getCompletionTime().isAfter(endDate))
                    .collect(Collectors.toList());

            double totalServiceHours = employeeTickets.stream()
                    .mapToDouble(ticket -> ticket.calculateServiceTime() / 60.0)
                    .sum();

            double ticketsPerHour = totalServiceHours > 0 ?
                    employeeTickets.size() / totalServiceHours : 0.0;

            periodStats.updateEmployeePerformance(employee.getId(), ticketsPerHour);
        }

        StringBuilder report = new StringBuilder();
        report.append("=== ").append(periodType).append(" STATISTICS REPORT ===\n");
        report.append("Period: ").append(startDate.toLocalDate()).append(" to ").append(endDate.toLocalDate()).append("\n");
        report.append("Total tickets generated: ").append(periodStats.getGeneratedTickets()).append("\n");
        report.append("Total tickets attended: ").append(periodStats.getAttendedTickets()).append("\n");
        report.append("Average waiting time: ").append(String.format("%.2f", periodStats.getAverageWaitingTime())).append(" minutes\n");
        report.append("Average service time: ").append(String.format("%.2f", periodStats.getAverageServiceTime())).append(" minutes\n");

        report.append("\nTickets por categoría:\n");
        for (Map.Entry<String, Integer> entry : periodStats.getTicketsByCategory().entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        report.append("\nRendimiento de empleados (tickets por hora):\n");
        for (Map.Entry<String, Double> entry : periodStats.getEmployeePerformance().entrySet()) {
            Optional<Employee> employeeOpt = userRepository.findById(entry.getKey())
                    .filter(user -> user instanceof Employee)
                    .map(user -> (Employee) user);

            employeeOpt.ifPresent(employee -> report.append("- ")
                    .append(employee.getName())
                    .append(": ")
                    .append(String.format("%.2f", entry.getValue()))
                    .append("\n"));
        }

        return report.toString();
    }

    /**
     * Calcula el tiempo promedio de espera por categoría.
     *
     * @param categoryId ID de la categoría.
     * @return Tiempo promedio de espera en minutos.
     */
    public double getAverageWaitingTimeByCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return 0.0;
        }

        Category category = categoryOpt.get();

        List<Ticket> categoryTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCategory() != null &&
                        ticket.getCategory().getId() == category.getId() &&
                        ticket.getAttentionTime() != null)
                .collect(Collectors.toList());

        if (categoryTickets.isEmpty()) {
            return 0.0;
        }

        double totalWaitingTime = categoryTickets.stream()
                .mapToLong(Ticket::calculateWaitingTime)
                .sum();

        return totalWaitingTime / categoryTickets.size();
    }

    /**
     * Obtiene estadísticas de productividad por empleado.
     *
     * @return Mapa con el ID del empleado como clave y su productividad como valor.
     */
    public Map<String, Double> getEmployeeProductivityStatistics() {
        Map<String, Double> productivityStats = new HashMap<>();

        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            double productivity = currentDayStatistics.calculateEmployeeProductivity(employee);
            productivityStats.put(employee.getId(), productivity);
        }

        return productivityStats;
    }

    /**
     * Obtiene las estadísticas actuales del sistema.
     *
     * @return Objeto con las estadísticas actuales del servicio.
     */
    public ServiceStatistics getCurrentStatistics() {
        return currentDayStatistics;
    }
}