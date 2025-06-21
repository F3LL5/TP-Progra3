package com.owo.TP_prg3.Front.Menu;


import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Front.Auntenticacion.AuthService;
import com.owo.TP_prg3.Front.Auntenticacion.UsuarioAutenticado;
import com.owo.TP_prg3.Front.MenuV2.MenuDuenoPuesto;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class MenuPrincipal {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("--- Cliente de Consola para API ---");
        AuthService authService = new AuthService();

        do {
            UsuarioAutenticado usuario = null;
            Puesto puestoUsuario = null;
            Entidad entidadUsuario = null;
            //Te obliga a iniciar sesion para poder salir del bucle
            while (usuario == null) {
                Map<String, Object> datosUsuario = authService.iniciarSesion();
                if (datosUsuario != null) {
                    usuario = (UsuarioAutenticado) datosUsuario.get("usuario");

                    //Se asigna el puesto si tiene
                    if (datosUsuario.get("puesto") != null) {
                        puestoUsuario = (Puesto) datosUsuario.get("puesto");
                        System.out.println("Puesto encontrado.");
                    }
                    //Se asigna la entidad si tiene
                    if (datosUsuario.get("entidad") != null) {
                        entidadUsuario = (Entidad) datosUsuario.get("entidad");
                        System.out.println("Entidad encontrada.");
                    }
                    if (usuario == null) {
                        System.out.println("Login fallido. Por favor, intente de nuevo.");
                    }
                }
            }

            if (entidadUsuario != null)
                System.out.println("\nBienvenido! " + entidadUsuario.getNombre() + ", permisos: " + usuario.getRol());
            else System.out.println("\nBienvenido! permisos: " + usuario.getRol());

            if ("DUENO_PUESTO".equalsIgnoreCase(usuario.getRol())) { // Si el rol es DUENO_PUESTO
                MenuDuenoPuesto menuDuenoPuesto = new MenuDuenoPuesto(authService.getAuthHeader(), puestoUsuario);
                menuDuenoPuesto.gestionar();
            } else {
                Scanner scanner = new Scanner(System.in);
                String opcion;
                do {
                    System.out.print("""
                            \n--- MENÚ ADMIN ---
                            1. Gestionar Entidades
                            2. Gestionar Usuarios
                            
                            3. Gestionar Puestos
                            4. Gestionar Items
                            5. Gestionar Inventario de puestos
                            
                            6. Gestionar Cuentas bancarias
                            7. Gestionar Transacciones
                            
                            8. Gestionar Pedidos
                            9. Gestionar Detalles de pedido
                            
                            0. Cerrar sesión
                            -1. Salir
                            Ingrese una opción:""");
                    opcion = Escaner.stringValido(scanner);

                    switch (opcion) {
                        case "1" -> {
                            MenuEntidades menuEntidades = new MenuEntidades(authService.getAuthHeader());
                            menuEntidades.gestionar();
                        }
                        case "2" -> {
                            MenuUsuarios menuUsuarios = new MenuUsuarios(authService.getAuthHeader());
                            menuUsuarios.gestionar();
                        }
                        case "3" -> {
                            MenuPuestos menuPuestos = new MenuPuestos(authService.getAuthHeader());
                            menuPuestos.gestionar();
                        }
                        case "4" -> {
                            MenuItem menuItem = new MenuItem(authService.getAuthHeader());
                            menuItem.gestionar();
                        }
                        case "5" -> {
                            MenuInventarioPuesto menuInventarioPuesto = new MenuInventarioPuesto(authService.getAuthHeader());
                            menuInventarioPuesto.gestionar();
                        }
                        case "6" -> {
                            MenuCuentasBancarias menuCuentasBancarias = new MenuCuentasBancarias(authService.getAuthHeader());
                            menuCuentasBancarias.gestionar();
                        }
                        case "7" -> {
                            MenuTransacciones menuTransacciones = new MenuTransacciones(authService.getAuthHeader());
                            menuTransacciones.gestionar();
                        }
                        case "8" -> {
                            MenuPedidos menuPedidos = new MenuPedidos(authService.getAuthHeader());
                            menuPedidos.gestionar();
                        }
                        case "9" -> {
                            MenuDetallePedido menuDetallePedido = new MenuDetallePedido(authService.getAuthHeader());
                            menuDetallePedido.gestionar();
                        }
                        case "0" -> System.out.println("Saliendo del programa.");
                        case "-1" -> {
                            return;
                        }
                        default -> System.out.println("Opción no válida.");
                    }
                } while (!opcion.equals("0"));
            }
        } while(true);
    }
}
