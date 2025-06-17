package com.owo.TP_prg3.Front.MenuV2;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Front.Menu.*;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.util.Scanner;

public class MenuAdmin {

    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final Puesto puestoUsuario; // Necesario para las gestiones por puesto
    private final Entidad entidadUsuario; // Necesario si la entidad es relevante para filtros o permisos específicos

    // Constructor que ahora recibe el Puesto y la Entidad del usuario logueado
    public MenuAdmin(String authHeader, Puesto puestoUsuario, Entidad entidadUsuario) {
        this.authHeader = authHeader;
        this.puestoUsuario = puestoUsuario;
        this.entidadUsuario = entidadUsuario;
    }

    public void iniciar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenuAdmin();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> gestionarEntidades();
                case "2" -> gestionarUsuarios();
                case "3" -> gestionarPuestos();
                case "4" -> gestionarItems();
                case "5" -> gestionarCuentasBancarias();
                case "6" -> gestionarTransacciones();
                case "7" -> gestionarPedidos();
                case "8" -> gestionarDetallesPedido();
                case "0" -> System.out.println("Saliendo del menú de administrador.");
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrarMenuAdmin() {
        System.out.print("""
                \n--- MENÚ DE ADMINISTRADOR ---
                1. Gestionar Entidades
                2. Gestionar Usuarios
                3. Gestionar Puestos
                4. Gestionar Items (Solo si tiene un puesto)
                5. Gestionar Cuentas bancarias (Solo su cuenta)
                6. Gestionar Transacciones (Solo de su puesto)
                7. Gestionar Pedidos (Solo si tiene un puesto)
                8. Gestionar Detalles de Pedido (Solo si tiene un puesto)
                0. Salir
                Ingrese una opción:"""
        );
    }

    // Métodos
    /*
     Gestiona las entidades.
     El admin puede ver y gestionar todas las entidades.
     La clase MenuEntidades ya permite gestionar todas las entidades.
    */
    private void gestionarEntidades() throws IOException, InterruptedException {
        MenuEntidades menuEntidades = new MenuEntidades(authHeader);
        menuEntidades.gestionar();
    }

    /*
     Gestiona los usuarios.
     El admin puede crear, listar todos y ver su propio usuario.
     La clase MenuUsuarios ya implementa estas funcionalidades.
    */
    private void gestionarUsuarios() throws IOException, InterruptedException {
        MenuUsuarios menuUsuarios = new MenuUsuarios(authHeader);
        menuUsuarios.gestionar();
    }

    /*
     Gestiona los puestos.
     El admin puede obtener, buscar, agregar, eliminar y modificar puestos.
     La clase MenuPuestos ya implementa estas funcionalidades.
    */
    private void gestionarPuestos() throws IOException, InterruptedException {
        MenuPuestos menuPuestos = new MenuPuestos(authHeader);
        menuPuestos.gestionar();
    }

    /*
     Gestiona los ítems.
     SOLO SI EL USUARIO ADMIN TIENE UN PUESTO ASIGNADO y maneja los ítems de SU PUESTO.
     MenuInventarioPuesto ya tiene la lógica para obtener por puesto (obtener_todos, buscar_x_id, etc.
     necesitarán ser adaptados para usar el puestoUsuario.getId() en la URL si no lo hacen por defecto).
    */
    private void gestionarItems() throws IOException, InterruptedException {
        if (puestoUsuario != null) {
            MenuInventarioPuesto menuInventarioPuesto = new MenuInventarioPuesto(authHeader);
            menuInventarioPuesto.gestionar(); // Este menu gestiona el inventario por puesto
        } else {
            System.out.println("No tiene un puesto asignado para gestionar ítems de inventario.");
        }
    }

    /*
     Gestiona las cuentas bancarias.
     SOLO SI EL USUARIO ADMIN PUEDE MANEJAR SU CUENTA BANCARIA.
     La intención es que el ADMIN solo vea/gestione SU cuenta, se debe adaptar MenuCuentasBancarias
     para filtrar por entidadUsuario.getId()
    */
    private void gestionarCuentasBancarias() throws IOException, InterruptedException {
        MenuCuentasBancarias menuCuentasBancarias = new MenuCuentasBancarias(authHeader);
        menuCuentasBancarias.gestionar();
    }

    /*
     Gestiona las transacciones.
     SOLAMENTE DE SU PUESTO. No puede agregar/eliminar/modificar.
     Adaptar MenuTransacciones para que esas opciones no se muestren o estén deshabilitadas
     si se llama desde el MenuAdmin, y para que las operaciones GET siempre filtren por puestoUsuario.getId().
    */
    private void gestionarTransacciones() throws IOException, InterruptedException {
        if (puestoUsuario != null) {
            // 1. Sus métodos `agregar()`, `eliminar()`, `modificar()` no sean llamados o se muestren en el menú.
            // 2. Los métodos `obtenerTodas()` y `buscarPorId()` filtren por `puestoUsuario.getId()`.
            MenuTransacciones menuTransacciones = new MenuTransacciones(authHeader);
            menuTransacciones.gestionar();
        } else {
            System.out.println("No tiene un puesto asignado para gestionar transacciones.");
        }
    }

    /*
     Gestiona los pedidos.
     SOLAMENTE SI EL USUARIO ADMIN TIENE UN PUESTO ASIGNADO.
     Deberás adaptar MenuPedidos para que sus operaciones siempre filtren por puestoUsuario.getId().
    */
    private void gestionarPedidos() throws IOException, InterruptedException {
        if (puestoUsuario != null) {
            // Modificar `MenuPedidos` para que:
            // 1. Sus métodos `obtenerTodas()`, `buscarPorId()`, `agregar()`, `eliminar()`, `modificar()`
            //    filtren o asocien operaciones al `puestoUsuario.getId()`.
            MenuPedidos menuPedidos = new MenuPedidos(authHeader);
            menuPedidos.gestionar();
        } else {
            System.out.println("No tiene un puesto asignado para gestionar pedidos.");
        }
    }

    /*
     Gestiona los detalles de pedido.
     SOLAMENTE SI EL USUARIO ADMIN TIENE UN PUESTO ASIGNADO.
     Adaptar MenuDetallePedido para que sus operaciones siempre filtren por puestoUsuario.getId().
    */
    private void gestionarDetallesPedido() throws IOException, InterruptedException {
        if (puestoUsuario != null) {
            // Modificar `MenuDetallePedido` para que:
            // 1. Sus métodos `obtenerTodas()`, `buscarPorId()`, `agregar()`, `eliminar()`, `modificar()`
            //    filtren o asocien operaciones al `puestoUsuario.getId()`.
            MenuDetallePedido menuDetallePedido = new MenuDetallePedido(authHeader);
            menuDetallePedido.gestionar();
        } else {
            System.out.println("No tiene un puesto asignado para gestionar detalles de pedido.");
        }
    }
}