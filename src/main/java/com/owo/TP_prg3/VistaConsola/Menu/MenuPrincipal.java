package com.owo.TP_prg3.VistaConsola.Menu;


import com.owo.TP_prg3.VistaConsola.AuthService;
import com.owo.TP_prg3.VistaConsola.UsuarioAutenticado;

import java.io.IOException;
import java.util.Scanner;

public class MenuPrincipal {

    public static void main(String[] args) throws IOException {
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
                0. Salir
                Ingrese una opción: """);
            opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> {
                    MenuEntidades menuEntidades = new MenuEntidades(authService.getAuthHeader());
                    menuEntidades.gestionar();
                }
                case "2" -> {
                    MenuUsuarios menuUsuarios = new MenuUsuarios(authService.getAuthHeader());
                    menuUsuarios.gestionar();
                }
                case "0" -> System.out.println("Saliendo del programa.");
                default -> System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }
}
