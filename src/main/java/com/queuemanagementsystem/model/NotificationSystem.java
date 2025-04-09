package com.queuemanagementsystem.model;

/**
 * Administra las notificaciones visuales para los clientes.
 */
public class NotificationSystem {
    private String displayMessage;
    private String currentTicket;
    private int currentStation;

    /**
     * Constructor por defecto.
     */
    public NotificationSystem() {
        this.displayMessage = "Bienvenido al Sistema de Gestión de Filas";
    }

    /**
     * Muestra un ticket en la pantalla de notificación.
     *
     * @param ticket El ticket a mostrar.
     * @param stationNumber El número de estación donde debe ser atendido el ticket.
     * @return true si la pantalla fue actualizada exitosamente, false en caso contrario.
     */
    public boolean displayTicket(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        this.currentTicket = ticket.getCode();
        this.currentStation = stationNumber;
        this.displayMessage = "Ticket " + currentTicket + " por favor diríjase a la estación " + currentStation;

        // En un sistema real, esto actualizaría una pantalla física o enviaría notificaciones
        System.out.println("ACTUALIZACIÓN DE PANTALLA: " + displayMessage);

        return true;
    }

    /**
     * Genera una alerta visual para un ticket específico.
     *
     * @param ticket El ticket al que se desea alertar.
     * @return true si la alerta fue generada exitosamente, false en caso contrario.
     */
    public boolean generateVisualAlert(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        // Parpadeo de pantalla u otros indicadores visuales
        this.displayMessage = "**ALERTA** ¡El ticket " + ticket.getCode() + " está siendo llamado!";

        // En un sistema real, esto activaría efectos visuales especiales
        System.out.println("ALERTA VISUAL: " + displayMessage);

        return true;
    }

    /**
     * Actualiza la pantalla con un mensaje personalizado.
     *
     * @param message El mensaje a mostrar.
     * @return true si la pantalla fue actualizada exitosamente, false en caso contrario.
     */
    public boolean updateDisplay(String message) {
        if (message != null && !message.isEmpty()) {
            this.displayMessage = message;

            // En un sistema real, esto actualizaría una pantalla física
            System.out.println("ACTUALIZACIÓN DE PANTALLA: " + displayMessage);

            return true;
        }
        return false;
    }

    /**
     * Limpia el contenido de la pantalla.
     *
     * @return true indicando que la pantalla fue limpiada.
     */
    public boolean clearDisplay() {
        this.displayMessage = "";
        this.currentTicket = null;
        this.currentStation = 0;

        // En un sistema real, esto limpiaría una pantalla física
        System.out.println("PANTALLA LIMPIADA");

        return true;
    }

    // Getters y Setters

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getCurrentTicket() {
        return currentTicket;
    }

    public int getCurrentStation() {
        return currentStation;
    }

    /**
     * Devuelve una representación en cadena de este NotificationSystem.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "NotificationSystem{" +
                "displayMessage='" + displayMessage + '\'' +
                ", currentTicket='" + currentTicket + '\'' +
                ", currentStation=" + currentStation +
                '}';
    }
}