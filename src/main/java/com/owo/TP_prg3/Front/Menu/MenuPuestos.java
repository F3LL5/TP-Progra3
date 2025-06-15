package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.util.Scanner;

public class MenuPuestos {
    private static final String API_URL = "http://localhost:8080/api/puestos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuPuestos(String authHeader) {this.authHeader = authHeader;}

    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrar_menu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtener_todos();
                case "2" -> buscar_x_id();
                case "3" -> agregar();
                case "4" -> eliminar();
                case "5" -> modificar();
                case "6" -> buscar_x_nombre();
                case "7" -> ordenar_x_nombre();
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
                6. Buscar por NOMBRE
                7. Ordenar por NOMBRES
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:""");
    }

    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("OBTENIENDO PUESTOS...");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.println("INGRESE ID DEL PUESTO: ");
        String id = Escaner.stringValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO PUESTO");
        System.out.print("Ingrese NOMBRE: "); String nombre = Escaner.stringValido(scanner);
        System.out.print("Ingrese ID dueño: "); String duenioId = Escaner.stringValido(scanner);

        String jsonBody = "{" +
                "\"nombre\":\"" + nombre + "\",\n" +
                "\"duenioId\":" + duenioId +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    private void eliminar() throws IOException, InterruptedException {
        System.out.println("Ingrese ID del puesto que desea eliminar: ");
        String id = Escaner.stringValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    private void modificar() throws IOException, InterruptedException {
        System.out.println("Ingrese ID del puesto que desea modificar: ");
        String id = Escaner.stringValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Dueño (Desasociar/Cambiar)
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
                System.out.print("(Vacío para desasociar): ");
                String duenioId = Escaner.stringValido(scanner);
                jsonBody = "{\"duenioId\":"  + duenioId + "}";
            }
            case 0 -> {}
            default -> {
                System.out.println("Opcion no válida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);
    }

    private void buscar_x_nombre() throws IOException, InterruptedException {
        System.out.print("--- Ingrese NOMBRE a buscar: ");
        String nombre = Escaner.stringValido(scanner);

        String url = API_URL + "/buscarPorNombre?nombre=" + nombre;
        HttpService.realizarPeticion("GET", url, authHeader, null);
    }

    private void ordenar_x_nombre () throws IOException, InterruptedException {
        System.out.print("Dirección de orden... (asc / desc): ");
        String sortDir = Escaner.stringValido(scanner);

        String url = API_URL + "/ordenarPorNombre?sortDir=" + sortDir;
        HttpService.realizarPeticion("GET", url, authHeader, null);
    }
}
