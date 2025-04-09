package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Station;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para operaciones de acceso a datos de estaciones.
 */
public interface StationRepository {
    /**
     * Guarda una estación en el repositorio
     *
     * @param station La estación a guardar
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean save(Station station);

    /**
     * Busca una estación por su ID
     *
     * @param id El ID de la estación a buscar
     * @return Optional con la estación si se encuentra, vacío en caso contrario
     */
    Optional<Station> findById(int id);

    /**
     * Busca una estación por su número
     *
     * @param number El número de la estación a buscar
     * @return Optional con la estación si se encuentra, vacío en caso contrario
     */
    Optional<Station> findByNumber(int number);

    /**
     * Obtiene todas las estaciones del repositorio
     *
     * @return Lista de todas las estaciones
     */
    List<Station> findAll();

    /**
     * Obtiene todas las estaciones abiertas del repositorio
     *
     * @return Lista de todas las estaciones abiertas
     */
    List<Station> findAllOpen();

    /**
     * Elimina una estación por su ID
     *
     * @param id El ID de la estación a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(int id);

    /**
     * Actualiza una estación existente
     *
     * @param station La estación con la información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Station station);

    /**
     * Guarda todas las estaciones en el almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todas las estaciones desde el almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();
}