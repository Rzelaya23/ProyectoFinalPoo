package com.queuemanagementsystem.ui;

import java.util.Scanner;

/**
 * Interfaz base para clases de menú con funcionalidades comunes.
 */
public interface Menu {
    /**
     * Muestra las opciones del menú.
     */
    void displayMenu();

    /**
     * Procesa la opción ingresada por el usuario y ejecuta la acción correspondiente.
     *
     * @param option La opción seleccionada por el usuario.
     * @return true si el menú debe continuar, false si debe finalizar.
     */
    boolean processOption(String option);

    /**
     * Inicia el ciclo de interacción del menú.
     *
     * @param scanner El escáner para leer la entrada del usuario.
     */
    default void start(Scanner scanner) {
        boolean continueMenu = true;
        while (continueMenu) {
            displayMenu();
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            continueMenu = processOption(option);
        }
    }
}