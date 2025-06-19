package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuPedidos {
    ///-----------------------------------ATRIBUTOS---------------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/pedidos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    ///----------------------------------CONSTRUCTOR--------------------------------------------------------------------
    public MenuPedidos(String authHeader) {
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
                case "6" -> filtrarYOrdenar();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE PEDIDO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                6. Filtrar y Ordenar VENTAS, COMPRAS, INGRESOS Y EGRESOS
                
                0. Salir
                Ingrese la opción:""");
    }

    ///------------------------------------METODOS----------------------------------------------------------------------
    ///--------------------------------------GET------------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas los pedido... ---");
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                PedidoDTO.class,
                "Pedidos obtenidos exitosamente.",
                "No se pudieron obtener los pedidos."
        );

        result.ifPresent(obj -> {
            List<PedidoDTO> detalles = (List<PedidoDTO>) obj;
            if (!detalles.isEmpty()) {
                FlipTableHelper.imprimir(detalles);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });

    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                PedidoDTO.class,
                "Pedido encontrado:",
                "No se encontró el pedido con ID " + id + "."
        );

        result.ifPresent(obj -> {
            PedidoDTO pedidoDTO = (PedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(pedidoDTO));
        });
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR PEDIDOS ---");

        System.out.println("""
            TIPO DE TRANSACCIÓN:
            [1] COMPRA
            [2] VENTA
            [3] INGRESO
            [4] EGRESO
            [0] SIN FILTRO
            Opción:""");
        int estado = Escaner.enteroValido(scanner);
        String estado_pedido = switch (estado) {
            case 1 -> "COMPRA";
            case 2 -> "VENTA";
            case 3 -> "INGRESO";
            case 4 -> "EGRESO";
            default -> null;
        };

        System.out.println("""
        ORDENAR POR:
        [1] FECHA
        [2] MONTO
        [3] ID DE CUENTA ORIGEN
        [4] ID DE CUENTA DESTINO
        [0] SIN ORDENAMIENTO
        Opción:""");
        int by = Escaner.enteroValido(scanner);
        String sortBy = switch (by) {
            case 1 -> "fecha";
            case 2 -> "monto";
            case 3 -> "id_cuenta_origen";
            case 4 -> "id_cuenta_destino";
            default -> null;
        };

        System.out.println("""
        DIRECCIÓN DE ORDEN:
        [1] ASCENDENTE
        [2] DESCENDENTE
        [0] SIN DIRECCIÓN
        Opción:""");
        int dir = Escaner.enteroValido(scanner);
        String sortDir = switch (dir) {
            case 1 -> "asc";
            case 2 -> "desc";
            default -> null;
        };

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenar?");
        if (estado_pedido != null) urlBuilder.append("tipo_transaccion=").append(estado_pedido).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                PedidoDTO.class,
                "Pedidos filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los pedidos."
        );

        result.ifPresent(obj -> {
            List<PedidoDTO> pedidoDTOS = (List<PedidoDTO>) obj;
            if (!pedidoDTOS.isEmpty()) {
                FlipTableHelper.imprimir(pedidoDTOS);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    ///-------------------------------------POST------------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar pedido ---");

        System.out.print("Ingrese el id de la transaccion: ");
        int transaccionId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese el id del puesto: ");
        int puestoId = Escaner.enteroValido(scanner);

        CreatePedidoDTO createPedido = new CreatePedidoDTO((long) transaccionId, (long) puestoId);
        String jsonBody = new ObjectMapper().writeValueAsString(createPedido);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                PedidoDTO.class,
                "Pedido agregado exitosamente.",
                "Error al agregar pedido."
        );

        result.ifPresent(obj -> {
            PedidoDTO pedidoDTO = (PedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(pedidoDTO));
        });
    }

    ///------------------------------------DELETE-----------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Pedido con ID " + id + " eliminado exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    ///-------------------------------------PATCH-----------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID de la transaccion
                2. ID del puestoId
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        UpdatePedidoDTO updateDTO = new UpdatePedidoDTO();
        boolean attributeSelected = false;

        switch (opcion) {
            case 1 -> {
                System.out.print("Ingrese el nuevo ID de la transacción: ");
                Integer transaccionId = Escaner.enteroValido(scanner);
                updateDTO.setTransaccionId(Long.valueOf(transaccionId));
                attributeSelected = true;
            }
            case 2 -> {
                System.out.print("Ingrese el nuevo ID del puesto: ");
                Integer puestoId = Escaner.enteroValido(scanner);
                updateDTO.setPuestoId(Long.valueOf(puestoId));
                attributeSelected = true;
            }
            case 0 -> {
                System.out.println("Modificación cancelada.");
                return;
            }
            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }

        if (!attributeSelected) {
            System.out.println("No se seleccionó ningún atributo para modificar.");
            return;
        }

        String jsonBody = new ObjectMapper().writeValueAsString(updateDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                PedidoDTO.class,
                "Pedido modificado exitosamente.",
                "Error al modificar el pedido."
        );

        result.ifPresent(obj -> {
            PedidoDTO pedido = (PedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(pedido));
        });
    }

}
