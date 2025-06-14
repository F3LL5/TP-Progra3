package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Utilidades.Escaner;
import com.owo.TP_prg3.Front.HttpService;
import java.io.IOException;
import java.util.Scanner;

public class MenuItem {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/items";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuItem(String authHeader) {this.authHeader = authHeader;}

    //Menu
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

                case "1.2" -> listado();

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
                
                1.2 Listado
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las entidades... ---");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las entidades... ---");
        HttpService.realizarPeticion("GET", API_URL + "/lista", authHeader, null);
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nuevo item ---");

        System.out.print("Nombre del item: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Categoria del item: ");
        String categoria= Escaner.stringValido(scanner);
        System.out.print("Costo del item: ");
        Double costo = Escaner.doubleValido(scanner);

        String jsonBody = "{" +
                "\"nombre\":\"" + nombre + "\",\n" +
                "\"categoria\":\"" + categoria + "\",\n" +
                "\"costo\":" + costo + "\n" +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Categoria
                3. Costo
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\"" +  nombre + "\"}" ;
            }
            case 2 -> {
                String categoria = Escaner.stringValido(scanner);
                jsonBody = "{\"categoria\":\"" + categoria + "\"}";
            }
            case 3 -> {
                Double costo = Escaner.doubleValido(scanner);
                jsonBody = "{\"costo\":"  + costo + "\"}";
            }
            case 0 -> {}
            default -> {
                System.out.println("Opcion no valida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);
    }

   }