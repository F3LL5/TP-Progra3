package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuCuentasBancarias {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/cuentas-bancarias";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuCuentasBancarias(String authHeader){
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }

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
                
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las transacciones... ---");

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        List<CuentaBancariaDTO> cuentas = Arrays.asList(
                new ObjectMapper().readValue(respuestaJson, CuentaBancariaDTO[].class)
        );

        if (cuentas.isEmpty()) {
            System.out.println("No se encontraron transacciones.");
            return;
        }

        FlipTableHelper.imprimir(cuentas);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                CuentaBancariaDTO.class, 
                "Cuenta no  encontrado:",
                "No se encontró la cuenta con ID " + id + "."
        );

        result.ifPresent(obj -> {
            CuentaBancariaDTO cuenta = (CuentaBancariaDTO) obj;
            FlipTableHelper.imprimir(List.of(cuenta));
        });
    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva cuenta bancaria ---");

        System.out.print("Ingrese el id de la entidad: ");
        int id = Escaner.enteroValido(scanner);

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
