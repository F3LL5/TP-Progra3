package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuCuentasBancarias {
    ///---------------------------------ATRIBUTOS-----------------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/cuentas-bancarias";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    ///--------------------------------CONSTRUCTOR----------------------------------------------------------------------
    public MenuCuentasBancarias(String authHeader){
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }

    ///----------------------------------MENU---------------------------------------------------------------------------
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
            if (!opcion.equals("0")) Escaner.pausa(scanner);
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

    ///--------------------------------METODOS--------------------------------------------------------------------------
    ///---------------------------------GET-----------------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las cuentas bancarias... ---");

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        List<CuentaBancariaDTO> cuentas = Arrays.asList(
                new ObjectMapper().readValue(respuestaJson, CuentaBancariaDTO[].class)
        );

        if (cuentas.isEmpty()) {
            System.out.println("No se encontraron cuentas bancarias.");
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

    ///--------------------------------POST-----------------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva cuenta bancaria ---");

        System.out.print("Ingrese el id de la entidad: ");
        int id = Escaner.enteroValido(scanner);

        System.out.print("Saldo en la cuenta: ");
        double saldo = Escaner.doubleValido(scanner);

        CreateCuentaBancariaDTO cuentaBancariaDTO = new CreateCuentaBancariaDTO((long) id, BigDecimal.valueOf(saldo));
        String jsonBody = new ObjectMapper().writeValueAsString(cuentaBancariaDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                CuentaBancariaDTO.class,
                "Cuenta bancaria agregada exitosamente.",
                "Error al agregar cuenta bancaria."
        );

        result.ifPresent(obj -> {
            CuentaBancariaDTO cuentaBancaria = (CuentaBancariaDTO) obj;
            FlipTableHelper.imprimir(List.of(cuentaBancaria));
        });
    }

    ///-------------------------------DELETE----------------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la cuenta a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Cuenta bancaria con ID " + id + " eliminada exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

}
