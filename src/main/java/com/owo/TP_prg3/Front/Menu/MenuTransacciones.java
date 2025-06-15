package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Utilidades.Escaner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MenuTransacciones {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/transacciones";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuTransacciones(String authHeader) {this.authHeader = authHeader;}

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
                \n--- MENÚ DE GESTIÓN DE TRANSACCIONES ---
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
        System.out.println("\n--- Obteniendo todas las transacciones... ---");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las transacciones... ---");
        HttpService.realizarPeticion("GET", API_URL + "/listado", authHeader, null);
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar transaccion ---");

        System.out.print("Tipo de transaccion: ");
        String tipo = Escaner.stringValido(scanner);
        System.out.print("Monto: ");
        String monto = Escaner.stringValido(scanner);
        System.out.print("id de la cuenta origen: ");
        Double CuentaOrigenId = Escaner.doubleValido(scanner);
        System.out.print("id de la cuenta destino : ");
        Double CuentaDestinoId = Escaner.doubleValido(scanner);

        String jsonBody = "{" +
                "\"tipo\":\"" + tipo + "\",\n" +
                "\"monto\":" + monto + ",\n" +
                "\"cuentaOrigenId\":" + CuentaOrigenId + ",\n" +
                "\"cuentaDestinoId\":" + CuentaDestinoId  +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la transaccion a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del transaccion a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Tipo
                2. Monto
                3. Fecha
                4. ID de la cuenta origen
                5. ID de la cuenta destino
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                String tipo = Escaner.stringValido(scanner);
                jsonBody = "{\"tipo\":\"" +  tipo + "\"}" ;
            }
            case 2 -> {
                Double monto = Escaner.doubleValido(scanner);
                jsonBody = "{\"monto\":" + monto + "}";
            }
            case 3 -> {
                LocalDateTime fecha = Escaner.fechaYhora(scanner);
                jsonBody = "{\"fecha\":\"" + fecha + "\"}";
            }
            case 4 -> {
                String cuentaOrigenId = Escaner.stringValido(scanner);
                jsonBody = "{\"cuentaOrigenId\":"  + cuentaOrigenId + "}";
            }
            case 5 -> {
                String cuentaDestinoId = Escaner.stringValido(scanner);
                jsonBody = "{\"cuentaDestinoId\":"  + cuentaDestinoId + "}";
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
