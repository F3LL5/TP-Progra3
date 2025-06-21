package com.owo.TP_prg3.Front.MenuV2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.fliptables.FlipTable;
import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Menu.MenuItem;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

public class MenuDuenoPuesto {

    /// RECOMENDACIÓN: PARA PODER LEER BIEN ESTA CLASE RECOMIENDO APRETAR CLICK DERECHO, FOLDING, COLLAPSE ALL. E ir expandiendo el metodo que desee leer

    private static final String API_URL_ENTIDADES = "http://localhost:8080/api/entidades";
    private static final String API_URL_INVENTARIO_PUESTO = "http://localhost:8080/api/inventario-puesto";
    private static final String API_URL_PEDIDOS = "http://localhost:8080/api/pedidos";
    private static final String API_URL_DETALLES_PEDIDO = "http://localhost:8080/api/detalles-pedido";
    private static final String API_URL_TRANSACCIONES = "http://localhost:8080/api/transacciones";
    private static final String API_URL_CUENTAS_BANCARIAS = "http://localhost:8080/api/cuentas-bancarias";
    private static final String API_URL_PUESTOS = "http://localhost:8080/api/puestos";


    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final Puesto puestoUsuario;
    private final ObjectMapper mapper = new ObjectMapper();

    public MenuDuenoPuesto(String authHeader, Puesto puestoUsuario) {
        this.authHeader = authHeader;
        this.puestoUsuario = puestoUsuario;
        HandlerResponse.setObjectMapper(new ObjectMapper());
    }

    public void gestionar() throws IOException, InterruptedException {
        if (puestoUsuario == null) {
            System.out.println("No tiene un puesto asignado. No puede acceder al menú de Dueño de Puesto.");
            return;
        }

        String opcion;
        do {
            mostrarMenuDueñoPuesto();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> gestionarEntidadesPuesto();
                case "2" -> modificarMiPuesto();
                case "3" -> gestionarItemsGeneral();
                case "4" -> gestionarInventarioPuesto();
                case "5" -> verMiCuentaBancaria();
                case "6" -> gestionarTransaccionesPuesto();
                case "7" -> gestionarPedidosPuesto();
                case "8" -> gestionarDetallesPedidoPuesto();
                case "0" -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrarMenuDueñoPuesto() {
        System.out.printf("""
        
        \n--- PUESTO: "%s" ---
        1. Gestionar Clientes y Proveedores
        2. Modificar Mi Puesto
        
        3. Gestionar items/productos
        4. Gestionar inventario de mis items
        
        5. Ver Detalles de Mi Cuenta Bancaria
        6. Gestionar mis Transacciones
        7. Gestionar mis Pedidos (Realizar ventas)
        8. Gestionar Detalles de un Pedido
        
        0. Cerrar sesión
        Ingrese una opción:""", puestoUsuario.getNombre());
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// ENTIDADES
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void gestionarEntidadesPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE ENTIDADES ---
                    1. Obtener todos los clientes y proveedores
                    2. Filtrar y Ordenar Entidades
                    3. Filtrar Clientes con pedidos en el puesto
                    
                    4. Agregar
                    5. Agregar Cuenta Bancaria Automática (Testeo)
                    6. Eliminar por id
                    7. Modificar por id
                    
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerEntidadesDeMiPuesto();
                case "2" -> filtrarYOrdenarEntidadesDeMiPuesto();
                case "3" -> filtrarClientesConPedidosDeMiPuesto();
                case "4" -> agregarEntidadAMiPuesto();
                case "5" -> agregarEntidadAMiPuesto2();
                case "6" -> eliminarEntidadDeMiPuesto();
                case "7" -> modificarEntidadDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void obtenerEntidadesDeMiPuesto() throws IOException, InterruptedException {
        String rol1 = "CLIENTE";
        String rol2 = "PROVEEDOR";

        // Construcción de la URL
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_ENTIDADES + "/filtrarYOrdenar?rol_entidad="  + rol1 , authHeader, null),
                EntidadDTO.class,
                "Entidades de su puesto obtenidas exitosamente.",
                "No se pudieron obtener las entidades de su puesto."
        );
        //Y otra tabla para los proveedores
        Optional<Object> result2 = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_ENTIDADES + "/filtrarYOrdenar?rol_entidad=" + rol2, authHeader, null),
                EntidadDTO.class,
                "Entidades de su puesto obtenidas exitosamente.",
                "No se pudieron obtener las entidades de su puesto."
        );

        System.out.println("\n--- Obteniendo entidades (Clientes) ---");
        result.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                FlipTableHelper.imprimir(entidades);
            } else {
                System.out.println("No se encontraron clientes.");
            }
        });
        System.out.println("\n--- Obteniendo entidades (Proveedores) ---");
        result2.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                FlipTableHelper.imprimir(entidades);
            } else {
                System.out.println("No se encontraron proveedores.");
            }
        });
    }

    private void agregarEntidadAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar un cliente/proveedor ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        boolean bucle;
        String rol = "";
        do {
            bucle = false;
            System.out.print("ROL (1. CLIENTE, 2.PROVEEDOR): ");
            int opcion = Escaner.enteroValido(scanner);
            switch (opcion) {
                case 1 -> rol = "CLIENTE";
                case 2 -> rol = "PROVEEDOR";
                default -> bucle = true;
            };
        } while (bucle);
        System.out.print("Edad: ");
        Integer edad = Escaner.enteroValido(scanner);
        System.out.print("DNI: ");
        Integer dni = Escaner.enteroValido(scanner);

        CreateEntidadDTO createEntidadDTO = new CreateEntidadDTO(nombre, RolEntidad.valueOf(rol), tipoEntidad, edad, dni);
        String jsonBody = mapper.writeValueAsString(createEntidadDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL_ENTIDADES + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody),
                EntidadDTO.class,
                "Entidad agregada exitosamente a su puesto.",
                "Error al agregar entidad a su puesto."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidadDTO = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidadDTO));
        });
    }

    private void agregarEntidadAMiPuesto2() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad para su puesto ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        boolean bucle = false;
        String rol = "";
        do {
            System.out.print("Filtrar por ROL (1. CLIENTE, 2.PROVEEDOR): ");
            int opcion = Escaner.enteroValido(scanner);
            switch (opcion) {
                case 1 -> rol = "CLIENTE";
                case 2 -> rol = "PROVEEDOR";
                default -> bucle = true;
            };
        } while (bucle);
        System.out.print("Edad: ");
        Integer edad = Escaner.enteroValido(scanner);
        System.out.print("DNI: ");
        Integer dni = Escaner.enteroValido(scanner);

        CreateEntidadDTO createEntidadDTO = new CreateEntidadDTO(nombre, RolEntidad.valueOf(rol), tipoEntidad, edad, dni);
        String jsonBody = mapper.writeValueAsString(createEntidadDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("POST", API_URL_ENTIDADES + "/puesto/" + puestoUsuario.getPuestoId() + "/v2", authHeader, jsonBody),
                EntidadDTO.class,
                "Entidad agregada exitosamente a su puesto.",
                "Error al agregar entidad a su puesto."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidadDTO = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidadDTO));;
        });
    }

    private void eliminarEntidadDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a eliminar de su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL_ENTIDADES + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Entidad con ID " + id + " eliminada exitosamente de su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void modificarEntidadDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a modificar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Tipo de Entidad
                3. Rol [CLIENTE, PROVEEDOR]
                4. Edad
                5. DNI
                0. Cancelar
                Ingrese una opción:"""
        );
        Integer opcion = Escaner.enteroValido(scanner);

        if (opcion == 0) {
            return;
        }

        UpdateEntidadDTO updateEntidadDTO = new UpdateEntidadDTO();
        boolean attributeSelected = false;

        System.out.print("Ingrese el nuevo valor: ");

        switch (opcion) {
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                updateEntidadDTO.setNombre(nombre);
                attributeSelected = true;
            }
            case 2 -> {
                String tipoEntidad = Escaner.stringValido(scanner);
                updateEntidadDTO.setTipoEntidad(tipoEntidad);
                attributeSelected = true;
            }
            case 3 -> {
                System.out.print(" [CLIENTE, PROVEEDOR]: ");
                String rol = Escaner.stringValido(scanner);
                updateEntidadDTO.setRolEntidad(RolEntidad.valueOf(rol));
                attributeSelected = true;
            }
            case 4 -> {
                Integer edad = Escaner.enteroValido(scanner);
                updateEntidadDTO.setEdad(edad);
                attributeSelected = true;
            }
            case 5 -> {
                Integer dni = Escaner.enteroValido(scanner);
                updateEntidadDTO.setDni(dni);
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

        String jsonBody = mapper.writeValueAsString(updateEntidadDTO);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("PATCH", API_URL_ENTIDADES + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody),
                EntidadDTO.class,
                "Entidad modificada exitosamente en su puesto.",
                "Error al modificar la entidad en su puesto."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidad = (EntidadDTO) obj;
            FlipTableHelper.imprimir(List.of(entidad));;
        });
    }

    private void filtrarYOrdenarEntidadesDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar entidades del mercado ---");
        boolean bucle = false;
        String rol = "";
        do {
            System.out.print("Filtrar por ROL (1. CLIENTE, 2.PROVEEDOR): ");
            int opcion = Escaner.enteroValido(scanner);
            switch (opcion) {
                case 1 -> rol = "CLIENTE";
                case 2 -> rol = "PROVEEDOR";
                default -> bucle = true;
            };
        } while (bucle);

        System.out.print("Ordenar por 'nombre', 'edad', 'dni' o 'tipoEntidad' (dejar vacío si no aplica): ");
        String orden = scanner.nextLine().trim();
        String direccion = null;
        if (orden.isBlank()) {
            orden = null;
        } else {
            System.out.print("Dirección de orden: 'asc' o 'desc' (dejar vacío si no aplica): ");
            direccion = scanner.nextLine().trim();
            if (direccion.isBlank()) direccion = null;
        }

        StringBuilder urlBuilder = new StringBuilder(API_URL_ENTIDADES  + "/filtrarYOrdenar?");
        if (rol != null) urlBuilder.append("rol_entidad=").append(rol).append("&");
        if (orden != null) urlBuilder.append("sortBy=").append(orden).append("&");
        if (direccion != null) urlBuilder.append("sortDir=").append(direccion);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                EntidadDTO.class,
                "Entidades de su puesto filtradas y ordenadas exitosamente.",
                "No se pudieron filtrar/ordenar las entidades de su puesto."
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

    private void filtrarClientesConPedidosDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("--- Filtrando clientes con pedidos de su puesto ---");
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_ENTIDADES + "/clientesConPedidosPuesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                EntidadDTO.class,
                "Clientes con pedidos de su puesto obtenidos exitosamente.",
                "No se pudieron obtener los clientes con pedidos de su puesto."
        );

        result.ifPresent(obj -> {
            List<EntidadDTO> clientes = (List<EntidadDTO>) obj;
            if (!clientes.isEmpty()) {
                FlipTableHelper.imprimir(clientes);
            } else {
                System.out.println("No se encontraron clientes con pedidos para su puesto.");
            }
        });
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// PUESTO
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void modificarMiPuesto() throws IOException, InterruptedException {
        System.out.println("--- Modificar Mi Puesto ---");
        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Nombre
            0. Cancelar
            Ingrese una opción:""");
        int opcion = Escaner.enteroValido(scanner);

        String jsonBody = "";
        if (opcion == 1) {
            System.out.print("Ingrese el nuevo valor: ");
            String nombre = Escaner.stringValido(scanner);
            jsonBody = "{\"nombre\":\"" + nombre + "\"}";

            HttpResponse<String> response = HttpService.realizarPeticion("PATCH", API_URL_PUESTOS + "/" + puestoUsuario.getPuestoId() + "/puesto" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Puesto modificado exitosamente.");
            } else {
                HandlerResponse.handleErrorResponse(statusCode, response.body());
            }
        } else {
            System.out.println("Cancelando operación...");
        }
    }


    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// INVENTARIO
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /// GET
    private void gestionarInventarioPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE SU INVENTARIO ---
                    OPCIONES:
                    1. Obtener inventario completo
                    2. Buscar por ID de inventario
                    3. Filtrar y ordenar inventario de puesto
                    4. Mostrar productos en stock
                    5. Mostrar productos en stock bajo
                    
                    6. Agregar inventario
                    7. Eliminar inventario
                    8. Modificar inventario
                    
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerInventariosDeMiPuesto();
                case "2" -> buscarInventarioPorIdDeMiPuesto();
                case "3" -> filtrarYordenarItemsInventario();
                case "4" -> mostrarProductosEnStock();
                case "5" -> mostrarProductosEnStockBajo();
                case "6" -> agregarInventarioPorPuesto();
                case "7" -> eliminarInventarioPorPuesto();
                case "8" -> modificarInventarioPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void obtenerInventariosDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo ítems de inventario de su puesto ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_INVENTARIO_PUESTO + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                InventarioPuestoDTO.class, // Esperamos una lista de InventarioPuestoDTO
                "Ítems de inventario de su puesto obtenidos exitosamente.",
                "No se pudieron obtener los ítems de inventario de su puesto."
        );

        result.ifPresent(obj -> {
            List<InventarioPuestoDTO> items = (List<InventarioPuestoDTO>) obj;
            if (!items.isEmpty()) {
                FlipTableHelper.imprimir(items);
            } else {
                System.out.println("No se encontraron ítems en el inventario de su puesto.");
            }
        });
    }

    private void buscarInventarioPorIdDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del ítem de inventario a buscar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_INVENTARIO_PUESTO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                InventarioPuestoDTO.class,
                "Ítem de inventario encontrado en su puesto:",
                "No se encontró el ítem de inventario con ID " + id + " en su puesto."
        );

        result.ifPresent(obj -> {
            InventarioPuestoDTO item = (InventarioPuestoDTO) obj;
            FlipTableHelper.imprimir(List.of(item));;
        });
    }

    private void filtrarYordenarItemsInventario() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR ITEMS DEL INVENTARIO ---");

        Long puestoId = puestoUsuario.getPuestoId(); // Obtener el ID del puesto del atributo de la clase

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

        StringBuilder urlBuilder = new StringBuilder(API_URL_INVENTARIO_PUESTO + "/filtrarYordenarItemsInventario?");
        urlBuilder.append("id=").append(puestoId).append("&");
        if (categoria != null) urlBuilder.append("categoria=").append(categoria).append("&");
        if (sortBy != null) urlBuilder.append("orden=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("direccion=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                InventarioPuestoDTO.class,
                "Inventarios filtrados y ordenados exitosamente.",
                "No se pudieron filtrar/ordenar los inventarios."
        );

        result.ifPresent(obj -> {
            List<InventarioPuestoDTO> inventarios = (List<InventarioPuestoDTO>) obj;
            if (!inventarios.isEmpty()) {
                FlipTableHelper.imprimir(List.of(inventarios));;
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    private void mostrarProductosEnStock() throws IOException, InterruptedException {
        Long id = puestoUsuario.getPuestoId();

        // Realizar la petición HTTP
        HttpResponse<String> response = HttpService.realizarPeticion(
                "GET",
                API_URL_INVENTARIO_PUESTO + "/obtenerInvConStock/" + id,
                authHeader,
                null
        );

        String respuestaJson = response.body();

        List<Map<String, Object>> stock = this.mapper.readValue(
                respuestaJson,
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        // Verificar si hay resultados
        if (stock.isEmpty()) {
            System.out.println("No hay productos en stock.");
            return;
        }

        // Obtener headers (nombres de columnas)
        String[] headers = stock.getFirst().keySet().toArray(new String[0]);

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
        Long id = puestoUsuario.getPuestoId();

        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL_INVENTARIO_PUESTO + "/obtenerInvConStockBajo/" + id, authHeader, null);
        String respuestaJson = response.body();

        List<Map<String, Object>> stock = this.mapper.readValue(respuestaJson, new TypeReference<>() {
        });

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

    /// POST
    private void agregarInventarioPorPuesto() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO INVENTARIO");
        System.out.print("Ingrese cantidad: ");
        Integer cantidad = Escaner.enteroValido(scanner);
        Long puestoId = puestoUsuario.getPuestoId();
        System.out.print("Ingrese ID item: ");
        Long itemId = Long.valueOf(Escaner.enteroValido(scanner));
        System.out.print("Ingrese la cantidad de stock minimo que desea establecer: ");
        Integer stockMin = Escaner.enteroValido(scanner);
        System.out.print("Ingrese el precio de venta del item");
        BigDecimal precioVenta = BigDecimal.valueOf(Escaner.doubleValido(scanner));
        System.out.print("Ingrese el costo de adquisicion");
        BigDecimal costoAdquisicion = BigDecimal.valueOf(Escaner.doubleValido(scanner));

        String jsonBody = "{" +
                "\"cantidad\":" + cantidad + "," +
                "\"puestoId\":" + puestoId + "," +
                "\"itemId\":" + itemId + "," +
                "\"stockMin\":" + stockMin + "," +
                "\"precioVenta\":" + precioVenta + "," +
                "\"costoAdquisicion\":" + costoAdquisicion +
                "}";
        HttpResponse<String> response = HttpService.realizarPeticion("POST", API_URL_INVENTARIO_PUESTO, authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Inventario agregado exitosamente a su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    /// DELETE
    private void eliminarInventarioPorPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a eliminar de su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL_INVENTARIO_PUESTO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Inventario con ID " + id + " eliminado exitosamente de su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    ///PATCH
    private void modificarInventarioPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a modificar en su puesto: ");
        Long id = Long.valueOf(Escaner.enteroValido(scanner));

        // Obtener el inventario existente para obtener los valores actuales
        HttpResponse<String> responseGet = HttpService.realizarPeticion("GET", API_URL_INVENTARIO_PUESTO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, "");
        if (responseGet.statusCode() != 200) {
            HandlerResponse.handleErrorResponse(responseGet.statusCode(), responseGet.body());
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Para manejar tipos de fecha/hora si existen
        InventarioPuestoDTO inventarioExistente = mapper.readValue(responseGet.body(), InventarioPuestoDTO.class);

        System.out.println("Peticion:\"" + API_URL_INVENTARIO_PUESTO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId() + "\"");
        System.out.println("\nATRIBUTO A MODIFICAR:");
        System.out.println("1. Cantidad");
        System.out.println("2. Puesto");
        System.out.println("3. Item");
        System.out.println("4. Stock Minimo");
        System.out.println("5. Precio de venta");
        System.out.println("6. Costo adquisicion");
        System.out.println("0. Cancelar");
        System.out.print("Ingrese una opción: ");
        int opcion = Escaner.enteroValido(scanner);
        if (opcion == 0) return;

        System.out.print("Ingrese el nuevo valor: ");
        String valorNuevo = scanner.nextLine();

        UpdateInventarioPuestoDTO updateDTO = new UpdateInventarioPuestoDTO();
        String jsonBody = "";

        try {
            switch (opcion) {
                case 1 -> updateDTO.setCantidad(Integer.parseInt(valorNuevo));
                case 2 -> System.out.println("No se permite modificar el Puesto directamente desde aquí.");
                case 3 -> System.out.println("No se permite modificar el Item directamente desde aquí.");
                case 4 -> updateDTO.setStockMin(Integer.parseInt(valorNuevo));
                case 5 -> updateDTO.setPrecioVenta(new BigDecimal(valorNuevo));
                case 6 -> updateDTO.setCostoAdquisicion(new BigDecimal(valorNuevo));
                default -> System.out.println("Opción no válida.");
            }
            jsonBody = mapper.writeValueAsString(updateDTO);

        } catch (NumberFormatException e) {
            System.out.println("Error: El valor ingresado no es un número válido para la opción seleccionada.");
            return;
        }

        HttpResponse<String> response = HttpService.realizarPeticion("PATCH", API_URL_INVENTARIO_PUESTO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Inventario modificado exitosamente en su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// CUENTA BANCARIA
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void verMiCuentaBancaria() throws IOException, InterruptedException {
        System.out.println("\n--- Visualizando detalles de su cuenta bancaria ---");
        if (puestoUsuario != null && puestoUsuario.getDuenio().getEntidad_id() != null) {
            Optional<Object> result = HandlerResponse.handleResponse(
                    HttpService.realizarPeticion("GET", API_URL_CUENTAS_BANCARIAS + "/entidad/" + puestoUsuario.getDuenio().getEntidad_id(), authHeader, null),
                    CuentaBancariaDTO.class,
                    "Cuenta bancaria obtenida exitosamente.",
                    "No se encontró la cuenta bancaria para su puesto."
            );

            result.ifPresent(obj -> {
                CuentaBancariaDTO cuenta = (CuentaBancariaDTO) obj;
                FlipTableHelper.imprimir(List.of(cuenta));;
            });
        } else {
            System.out.println("No se encontró una entidad dueña asociada a su puesto para visualizar la cuenta bancaria.");
        }
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// TRANSACCIONES
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void gestionarTransaccionesPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE SUS TRANSACCIONES ---
                    1. Obtener todas las transacciones de su puesto
                    2. Buscar transacción por ID
                    3. Filtrar y Ordenar Transacciones
                    4. Registrar Moviento (INGRESO, EGRESO)
                    
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTransaccionesDeMiPuesto();
                case "2" -> buscarTransaccionPorIdDeMiPuesto();
                case "3" -> filtrarYOrdenarTransaccionesDeMiPuesto();
                case "4" -> registrarMoviento();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void obtenerTransaccionesDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo transacciones de su puesto... ---");
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_TRANSACCIONES + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                TransaccionDTO.class,
                "Transacciones de su puesto obtenidas exitosamente.",
                "No se pudieron obtener las transacciones de su puesto."
        );

        result.ifPresent(obj -> {
            List<TransaccionDTO> transacciones = (List<TransaccionDTO>) obj;
            if (!transacciones.isEmpty()) {
                FlipTableHelper.imprimir(transacciones);
            } else {
                System.out.println("No se encontraron transacciones para su puesto.");
            }
        });
    }

    private void buscarTransaccionPorIdDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la transacción a buscar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_TRANSACCIONES + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                TransaccionDTO.class,
                "Transacción encontrada en su puesto:",
                "No se encontró la transacción con ID " + id + " en su puesto."
        );

        result.ifPresent(obj -> {
            TransaccionDTO transaccion = (TransaccionDTO) obj;
            FlipTableHelper.imprimir(List.of(transaccion));;
        });
    }

    private void filtrarYOrdenarTransaccionesDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar transacciones de su puesto ---");

        System.out.print("Filtrar por TIPO (COMPRA, VENTA, INGRESO, EGRESO) o dejar vacío: ");
        String tipo_transaccion = scanner.nextLine().trim();
        if (tipo_transaccion.isBlank()) tipo_transaccion = null;

        System.out.print("Ordenar por 'fecha', 'monto', 'id_cuenta_origen', 'id_cuenta_destino' o dejar vacío: ");
        String sortBy = scanner.nextLine().trim();
        String sortDir = null;
        if (!sortBy.isBlank()) {
            System.out.print("Dirección de orden: 'asc' o 'desc' (dejar vacío si no aplica): ");
            sortDir = scanner.nextLine().trim();
            if (sortDir.isBlank()) sortDir = null;
        } else {
            sortBy = null;
        }

        StringBuilder urlBuilder = new StringBuilder(API_URL_TRANSACCIONES + "/puesto/" + puestoUsuario.getPuestoId() + "/filtrarYOrdenar?");
        if (tipo_transaccion != null) urlBuilder.append("tipo_transaccion=").append(tipo_transaccion).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                TransaccionDTO.class,
                "Transacciones de su puesto filtradas y ordenadas exitosamente.",
                "No se pudieron filtrar/ordenar las transacciones de su puesto."
        );

        result.ifPresent(obj -> {
            List<TransaccionDTO> transacciones = (List<TransaccionDTO>) obj;
            if (!transacciones.isEmpty()) {
                FlipTableHelper.imprimir(transacciones);
            } else {
                System.out.println("No se encontraron transacciones con esos filtros para su puesto.");
            }
        });
    }

    private void registrarMoviento() throws IOException, InterruptedException {
        String tipoTransaccion = "";

        Long idDuenio=puestoUsuario.getDuenio().getEntidad_id();

        String jsonBody =
                "{"+
                    "\"puestoId\":" + puestoUsuario.getPuestoId() +
                    ",\"tipoTransaccion\":\"" + tipoTransaccion + "\"" +
                    ",\"idEntidad\":" + idDuenio +
                    ",\"idCuentaDestino\":" + 0 + // Porque no hay cuenta de tercero en este caso
                "}";

        boolean bucle = false;
        do {
            bucle = false;
            System.out.print("""
                    Ingrese el tipo de transaccion:
                    1.INGRESO
                    2.EGRESO
                    Ingrese una opcion:""");
            int n = Escaner.enteroValido(scanner);
            switch (n) {
                case 1 -> tipoTransaccion = TipoTransaccion.INGRESO.name();
                case 2 -> tipoTransaccion = TipoTransaccion.EGRESO.name();
                default -> {
                    System.out.println("Opcion no valida. Intente nuevamente...");
                    bucle = true;
                }
            }
        } while (bucle);

        HttpResponse<String> response = HttpService.realizarPeticion("POST", API_URL_PEDIDOS+"/createTransaccion", authHeader, jsonBody);
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// PEDIDOS
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void gestionarPedidosPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE LOS PEDIDOS DE SU PUESTO ---
                    1. Obtener todos los pedidos de su puesto
                    2. Buscar pedido por ID (en su puesto)
                    3. Filtrar y Ordenas pedidos
                    
                    4. Agregar Pedido (a su puesto)
                    5. Eliminar Pedido (de su puesto)
                    6. Modificar Pedido (de su puesto)
                   
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerPedidosDeMiPuesto();
                case "2" -> buscarPedidoPorIdDeMiPuesto();
                case "3" -> filtrarYOrdenarPedido();
                case "4" -> agregarPedidoAMiPuesto();
                case "5" -> eliminarPedidoDeMiPuesto();
                case "6" -> modificarPedidoDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void obtenerPedidosDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los pedidos de su puesto... ---");
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_PEDIDOS + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                PedidoDTO.class,
                "Pedidos de su puesto obtenidos exitosamente.",
                "No se pudieron obtener los pedidos de su puesto."
        );

        result.ifPresent(obj -> {
            List<PedidoDTO> pedidos = (List<PedidoDTO>) obj;
            if (!pedidos.isEmpty()) {
                FlipTableHelper.imprimir(pedidos);
            } else {
                System.out.println("No se encontraron pedidos para su puesto.");
            }
        });
    }

    private void buscarPedidoPorIdDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a buscar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_PEDIDOS + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                PedidoDTO.class,
                "Pedido encontrado en su puesto:",
                "No se encontró el pedido con ID " + id + " en su puesto."
        );

        result.ifPresent(obj -> {
            PedidoDTO pedido = (PedidoDTO) obj;
            FlipTableHelper.imprimir(List.of(pedido));;
        });
    }

    private void agregarPedidoAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar pedido para su puesto ---");

        Long idDuenio=puestoUsuario.getDuenio().getEntidad_id();


        String tipoTransaccion = "";
        boolean bucle = false;
        do {
            bucle = false;
            System.out.print("""
                    Ingrese el tipo de transaccion:
                    1.COMPRA
                    2.VENTA
                    Ingrese una opcion:""");
            int n = Escaner.enteroValido(scanner);
            switch (n) {
                case 1 -> tipoTransaccion = TipoTransaccion.COMPRA.name();
                case 2 -> tipoTransaccion = TipoTransaccion.VENTA.name();
                default -> {
                    System.out.println("Opcion no valida. Intente nuevamente...");
                    bucle = true;
                }
            }
        } while (bucle);

        System.out.println("Ingrese el id de la cuenta bancaria del tercero: ");
        long idCuentaOtro=Long.valueOf(Escaner.enteroValido(scanner));

        String jsonBody =
                "{"+
                    "\"puestoId\":" + puestoUsuario.getPuestoId() +
                    ",\"tipoTransaccion\":\"" + tipoTransaccion + "\"" +
                    ",\"idEntidad\":" + idDuenio +
                    ",\"idCuentaDestino\":" + idCuentaOtro +
                "}";
        HttpResponse<String> response = HttpService.realizarPeticion("POST", API_URL_PEDIDOS+"/createPedidoYtransaccion", authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Pedido agregado exitosamente a su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void eliminarPedidoDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a eliminar de su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL_PEDIDOS + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Pedido con ID " + id + " eliminado exitosamente de su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void modificarPedidoDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido a modificar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID de la transacción
                0. Cancelar
                Ingrese una opción:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                int transaccionId = Escaner.enteroValido(scanner);
                jsonBody = "{\"transaccionId\":"  + transaccionId + "}";
            }
            case 0 -> {}
            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }
        HttpResponse<String> response = HttpService.realizarPeticion("PATCH", API_URL_PEDIDOS + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Pedido modificado exitosamente en su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void filtrarYOrdenarPedido() throws IOException, InterruptedException {
        System.out.println("--- FILTRAR Y ORDENAR PEDIDOS ---");

        boolean bucle;
        String tipoPedido = "";
        do {
            bucle = false;
            System.out.print("""
            TIPO DE TRANSACCIÓN:
            [1] COMPRA
            [2] VENTA
            [3] INGRESO
            [4] EGRESO
            [0] SIN FILTRO
            Opción:""");
            int opcion = Escaner.enteroValido(scanner);
            switch (opcion) {
                case 1 -> tipoPedido = "COMPRA";
                case 2 -> tipoPedido = "VENTA";
                case 3 -> tipoPedido = "INGRESO";
                case 4 -> tipoPedido = "EGRO";
                case 0 -> tipoPedido = null;
                default -> bucle = true;
            }
        } while (bucle);

        LocalDate fechaMin;
        LocalDate fechaMax;
        System.out.print("""
            FILTRADO POR FECHAS:
            [1] ESTABLECER FECHA MAX
            [0] SIN FILTRO FECHA MAX
            Opción:""");
        int opcionFecha = Escaner.enteroValido(scanner);
        switch (opcionFecha) {
            case 1 -> fechaMin = Escaner.fecha(scanner);
            default -> fechaMin = null;
        };
        System.out.print("""
            FILTRADO POR FECHAS:
            [1] ESTABLECER FECHA MIN
            [0] SIN FILTRO FECHA MIN
            Opción:""");
        int opcionFecha2 = Escaner.enteroValido(scanner);
        switch (opcionFecha2) {
            case 1 -> fechaMax = Escaner.fecha(scanner);
            default -> fechaMax = null;
        };

        System.out.print("""
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

        StringBuilder urlBuilder = new StringBuilder(API_URL_PEDIDOS + "/filtrarYOrdenar?");
        if (tipoPedido != null) urlBuilder.append("tipo_transaccion=").append(tipoPedido).append("&");
        if (fechaMin != null) urlBuilder.append("fechaMin=").append(fechaMin).append("&");
        if (fechaMax != null) urlBuilder.append("fechaMax=").append(fechaMax).append("&");
        if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
        if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        HttpResponse<String> response = HttpService.realizarPeticion("GET", finalUrl, authHeader, null);
        String respuestaJson = response.body();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Soporte para LocalDateTime

        List<TransaccionDTO> transacciones = Arrays.asList(mapper.readValue(respuestaJson, TransaccionDTO[].class));

        if (transacciones.isEmpty()) {
            System.out.println("No se encontraron transacciones con esos filtros.");
            return;
        }

        FlipTableHelper.imprimir(transacciones);
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// DETALLES DE PEDIDO
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void gestionarDetallesPedidoPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE DETALLES DE PEDIDO (SU PUESTO) ---
                    1. Detallar pedido por id
                    2. Agregar un Detalle de Pedido
                    3. Eliminar un Detalle de Pedido
                    4. Modificar un Detalle de Pedido
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerDetallesPedidoDeMiPuestoPorPedido();
                case "2" -> agregarDetallePedidoAMiPuesto();
                case "3" -> eliminarDetallePedidoDeMiPuesto();
                case "4" -> modificarDetallePedidoDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            if (!opcion.equals("0")) Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    private void obtenerDetallesPedidoDeMiPuestoPorPedido() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del pedido del cual desea ver los detalles (de su puesto): ");
        Integer pedidoId = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_DETALLES_PEDIDO + "/pedido/" + pedidoId + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                DetallePedidoDTO.class,
                "Detalles de pedido obtenidos exitosamente para el pedido " + pedidoId + ".",
                "No se pudieron obtener los detalles de pedido para el pedido " + pedidoId + " de su puesto."
        );

        result.ifPresent(obj -> {
            List<DetallePedidoDTO> detalles = (List<DetallePedidoDTO>) obj;
            if (!detalles.isEmpty()) {
                FlipTableHelper.imprimir(detalles);
            } else {
                System.out.println("No se encontraron detalles de pedido para el pedido " + pedidoId + " de su puesto.");
            }
        });
    }

    private void agregarDetallePedidoAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar detalle de pedido para un pedido de su puesto ---");

        System.out.print("Ingrese el ID del pedido al que pertenece este detalle: ");
        int pedidoId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese el ID del ítem: ");
        int itemId = Escaner.enteroValido(scanner);

        System.out.print("Ingrese la cantidad: ");
        int cantidad = Escaner.enteroValido(scanner);

        String jsonBody = "{" +
                "\"pedidoId\":" + pedidoId + "," +
                "\"itemId\":" + itemId + "," +
                "\"cantidad\":" + cantidad +
                "}";

        HttpResponse<String> response = HttpService.realizarPeticion("POST", API_URL_DETALLES_PEDIDO + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Detalle de pedido agregado exitosamente a su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void eliminarDetallePedidoDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a eliminar de su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpResponse<String> response = HttpService.realizarPeticion("DELETE", API_URL_DETALLES_PEDIDO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null);

        int statusCode = response.statusCode();
        if (statusCode == 204) {
            System.out.println("Detalle de pedido con ID " + id + " eliminado exitosamente de su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    private void modificarDetallePedidoDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del detalle de pedido a modificar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. ID del pedido
                2. ID del ítem
                3. Cantidad
                0. Cancelar
                Ingrese una opción:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                int pedidoId = Escaner.enteroValido(scanner);
                jsonBody = "{\"pedidoId\":"  + pedidoId + "}";
            }
            case 2 -> {
                int itemId = Escaner.enteroValido(scanner);
                jsonBody = "{\"itemId\":"  + itemId + "}";
            }
            case 3 -> {
                int cantidad = Escaner.enteroValido(scanner);
                jsonBody = "{\"cantidad\":"  + cantidad + "}";
            }
            case 0 -> {}
            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }
        HttpResponse<String> response = HttpService.realizarPeticion("PATCH", API_URL_DETALLES_PEDIDO + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Detalle de pedido modificado exitosamente en su puesto.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }

    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /// ITEMS
    /// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void gestionarItemsGeneral() throws IOException, InterruptedException {
        MenuItem menuItem = new MenuItem(authHeader);
        menuItem.gestionar();
    }


}
