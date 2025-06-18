package com.owo.TP_prg3.Front.Menu.MenuAuditoria;

import com.owo.TP_prg3.Auditoria.DuenioHistorial.dto.DuenioHistorialDTO;
import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.dto.InventarioCostoHistorialDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuHistorial_InventarioCosto {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/InventarioCostoHistorial";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuHistorial_InventarioCosto(String authHeader) {this.authHeader = authHeader;}

    //Menu
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
            Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
        System.out.print("""
                \n--- MENÚ DE AUDITORIA COSTO ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                
                1.3 filtrarYordenar
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los historiales de inventario costo... ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                InventarioCostoHistorialDTO.class,
                "Historial obtenido exitosamente.",
                "No se pudieron obtener los historiales."
        );

        result.ifPresent(obj -> {
            List<InventarioCostoHistorialDTO> historiales = (List<InventarioCostoHistorialDTO>) obj;
            if (!historiales.isEmpty()) {
                FlipTableHelper.imprimir(historiales);
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del historial: ");
        Integer id = Escaner.enteroValido(scanner);

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null),
                InventarioCostoHistorialDTO.class,
                "Historial encontrado:",
                "No se encontró el historal con ID " + id + "."
        );

        result.ifPresent(obj -> {
            InventarioCostoHistorialDTO historial = (InventarioCostoHistorialDTO) obj;
            FlipTableHelper.imprimir(List.of(historial));
        });
    }



}
