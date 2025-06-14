package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Utilidades.Escaner;

import java.io.IOException;
import java.util.Scanner;

public class MenuUsuarios {
    private static final String API_URL = "http://localhost:8080/api/usuarios";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuUsuarios(String authHeader) {
        this.authHeader = authHeader;
    }

    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                \n--- MENÚ DE GESTIÓN DE USUARIOS ---
                1. Crear nuevo usuario para una entidad existente
                0. Volver al menú principal
                Ingrese una opción: """);
            opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> crearUsuario();
                case "0" -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }

    private void crearUsuario() throws IOException, InterruptedException {
        System.out.println("\n--- Crear Nuevo Usuario ---");
        System.out.print("Ingrese el DNI de la entidad existente para la cual crear el usuario: ");
        int dni = Escaner.enteroValido(scanner);
        System.out.print("Ingrese la contraseña para el nuevo usuario: ");
        String password = Escaner.stringValido(scanner);

        // El backend se encargará de buscar la entidad, verificar su tipo y asignar el rol.
        String jsonBody = String.format("{\"dni\":%d, \"password\":\"%s\"}", dni, password);

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }
}