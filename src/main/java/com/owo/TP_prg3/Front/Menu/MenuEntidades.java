package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.Utilidades.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuEntidades {
    /// --------------------------------------ATRIBUTOS-----------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/entidades";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    /// ------------------------------------CONSTRUCTOR-----------------------------------------------------------------
    public MenuEntidades(String authHeader) {
        this.authHeader = authHeader;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }
    /// -----------------------------------MENU-------------------------------------------------------------------------
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();
                case "3" -> agregar();
                case "4" -> agregarConCuentaBancaria();
                case "5" -> eliminar();
                case "6" -> modificar();
                case "7" -> filtrarYOrdenar();
                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE GESTIÓN DE ENTIDADES ---
                OPCIONES:
                1. Obtener todas
                2. Buscar por id
                3. Agregar
                4. Agregar (con cuenta bancaria automáticamente)
                5. Eliminar
                6. Modificar
                7. Ordenar y Filtrar
                0. Salir
                Ingrese la opción:""");
    }

    /// ---------------------------------------METODOS------------------------------------------------------------------

    /// ----------------------------------------GET---------------------------------------------------------------------
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las entidades... ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                EntidadDTO.class,
                "Entidades obtenidas exitosamente.",
                "No se pudieron obtener las entidades."
        );

        result.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                FlipTableHelper.imprimir(entidades);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        int id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                EntidadDTO.class,
                "Entidad encontrada:",
                "No se encontró la entidad con ID " + id + "."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidad = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidad));
        });
    }

    private void filtrarYOrdenar() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR ENTIDADES ---");

        System.out.print("""
        TIPO DE ENTIDAD:
        [1] DUEÑO DE PUESTO
        [2] CLIENTE
        [3] PROVEEDOR
        [0] SIN FILTRO
        Opción:""");
        int tipo = Escaner.enteroValido(scanner);
        String rol_entidad = switch (tipo) {
            case 1 -> "DUENO_PUESTO";
            case 2 -> "CLIENTE";
            case 3 -> "PROVEEDOR";
            case 4 -> "ADMIN";
            default -> null;
        };

        System.out.print("""
        ORDENAR POR:
        [1] NOMBRE
        [2] EDAD
        [3] DNI
        [4] TIPO DE ENTIDAD
        [0] SIN ORDENAMIENTO
        Opción:""");
        int by = Escaner.enteroValido(scanner);
        String sortBy = switch (by) {
            case 1 -> "nombre";
            case 2 -> "edad";
            case 3 -> "dni";
            case 4 -> "tipoEntidad";
            default -> null;
        };

        System.out.print("""
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
        if (rol_entidad != null) urlBuilder.append("rol_entidad=").append(rol_entidad).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                EntidadDTO.class,
                "Entidades filtradas y ordenadas exitosamente.",
                "No se pudieron filtrar/ordenar las entidades."
        );

        result.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                FlipTableHelper.imprimir(entidades);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }


    /// ---------------------------------------------POST---------------------------------------------------------------
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        System.out.print("""
        TIPO DE ENTIDAD:
        [1] DUEÑO DE PUESTO
        [2] CLIENTE
        [3] PROVEEDOR
        [4] ADMIN
        Opción:""");
        int roles = Escaner.enteroValido(scanner);
        String rol = switch (roles) {
            case 1 -> "CLIENTE";
            case 2 -> "PROVEEDOR";
            case 3-> "DUENO_PUESTO";
            case 4-> "ADMIN";
            default -> null;
        };
        System.out.print("Edad: ");
        Integer edad = Escaner.enteroValido(scanner);
        System.out.print("DNI: ");
        Integer dni = Escaner.enteroValido(scanner);

        CreateEntidadDTO createEntidadDTO = new CreateEntidadDTO(nombre, RolEntidad.valueOf(rol), tipoEntidad, edad, dni);
        String jsonBody = new ObjectMapper().writeValueAsString(createEntidadDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody),
                EntidadDTO.class,
                "Item agregado exitosamente.",
                "Error al agregar item."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidadDTO = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidadDTO));
        });
    }

    private void agregarConCuentaBancaria() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad con cuenta bancaria ---");
        System.out.print("Nombre: "); String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: "); String tipoEntidad = Escaner.stringValido(scanner);

        System.out.print("""
        TIPO DE ENTIDAD:
        [1] CLIENTE
        [2] PROVEEDOR
        [3] DUEÑO DE PUESTO
        [4] ADMIN
        Opción:""");
        int roles = Escaner.enteroValido(scanner);
        String rol = switch (roles) {
            case 1 -> "CLIENTE";
            case 2 -> "PROVEEDOR";
            case 3 -> "DUENO_PUESTO";
            case 4 -> "ADMIN";
            default -> null;
        };

        System.out.print("Edad: ");
        Integer edad = Escaner.enteroValido(scanner);

        System.out.print("DNI: ");
        Integer dni = Escaner.enteroValido(scanner);

        CreateEntidadDTO createEntidadDTO = new CreateEntidadDTO(nombre, RolEntidad.valueOf(rol), tipoEntidad, edad, dni);
        String jsonBody = new ObjectMapper().writeValueAsString(createEntidadDTO);
        String url = API_URL + "/v2";

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", url, authHeader, jsonBody),
                EntidadDTO.class,
                "Entidad y cuenta bancaria creadas exitosamente.",
                "Error al crear la entidad y la cuenta bancaria."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidadDTO = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidadDTO));
        });
    }

    /// -------------------------------------------DELETE---------------------------------------------------------------
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Entidad con ID " + id + " eliminada exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }


    /// --------------------------------------PATCH---------------------------------------------------------------------
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a modificar: ");
        Integer id = Escaner.enteroValido(scanner);

        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Tipo de Entidad
                3. Rol
                4. Edad
                5. DNI
                0. Cancelar
                Ingrese una opción:"""
        );
        Integer opcion = Escaner.enteroValido(scanner);

        if (opcion == 0) {
            return;
        }

        boolean attributeSelected = false;
        String jsonBody = "";

        System.out.print("Ingrese el nuevo valor: ");
        switch (opcion) {
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\""  + nombre + "\"}";
                attributeSelected = true;
            }
            case 2 -> {
                String tipoEntidad = Escaner.stringValido(scanner);
                jsonBody = "{\"tipoEntidad\":\""  + tipoEntidad + "\"}";
                attributeSelected = true;
            }
            case 3 -> {
                boolean bucle;
                String rolEntidad = "";
                do {
                    bucle = false;
                    System.out.print("Rol \n1-CLIENTE \n2-PROVEEDOR \n3-DUENIO \n4-ADMIN \n Ingrese una opción: ");
                    int roles = Escaner.enteroValido(scanner);
                    switch (roles) {
                        case 1 -> rolEntidad = "CLIENTE";
                        case 2 -> rolEntidad = "PROVEEDOR";
                        case 3 -> rolEntidad = "DUENO_PUESTO";
                        case 4 -> rolEntidad = "ADMIN";
                        default -> bucle = true;
                    }
                } while (bucle);
                    jsonBody = "{\"rolEntidad\":\""  + rolEntidad + "\"}";
                    attributeSelected = true;
            }
            case 4 -> {
                Integer edad = Escaner.enteroValido(scanner);
                jsonBody = "{\"edad\":"  + edad + "}";
                attributeSelected = true;
            }
            case 5 -> {
                Integer dni = Escaner.enteroValido(scanner);
                jsonBody = "{\"dni\":"  + dni + "}";
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

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody),
                EntidadDTO.class,
                "Entidad modificada exitosamente.",
                "Error al modificar la entidad."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidad = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidad));
        });
    }
}
