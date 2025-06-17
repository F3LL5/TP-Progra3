package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuCuentasBancarias {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/cuentas-bancarias";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();

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

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<CuentaBancariaDTO> cuentas = Arrays.asList(
                mapper.readValue(respuestaJson, CuentaBancariaDTO[].class)
        );

        if (cuentas.isEmpty()) {
            System.out.println("No se encontraron transacciones.");
            return;
        }

        System.out.println(FlipTableConverters.fromIterable(cuentas, CuentaBancariaDTO.class));
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                ItemDTO.class, // Clase esperada para un único ítem
                "Item encontrado:",
                "No se encontró el item con ID " + id + "."
        );

        result.ifPresent(obj -> {
            // Se asume que si hay un resultado, es un único ItemDTO
            ItemDTO item = (ItemDTO) obj;
            System.out.println(FlipTableConverters.fromIterable(List.of(item), ItemDTO.class));
        });
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

    /// METODOS HANDLERS DE ERRORES
    private Optional<Object> handleResponse(HttpResponse<String> response, Class<?> clazz, String successMessage, String errorMessage) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode >= 200 && statusCode < 300) { // Códigos de éxito (2xx)
            System.out.println(successMessage); // El mensaje de éxito aún se imprime aquí
            if (responseBody != null && !responseBody.isBlank()) {
                try {
                    if (responseBody.startsWith("[")) { // Asumimos que es una lista
                        // Se utiliza mapper.getTypeFactory().constructCollectionType para List<?>
                        List<?> items = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
                        return Optional.of(items); // Retorna la lista parseada
                    } else { // Asumimos que es un objeto único
                        Object item = mapper.readValue(responseBody, clazz);
                        return Optional.of(item); // Retorna el objeto parseado
                    }
                } catch (IOException e) {
                    System.err.println("Error al parsear la respuesta JSON: " + e.getMessage());
                    return Optional.empty(); // Retorna Optional vacío si hay error de parseo
                }
            } else {
                System.out.println("La respuesta del servidor está vacía.");
                return Optional.empty(); // Retorna Optional vacío si la respuesta está vacía
            }
        } else { // Códigos de error
            handleErrorResponse(statusCode, responseBody);
            System.err.println(errorMessage);
            return Optional.empty(); // Retorna Optional vacío en caso de error
        }
    }

    private void handleErrorResponse(int statusCode, String responseBody) {
        System.err.println("Error HTTP - Código: " + statusCode);
        try {
            // Attempt to parse the error response as a Map
            Map<String, Object> errorMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
            });

            if (errorMap.containsKey("errores")) { // For MethodArgumentNotValidException (400)
                List<String> errors = (List<String>) errorMap.get("errores");
                System.err.println("Detalles de la validación:");
                errors.forEach(System.err::println);
            } else if (errorMap.containsKey("mensaje")) { // For custom exceptions
                System.err.println("Mensaje: " + errorMap.get("mensaje"));
            } else if (errorMap.containsKey("error")) { // Generic Spring Boot errors
                System.err.println("Error: " + errorMap.get("error"));
                System.err.println("Ruta: " + errorMap.get("path"));
            } else {
                System.err.println("Respuesta de error no reconocida: " + responseBody);
            }
        } catch (IOException e) {
            System.err.println("Error al parsear el cuerpo del error: " + e.getMessage());
            System.err.println("Cuerpo de la respuesta original: " + responseBody);
        }
    }

}
