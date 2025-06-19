package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuTransacciones {
    ///---------------------------------------ATRIBUTOS-----------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/transacciones";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    ///-------------------------------------CONSTRUCTOR-----------------------------------------------------------------
    public MenuTransacciones(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
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
                \n--- MENÚ DE GESTIÓN DE TRANSACCIONES ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                6. Filtrar y Ordenar
                
                0. Salir
                Ingrese la opción:""");
    }

    ///-----------------------------------METODOS-----------------------------------------------------------------------
    ///-------------------------------------GET-------------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las transacciones... ---");

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        List<TransaccionDTO> transacciones = Arrays.asList(
                new ObjectMapper().readValue(respuestaJson, TransaccionDTO[].class)
        );

        if (transacciones.isEmpty()) {
            System.out.println("No se encontraron transacciones.");
            return;
        }

        FlipTableHelper.imprimir(transacciones);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                TransaccionDTO.class,
                "Transacción no  encontrado:",
                "No se encontró la transacción con ID " + id + "."
        );

        result.ifPresent(obj -> {
            TransaccionDTO transaccionDTO = (TransaccionDTO) obj;
            FlipTableHelper.imprimir(List.of(transaccionDTO));
        });
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR TRANSACCIONES ---");

        System.out.println("""
            TIPO DE TRANSACCIÓN:
            [1] COMPRA
            [2] VENTA
            [3] INGRESO
            [4] EGRESO
            [0] SIN FILTRO
            Opción:""");
        int tipo = Escaner.enteroValido(scanner);
        String tipo_transaccion = switch (tipo) {
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

        // Construcción de la URL
        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenar?");
        if (tipo_transaccion != null) urlBuilder.append("tipo_transaccion=").append(tipo_transaccion).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando: " + finalUrl);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                TransaccionDTO.class,
                "Items filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los items."
        );

        result.ifPresent(obj -> {
            List<TransaccionDTO> transacciones = (List<TransaccionDTO>) obj;
            if (transacciones.isEmpty()) {
                System.out.println("No se encontraron transacciones con esos filtros.");
                return;
            }
            FlipTableHelper.imprimir(transacciones);
        });
    }

    ///--------------------------------------POST-----------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar transacción ---");

        System.out.print("Tipo de transacción :\n1-COMPRA, \n2-VENTA,\n3-INGRESO,\n4-EGRESO]: ");
        int tipos = Escaner.enteroValido(scanner);
        String tipo = switch (tipos) {
            case 1 -> "COMPRA";
            case 2 -> "VENTA";
            case 3-> "INGRESO";
            case 4 -> "EGRESO";
            default -> null;
        };

        System.out.print("Monto: ");
        BigDecimal monto = BigDecimal.valueOf(Escaner.doubleValido(scanner));

        Long cuentaOrigenId = null;
        Long cuentaDestinoId = null;

        switch (tipo) {
            case "COMPRA", "VENTA" -> {
                System.out.print("ID de la cuenta vendedor: ");
                cuentaOrigenId = Escaner.enteroValido(scanner).longValue();
                System.out.print("ID de la cuenta comprador: ");
                cuentaDestinoId = Escaner.enteroValido(scanner).longValue();
            }
            case "INGRESO", "EGRESO" -> {
                System.out.print("ID de la cuenta: ");
                cuentaDestinoId = Escaner.enteroValido(scanner).longValue();
            }
            default -> {
                System.out.println("Tipo de transacción incorrecto.");
                return;
            }
        }

        // Armás el DTO
        CreateTransaccionDTO dto = new CreateTransaccionDTO();
        dto.setTipo(tipo);
        dto.setMonto(monto);
        dto.setCuentaOrigenId(cuentaOrigenId);
        dto.setCuentaDestinoId(cuentaDestinoId);

        String jsonBody = new ObjectMapper().writeValueAsString(dto);

        // Hacés la petición y manejás la respuesta
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                TransaccionDTO.class,
                "Transacción registrada exitosamente.",
                "Error al registrar transacción."
        );

        result.ifPresent(obj -> {
            TransaccionDTO transaccion = (TransaccionDTO) obj;
            FlipTableHelper.imprimir(List.of(transaccion));
        });
    }

    ///---------------------------------------DELETE--------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la transaccion a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    ///--------------------------------------------PATCH----------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del transacción a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Tipo [COMPRA,VENTA,INGRESO,EGRESO]
            2. Monto
            3. Fecha
            4. ID de la cuenta origen
            5. ID de la cuenta destino
            0. Cancelar
            Ingrese una opción:""");

        Integer opcion = Escaner.enteroValido(scanner);

        if (opcion != 0 && opcion !=1) System.out.print("Ingrese el nuevo valor: ");
        UpdateTransaccionDTO updateDTO = new UpdateTransaccionDTO();
        boolean attributeSelected = false;

        switch (opcion) {
            case 1 -> {
                System.out.print("Ingrese el nuevo tipo de transacción :\n1-COMPRA, \n2-VENTA,\n3-INGRESO,\n4-EGRESO]: ");
                int tipos = Escaner.enteroValido(scanner);
                String tipo = switch (tipos) {
                    case 1 -> "COMPRA";
                    case 2 -> "VENTA";
                    case 3-> "INGRESO";
                    case 4 -> "EGRESO";
                    default -> null;
                };

                String nuevo_tipo = Escaner.stringValido(scanner).toUpperCase();
                updateDTO.setTipo(nuevo_tipo);
                attributeSelected = true;
            }
            case 2 -> {
                BigDecimal monto = BigDecimal.valueOf(Escaner.doubleValido(scanner));
                updateDTO.setMonto(monto);
                attributeSelected = true;
            }
            case 3 -> {
                LocalDateTime fecha = Escaner.fechaYhora(scanner);
                updateDTO.setFecha(fecha);
                attributeSelected = true;
            }
            case 4 -> {
                Long cuentaOrigenId = Escaner.enteroValido(scanner).longValue();
                updateDTO.setCuentaOrigenId(cuentaOrigenId);
                attributeSelected = true;
            }
            case 5 -> {
                Long cuentaDestinoId = Escaner.enteroValido(scanner).longValue();
                updateDTO.setCuentaDestinoId(cuentaDestinoId);
                attributeSelected = true;
            }
            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }

        if (!attributeSelected) return;

        String jsonBody = new ObjectMapper().writeValueAsString(updateDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                TransaccionDTO.class,
                "Transacción modificada exitosamente.",
                "Error al modificar transacción."
        );

        result.ifPresent(obj -> {
            TransaccionDTO transaccion = (TransaccionDTO) obj;
            FlipTableHelper.imprimir(List.of(transaccion));
        });
    }

}
