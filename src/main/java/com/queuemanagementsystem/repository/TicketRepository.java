package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Ticket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para operaciones de acceso a datos de tickets.
 */
public interface TicketRepository {
    /**
     * Guarda un ticket en el repositorio
     *
     * @param ticket El ticket a guardar
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean save(Ticket ticket);

    /**
     * Busca un ticket por su código
     *
     * @param code El código del ticket a buscar
     * @return Optional con el ticket si se encuentra, vacío en caso contrario
     */
    Optional<Ticket> findByCode(String code);

    /**
     * Obtiene todos los tickets del repositorio
     *
     * @return Lista de todos los tickets
     */
    List<Ticket> findAll();

    /**
     * Busca tickets por ID de cliente
     *
     * @param clientId El ID del cliente a buscar
     * @return Lista de tickets para el cliente especificado
     */
    List<Ticket> findByClientId(String clientId);

    /**
     * Busca tickets por ID de categoría
     *
     * @param categoryId El ID de la categoría a buscar
     * @return Lista de tickets para la categoría especificada
     */
    List<Ticket> findByCategoryId(int categoryId);

    /**
     * Busca tickets por estado
     *
     * @param status El estado a buscar
     * @return Lista de tickets con el estado especificado
     */
    List<Ticket> findByStatus(String status);

    /**
     * Busca tickets generados entre tiempos específicos
     *
     * @param start Hora de inicio
     * @param end Hora de fin
     * @return Lista de tickets generados entre la hora de inicio y fin
     */
    List<Ticket> findByGenerationTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Elimina un ticket por su código
     *
     * @param code El código del ticket a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteByCode(String code);

    /**
     * Actualiza un ticket existente
     *
     * @param ticket El ticket con la información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Ticket ticket);

    /**
     * Guarda todos los tickets en el almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todos los tickets desde el almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();
}