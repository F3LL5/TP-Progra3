package com.owo.TP_prg3.VistaConsola.Menu;

import com.owo.TP_prg3.VistaConsola.HttpService;

import java.io.IOException;
import java.util.Scanner;

public class MenuEntidades {

    private static final String API_URL = "http://localhost:8080/api/entidades";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuEntidades(String authHeader) {
        this.authHeader = authHeader;
    }

    /**
     * Muestra el menú de gestión de Entidades y maneja la lógica.
     */
    public void gestionar() throws IOException {
        String opcion;
        do {
            mostrarMenu();
            opcion = scanner.nextLine();
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();
                case "3" -> agregar();
                case "4" -> eliminar();
                case "5" -> modificar();
                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE ENTIDADES ---
                OPCIONES:
                1. Obtener todas
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                0. Salir
                Ingrese la opción: """);
    }

    // --- Métodos de operaciones CRUD ---

    private void obtenerTodas() throws IOException {
        System.out.println("\n--- Obteniendo todas las entidades... ---");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscarPorId() throws IOException {
        System.out.print("Ingrese el ID de la entidad: ");
        String id = scanner.nextLine();
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void agregar() throws IOException {
        System.out.println("\n--- Agregar nueva entidad ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Tipo de Entidad [DUENO_PUESTO, CLIENTE, PROVEEDOR, ADMIN]: ");
        String tipoEntidad = scanner.nextLine();
        System.out.print("Rol (ej: ADMIN, DUENO): ");
        String rol = scanner.nextLine();
        System.out.print("Edad: ");
        int edad = Integer.parseInt(scanner.nextLine());
        System.out.print("DNI: ");
        int dni = Integer.parseInt(scanner.nextLine());
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"tipoEntidad\":\"%s\", \"rol\":\"%s\", \"edad\":%d, \"dni\":%d, \"password\":\"%s\"}",
                nombre, tipoEntidad, rol, edad, dni, password
        );
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    private void eliminar() throws IOException {
        System.out.print("Ingrese el ID de la entidad a eliminar: ");
        String id = scanner.nextLine();
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    private void modificar() throws IOException {
        System.out.print("Ingrese el ID de la entidad a modificar: ");
        String id = scanner.nextLine();
        System.out.println("Ingrese los nuevos datos (deje en blanco para no modificar):");
        System.out.print("Nuevo Nombre: ");
        String nombre = scanner.nextLine();

        StringBuilder jsonBodyBuilder = new StringBuilder("{");
        if (!nombre.isEmpty()) jsonBodyBuilder.append(String.format("\"nombre\":\"%s\",", nombre));
        // Agrega aquí más campos si deseas que se puedan modificar

        if (jsonBodyBuilder.length() > 1) {
            jsonBodyBuilder.deleteCharAt(jsonBodyBuilder.length() - 1); // Elimina la última coma
        }
        jsonBodyBuilder.append("}");

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBodyBuilder.toString());
    }
}
