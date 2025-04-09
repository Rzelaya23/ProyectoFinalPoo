package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

/**
 * Clase de servicio para la gestión de clientes.
 */
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Constructor con inyección del repositorio.
     *
     * @param clientRepository Repositorio para los datos de clientes.
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Registra un nuevo cliente.
     *
     * @param client El cliente a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registerClient(Client client) {
        if (client == null || client.getId() == null || client.getId().isEmpty()) {
            return false;
        }

        // Verifica si el cliente ya existe
        if (clientRepository.findById(client.getId()).isPresent()) {
            return false;
        }

        return clientRepository.save(client) != null;
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param client El cliente con la información actualizada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateClient(Client client) {
        if (client == null || client.getId() == null || client.getId().isEmpty()) {
            return false;
        }

        // Verifica si el cliente existe
        if (!clientRepository.findById(client.getId()).isPresent()) {
            return false;
        }

        return clientRepository.save(client) != null;
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param clientId El ID del cliente.
     * @return Optional con el cliente si se encuentra, vacío si no.
     */
    public Optional<Client> getClientById(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return Optional.empty();
        }

        return clientRepository.findById(clientId);
    }

    /**
     * Obtiene la lista de todos los clientes.
     *
     * @return Lista de todos los clientes.
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Elimina un cliente.
     *
     * @param clientId El ID del cliente a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean deleteClient(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return false;
        }

        return clientRepository.deleteById(clientId);
    }
}