package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.NotificationSystem;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.ClientRepository;

import java.util.Optional;

/**
 * Clase de servicio para gestionar las notificaciones a clientes y pantallas.
 */
public class NotificationService {
    private final NotificationSystem notificationSystem;
    private final ClientRepository clientRepository;

    /**
     * Constructor con dependencias.
     *
     * @param notificationSystem Sistema de notificaciones para pantallas.
     * @param clientRepository Repositorio para datos de clientes.
     */
    public NotificationService(NotificationSystem notificationSystem, ClientRepository clientRepository) {
        this.notificationSystem = notificationSystem;
        this.clientRepository = clientRepository;
    }

    /**
     * Notifica a un cliente que su ticket está siendo atendido.
     *
     * @param ticket El ticket que se encuentra en proceso.
     * @param stationNumber Número de la estación donde debe acudir el cliente.
     * @return true si la notificación se envió correctamente, false en caso contrario.
     */
    public boolean notifyClientTicketInProgress(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        // Actualiza la pantalla
        boolean displayUpdated = notificationSystem.displayTicket(ticket, stationNumber);

        // Genera una alerta visual
        boolean alertGenerated = notificationSystem.generateVisualAlert(ticket);

        // También notifica al cliente directamente si es posible
        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());
        boolean clientNotified = false;

        if (clientOpt.isPresent()) {
            String message = "Su ticket " + ticket.getCode() + " está siendo atendido en la estación " + stationNumber;
            clientNotified = clientOpt.get().receiveAlert(message);
        }

        return displayUpdated && alertGenerated;
    }

    /**
     * Notifica a un cliente que su ticket ha sido creado.
     *
     * @param ticket El ticket recién creado.
     * @return true si la notificación fue enviada correctamente, false en caso contrario.
     */
    public boolean notifyClientTicketCreated(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Su ticket " + ticket.getCode() + " ha sido creado para " +
                (ticket.getCategory() != null ? ticket.getCategory().getName() : "servicio general");

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Notifica a un cliente sobre su posición actual en la fila.
     *
     * @param ticket El ticket correspondiente.
     * @param position La posición actual en la fila (basada en 1).
     * @return true si la notificación fue enviada correctamente, false en caso contrario.
     */
    public boolean notifyClientQueuePosition(Ticket ticket, int position) {
        if (ticket == null || position < 1) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Su ticket " + ticket.getCode() + " está actualmente en la posición " + position + " de la fila.";

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Actualiza la pantalla principal con un mensaje personalizado.
     *
     * @param message El mensaje a mostrar.
     * @return true si la pantalla fue actualizada correctamente, false en caso contrario.
     */
    public boolean updateMainDisplay(String message) {
        return notificationSystem.updateDisplay(message);
    }

    /**
     * Limpia el contenido de la pantalla de notificaciones.
     *
     * @return true si la pantalla fue limpiada correctamente, false en caso contrario.
     */
    public boolean clearDisplay() {
        return notificationSystem.clearDisplay();
    }

    /**
     * Obtiene el mensaje actualmente mostrado en pantalla.
     *
     * @return El mensaje actual de la pantalla.
     */
    public String getCurrentDisplayMessage() {
        return notificationSystem.getDisplayMessage();
    }

    /**
     * Obtiene el ticket actualmente mostrado en pantalla.
     *
     * @return El código del ticket actualmente mostrado.
     */
    public String getCurrentTicket() {
        return notificationSystem.getCurrentTicket();
    }

    /**
     * Obtiene el número de estación actualmente mostrado en pantalla.
     *
     * @return El número de la estación actual.
     */
    public int getCurrentStation() {
        return notificationSystem.getCurrentStation();
    }
}