package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTable;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuInventarioPuesto {

    private static final String API_URL = "http://localhost:8080/api/inventario-puesto";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuInventarioPuesto(String authHeader) {this.authHeader = authHeader;}

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
        } while (!opcion.equals("0"));
    }

    private void mostrar_menu() {
        System.out.println("""
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
                //agregar mostrar todos los inv del puesto
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:""");
    }

    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("OBTENIENDO inventarios...");
        HttpResponse<String> response =  HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        // Convertir JSON a lista de ItemDTO
        ObjectMapper mapper = new ObjectMapper();
        List<InventarioPuestoDTO> stock = Arrays.asList(mapper.readValue(respuestaJson, InventarioPuestoDTO[].class));

        // Imprimir en formato tabla
        System.out.println(FlipTableConverters.fromIterable(stock, InventarioPuestoDTO.class));
    }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.println("INGRESE ID DEL INVENTARIO: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response =   HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        InventarioPuestoDTO inventario= mapper.readValue(respuestaJson, InventarioPuestoDTO.class);

        System.out.println(FlipTableConverters.fromIterable(List.of(inventario), InventarioPuestoDTO.class));

    }

    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO INVENTARIO");
        System.out.print("Ingrese cantidad: "); Integer cantidad = Escaner.enteroValido(scanner);
        System.out.println("Ingrese id del puesto"); Long puestoId=Long.valueOf(Escaner.enteroValido(scanner));
        System.out.print("Ingrese ID item: "); Long itemId = Long.valueOf(Escaner.enteroValido(scanner));
        System.out.println("Ingrese la cantidad de stock minimo que desea establecer: "); Integer stockMin=Escaner.enteroValido(scanner);
        System.out.println("Ingrese el precio del item"); BigDecimal precioVenta=BigDecimal.valueOf(Escaner.doubleValido(scanner));


        String jsonBody = "{" +
                "\"cantidad\":" +cantidad + "," +
                "\"puestoId\":" + puestoId +"," +
                "\"itemId\":" + itemId + ","+
                "\"stockMin\":" + stockMin + ","+
                "\"precioVenta\":" + precioVenta +
        "}";
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }




    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }


    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a modificar: ");
        Long id = Long.valueOf(Escaner.enteroValido(scanner));
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Cantidad
                2. Puesto
                3. Item
                4. Stock Minimo
                5. Precio de venta
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
                Long puestoId = Long.valueOf(Escaner.enteroValido(scanner));
                jsonBody = "{\"puestoId\":" + puestoId + "}";
            }
            case 3 -> {
                Long itemId = Long.valueOf(Escaner.enteroValido(scanner));
                jsonBody = "{\"itemId\":" + itemId + "}";
            }
            case 4 -> {
                Integer stockMin = Escaner.enteroValido(scanner);
                jsonBody = "{\"stockMin\":"  + stockMin + "}";
            }
            case 5 -> {
                BigDecimal precioVenta = BigDecimal.valueOf(Escaner.enteroValido(scanner));
                jsonBody = "{\"precioVenta\":"  + precioVenta + "}";
            }
            default -> {
                System.out.println("Opcion no válida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);

    }

    private void mostrarProductosEnStock() throws IOException, InterruptedException {
        System.out.println("Ingrese id de puesto");
        Integer id = Escaner.enteroValido(scanner);

        // Realizar la petición HTTP
        HttpResponse<String> response = HttpService.realizarPeticion(
                "GET",
                API_URL + "/obtenerInvConStock/" + id,
                authHeader,
                null
        );

        String respuestaJson = response.body();

        // Deserializar JSON a lista de Map<String, Object>
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> stock = mapper.readValue(
                respuestaJson,
                new TypeReference<List<Map<String, Object>>>() {}
        );

        // Verificar si hay resultados
        if (stock.isEmpty()) {
            System.out.println("No hay productos en stock.");
            return;
        }

        // Obtener headers (nombres de columnas)
        String[] headers = stock.get(0).keySet().toArray(new String[0]);

        // Obtener los datos como matriz de strings
        String[][] data = stock.stream()
                .map(m -> m.values().stream()
                        .map(v -> v == null ? "" : v.toString())
                        .toArray(String[]::new))
                .toArray(String[][]::new);

        // Imprimir la tabla con FlipTable
        System.out.println(FlipTable.of(headers, data));
    }

    private void mostrarProductosEnStockBajo() throws IOException, InterruptedException {
        System.out.println("Ingrese id de puesto");
        Integer id = Escaner.enteroValido(scanner);

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/obtenerInvConStockBajo/" + id, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> stock = mapper.readValue(respuestaJson, new TypeReference<List<Map<String, Object>>>() {});

        if (stock.isEmpty()) {
            System.out.println("No hay productos con stock bajo.");
            return;
        }

        String[] headers = stock.get(0).keySet().toArray(new String[0]);
        String[][] data = stock.stream()
                .map(m -> m.values().stream().map(String::valueOf).toArray(String[]::new))
                .toArray(String[][]::new);

        System.out.println(FlipTable.of(headers, data));
    }
    
    private void filtrarYordenarItemsInventario() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar items del inventario ---");
        System.out.println("Ingrese id del puesto");
        Long puestoId=Long.valueOf(Escaner.enteroValido(scanner));
        System.out.print("Filtrar por categoría (dejar vacío si no aplica): ");
        String categoria = scanner.nextLine();
        if (categoria.isBlank()) categoria = null;

        System.out.print("Ordenar por 'nombre' , 'costo','cantidad', (dejar vacío si no aplica): ");
        String orden = scanner.nextLine();
        String direccion = null;
        if (orden.isBlank()) {
            orden = null;
        } else {
            System.out.print("Ordenar 'asc' o 'desc' (dejar vacío si no aplica): ");
            direccion = scanner.nextLine();
            if (direccion.isBlank()) direccion = null;
        }

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYordenarItemsInventario?");
        urlBuilder.append("id=").append(puestoId).append("&");
        if (categoria != null) urlBuilder.append("categoria=").append(categoria).append("&");
        if (orden != null) urlBuilder.append("orden=").append(orden).append("&");
        if (direccion != null) urlBuilder.append("direccion=").append(direccion);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);

        HttpResponse<String> response = HttpService.realizarPeticion("GET", finalUrl, authHeader, null);








    }
































}
