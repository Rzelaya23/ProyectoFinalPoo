package com.queuemanagementsystem.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * Representa una categoría de servicio que los clientes pueden solicitar.
 */
public class Category {
    private int id;
    private String name;
    private String description;
    private String prefix;
    private boolean active;
    // La cola de tickets se marca como transient para excluirla de la serialización
    private transient Queue<Ticket> ticketQueue;
    private List<Employee> assignedEmployees;

    /**
     * Constructor por defecto.
     */
    public Category() {
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
        this.active = true;
    }

    /**
     * Constructor parametrizado con campos esenciales.
     *
     * @param id Identificador único de la categoría.
     * @param name Nombre de la categoría.
     * @param prefix Prefijo de la categoría para los códigos de tickets.
     */
    public Category(int id, String name, String prefix) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.active = true;
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
    }

    /**
     * Constructor completo con todos los campos.
     *
     * @param id Identificador único de la categoría.
     * @param name Nombre de la categoría.
     * @param description Descripción de la categoría.
     * @param prefix Prefijo de la categoría para los códigos de tickets.
     * @param active Indica si la categoría está activa.
     */
    public Category(int id, String name, String description, String prefix, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.active = active;
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
    }

    /**
     * Agrega un ticket a la cola.
     *
     * @param ticket El ticket a agregar.
     * @return true si el ticket fue agregado exitosamente, false en caso contrario.
     */
    public boolean addTicketToQueue(Ticket ticket) {
        if (ticket != null && active) {
            if (ticketQueue == null) {
                ticketQueue = new LinkedList<>();
            }
            return ticketQueue.offer(ticket);
        }
        return false;
    }

    /**
     * Obtiene el siguiente ticket en la cola.
     *
     * @return El siguiente ticket o null si la cola está vacía.
     */
    public Ticket getNextTicket() {
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return ticketQueue.poll();
    }

    /**
     * Cuenta la cantidad de tickets pendientes en la cola.
     *
     * @return El número de tickets pendientes.
     */
    public int countPendingTickets() {
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return ticketQueue.size();
    }

    /**
     * Activa la categoría, permitiendo agregar nuevos tickets.
     *
     * @return true indicando que la categoría fue activada.
     */
    public boolean activate() {
        this.active = true;
        return true;
    }

    /**
     * Desactiva la categoría, impidiendo agregar nuevos tickets.
     *
     * @return true indicando que la categoría fue desactivada.
     */
    public boolean deactivate() {
        this.active = false;
        return true;
    }

    /**
     * Asigna un empleado a esta categoría.
     *
     * @param employee El empleado a asignar.
     * @return true si el empleado fue asignado exitosamente, false en caso contrario.
     */
    public boolean assignEmployee(Employee employee) {
        if (employee != null && !assignedEmployees.contains(employee)) {
            return assignedEmployees.add(employee);
        }
        return false;
    }

    /**
     * Elimina un empleado de esta categoría.
     *
     * @param employee El empleado a eliminar.
     * @return true si el empleado fue eliminado exitosamente, false en caso contrario.
     */
    public boolean removeEmployee(Employee employee) {
        return assignedEmployees.remove(employee);
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Employee> getAssignedEmployees() {
        return new ArrayList<>(assignedEmployees); // Devuelve una copia para mantener la encapsulación
    }

    /**
     * Revisa los tickets en la cola sin retirarlos.
     *
     * @return Una lista de tickets en la cola.
     */
    public List<Ticket> peekTicketQueue() {
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return new ArrayList<>(ticketQueue); // Devuelve una copia para mantener la encapsulación
    }

    /**
     * Compara esta categoría con otro objeto para determinar igualdad.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    /**
     * Genera un código hash para esta categoría.
     *
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Devuelve una representación en cadena de esta categoría.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", active=" + active +
                ", pendingTickets=" + (ticketQueue != null ? ticketQueue.size() : 0) +
                ", assignedEmployees=" + assignedEmployees.size() +
                '}';
    }
}