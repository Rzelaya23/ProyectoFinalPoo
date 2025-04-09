package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Station;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.StationRepository;
import com.queuemanagementsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase de servicio para la gestión de estaciones de servicio.
 */
public class StationService {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor con dependencias de repositorios.
     *
     * @param stationRepository Repositorio de estaciones.
     * @param userRepository Repositorio de usuarios (para acceder a empleados).
     * @param categoryRepository Repositorio de categorías.
     */
    public StationService(StationRepository stationRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una nueva estación de servicio.
     *
     * @param stationNumber Número visible de la estación.
     * @return La estación creada si fue exitosa, null en caso contrario.
     */
    public Station createStation(int stationNumber) {
        boolean numberExists = stationRepository.findAll().stream()
                .anyMatch(s -> s.getNumber() == stationNumber);

        if (numberExists) {
            return null;
        }

        int newId = getNextStationId();
        Station station = new Station(newId, stationNumber);

        if (stationRepository.save(station)) {
            return station;
        }

        return null;
    }

    /**
     * Abre una estación para brindar servicio.
     *
     * @param stationId ID de la estación.
     * @return true si se abrió correctamente, false en caso contrario.
     */
    public boolean openStation(int stationId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent() || stationOpt.get().getAssignedEmployee() == null) {
            return false;
        }

        Station station = stationOpt.get();
        if (station.openStation()) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Cierra una estación.
     *
     * @param stationId ID de la estación.
     * @return true si fue cerrada correctamente, false en caso contrario.
     */
    public boolean closeStation(int stationId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        if (station.closeStation()) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Asigna un empleado a una estación.
     *
     * @param stationId ID de la estación.
     * @param employeeId ID del empleado.
     * @return true si fue asignado correctamente, false en caso contrario.
     */
    public boolean assignEmployeeToStation(int stationId, String employeeId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent()) {
            return false;
        }

        Optional<Employee> employeeOpt = userRepository.findById(employeeId)
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user);

        if (!employeeOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Employee employee = employeeOpt.get();

        if (employee.getAssignedStation() != null &&
                employee.getAssignedStation().getId() != stationId) {
            Station currentStation = employee.getAssignedStation();
            currentStation.setAssignedEmployee(null);
            stationRepository.update(currentStation);
        }

        station.setAssignedEmployee(employee);
        boolean stationUpdated = stationRepository.update(station);

        employee.setAssignedStation(station);
        boolean employeeUpdated = userRepository.update(employee);

        return stationUpdated && employeeUpdated;
    }

    /**
     * Agrega una categoría de servicio a una estación.
     *
     * @param stationId ID de la estación.
     * @param categoryId ID de la categoría.
     * @return true si fue agregada correctamente, false en caso contrario.
     */
    public boolean addCategoryToStation(int stationId, int categoryId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!stationOpt.isPresent() || !categoryOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Category category = categoryOpt.get();

        if (station.getSupportedCategoryIds().contains(categoryId)) {
            System.out.println("Esta categoría ya está soportada por esta estación.");
            return true;
        }

        if (station.addCategory(category)) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Elimina una categoría de una estación.
     *
     * @param stationId ID de la estación.
     * @param categoryId ID de la categoría.
     * @return true si fue eliminada correctamente, false en caso contrario.
     */
    public boolean removeCategoryFromStation(int stationId, int categoryId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!stationOpt.isPresent() || !categoryOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Category category = categoryOpt.get();

        if (station.removeCategory(category)) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Obtiene todas las estaciones abiertas.
     *
     * @return Lista de estaciones con estado "OPEN".
     */
    public List<Station> getAllOpenStations() {
        return stationRepository.findAll().stream()
                .filter(s -> "OPEN".equals(s.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las estaciones registradas.
     *
     * @return Lista de todas las estaciones.
     */
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    /**
     * Obtiene una estación por su ID.
     *
     * @param stationId ID de la estación.
     * @return Optional con la estación si se encuentra, vacío si no.
     */
    public Optional<Station> getStationById(int stationId) {
        return stationRepository.findById(stationId);
    }

    /**
     * Obtiene todas las estaciones que soportan una categoría específica.
     *
     * @param categoryId ID de la categoría.
     * @return Lista de estaciones que soportan esa categoría.
     */
    public List<Station> getStationsBySupportedCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return List.of();
        }

        return stationRepository.findAll().stream()
                .filter(station -> station.getSupportedCategoryIds().contains(categoryId))
                .collect(Collectors.toList());
    }

    /**
     * Genera el siguiente ID disponible para una estación nueva.
     *
     * @return El próximo ID disponible.
     */
    private int getNextStationId() {
        return stationRepository.findAll().stream()
                .mapToInt(Station::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Resuelve referencias cruzadas entre estaciones y empleados ya asignados.
     * Esto es útil para restaurar las relaciones luego de cargar los datos.
     */
    public void resolveReferences() {
        List<Station> stations = stationRepository.findAll();

        for (Station station : stations) {
            Employee assignedEmployee = station.getAssignedEmployee();
            if (assignedEmployee != null) {
                String employeeId = assignedEmployee.getId();
                if (employeeId != null && !employeeId.isEmpty()) {
                    userRepository.findById(employeeId).ifPresent(user -> {
                        if (user instanceof Employee) {
                            Employee employee = (Employee) user;
                            station.setAssignedEmployee(employee);
                        }
                    });
                }
            }
        }
    }
}