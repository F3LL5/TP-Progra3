package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Front.Menu.MenuAuditoria.MenuHistorial_Item;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.HttpService;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class MenuItem {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/items";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();


    //Constructor
    public MenuItem(String authHeader) {
        this.authHeader = authHeader;
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
                case "5" -> modificar();

                case "1.2" -> listado();
                case "1.3" -> filtrarYordenar();

                case "6" -> {
                    MenuHistorial_Item auditoria = new MenuHistorial_Item(authHeader);
                    auditoria.gestionar();
                }

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
    System.out.print("""
                \n--- MENÚ DE GESTIÓN DE ITEMS ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar

                1.2 Listado
                1.3 filtrarYordenar

                6. Historial de costos

                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los items... ---");

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                ItemDTO.class, // Clase esperada para los ítems individuales de la lista
                "Items obtenidos exitosamente.",
                "No se pudieron obtener los items."
        );

        result.ifPresent(obj -> {
            // Si es una lista de ItemDTOs
            List<ItemDTO> items = (List<ItemDTO>) obj;
            if (!items.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(items, ItemDTO.class));
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });

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

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo listado de items... ---");

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/listado", authHeader, null);
        int statusCode = response.statusCode();
        String respuesta = response.body();

        if (statusCode == 200) {
            if (respuesta == null || respuesta.isBlank()) {
                System.out.println("No se recibieron items desde el servidor.");
                return;
            }

            List<ItemDTO> items = new ArrayList<>();
            String[] lineas = respuesta.split("\\r?\\n");

            for (String linea : lineas) {
                if (linea.contains("ItemDTO(")) {
                    try {
                        String contenido = linea.substring(linea.indexOf('(') + 1, linea.indexOf(')'));
                        Map<String, String> partsMap = Arrays.stream(contenido.split(", "))
                                .map(s -> s.split("="))
                                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

                        Long itemId = Long.parseLong(partsMap.get("item_id"));
                        String nombre = partsMap.get("nombre");
                        String categoria = partsMap.get("categoria");
                        double costo = Double.parseDouble(partsMap.get("costo"));

                        ItemDTO item = new ItemDTO(itemId, nombre, categoria, costo);
                        items.add(item);
                    } catch (Exception e) {
                        System.err.println("Error al parsear línea: " + linea + " - " + e.getMessage());
                    }
                }
            }
            if (items.isEmpty()) {
                System.out.println("No se pudieron interpretar los items del listado.");
            } else {
                System.out.println(FlipTableConverters.fromIterable(items, ItemDTO.class));
            }
        } else {
            handleErrorResponse(statusCode, respuesta);
        }
    }

    private void filtrarYordenar() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar items ---");
        System.out.print("Filtrar por categoría (dejar vacío si no aplica): ");
        String categoria = scanner.nextLine();
        if (categoria.isBlank()) categoria = null;

        System.out.print("Ordenar por 'nombre' o 'costo' (dejar vacío si no aplica): ");
        String orden = scanner.nextLine();
        String direccion = null;
        if (orden.isBlank()) {
            orden = null;
        } else {
            System.out.print("Ordenar 'asc' o 'desc' (dejar vacío si no aplica): ");
            direccion = scanner.nextLine();
            if (direccion.isBlank()) direccion = null;
        }

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYordenar?");
        if (categoria != null) urlBuilder.append("categoria=").append(categoria).append("&");
        if (orden != null) urlBuilder.append("orden=").append(orden).append("&");
        if (direccion != null) urlBuilder.append("direccion=").append(direccion);

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

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nuevo item ---");

        System.out.print("Nombre del item: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Categoria del item: ");
        String categoria = Escaner.stringValido(scanner);
        System.out.print("Costo del item: ");
        Double costo = Escaner.doubleValido(scanner);

        CreateItemDTO createItemDTO = new CreateItemDTO(nombre, categoria, costo);
        String jsonBody = mapper.writeValueAsString(createItemDTO);

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                ItemDTO.class, // Clase esperada para el ítem creado
                "Item agregado exitosamente.",
                "Error al agregar item."
        );

        result.ifPresent(obj -> {
            ItemDTO item = (ItemDTO) obj;
            System.out.println(FlipTableConverters.fromIterable(List.of(item), ItemDTO.class));
        });
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) { // el 204 es operación éxitosa pero sin respuesta
            System.out.println("Item con ID " + id + " eliminado exitosamente.");
        } else {
            handleErrorResponse(statusCode, response.body());
        }
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Nombre
            2. Categoria
            3. Costo
            0. Cancelar
            Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0) System.out.print("Ingrese el nuevo valor: ");

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        boolean attributeSelected = false;

        switch (opcion) {
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                updateItemDTO.setNombre(nombre);
                attributeSelected = true;
            }
            case 2 -> {
                String categoria = Escaner.stringValido(scanner);
                updateItemDTO.setCategoria(categoria);
                attributeSelected = true;
            }
            case 3 -> {
                Double costo = Escaner.doubleValido(scanner);
                updateItemDTO.setCosto(costo);
                attributeSelected = true;
            }
            case 0 -> {
                return; // Cancelar
            }
            default -> {
                System.out.println("Opcion no valida.");
                return;
            }
        }

        String jsonBody = mapper.writeValueAsString(updateItemDTO);

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                ItemDTO.class,
                "Item modificado exitosamente.",
                "Error al modificar item."
        );

        result.ifPresent(obj -> {
            ItemDTO item = (ItemDTO) obj;
            System.out.println(FlipTableConverters.fromIterable(List.of(item), ItemDTO.class));
        });
    }


    ///  MÉTODOS AUXILIARES - HANDLERS DE ERROR Y SUCCESS
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