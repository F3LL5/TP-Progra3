package com.owo.TP_prg3.Front.Menu;


import com.owo.TP_prg3.Front.Auntenticacion.AuthService;
import com.owo.TP_prg3.Front.Auntenticacion.UsuarioAutenticado;
import com.owo.TP_prg3.Utilidades.Escaner;

import java.io.IOException;
import java.util.Scanner;

public class MenuPrincipal {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("--- Cliente de Consola para API ---");
        AuthService authService = new AuthService();
        UsuarioAutenticado usuario = null;

        while (usuario == null) {
            usuario = authService.iniciarSesion();
            if (usuario == null) {
                System.out.println("Login fallido. Por favor, intente de nuevo.");
            }
        }

        System.out.println("\nBienvenido! Rol de usuario: " + usuario.getRol());

        Scanner scanner = new Scanner(System.in);
        String opcion;
        do {
            System.out.print("""
                \n--- MENÚ PRINCIPAL ---
                1. Gestionar Entidades
                2. Gestionar Usuarios
                3. Gestionar Puestos
                4. Gestionar Items
                5. Gestionar Cuentas bancarias
                6. Gestionar Transacciones
                0. Salir
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
                case "4" ->{
                    MenuItem menuItem = new MenuItem(authService.getAuthHeader());
                    menuItem.gestionar();
                }
                case "5" ->{
                    MenuCuentasBancarias menuCuentasBancarias= new MenuCuentasBancarias(authService.getAuthHeader());
                    menuCuentasBancarias.gestionar();
                }
                case "6" ->{
                    MenuTransacciones menuTransacciones = new MenuTransacciones(authService.getAuthHeader());
                    menuTransacciones.gestionar();
                }
                case "0" -> System.out.println("Saliendo del programa.");
                default -> System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }
}
