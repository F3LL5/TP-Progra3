package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuPedidos {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/pedidos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuPedidos(String authHeader) {this.authHeader = authHeader;}

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
                case "6" -> filtrarYOrdenar();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE PEDIDO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                6. Filtrar y Ordenar VENTAS, COMPRAS, INGRESOS Y EGRESOS
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas los pedido... ---");
        HttpResponse<String> response =  HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();
        ObjectMapper mapper = new ObjectMapper();
        List<PedidoDTO> pedidos = Arrays.asList(mapper.readValue(respuestaJson, PedidoDTO[].class));

        System.out.println(FlipTableConverters.fromIterable(pedidos, PedidoDTO.class));
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response =   HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        PedidoDTO pedido= mapper.readValue(respuestaJson, PedidoDTO.class);

        System.out.println(FlipTableConverters.fromIterable(List.of(pedido), PedidoDTO.class));

    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR PEDIDOS ---");

        System.out.println("""
            TIPO DE TRANSACCIÓN:
            [1] COMPRA
            [2] VENTA
            [3] INGRESO
            [4] EGRESO
            [0] SIN FILTRO
            Opción:""");
        int estado = Escaner.enteroValido(scanner);
        String estado_pedido = switch (estado) {
            case 1 -> "COMPRA";
            case 2 -> "VENTA";
            case 3 -> "INGRESO";
            case 4 -> "EGRESO";
            default -> null;
        };

        System.out.println("""
        ORDENAR POR:
        [1] FECHA
        [2] MONTO
        [3] ID DE CUENTA ORIGEN
        [4] ID DE CUENTA DESTINO
        [0] SIN ORDENAMIENTO
        Opción:""");
        int by = Escaner.enteroValido(scanner);
        String sortBy = switch (by) {
            case 1 -> "fecha";
            case 2 -> "monto";
            case 3 -> "id_cuenta_origen";
            case 4 -> "id_cuenta_destino";
            default -> null;
        };

        System.out.println("""
        DIRECCIÓN DE ORDEN:
        [1] ASCENDENTE
        [2] DESCENDENTE
        [0] SIN DIRECCIÓN
        Opción:""");
        int dir = Escaner.enteroValido(scanner);
        String sortDir = switch (dir) {
            case 1 -> "asc";
            case 2 -> "desc";
            default -> null;
        };

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenar?");
        if (estado_pedido != null) urlBuilder.append("tipo_transaccion=").append(estado_pedido).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);

        HttpResponse<String> response = HttpService.realizarPeticion("GET", finalUrl, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Soporte para LocalDateTime

        List<TransaccionDTO> transacciones = Arrays.asList(mapper.readValue(respuestaJson, TransaccionDTO[].class));

        if (transacciones.isEmpty()) {
            System.out.println("No se encontraron transacciones con esos filtros.");
            return;
        }

        System.out.println(FlipTableConverters.fromIterable(transacciones, TransaccionDTO.class));
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar pedido ---");

        System.out.print("Ingrese el id de la transaccion: ");
        int transaccionId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese el id del puesto: ");
        int puestoId = Escaner.enteroValido(scanner);

        String jsonBody =
                   "{" +
                       "\"transaccionId\":" + transaccionId  +
                       ",\"puestoId\":" + puestoId  +
                   "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID de la transaccion
                2. ID del puestoId
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
                int puestoId = Escaner.enteroValido(scanner);
                jsonBody = "{\"puestoId\":"  + puestoId + "}";
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
