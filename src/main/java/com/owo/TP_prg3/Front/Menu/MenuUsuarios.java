package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.User.dto.UsuarioDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuUsuarios {
    /// ------------------------------------------ATRIBUTOS-------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/usuarios";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();

    /// -----------------------------------------CONSTRUCTORES----------------------------------------------------------
    public MenuUsuarios(String authHeader) {
        this.authHeader = authHeader;
    }

    /// --------------------------------------MENU----------------------------------------------------------------------
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- MENÚ DE GESTIÓN DE USUARIOS ---
                    1. Crear nuevo usuario para una entidad existente
                    2. Listar todos los usuarios.
                    
                    0. Volver al menú principal
                    Ingrese una opción:""");
            opcion = Escaner.stringValido(scanner);

            switch (opcion) {
                case "1" -> crearUsuario();
                case "2" -> obtenerTodos();
                case "0" -> {
                }
                default -> System.out.println("Opción no válida.");
            }
            Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    /// ------------------------------------GET-------------------------------------------------------------------------
    private void obtenerTodos() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas los usuarios... ---");

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                UsuarioDTO.class,
                "Usuarios obtenidos exitosamente.",
                "No se pudieron obtener los Usuarios."
        );

        result.ifPresent(obj -> {
            List<UsuarioDTO> items = (List<UsuarioDTO>) obj;
            if (!items.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(items, UsuarioDTO.class));
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    /// ----------------------------------POST--------------------------------------------------------------------------
    private void crearUsuario() throws IOException, InterruptedException {
        System.out.println("\n--- Crear Nuevo Usuario ---");
        System.out.print("Ingrese el DNI de la entidad existente para la cual crear el usuario: ");
        int dni = Escaner.enteroValido(scanner);
        System.out.print("Ingrese la contraseña para el nuevo usuario: ");
        String password = Escaner.stringValido(scanner);

        // El backend se encargará de buscar la entidad, verificar su tipo y asignar el rol.
        String jsonBody = String.format("{\"dni\":%d, \"password\":\"%s\"}", dni, password);

        System.out.println("\n→ Consultando(no se olviden de sacar esto por favor): " + jsonBody);
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }
    ///--------------------------MÉTODOS AUXILIARES - HANDLERS DE ERROR Y SUCCESS---------------------------------------
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