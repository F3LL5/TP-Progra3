package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Puesto.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.UpdatePuestoDTO;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Menu.MenuAuditoria.MenuHistorial_Duenio;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuPuestos {
    ///-------------------------------------ATRIBUTOS-------------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/puestos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
   ///-------------------------------------CONSTRUCTOR------------------------------------------------------------------
    public MenuPuestos(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }
   ///--------------------------------------MENU------------------------------------------------------------------------
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
                    MenuHistorial_Duenio auditoria = new MenuHistorial_Duenio(authHeader);
                    auditoria.gestionar();
                }
                case "0" -> {
                }
                default -> System.out.println("OPCIÓN INVÁLIDA. VUELVA A INTENTAR");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
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

    ///------------------------------------GET--------------------------------------------------------------------------
    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("OBTENIENDO PUESTOS...");
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        List<PuestoDTO> puestos = Arrays.asList(new ObjectMapper().readValue(respuestaJson, PuestoDTO[].class));

        if (puestos.isEmpty()) {
            System.out.println("No hay puestos registrados.");
        } else {
            FlipTableHelper.imprimir(puestos);
        }
    }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.println("INGRESE ID DEL PUESTO: ");
        String id = Escaner.stringValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        String respuestaJson = response.body();

        if (statusCode == 200) {
            PuestoDTO puesto = new ObjectMapper().readValue(respuestaJson, PuestoDTO.class);
            FlipTableHelper.imprimir(List.of(puesto));
        } else {
            HandlerResponse.handleErrorResponse(statusCode, respuestaJson);
        }
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("""
        ORDENAR POR:
        [1] NOMBRE
        [0] SIN ORDENAMIENTO
        Opción:""");
        int by = Escaner.enteroValido(scanner);
        String sortBy = switch (by) {
            case 1 -> "nombre";
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

        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrarYOrdenarPorNombre?");
        if (sortBy != null) urlBuilder.append("nombre=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                PuestoDTO.class,
                "Items filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los items."
        );

        result.ifPresent(obj -> {
            List<PuestoDTO> puestos = (List<PuestoDTO>) obj;
            if (!puestos.isEmpty()) {
                FlipTableHelper.imprimir(puestos);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    ///-----------------------------------POST--------------------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO PUESTO");
        System.out.print("Ingrese NOMBRE: ");
        String nombre = Escaner.stringValido(scanner);

        System.out.print("Ingrese ID dueño: ");
        int duenioId = Escaner.enteroValido(scanner);

      
        double comision = Escaner.porcentaje(scanner);

        CreatePuestoDTO puestoDTO = new CreatePuestoDTO(nombre, (long) duenioId, BigDecimal.valueOf(comision));
        String jsonBody = new ObjectMapper().writeValueAsString(puestoDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                PuestoDTO.class,
                "Puesto agregado exitosamente.",
                "Error al agregar puesto."
        );

        result.ifPresent(obj -> {
            PuestoDTO puesto = (PuestoDTO) obj;
            FlipTableHelper.imprimir(List.of(puesto));
        });
    }

    ///----------------------------------DELETE-------------------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.println("Ingrese ID del puesto que desea eliminar: ");
        String id = Escaner.stringValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Puesto con ID " + id + " eliminado exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    ///----------------------------------PATCH--------------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del puesto que desea modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Dueño (Desasociar/Cambiar)
                3. Comisión
                0. Cancelar
                Ingrese una opción:""");

        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion == 0) return;

        UpdatePuestoDTO updatePuestoDTO = new UpdatePuestoDTO();
        boolean attributeSelected = false;

        switch (opcion) {
            case 1 -> {
                System.out.print("Ingrese el nuevo nombre: ");
                String nombre = Escaner.stringValido(scanner);
                updatePuestoDTO.setNombre(nombre);
                attributeSelected = true;
            }
            case 2 -> {
                System.out.print("Ingrese el ID del nuevo dueño (0 para desasociar): ");
                Long idDuenio = Escaner.enteroValido(scanner).longValue();
                updatePuestoDTO.setDuenioId(idDuenio == 0 ? null : idDuenio);
                attributeSelected = true;
            }
            case 3 -> {
                System.out.print("Ingrese la nueva comisión: ");
                Double comision = Escaner.porcentaje(scanner);
                updatePuestoDTO.setComision(BigDecimal.valueOf(comision));
                attributeSelected = true;
            }
            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }

        if (!attributeSelected) return;

        String jsonBody = new ObjectMapper().writeValueAsString(updatePuestoDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                PuestoDTO.class,
                "Puesto modificado exitosamente.",
                "Error al modificar el puesto."
        );

        result.ifPresent(obj -> {
            PuestoDTO puesto = (PuestoDTO) obj;
            FlipTableHelper.imprimir(List.of(puesto));
        });
    }
}
