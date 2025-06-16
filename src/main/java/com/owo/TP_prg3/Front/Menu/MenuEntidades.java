package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuEntidades {

    private static final String API_URL = "http://localhost:8080/api/entidades";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuEntidades(String authHeader) {
        this.authHeader = authHeader;
    }

    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();
                case "3" -> agregar();
                case "4" -> eliminar();
                case "5" -> modificar();
                case "6" -> filtrarYOrdenar();
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
                6. Ordenar y Filtrar
                0. Salir
                Ingrese la opción: """);
    }

    // --- Métodos de operaciones CRUD ---

    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las entidades... ---");

        HttpResponse<String> response =  HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        List<EntidadDTO> entidades = Arrays.asList(mapper.readValue(respuestaJson, EntidadDTO[].class));

        System.out.println(FlipTableConverters.fromIterable(entidades, EntidadDTO.class));
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        String id = Escaner.stringValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        System.out.print("Rol [DUENO_PUESTO, CLIENTE, PROVEEDOR, ADMIN]: ");
        String rol = Escaner.stringValido(scanner);
        System.out.print("Edad: ");
        Integer edad = Escaner.enteroValido(scanner);
        System.out.print("DNI: ");
        Integer dni = Escaner.enteroValido(scanner);

        String jsonBody = "{" +
                "\"nombre\":\"" + nombre + "\",\n" +
                "\"tipoEntidad\":\"" + tipoEntidad + "\",\n" +
                "\"rolEntidad\":\"" + rol + "\",\n" +
                "\"edad\":" + edad + ",\n" +
                "\"dni\":" + dni +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a eliminar: ");
        String id = Escaner.stringValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a modificar: ");
        String id = Escaner.stringValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Tipo de Entidad
                3. Rol
                4. Edad
                5. DNI
                0. Cancelar
                Ingrese una opción:"""
        );
        Integer opcion = Escaner.enteroValido(scanner);

        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");
        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\"" +  nombre + "\"}" ;
            }
            case 2 -> {
                String tipoEntidad = Escaner.stringValido(scanner);
                jsonBody = "{\"tipoEntidad\":\"" + tipoEntidad + "\"}";
            }
            case 3 -> {
                System.out.print(" [DUENO_PUESTO, CLIENTE, PROVEEDOR, ADMIN]: ");
                String rol = Escaner.stringValido(scanner);
                jsonBody = "{\"rol\":\"" + rol + "\"}";
            }
            case 4 -> {
                Integer edad = Escaner.enteroValido(scanner);
                jsonBody = "{\"edad\":"  + edad + "}";
            }
            case 5 -> {
                Integer dni = Escaner.enteroValido(scanner);
                jsonBody = "{\"dni\":"  + dni + "}";
            }
            default -> {
                System.out.println("Opcion no válida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar entidades ---");
        System.out.print("Filtrar por ROL (DUENO_PUESTO, CLIENTE, PROVEEDOR) o dejar vacío: ");
        String rol = scanner.nextLine().trim();
        if (rol.isBlank()) rol = null;

        System.out.print("Ordenar por... (nombre, edad, dni, tipoEntidad) o dejar vacío: ");
        String sortBy = scanner.nextLine().trim();
        if (sortBy.isBlank()) sortBy = null;

        System.out.print("Dirección de orden... (asc / desc) o dejar vacío: ");
        String sortDir = scanner.nextLine().trim();
        if (sortDir.isBlank()) sortDir = null;

        // 4. Construir la query string
        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenar?");
        if (rol != null) urlBuilder.append("rol_entidad=").append(rol).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        HttpService.realizarPeticion("GET", finalUrl, authHeader, null);
    }
}
