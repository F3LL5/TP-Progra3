package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuDetallePedido {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/detalles-pedido";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuDetallePedido(String authHeader) {this.authHeader = authHeader;}

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

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE DETALLE PEDIDO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas los detalle de pedido... ---");
        HttpResponse<String> response =  HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();
        ObjectMapper mapper = new ObjectMapper();
        List<DetallePedidoDTO> detalles = Arrays.asList(mapper.readValue(respuestaJson, DetallePedidoDTO[].class));

        System.out.println(FlipTableConverters.fromIterable(detalles, DetallePedidoDTO.class));
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar el detalle de pedido ---");

        System.out.print("Ingrese el id del pedido: ");
        int pedidoId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese el id del item: ");
        int itemId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese la cantidad: ");
        int cantidad = Escaner.enteroValido(scanner);


        String jsonBody =
                "{" +
                    "\"pedidoId\":" + pedidoId  +
                    ",\"itemId\":" + itemId  +
                    ",\"cantidad\":" + cantidad  +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID del pedido
                2. ID del item
                3. Cantidad
                
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                int transaccionId = Escaner.enteroValido(scanner);
                jsonBody = "{\"transaccionId\":"  + transaccionId + "}";
            }
            case 2 -> {
                int itemId = Escaner.enteroValido(scanner);
                jsonBody = "{\"itemId\":"  + itemId + "}";
            }
            case 3 -> {
                int cantidad = Escaner.enteroValido(scanner);
                jsonBody = "{\"cantidad\":"  + cantidad + "}";
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
