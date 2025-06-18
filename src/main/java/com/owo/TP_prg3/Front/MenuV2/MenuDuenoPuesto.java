package com.owo.TP_prg3.Front.MenuV2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Menu.MenuInventarioPuesto;
import com.owo.TP_prg3.Front.Menu.MenuItem;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class MenuDuenoPuesto {

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
                case "2" -> System.out.println("No tiene permisos para gestionar usuarios desde este menú."); // Según el gráfico
                case "3" -> modificarMiPuesto();
                case "4" -> gestionarItemsPuesto();
                case "5" -> verMiCuentaBancaria();
                case "6" -> gestionarTransaccionesPuesto();
                case "7" -> gestionarPedidosPuesto();
                case "8" -> gestionarDetallesPedidoPuesto();
                case "9" -> gestionarItemsGeneral();
                case "0" -> System.out.println("Saliendo del menú de Dueño de Puesto.");
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrarMenuDueñoPuesto() {
        System.out.print("""
                \n--- MENÚ DE DUEÑO DE PUESTO (Puesto: %s) ---
                1. Gestionar Entidades (Clientes y Proveedores de su puesto)
                2. Gestionar Usuarios (No permitido para Dueños de Puesto)
                3. Modificar Mi Puesto
                4. Gestionar inventario
                5. Ver Detalles de Mi Cuenta Bancaria
                6. Gestionar Transacciones (De su puesto)
                7. Gestionar Pedidos (De su puesto)
                8. Gestionar Detalles de Pedido (De su puesto)
                9. Gestionar items
                0. Salir
                Ingrese una opción:""".formatted(puestoUsuario.getNombre())
        );
    }

    // --- Métodos de gestión específicos para Dueño de Puesto ---

    /*
     Gestiona entidades de tipo Cliente y Proveedor vinculadas a su puesto.
     Incluye obtener todos, buscar por ID, agregar, eliminar y modificar.
     También permite filtrar clientes que tengan un pedido de su puesto.
    */
    private void gestionarEntidadesPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE ENTIDADES (SU PUESTO) ---
                    1. Obtener todas las entidades (Clientes/Proveedores de su puesto)
                    2. Buscar entidad por ID (en su puesto)
                    3. Agregar Entidad (para su puesto)
                    3.1 Agregar Entidad (Cuenta bancaria automatica)
                    4. Eliminar Entidad (de su puesto)
                    5. Modificar Entidad (de su puesto)
                    6. Filtrar y Ordenar Entidades (de su puesto)
                    7. Filtrar Clientes con pedidos de su puesto
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerEntidadesDeMiPuesto();
                case "2" -> buscarEntidadPorIdDeMiPuesto();
                case "3" -> agregarEntidadAMiPuesto();
                case "3.1" -> agregarEntidadAMiPuesto2();
                case "4" -> eliminarEntidadDeMiPuesto();
                case "5" -> modificarEntidadDeMiPuesto();
                case "6" -> filtrarYOrdenarEntidadesDeMiPuesto();
                case "7" -> filtrarClientesConPedidosDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    // Métodos para gestionar entidades de su puesto
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

        System.out.println("\n--- Obteniendo entidades (Clientes/Proveedores) ---");
        result.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(entidades, EntidadDTO.class));
            } else {
                System.out.println("No se encontraron entidades para su puesto.");
            }
        });

        result2.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(entidades, EntidadDTO.class));
            } else {
                System.out.println("No se encontraron entidades para su puesto.");
            }
        });
    }

    private void buscarEntidadPorIdDeMiPuesto() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad a buscar en su puesto: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL_ENTIDADES + "/" + id + "/puesto/" + puestoUsuario.getPuestoId(), authHeader, null),
                EntidadDTO.class,
                "Entidad encontrada en su puesto:",
                "No se encontró la entidad con ID " + id + " en su puesto."
        );

        result.ifPresent(obj -> {
            EntidadDTO entidad = (EntidadDTO) obj;
            System.out.println(FlipTableConverters.fromIterable(List.of(entidad), EntidadDTO.class));
        });
    }

    private void agregarEntidadAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad para su puesto ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        System.out.print("Rol [CLIENTE, PROVEEDOR]: ");
        String rol = Escaner.stringValido(scanner);
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
            System.out.println(FlipTableConverters.fromIterable(List.of(entidadDTO), EntidadDTO.class));
        });
    }
    private void agregarEntidadAMiPuesto2() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nueva entidad para su puesto ---");
        System.out.print("Nombre: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Tipo de Entidad: ");
        String tipoEntidad = Escaner.stringValido(scanner);
        System.out.print("Rol [CLIENTE, PROVEEDOR]: ");
        String rol = Escaner.stringValido(scanner);
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
            System.out.println(FlipTableConverters.fromIterable(List.of(entidadDTO), EntidadDTO.class));
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
            System.out.println(FlipTableConverters.fromIterable(List.of(entidad), EntidadDTO.class));
        });
    }

    private void filtrarYOrdenarEntidadesDeMiPuesto() throws IOException, InterruptedException {
        System.out.println("--- Filtrar y ordenar entidades de su puesto ---");

        System.out.print("Filtrar por ROL (CLIENTE, PROVEEDOR) o dejar vacío: ");
        String rol = scanner.nextLine().trim();
        if (rol.isBlank()) rol = null;

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

        StringBuilder urlBuilder = new StringBuilder(API_URL_ENTIDADES + "/puesto/" + puestoUsuario.getPuestoId() + "/filtrarYOrdenar?");
        if (rol != null) urlBuilder.append("rol_entidad=").append(rol).append("&");
        if (orden != null) urlBuilder.append("sortBy=").append(orden).append("&");
        if (direccion != null) urlBuilder.append("sortDir=").append(direccion);

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        System.out.println("\n-> Consultando " + finalUrl);
        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                EntidadDTO.class,
                "Entidades de su puesto filtradas y ordenadas exitosamente.",
                "No se pudieron filtrar/ordenar las entidades de su puesto."
        );

        result.ifPresent(obj -> {
            List<EntidadDTO> entidades = (List<EntidadDTO>) obj;
            if (!entidades.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(entidades, EntidadDTO.class));
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
                System.out.println(FlipTableConverters.fromIterable(clientes, EntidadDTO.class));
            } else {
                System.out.println("No se encontraron clientes con pedidos para su puesto.");
            }
        });
    }

    private void modificarMiPuesto() throws IOException, InterruptedException {
        System.out.print("--- Modificar Mi Puesto ---");
        System.out.println();

        System.out.print("""
            ATRIBUTO A MODIFICAR:
            1. Nombre
            2. Comisión
            0. Cancelar
            Ingrese una opción:""");
        int opcion = Escaner.enteroValido(scanner);

        String jsonBody = "";
        if (opcion == 0) {
            System.out.println("Modificación cancelada.");
            return;
        } else if (opcion < 1 || opcion > 2) {
            System.out.println("Opción no válida.");
            return;
        }

        System.out.print("Ingrese el nuevo valor: ");
        switch (opcion){
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\"" +  nombre + "\"}" ;
            }
            case 2 -> {
                double comision = Escaner.doubleValido(scanner);
                jsonBody = "{\"comision\":" + comision + "}";
            }
        }
        System.out.println(jsonBody);
        HttpResponse<String> response = HttpService.realizarPeticion("PATCH", API_URL_PUESTOS + "/" + puestoUsuario.getPuestoId(), authHeader, jsonBody);

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Puesto modificado exitosamente.");
        } else {
            HandlerResponse.handleErrorResponse(statusCode, response.body());
        }
    }


    /*
     Gestiona los ítems de inventario del puesto del dueño.
     Invoca a MenuInventarioPuesto, pasando el ID del puesto para que filtre las operaciones.
    */
    private void gestionarItemsPuesto() throws IOException, InterruptedException {
        MenuInventarioPuesto menuInventarioPuesto = new MenuInventarioPuesto(authHeader);
        menuInventarioPuesto.gestionar(); // Pasar el puestoUsuario.getPuestoId()
    }

    /*
     Permite al dueño ver los detalles de su propia cuenta bancaria.
     Se asume que la entidad asociada al puesto tiene una cuenta bancaria.
    */
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
                System.out.println(FlipTableConverters.fromIterable(List.of(cuenta), CuentaBancariaDTO.class));
            });
        } else {
            System.out.println("No se encontró una entidad dueña asociada a su puesto para visualizar la cuenta bancaria.");
        }
    }

    /*
     Gestiona las transacciones del puesto
     Incluye obtener todas, buscar por ID y filtrar/ordenar.
     Las operaciones de agregar, eliminar y modificar no están permitidas para el dueño.
    */
    private void gestionarTransaccionesPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE TRANSACCIONES (SU PUESTO) ---
                    1. Obtener todas las transacciones de su puesto
                    2. Buscar transacción por ID (en su puesto)
                    3. Filtrar y Ordenar Transacciones (de su puesto)
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTransaccionesDeMiPuesto();
                case "2" -> buscarTransaccionPorIdDeMiPuesto();
                case "3" -> filtrarYOrdenarTransaccionesDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    // Métodos para gestionar transacciones de su puesto
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
                System.out.println(FlipTableConverters.fromIterable(transacciones, TransaccionDTO.class));
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
            System.out.println(FlipTableConverters.fromIterable(List.of(transaccion), TransaccionDTO.class));
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

        System.out.println("\n-> Consultando " + finalUrl);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", finalUrl, authHeader, null),
                TransaccionDTO.class,
                "Transacciones de su puesto filtradas y ordenadas exitosamente.",
                "No se pudieron filtrar/ordenar las transacciones de su puesto."
        );

        result.ifPresent(obj -> {
            List<TransaccionDTO> transacciones = (List<TransaccionDTO>) obj;
            if (!transacciones.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(transacciones, TransaccionDTO.class));
            } else {
                System.out.println("No se encontraron transacciones con esos filtros para su puesto.");
            }
        });
    }

    /*
     Gestiona los pedidos del puesto del dueño.
     Incluye obtener todos, buscar por ID, agregar, eliminar y modificar.
    */
    private void gestionarPedidosPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE PEDIDOS (SU PUESTO) ---
                    1. Obtener todos los pedidos de su puesto
                    2. Buscar pedido por ID (en su puesto)
                    3. Agregar Pedido (a su puesto)
                    4. Eliminar Pedido (de su puesto)
                    5. Modificar Pedido (de su puesto)
                    0. Volver al menú de Dueño de Puesto
                    Ingrese una opción:"""
            );
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerPedidosDeMiPuesto();
                case "2" -> buscarPedidoPorIdDeMiPuesto();
                case "3" -> agregarPedidoAMiPuesto();
                case "4" -> eliminarPedidoDeMiPuesto();
                case "5" -> modificarPedidoDeMiPuesto();
                case "0" -> {}
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    // Métodos para gestionar pedidos de su puesto
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
                System.out.println(FlipTableConverters.fromIterable(pedidos, PedidoDTO.class));
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
            System.out.println(FlipTableConverters.fromIterable(List.of(pedido), PedidoDTO.class));
        });
    }

    private void agregarPedidoAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar pedido para su puesto ---");

        Long idDuenio=puestoUsuario.getDuenio().getEntidad_id();



        String tipoTransaccion = "";
        boolean bucle = false;
        do {
            bucle = false;
            System.out.println("""
                    Ingrese el tipo de transaccion:
                    1.COMPRA
                    2.VENTA
                    3.INGRESO
                    4.EGRESO
                    """);
            int n = Escaner.enteroValido(scanner);
            switch (n) {
                case 1 -> tipoTransaccion = TipoTransaccion.COMPRA.name();
                case 2 -> tipoTransaccion = TipoTransaccion.VENTA.name();
                case 3 -> tipoTransaccion = TipoTransaccion.INGRESO.name();
                case 4 -> tipoTransaccion = TipoTransaccion.EGRESO.name();
                default -> {
                    System.out.println("Opcion no valida. Intente nuevamente...");
                    bucle = true;
                }
            }
        } while (bucle);

        Long idCuentaOtro = Long.valueOf("0");

        if (TipoTransaccion.COMPRA.name().equals(tipoTransaccion) || TipoTransaccion.VENTA.name().equals(tipoTransaccion)){
            System.out.println("Ingrese el id de la cuenta cliente/proveedor");
            idCuentaOtro=Long.valueOf(Escaner.enteroValido(scanner));
        }
        System.out.println(puestoUsuario.getPuestoId()+"<---asdasdasdasdasdasd");

        String jsonBody =
                "{"+
                    "\"puestoId\":" + puestoUsuario.getPuestoId() +
                    ",\"tipoTransaccion\":\"" + tipoTransaccion + "\"" +
                    ",\"idEntidad\":" + idDuenio +
                    ",\"idCuentaDestino\":" + idCuentaOtro +
                "}";
        System.out.println(jsonBody);
        HttpResponse<String> response = HttpService.realizarPeticion("POST", API_URL_PEDIDOS+"/createPedidoYtransaccion", authHeader, jsonBody);

        System.out.println(response+"<--asdasdasd");
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

    /*
     Gestiona los detalles de pedido del puesto del dueño.
     Incluye obtener todos, buscar por ID, agregar, eliminar y modificar.
    */
    private void gestionarDetallesPedidoPuesto() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- GESTIÓN DE DETALLES DE PEDIDO (SU PUESTO) ---
                    1. Obtener todos los detalles de pedido de su puesto (por ID de Pedido)
                    2. Agregar Detalle de Pedido (a un pedido de su puesto)
                    3. Eliminar Detalle de Pedido (de un pedido de su puesto)
                    4. Modificar Detalle de Pedido (de un pedido de su puesto)
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
        } while (!opcion.equals("0"));
    }

    // Métodos para gestionar detalles de pedido de su puesto
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
                System.out.println(FlipTableConverters.fromIterable(detalles, DetallePedidoDTO.class));
            } else {
                System.out.println("No se encontraron detalles de pedido para el pedido " + pedidoId + " de su puesto.");
            }
        });
    }


    private void agregarDetallePedidoAMiPuesto() throws IOException, InterruptedException {
        System.out.println("\n--- Registrar detalle de pedido para un pedido de su puesto ---");

        System.out.print("Ingrese el ID del pedido (de su puesto) al que pertenece este detalle: ");
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

    private void gestionarItemsGeneral() throws IOException, InterruptedException {
        MenuItem menuItem = new MenuItem(authHeader);
        menuItem.gestionar();
    }


}
