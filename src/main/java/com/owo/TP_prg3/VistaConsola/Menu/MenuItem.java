package com.owo.TP_prg3.VistaConsola.Menu;

import com.owo.TP_prg3.VistaConsola.HttpService;
import java.io.IOException;
import java.util.Scanner;

public class MenuItem {
    private static final String API_URL = "http://localhost:8080/api/items";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuItem(String authHeader) {this.authHeader = authHeader;}

    public void gestionar() throws IOException{
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
                \n--- MENÚ DE GESTIÓN DE ITEMS ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                0. Salir
                Ingrese la opción: """);
}

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
    System.out.println("\n--- Agregar nuevo item ---");

    System.out.println("Nombre del item:");
    String nombre=scanner.nextLine();

    System.out.println("Categoria del item:");
    String categoria= scanner.nextLine();

    System.out.println("Costo del item:");
    double costo= Double.parseDouble(scanner.nextLine());

    String jsonBody = "{" +
            "\"nombre\":\"" + nombre + "\",\n" +
            "\"categoria\":\"" + categoria + "\",\n" +
            "\"costo\":" + costo + "\n" +
            "}";
    HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
}

    private void eliminar() throws IOException {
        System.out.print("Ingrese el ID del item a eliminar: ");
        String id = scanner.nextLine();
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    private void modificar() throws IOException {
        System.out.print("Ingrese el ID del item a modificar: ");
        String id = scanner.nextLine();
        System.out.println("Ingrese los nuevos datos :");
        System.out.print("Nuevo Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Nueva Categoria:");
        String categoria=scanner.nextLine();
        System.out.println("Nuevo Costo:");
        double costo=scanner.nextDouble();

        StringBuilder jsonBodyBuilder = new StringBuilder("{");
        if (!nombre.isEmpty()) jsonBodyBuilder.append(String.format("{\"nombre\":\"%s\", \"categoria\":\"%s\", \"costo\":\"%s\"}",
                nombre, categoria, costo));
        
        if (jsonBodyBuilder.length() > 1) {
            jsonBodyBuilder.deleteCharAt(jsonBodyBuilder.length() - 1); // Elimina la última coma
        }
        jsonBodyBuilder.append("}");

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBodyBuilder.toString());
    }


   }