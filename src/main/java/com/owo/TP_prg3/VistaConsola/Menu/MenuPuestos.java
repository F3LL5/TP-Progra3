package com.owo.TP_prg3.VistaConsola.Menu;

import com.owo.TP_prg3.VistaConsola.HttpService;

import java.io.IOException;
import java.util.Scanner;

public class MenuPuestos {
    private static final String API_URL = "http://localhost:8080/api/puestos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuPuestos(String authHeader) {this.authHeader = authHeader;}

    public void gestionar() throws IOException{
        String opcion;
        do {
            mostrar_menu();
            opcion = scanner.next();
            switch (opcion) {
                case "1" -> obtener_todos();
                case "2" -> buscar_x_id();
                case "3" -> agregar();
                case "4" -> eliminar();
                case "5" -> modificar();
                case "0" -> {}
                default -> System.out.println("OPCIÓN INVÁLIDA. VUELVA A INTENTAR");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrar_menu() {
        System.out.println("""
                --- MENÚ DE GESTIÓN DE PUESTOS ---
                OPCIONES:
                1. Obtener todos los puestos
                2. Buscar por ID
                3. Agregar puesto
                4. Eliminar puesto
                5. Modificar puesto
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:\s""");
    }

    private void obtener_todos() throws IOException {
        System.out.println("OBTENIENDO PUESTOS...");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscar_x_id() throws IOException {
        System.out.println("INGRESE ID DEL PUESTO: ");
        String id = scanner.next();
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void agregar() throws IOException {
        System.out.println("AGREGAR NUEVO PUESTO");
        System.out.println("Ingrese NOMBRE: "); String nombre = scanner.next();
        System.out.println("Ingrese ID dueño: "); String duenioId = scanner.next();

        String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"duenioId\": %s}",
                nombre, duenioId
        );
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    private void eliminar() throws IOException {
        System.out.println("Ingrese ID del puesto que desea eliminar: ");
        String id = scanner.next();
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    private void modificar() throws IOException {
        System.out.println("Ingrese ID del puesto que desea modificar: ");
        String id = scanner.next();
        System.out.println("Ingrese los nuevos datos: ");
        System.out.println("Nuevo NOMBRE: "); String nombre = scanner.next();

        StringBuilder jsonBodyBuilder = new StringBuilder("{");
        if (!nombre.isEmpty()) jsonBodyBuilder.append(String.format("\"nombre\":\"%s\",", nombre));

        if (jsonBodyBuilder.length() > 1) {
            jsonBodyBuilder.deleteCharAt(jsonBodyBuilder.length() - 1);
        }
        jsonBodyBuilder.append("}");

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBodyBuilder.toString());
    }
}
