package com.owo.TP_prg3.Front.Menu.MenuAuditoria;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import java.io.IOException;
import java.util.Scanner;

public class MenuHistorial_Duenio {
    //Atributos
    private static final String API_URL = "http://localhost:8080/api/DuenioHistorial";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    /// CONSTRUCTOR
    public MenuHistorial_Duenio(String authHeader) {
        this.authHeader = authHeader;
    }

    /// MENU
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();

                case "1.2" -> listado();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE AUDITORIA DUENIO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                
                1.2 Listado
                0. Salir
                Ingrese la opción:""");
    }

    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los cambios... ---");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del cambio: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los cambios... ---");
        HttpService.realizarPeticion("GET", API_URL + "/listado", authHeader, null);
    }

}
