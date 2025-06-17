package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Menu.MenuAuditoria.MenuHistorial_Duenio;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuPuestos {
    private static final String API_URL = "http://localhost:8080/api/puestos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();

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
                case "6" -> filtrarYOrdenar();
                case "7" -> {
                    MenuHistorial_Duenio auditoria=new MenuHistorial_Duenio(authHeader);
                    auditoria.gestionar();
                }
                case "0" -> {}
                default -> System.out.println("OPCIÓN INVÁLIDA. VUELVA A INTENTAR");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrar_menu() {
        System.out.print("""
                
                --- MENÚ DE GESTIÓN DE PUESTOS ---
                OPCIONES:
                1. Obtener todos los puestos
                2. Buscar por ID
                3. Agregar puesto
                4. Eliminar puesto
                5. Modificar puesto
                6. Filtrar y Ordenar por NOMBRE de puesto
                7. Historial de Duenios
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:""");
    }

    /// GET

    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("OBTENIENDO PUESTOS...");
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        List<PuestoDTO> puestos = Arrays.asList(mapper.readValue(respuestaJson, PuestoDTO[].class));

        System.out.println(FlipTableConverters.fromIterable(puestos, PuestoDTO.class));
   }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.println("INGRESE ID DEL PUESTO: ");
        String id = Escaner.stringValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        PuestoDTO puesto= mapper.readValue(respuestaJson, PuestoDTO.class);

        System.out.println(FlipTableConverters.fromIterable(List.of(puesto), PuestoDTO.class));
    }

    private void filtrarYOrdenar () throws IOException, InterruptedException {
        System.out.println("--- Fitrar y ordenar por nombre ---");
        System.out.println("Filtrar por NOMBRE de puesto o dejar vacío: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isBlank()) nombre = null;

        System.out.println("Ordenar de forma... (asc / desc) o dejar vacío: ");
        String sortDir = scanner.nextLine().trim();
        if (sortDir.isBlank()) sortDir = null;

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenarPorNombre?");
        if (nombre != null) urlBuilder.append("nombre=").append(nombre).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                ItemDTO.class,
                "Items filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los items."
        );

        result.ifPresent(obj -> {
            List<ItemDTO> items = (List<ItemDTO>) obj;
            if (!items.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(items, ItemDTO.class));
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    /// POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO PUESTO");
        System.out.print("Ingrese NOMBRE: ");
        String nombre = Escaner.stringValido(scanner);

        System.out.print("Ingrese ID dueño: ");
        int duenioId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese COMISIÓN (por ejemplo 0.15): ");
        Double comision = Escaner.doubleValido(scanner);

        String jsonBody = "{" +
                "\"nombre\":\"" + nombre + "\"," +
                "\"duenioId\":" + duenioId + "," +
                "\"comision\":" + comision +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    /// DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.println("Ingrese ID del puesto que desea eliminar: ");
        String id = Escaner.stringValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    /// PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese ID del puesto que desea modificar: ");
        String id = Escaner.stringValido(scanner);
        System.out.println();

        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Nombre
            2. Dueño (Desasociar/Cambiar)
            3. Comisión
            0. Cancelar
            Ingrese una opción:""");
        int opcion = Escaner.enteroValido(scanner);

        String jsonBody = "";
        if (opcion == 0) {
            System.out.println("Modificación cancelada.");
        } else if (opcion < 1 || opcion > 3) {
            System.out.println("Opción no válida.");
            return;
        }

        System.out.print("Ingrese el nuevo valor: ");
        switch (opcion){
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\"" +  nombre + "\"}" ;
            }
            case 2 -> {
                System.out.print("(0 para desasociar): ");
                Long idDuenio = Escaner.enteroValido(scanner).longValue();
                jsonBody = "{\"duenioId\":"  + idDuenio + "}";
            }
            case 3 -> {
                double comision = Escaner.doubleValido(scanner);
                jsonBody = "{\"comision\":" + comision + "}";
            }
        }
        System.out.println(jsonBody);
        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);
    }

    /// METODOS HANDLER
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
