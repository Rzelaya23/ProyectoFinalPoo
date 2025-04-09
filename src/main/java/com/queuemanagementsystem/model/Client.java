package com.queuemanagementsystem.model;

import java.util.Objects;

/**
 * Representa a un cliente que solicita un servicio y obtiene un ticket.
 */
public class Client {
    private String id;
    private String name;
    private String contactInfo;

    /**
     * Constructor por defecto.
     */
    public Client() {
    }

    /**
     * Constructor parametrizado.
     *
     * @param id Identificador único del cliente.
     * @param name Nombre del cliente.
     */
    public Client(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor completo con todos los campos.
     *
     * @param id Identificador único del cliente.
     * @param name Nombre del cliente.
     * @param contactInfo Información de contacto del cliente.
     */
    public Client(String id, String name, String contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    /**
     * Solicita un ticket para una categoría de servicio específica.
     *
     * @param category La categoría de servicio.
     * @return Un mensaje que indica el resultado de la solicitud.
     */
    public String requestTicket(Category category) {
        // Nota: En la implementación real, esto interactuaría con un servicio.
        if (category == null) {
            return "Categoría seleccionada no válida.";
        }

        if (!category.isActive()) {
            return "La categoría de servicio seleccionada no está disponible actualmente.";
        }

        return "La solicitud de ticket para la categoría " + category.getName() + " ha sido creada.";
    }

    /**
     * Consulta el estado de la cola para una categoría específica.
     *
     * @param category La categoría de servicio a consultar.
     * @return Una cadena que describe el estado actual de la cola.
     */
    public String checkQueueStatus(Category category) {
        // Nota: En la implementación real, esto consultaría el servicio.
        if (category == null) {
            return "Categoría seleccionada no válida.";
        }

        return "Estado de la cola para " + category.getName() +
                ":\nTickets pendientes: " + category.countPendingTickets();
    }

    // La funcionalidad cancelTicket fue eliminada según los requerimientos.

    /**
     * Recibe una notificación de alerta.
     *
     * @param message El mensaje de alerta.
     * @return true indicando que la alerta fue recibida.
     */
    public boolean receiveAlert(String message) {
        // En un sistema real, esto podría enviar un SMS, notificación push, etc.
        System.out.println("ALERTA para el cliente " + name + ": " + message);
        return true;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Compara este cliente con otro objeto para verificar igualdad.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    /**
     * Genera un código hash para este cliente.
     *
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Devuelve una representación en cadena de este cliente.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}