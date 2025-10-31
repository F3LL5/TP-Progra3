package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.HttpService;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuItem {
    ///-----------------------------------ATRIBUTOS---------------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/items";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    ///----------------------------------CONSTRUCTOR--------------------------------------------------------------------
    public MenuItem(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }

    ///-------------------------------------MENU------------------------------------------------------------------------
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
                case "6" -> filtrarYordenar();
                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
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
                6. Filtrar y ordenar
                0. Salir
                Ingrese la opción:""");
    }

    ///------------------------------------METODOS----------------------------------------------------------------------
    ///--------------------------------------GET------------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los items... ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                ItemDTO.class,
                "Items obtenidos exitosamente.",
                "No se pudieron obtener los items."
        );

        result.ifPresent(obj -> {
            List<ItemDTO> items = (List<ItemDTO>) obj;
            if (!items.isEmpty()) {
                FlipTableHelper.imprimir(items);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });

    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                ItemDTO.class,
                "Item encontrado:",
                "No se encontró el itemId con ID " + id + "."
        );

        result.ifPresent(obj -> {
            ItemDTO item = (ItemDTO) obj;
            FlipTableHelper.imprimir(List.of(item));
        });
    }

    private void filtrarYordenar() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar items ---");
        System.out.print("Filtrar por categoría (dejar vacío si no aplica): ");
        String categoria = scanner.nextLine();
        if (categoria.isBlank()) categoria = null;

        System.out.print("""
        ORDENAR POR:
        [1] NOMBRE
        [0] SIN ORDENAMIENTO
        Opción:""");
        String orden;
        int by = Escaner.enteroValido(scanner);
        if (by == 1) orden = "nombre";
        else orden = null;

        System.out.print("""
        DIRECCIÓN DE ORDEN:
        [1] ASCENDENTE
        [2] DESCENDENTE
        [0] SIN DIRECCIÓN
        Opción:""");
        int dir = Escaner.enteroValido(scanner);
        String direccion =
        switch (dir) {
            case 1 -> "asc";
            case 2 -> "desc";
            default -> null;
        };

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYordenar?");
        if (categoria != null) urlBuilder.append("categoria=").append(categoria).append("&");
        if (orden != null) urlBuilder.append("orden=").append(orden).append("&");
        if (direccion != null) urlBuilder.append("direccion=").append(direccion);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                ItemDTO.class,
                "Items filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los items."
        );

        result.ifPresent(obj -> {
            List<ItemDTO> items = (List<ItemDTO>) obj;
            if (!items.isEmpty()) {
                FlipTableHelper.imprimir(items);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    ///--------------------------------------POST-----------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nuevo item ---");

        System.out.print("Nombre del item: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Categoria del item: ");
        String categoria = Escaner.stringValido(scanner);

        CreateItemDTO createItemDTO = new CreateItemDTO(nombre, categoria);
        String jsonBody = new ObjectMapper().writeValueAsString(createItemDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                ItemDTO.class,
                "Item agregado exitosamente.",
                "Error al agregar item."
        );

        result.ifPresent(obj -> {
            ItemDTO item = (ItemDTO) obj;
            FlipTableHelper.imprimir(List.of(item));
        });
    }

    ///-------------------------------------DELETE----------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) { // el 204 es operación éxitosa pero sin respuesta
            System.out.println("Item con ID " + id + " eliminado exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    ///-------------------------------------PATCH-----------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del itemId a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Nombre
            2. Categoria
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
            case 0 -> {
                return; // Cancelar
            }
            default -> {
                System.out.println("Opcion no valida.");
                return;
            }
        }

        String jsonBody = new ObjectMapper().writeValueAsString(updateItemDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                ItemDTO.class,
                "Item modificado exitosamente.",
                "Error al modificar item."
        );

        result.ifPresent(obj -> {
            ItemDTO item = (ItemDTO) obj;
            FlipTableHelper.imprimir(List.of(item));
        });
    }

}