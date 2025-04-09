package com.queuemanagementsystem;

import com.queuemanagementsystem.model.*;
import com.queuemanagementsystem.repository.*;
import com.queuemanagementsystem.service.*;
import com.queuemanagementsystem.ui.MainMenu;
import com.queuemanagementsystem.util.DateTimeUtil;

import java.util.Scanner;

/**
 * Clase principal del Sistema de Gestión de Turnos.
 * Inicializa todos los componentes necesarios y arranca la aplicación.
 */
public class Main {
    /**
     * Método principal para iniciar la aplicación
     *
     * @param args Argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        // Crear repositorios
        UserRepository userRepository = new JsonUserRepository();
        ClientRepository clientRepository = new JsonClientRepository();
        CategoryRepository categoryRepository = new JsonCategoryRepository();
        StationRepository stationRepository = new JsonStationRepository();
        TicketRepository ticketRepository = new JsonTicketRepository();

        // Crear clases utilitarias
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        NotificationSystem notificationSystem = new NotificationSystem();

        // Crear servicios
        UserService userService = new UserService(userRepository);
        ClientService clientService = new ClientService(clientRepository);
        NotificationService notificationService = new NotificationService(notificationSystem, clientRepository);
        CategoryService categoryService = new CategoryService(categoryRepository);
        StationService stationService = new StationService(stationRepository, userRepository, categoryRepository);
        TicketService ticketService = new TicketService(ticketRepository, clientRepository,
                categoryRepository, notificationService);
        StatisticsService statisticsService = new StatisticsService(ticketRepository, userRepository,
                categoryRepository, dateTimeUtil);

        // Crear scanner para entrada del usuario
        Scanner scanner = new Scanner(System.in);

        // Crear e iniciar menú principal
        MainMenu mainMenu = new MainMenu(scanner, clientService, categoryService,
                ticketService, userService, stationService, statisticsService);

        // Inicializar datos de ejemplo si los repositorios están vacíos
        initializeSampleData(userRepository, clientRepository, categoryRepository, stationRepository);

        // Resolver referencias entre entidades
        stationService.resolveReferences();

        // Iniciar la aplicación
        mainMenu.start(scanner);

        // Cerrar recursos
        scanner.close();
    }

    /**
     * Inicializa datos de ejemplo si los repositorios están vacíos
     *
     * @param userRepository Repositorio de usuarios
     * @param clientRepository Repositorio de clientes
     * @param categoryRepository Repositorio de categorías
     * @param stationRepository Repositorio de estaciones
     */
    private static void initializeSampleData(UserRepository userRepository, ClientRepository clientRepository,
                                             CategoryRepository categoryRepository, StationRepository stationRepository) {
        // Inicializar datos de ejemplo solo si los repositorios están vacíos
        if (userRepository.findAll().isEmpty() && clientRepository.findAll().isEmpty() &&
                categoryRepository.findAll().isEmpty() && stationRepository.findAll().isEmpty()) {

            System.out.println("Inicializando datos de ejemplo...");

            // Crear administrador de ejemplo
            Administrator admin = new Administrator("admin", "System Administrator", "admin123", 3);
            userRepository.save(admin);

            // Crear categorías de ejemplo
            Category generalCategory = new Category(1, "General Inquiry", "General customer inquiries", "GEN", true);
            Category billingCategory = new Category(2, "Billing", "Billing and payment inquiries", "BIL", true);
            Category technicalCategory = new Category(3, "Technical Support", "Technical issues and support", "TEC", true);
            Category complaintsCategory = new Category(4, "Complaints", "Customer complaints handling", "COM", true);

            categoryRepository.save(generalCategory);
            categoryRepository.save(billingCategory);
            categoryRepository.save(technicalCategory);
            categoryRepository.save(complaintsCategory);

            // Crear estaciones de ejemplo
            Station station1 = new Station(1, 1);
            Station station2 = new Station(2, 2);
            Station station3 = new Station(3, 3);

            stationRepository.save(station1);
            stationRepository.save(station2);
            stationRepository.save(station3);

            // Asignar categorías a estaciones
            station1.addCategory(generalCategory);
            station1.addCategory(billingCategory);
            station2.addCategory(technicalCategory);
            station3.addCategory(complaintsCategory);
            station3.addCategory(generalCategory);

            stationRepository.update(station1);
            stationRepository.update(station2);
            stationRepository.update(station3);

            // Crear empleados de ejemplo
            Employee employee1 = new Employee("emp1", "John Smith", "pass123", "OFFLINE", station1);
            Employee employee2 = new Employee("emp2", "Maria García", "pass123", "OFFLINE", station2);
            Employee employee3 = new Employee("emp3", "Carlos Rodríguez", "pass123", "OFFLINE", station3);

            userRepository.save(employee1);
            userRepository.save(employee2);
            userRepository.save(employee3);

            // Asignar empleados a categorías
            generalCategory.assignEmployee(employee1);
            billingCategory.assignEmployee(employee1);
            technicalCategory.assignEmployee(employee2);
            complaintsCategory.assignEmployee(employee3);
            generalCategory.assignEmployee(employee3);

            categoryRepository.update(generalCategory);
            categoryRepository.update(billingCategory);
            categoryRepository.update(technicalCategory);
            categoryRepository.update(complaintsCategory);

            // Crear clientes de ejemplo
            Client client1 = new Client("C001", "Ana Martinez", "ana@example.com");
            Client client2 = new Client("C002", "Luis Perez", "luis@example.com");
            Client client3 = new Client("C003", "Sofia Gutierrez", "sofia@example.com");

            clientRepository.save(client1);
            clientRepository.save(client2);
            clientRepository.save(client3);

            System.out.println("¡Datos de ejemplo inicializados exitosamente!");
            System.out.println("Inicio de sesión del Administrador - ID: admin, Contraseña: admin123");
            System.out.println("Inicio de sesión de Empleados - IDs: emp1, emp2, emp3, Contraseña: pass123");
            System.out.println();
        }
    }
}