package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase de servicio para la gestión de categorías de servicio.
 */
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Constructor con inyección del repositorio.
     *
     * @param categoryRepository Repositorio para el manejo de datos de categorías.
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una nueva categoría de servicio.
     *
     * @param name Nombre de la categoría.
     * @param description Descripción de la categoría.
     * @param prefix Prefijo para los códigos de ticket.
     * @return La categoría creada si fue exitosa, null en caso contrario.
     */
    public Category createCategory(String name, String description, String prefix) {
        // Verifica si ya existe una categoría con el mismo prefijo
        boolean prefixExists = categoryRepository.findAll().stream()
                .anyMatch(c -> prefix.equals(c.getPrefix()));

        if (prefixExists) {
            return null; // El prefijo debe ser único
        }

        // Generar nuevo ID
        int newId = getNextCategoryId();

        Category category = new Category(newId, name, description, prefix, true);

        Category savedCategory = categoryRepository.save(category);
        if (savedCategory != null) {
            return savedCategory;
        }

        return null;
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param category La categoría con información actualizada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateCategory(Category category) {
        if (category == null || !categoryRepository.findById(category.getId()).isPresent()) {
            return false;
        }

        // Verifica si el nuevo prefijo está duplicado
        if (categoryRepository.findAll().stream()
                .anyMatch(c -> category.getPrefix().equals(c.getPrefix()) && c.getId() != category.getId())) {
            return false;
        }

        return categoryRepository.update(category);
    }

    /**
     * Activa una categoría.
     *
     * @param categoryId ID de la categoría a activar.
     * @return true si la activación fue exitosa, false en caso contrario.
     */
    public boolean activateCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        category.activate();

        return categoryRepository.update(category);
    }

    /**
     * Desactiva una categoría.
     *
     * @param categoryId ID de la categoría a desactivar.
     * @return true si la desactivación fue exitosa, false en caso contrario.
     */
    public boolean deactivateCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        category.deactivate();

        return categoryRepository.update(category);
    }

    /**
     * Asigna un empleado a una categoría.
     *
     * @param categoryId ID de la categoría.
     * @param employee Empleado a asignar.
     * @return true si la asignación fue exitosa, false en caso contrario.
     */
    public boolean assignEmployeeToCategory(int categoryId, Employee employee) {
        if (employee == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        if (category.assignEmployee(employee)) {
            return categoryRepository.update(category);
        }

        return false;
    }

    /**
     * Elimina un empleado de una categoría.
     *
     * @param categoryId ID de la categoría.
     * @param employee Empleado a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean removeEmployeeFromCategory(int categoryId, Employee employee) {
        if (employee == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        if (category.removeEmployee(employee)) {
            return categoryRepository.update(category);
        }

        return false;
    }

    /**
     * Obtiene todas las categorías activas.
     *
     * @return Lista de categorías activas.
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las categorías (activas e inactivas).
     *
     * @return Lista de todas las categorías.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Obtiene una categoría por su ID.
     *
     * @param categoryId ID de la categoría.
     * @return Optional con la categoría si se encuentra, vacío si no.
     */
    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /**
     * Obtiene una categoría por su prefijo.
     *
     * @param prefix Prefijo de la categoría.
     * @return Optional con la categoría si se encuentra, vacío si no.
     */
    public Optional<Category> getCategoryByPrefix(String prefix) {
        return categoryRepository.findAll().stream()
                .filter(c -> prefix.equals(c.getPrefix()))
                .findFirst();
    }

    /**
     * Genera el próximo ID disponible para una nueva categoría.
     *
     * @return El siguiente ID disponible.
     */
    private int getNextCategoryId() {
        return categoryRepository.findAll().stream()
                .mapToInt(Category::getId)
                .max()
                .orElse(0) + 1;
    }
}