package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
                case "6" -> filtrarYOrdenar();

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
                6. Filtrar y Ordenar
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las transacciones... ---");
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Habilita el soporte para LocalDateTime

        List<TransaccionDTO> transacciones = Arrays.asList(mapper.readValue(respuestaJson, TransaccionDTO[].class));

        System.out.println(FlipTableConverters.fromIterable(transacciones, TransaccionDTO.class));
    }


    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Soporte para LocalDateTime

        TransaccionDTO transaccion = mapper.readValue(respuestaJson, TransaccionDTO.class);
        System.out.println(FlipTableConverters.fromIterable(List.of(transaccion), TransaccionDTO.class));
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar transacciones ---");

        System.out.print("Filtrar por TIPO (COMPRA, VENTA, INGRESO, EGRESO) o dejar vacío: ");
        String tipo_transaccion = scanner.nextLine().trim();
        if (tipo_transaccion.isBlank()) tipo_transaccion = null;

        System.out.print("Ordenar por 'fecha', 'monto', 'id_cuenta_origen', 'id_cuenta_destino' o dejar vacío: ");
        String sortBy = scanner.nextLine().trim();
        String sortDir = null;
        if (!sortBy.isBlank()) {
            System.out.print("Dirección de orden: 'asc' o 'desc' (dejar vacío si no aplica): ");
            sortDir = scanner.nextLine().trim();
            if (sortDir.isBlank()) sortDir = null;
        } else {
            sortBy = null;
        }

        // Construcción de la URL
        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenar?");
        if (tipo_transaccion != null) urlBuilder.append("tipo_transaccion=").append(tipo_transaccion).append("&");
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
        System.out.println("\n--- Registrar transaccion ---");

        System.out.print("Tipo de transaccion [COMPRA,VENTA,INGRESO,EGRESO]: ");
        String tipo = Escaner.stringValido(scanner);
        System.out.print("Monto: ");
        String monto = Escaner.stringValido(scanner);

        String jsonBody = "";
        if (tipo.equals("COMPRA") || tipo.equals("VENTA")){
            System.out.print("id de la cuenta comprador: ");
            Double CuentaOrigenId = Escaner.doubleValido(scanner);
            System.out.print("id de la cuenta vendedor : ");
            Double CuentaDestinoId = Escaner.doubleValido(scanner);

            jsonBody =
                    "{" +
                        "\"tipo\":\"" + tipo + "\"," +
                        "\"monto\":" + monto + "," +
                        "\"cuentaOrigenId\":" + CuentaOrigenId + "," +
                        "\"cuentaDestinoId\":" + CuentaDestinoId  +
                    "}";
            HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);

        } else if (tipo.equals("INGRESO") || tipo.equals("EGRESO")){
            System.out.print("id de la cuenta: ");
            Double CuentaDestinoId = Escaner.doubleValido(scanner);

            jsonBody =
                    "{" +
                        "\"tipo\":\"" + tipo + "\"," +
                        "\"monto\":" + monto + "," +
                        "\"cuentaDestinoId\":" + CuentaDestinoId  +
                    "}";
            HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
        } else {
            System.out.println("Tipo de transaccion incorrecta.");
        }
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
                1. Tipo [COMPRA,VENTA,INGRESO,EGRESO]
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
