package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuDetallePedido {

    ///----------------------------------------ATRIBUTOS----------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/detalles-pedido";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    ///--------------------------------------CONSTRUCTOR----------------------------------------------------------------

    public MenuDetallePedido(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }
    ///----------------------------------------MENU---------------------------------------------------------------------
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
                case "6" -> calcularTotalVentaPedido();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }
    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE DETALLE PEDIDO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                6. Calcular el Total de la venta
                
                0. Salir
                Ingrese la opción:""");
    }

    ///--------------------------------------METODOS--------------------------------------------------------------------
    ///---------------------------------------GET-----------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los detalles de pedidos... ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                DetallePedidoDTO.class,
                "Detalles de pedidos obtenidos exitosamente.",
                "No se pudieron obtener los items."
        );

        result.ifPresent(obj -> {
            List<DetallePedidoDTO> detalles = (List<DetallePedidoDTO>) obj;
            if (!detalles.isEmpty()) {
                FlipTableHelper.imprimir(detalles);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });

    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                DetallePedidoDTO.class,
                "Detalle del pedido encontrado:",
                "No se encontró el detalle del pedido con ID " + id + "."
        );

        result.ifPresent(obj -> {
            DetallePedidoDTO detalle = (DetallePedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(detalle));
        });
    }

    private void calcularTotalVentaPedido() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del Pedido para calcular el total de venta: ");
        Integer pedidoId = Escaner.enteroValido(scanner);

        String url = API_URL + "/" + pedidoId + "/total-venta";

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", url, authHeader, null),
                BigDecimal.class,
                "Total de Venta obtenido exitosamente para el Pedido " + pedidoId,
                "Error al obtener el total de venta para el Pedido " + pedidoId
        );

        result.ifPresent(obj -> {
            if (obj instanceof BigDecimal totalVenta) {
                System.out.println("Total de Venta para el Pedido " + pedidoId + ": $" + totalVenta);
            }
        });
    }

    ///-------------------------------------POST------------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar el detalle de pedido ---");

        System.out.print("Ingrese el id del pedido: ");
        int pedidoId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese el id del item: ");
        int itemId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese la cantidad: ");
        int cantidad = Escaner.enteroValido(scanner);

        CreateDetallePedidoDTO createDetallePedidoDTO = new CreateDetallePedidoDTO((long) pedidoId, (long) itemId,cantidad);
        String jsonBody = new ObjectMapper().writeValueAsString(createDetallePedidoDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                DetallePedidoDTO.class,
                "Detalle pedido agregado exitosamente.",
                "Error al agregar detalle pedido."
        );

        result.ifPresent(obj -> {
            DetallePedidoDTO detallePedidoDTO = (DetallePedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(detallePedidoDTO));
        });
    }

    ///-----------------------------------DELETE------------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Detalle pedido con ID " + id + " eliminado exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    ///-----------------------------------PATCH-------------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID del pedido
                2. ID del itemId
                3. Cantidad
                
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        UpdateDetallePedidoDTO updateDTO = new UpdateDetallePedidoDTO();
        boolean attributeSelected = false;

        System.out.print("Ingrese el nuevo valor: ");

        switch (opcion) {
            case 1 -> {
                int pedidoId = Escaner.enteroValido(scanner);
                updateDTO.setPedidoId((long) pedidoId);
                attributeSelected = true;
            }
            case 2 -> {
                int itemId = Escaner.enteroValido(scanner);
                updateDTO.setItemId((long) itemId);
                attributeSelected = true;
            }
            case 3 -> {
                Integer cantidad = Escaner.enteroValido(scanner);
                updateDTO.setCantidad(cantidad);
                attributeSelected = true;
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
                DetallePedidoDTO.class,
                "Detalle de pedido modificado exitosamente.",
                "Error al modificar el detalle de pedido."
        );

        result.ifPresent(obj -> {
            DetallePedidoDTO detalle = (DetallePedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(detalle));
        });
    }
}
