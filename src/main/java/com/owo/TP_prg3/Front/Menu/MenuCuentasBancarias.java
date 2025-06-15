package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Utilidades.Escaner;

import java.io.IOException;
import java.util.Scanner;

public class MenuCuentasBancarias {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/cuentas-bancarias";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuCuentasBancarias(String authHeader) {this.authHeader = authHeader;}

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

                case "1.2" -> listado();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE CUENTAS BANCARIAS ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                
                1.2 Listado
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las cuentas bancarias... ---");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la cuenta bancaria: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las cuentas bancarias... ---");
        HttpService.realizarPeticion("GET", API_URL + "/listado", authHeader, null);
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva cuenta bancaria ---");

        System.out.print("Ingrese el id de la entidad: ");
        int id = Escaner.enteroValido(scanner);

        // Se solicitaria el monto a la api del banco
        System.out.print("Saldo en la cuenta: ");
        double saldo = Escaner.doubleValido(scanner);


        String jsonBody = "{" +
                "\"entidadId\":" + id + ",\n" +
                "\"saldo\":" + saldo +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la cuenta a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }
}
