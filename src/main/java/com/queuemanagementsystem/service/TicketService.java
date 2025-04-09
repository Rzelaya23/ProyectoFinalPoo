package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.ClientRepository;
import com.queuemanagementsystem.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase de servicio para gestionar la creación, asignación y finalización de tickets.
 */
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    /**
     * Constructor con las dependencias de los repositorios.
     *
     * @param ticketRepository Repositorio de tickets.
     * @param clientRepository Repositorio de clientes.
     * @param categoryRepository Repositorio de categorías.
     * @param notificationService Servicio para el envío de notificaciones.
     */
    public TicketService(TicketRepository ticketRepository, ClientRepository clientRepository,
                         CategoryRepository categoryRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.clientRepository = clientRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    /**
     * Crea un nuevo ticket para un cliente.
     *
     * @param clientId ID del cliente.
     * @param categoryId ID de la categoría del servicio.
     * @return El ticket creado si fue exitoso, null en caso contrario.
     */
    public Ticket createTicket(String clientId, int categoryId) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!clientOpt.isPresent() || !categoryOpt.isPresent()) {
            return null;
        }

        Client client = clientOpt.get();
        Category category = categoryOpt.get();

        if (!category.isActive()) {
            return null;
        }

        Ticket ticket = new Ticket(category, clientId);

        if (ticketRepository.save(ticket)) {
            category.addTicketToQueue(ticket);
            categoryRepository.update(category);
            return ticket;
        }

        return null;
    }

    /**
     * Asigna el siguiente ticket disponible a un empleado.
     *
     * @param employee Empleado disponible.
     * @return El ticket asignado si fue exitoso, null si no hay tickets.
     */
    public Ticket assignNextTicket(Employee employee) {
        if (employee == null || !"AVAILABLE".equals(employee.getAvailabilityStatus())) {
            return null;
        }

        if (employee.getAssignedStation() == null) {
            return null;
        }

        List<Integer> categoryIds = employee.getAssignedStation().getSupportedCategoryIds();
        List<Category> supportedCategories = new ArrayList<>();

        for (Integer categoryId : categoryIds) {
            categoryRepository.findById(categoryId).ifPresent(supportedCategories::add);
        }

        for (Category category : supportedCategories) {
            Ticket nextTicket = category.getNextTicket();

            if (nextTicket != null && employee.attendNextClient(nextTicket)) {
                nextTicket.changeStatus("IN_PROGRESS");
                nextTicket.setAttentionTime(LocalDateTime.now());
                ticketRepository.update(nextTicket);
                notificationService.notifyClientTicketInProgress(nextTicket, employee.getAssignedStation().getNumber());
                return nextTicket;
            }
        }

        return null;
    }

    /**
     * Finaliza un ticket que está siendo atendido por un empleado.
     *
     * @param ticket Ticket que se desea finalizar.
     * @param employee Empleado que lo está atendiendo.
     * @return true si se completó con éxito, false en caso contrario.
     */
    public boolean completeTicket(Ticket ticket, Employee employee) {
        if (ticket == null || employee == null || !"IN_PROGRESS".equals(ticket.getStatus())) {
            return false;
        }

        if (employee.markTicketAsCompleted(ticket)) {
            ticket.changeStatus("COMPLETED");
            ticket.setCompletionTime(LocalDateTime.now());
            return ticketRepository.update(ticket);
        }

        return false;
    }

    /**
     * Cancela un ticket en estado de espera.
     *
     * @param ticketCode Código del ticket a cancelar.
     * @return true si el ticket fue cancelado correctamente, false en caso contrario.
     */
    public boolean cancelTicket(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        ticket.setStatus("CANCELLED");

        Category category = ticket.getCategory();
        if (category != null) {
            category.peekTicketQueue().remove(ticket); // Asume que esta operación es segura.
            categoryRepository.update(category);
        }

        return ticketRepository.update(ticket);
    }

    /**
     * Obtiene todos los tickets en espera para una categoría específica.
     *
     * @param categoryId ID de la categoría.
     * @return Lista de tickets en espera.
     */
    public List<Ticket> getWaitingTicketsByCategory(int categoryId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCategory() != null &&
                        ticket.getCategory().getId() == categoryId &&
                        "WAITING".equals(ticket.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los tickets asociados a un cliente.
     *
     * @param clientId ID del cliente.
     * @return Lista de tickets del cliente.
     */
    public List<Ticket> getTicketsByClient(String clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    /**
     * Obtiene todos los tickets atendidos por un empleado.
     *
     * @param employeeId ID del empleado.
     * @return Lista de tickets en progreso o completados.
     */
    public List<Ticket> getTicketsAttendedByEmployee(String employeeId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus().equals("IN_PROGRESS") ||
                        ticket.getStatus().equals("COMPLETED"))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un ticket por su código único.
     *
     * @param code Código del ticket.
     * @return Optional con el ticket si se encuentra, vacío si no.
     */
    public Optional<Ticket> getTicketByCode(String code) {
        return ticketRepository.findByCode(code);
    }

    /**
     * Calcula la posición en la fila de un ticket en espera.
     *
     * @param ticketCode Código del ticket.
     * @return Posición en la fila (empezando desde 1), o -1 si no está en espera.
     */
    public int getTicketQueuePosition(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return -1;
        }

        Ticket ticket = ticketOpt.get();
        Category category = ticket.getCategory();

        if (category == null) {
            return -1;
        }

        List<Ticket> queue = category.peekTicketQueue();
        for (int i = 0; i < queue.size(); i++) {
            if (ticket.equals(queue.get(i))) {
                return i + 1;
            }
        }

        return -1;
    }
}