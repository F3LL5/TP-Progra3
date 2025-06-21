package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTable;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;

import static com.owo.TP_prg3.Excepciones.Handler.HandlerResponse.handleResponse;

public class MenuInventarioPuesto {
    /// ----------------------------------------ATRIBUTOS---------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/inventario-puesto";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();

    /// --------------------------------------CONSTRUCTOR---------------------------------------------------------------
    public MenuInventarioPuesto(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }
    /// --------------------------------------MENU----------------------------------------------------------------------
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
                case "6" -> mostrarProductosEnStock();
                case "7" -> mostrarProductosEnStockBajo();
                case "8" -> filtrarYordenarItemsInventario();
                case "0" -> {}
                default -> System.out.println("OPCIÓN INVÁLIDA. VUELVA A INTENTAR");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void mostrar_menu() {
        System.out.print("""
                --- MENÚ DE GESTIÓN DE INVENTARIO DE PUESTOS ---
                OPCIONES:
                1. Obtener todos los inventarios
                2. Buscar por ID
                3. Agregar inventario
                4. Eliminar inventario
                5. Modificar inventario
                6. Mostrar productos en stock
                7. Mostrar productos en stock bajo
                8. Filtrar y ordenar inventario de puesto
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:""");
    }


    /// ----------------------------------------GET---------------------------------------------------------------------
    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los inventarios... ---");

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                InventarioPuestoDTO.class,
                "Inventarios obtenidos exitosamente.",
                "No se pudieron obtener los inventarios."
        );

        result.ifPresent(obj -> {
            List<InventarioPuestoDTO> inventarios = (List<InventarioPuestoDTO>) obj;
            if (!inventarios.isEmpty()) {
                FlipTableHelper.imprimir(inventarios);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                InventarioPuestoDTO.class,
                "Inventario encontrado:",
                "No se encontró el inventario con ID " + id + "."
        );

        result.ifPresent(obj -> {
            InventarioPuestoDTO inventario = (InventarioPuestoDTO) obj;
            FlipTableHelper.imprimir(List.of(inventario));
        });

    }

    private void filtrarYordenarItemsInventario() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR ITEMS DEL INVENTARIO ---");

        System.out.print("Ingrese ID del puesto: ");
        int puestoId = Escaner.enteroValido(scanner);

        System.out.print("Filtrar por categoría (dejar vacío si no aplica): ");
        String categoria = scanner.nextLine();
        if (categoria.isBlank()) categoria = null;

        System.out.print("""
        ORDENAR POR:
        [1] NOMBRE
        [2] PRECIO VENTA
        [3] COSTO ADQUISICIÓN
        [4] CANTIDAD
        [0] SIN ORDENAMIENTO
        Opción:""");
        int opcOrden = Escaner.enteroValido(scanner);
        String sortBy = switch (opcOrden) {
            case 1 -> "nombre";
            case 2 -> "precioVenta";
            case 3 -> "costoAdquisicion";
            case 4 -> "cantidad";
            default -> null;
        };

        System.out.print("""
        DIRECCIÓN DE ORDEN:
        [1] ASCENDENTE
        [2] DESCENDENTE
        [0] SIN DIRECCIÓN
        Opción:""");
        int opcDir = Escaner.enteroValido(scanner);
        String sortDir = switch (opcDir) {
            case 1 -> "asc";
            case 2 -> "desc";
            default -> null;
        };

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYordenarItemsInventario?");
        urlBuilder.append("id=").append(puestoId).append("&");
        if (categoria != null) urlBuilder.append("categoria=").append(categoria).append("&");
        if (sortBy != null) urlBuilder.append("orden=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("direccion=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                InventarioPuestoDTO.class,
                "Inventarios filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los inventarios."
        );

        result.ifPresent(obj -> {
            List<InventarioPuestoDTO> inventarios = (List<InventarioPuestoDTO>) obj;
            if (!inventarios.isEmpty()) {
                FlipTableHelper.imprimir(inventarios);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    ///--------------------------------------POST-----------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO INVENTARIO");
        System.out.print("Ingrese cantidad: "); Integer cantidad = Escaner.enteroValido(scanner);
        System.out.print("Ingrese id del puesto"); int puestoId=Escaner.enteroValido(scanner);
        System.out.print("Ingrese ID item: "); int itemId = Escaner.enteroValido(scanner);
        System.out.print("Ingrese la cantidad de stock minimo que desea establecer: "); Integer stockMin=Escaner.enteroValido(scanner);
        System.out.print("Ingrese el precio del item"); BigDecimal precioVenta=BigDecimal.valueOf(Escaner.doubleValido(scanner));
        System.out.print("Ingrese el costo de adquisicion"); BigDecimal costoAdquisicion=BigDecimal.valueOf(Escaner.doubleValido(scanner));

        String jsonBody = "{" +
                "\"cantidad\":" +cantidad + "," +
                "\"puestoId\":" + puestoId +"," +
                "\"itemId\":" + itemId + "," +
                "\"stockMin\":" + stockMin + "," +
                "\"precioVenta\":" + precioVenta + "," +
                "\"costoAdquisicion\":" + costoAdquisicion +
                "}";
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

   ///------------------------------------- DELETE----------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

   ///--------------------------------------PATCH-----------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a modificar: ");
        int id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Cantidad
                2. Puesto
                3. Item
                4. Stock Minimo
                5. Precio de venta
                6. Costo adquisicion
                0. Cancelar
                Ingrese una opción:"""
        );
        Integer opcion = Escaner.enteroValido(scanner);

        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");
        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                Integer cantidad = Escaner.enteroValido(scanner);
                jsonBody = "{\"cantidad\":" +  cantidad + "}" ;
            }
            case 2 -> {
                int puestoId = Escaner.enteroValido(scanner);
                jsonBody = "{\"puestoId\":" + puestoId + "}";
            }
            case 3 -> {
                int itemId = Escaner.enteroValido(scanner);
                jsonBody = "{\"itemId\":" + itemId + "}";
            }
            case 4 -> {
                Integer stockMin = Escaner.enteroValido(scanner);
                jsonBody = "{\"stockMin\":"  + stockMin + "}";
            }
            case 5 -> {
                BigDecimal precioVenta = BigDecimal.valueOf(Escaner.doubleValido(scanner));
                jsonBody = "{\"precioVenta\":"  + precioVenta + "}";
            }
            case 6 -> {
                BigDecimal costoAdquisicion = BigDecimal.valueOf(Escaner.doubleValido(scanner));
                jsonBody = "{\"costoAdquisicion\":"  + costoAdquisicion + "}";
            }
            default -> {
                System.out.println("Opcion no válida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);

    }

    ///--------------------------------METODOS DE STOCK-----------------------------------------------------------------
    private void mostrarProductosEnStock() throws IOException, InterruptedException {
        System.out.print("Ingrese id de puesto");
        Integer id = Escaner.enteroValido(scanner);

        // Realizar la petición HTTP
        HttpResponse<String> response = HttpService.realizarPeticion(
                "GET",
                API_URL + "/obtenerInvConStock/" + id,
                authHeader,
                null
        );
        // Obtenemos el json de la respuesta
        String respuestaJson = response.body();

        // Deserializar JSON a Map<String, Object>
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> stock = mapper.readValue(
                respuestaJson,
                new TypeReference<>() {}
        );

        FlipTableHelper.imprimirMapa(stock);
    }

    private void mostrarProductosEnStockBajo() throws IOException, InterruptedException {
        System.out.print("Ingrese id de puesto");
        Integer id = Escaner.enteroValido(scanner);

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/obtenerInvConStockBajo/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> stock = mapper.readValue(respuestaJson, new TypeReference<>() {});

        FlipTableHelper.imprimirMapa(stock);
    }

}
