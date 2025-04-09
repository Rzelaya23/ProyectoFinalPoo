package com.queuemanagementsystem.model;

/**
 * Representa a un administrador responsable de la configuración y gestión del sistema.
 * Extiende la clase base User.
 */
public class Administrator extends User {
    private int accessLevel;

    /**
     * Constructor por defecto.
     */
    public Administrator() {
        super();
        this.accessLevel = 1; // Nivel de acceso predeterminado
    }

    /**
     * Constructor parametrizado con los campos básicos del usuario.
     *
     * @param id Identificador único del administrador.
     * @param name Nombre completo del administrador.
     * @param password Contraseña de autenticación del administrador.
     */
    public Administrator(String id, String name, String password) {
        super(id, name, password);
        this.accessLevel = 1; // Nivel de acceso predeterminado
    }

    /**
     * Constructor completo con todos los campos.
     *
     * @param id Identificador único del administrador.
     * @param name Nombre completo del administrador.
     * @param password Contraseña de autenticación del administrador.
     * @param accessLevel Nivel de permiso del administrador.
     */
    public Administrator(String id, String name, String password, int accessLevel) {
        super(id, name, password);
        this.accessLevel = accessLevel;
    }

    /**
     * Configura una categoría de servicio.
     *
     * @param category La categoría a configurar.
     * @return true si la categoría fue configurada exitosamente, false en caso contrario.
     */
    public boolean configureCategory(Category category) {
        // Esto típicamente implicaría lógica de validación y persistencia.
        // Por ahora, se asume válida si tiene nombre y prefijo.
        return category != null && category.getName() != null &&
                !category.getName().isEmpty() && category.getPrefix() != null &&
                !category.getPrefix().isEmpty();
    }

    /**
     * Crea una nueva estación de servicio.
     *
     * @param station La estación a crear.
     * @return true si la estación fue creada exitosamente, false en caso contrario.
     */
    public boolean createStation(Station station) {
        // Esto típicamente implicaría lógica de validación y persistencia.
        return station != null && station.getNumber() > 0;
    }

    /**
     * Elimina una estación de servicio.
     *
     * @param station La estación a eliminar.
     * @return true si la estación fue eliminada exitosamente, false en caso contrario.
     */
    public boolean removeStation(Station station) {
        // Esto típicamente implicaría lógica de validación y persistencia.
        return station != null;
    }

    /**
     * Supervisa las estadísticas del servicio.
     *
     * @return Una cadena con la información actual de las estadísticas.
     */
    public String superviseStatistics() {
        // Esto típicamente consultaría el servicio de estadísticas.
        return "Funcionalidad de supervisión de estadísticas accedida por el administrador: " + getName();
    }

    /**
     * Genera un informe de productividad.
     *
     * @param reportType El tipo de informe a generar (por ejemplo, "DAILY", "WEEKLY").
     * @return Una cadena que contiene el informe de productividad.
     */
    public String generateProductivityReport(String reportType) {
        // Esto típicamente consultaría el servicio de estadísticas para obtener los datos específicos del informe.
        StringBuilder report = new StringBuilder();
        report.append("=== ").append(reportType).append(" INFORME DE PRODUCTIVIDAD ===\n");
        report.append("Generado por: ").append(getName()).append("\n");
        report.append("Fecha: ").append(java.time.LocalDate.now()).append("\n");
        report.append("Este es un marcador de posición para el contenido real del informe");

        return report.toString();
    }

    // Getters y Setters

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Devuelve una representación en cadena de este Administrador.
     *
     * @return Una representación en cadena.
     */
    @Override
    public String toString() {
        return "Administrator{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", accessLevel=" + accessLevel +
                '}';
    }
}