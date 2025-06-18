package com.owo.TP_prg3.Front.Menu.MenuAuditoria;

import com.owo.TP_prg3.Auditoria.DuenioHistorial.dto.DuenioHistorialDTO;
import com.owo.TP_prg3.Auditoria.DuenioHistorial.modelo.DuenioHistorial;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.Utilidades.FlipTableHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
                
                0. Salir
                Ingrese la opción:""");
    }

    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los historiales de duenios... ---");

        Optional<Object> result = HandlerResponse.handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                DuenioHistorialDTO.class,
                "Historial obtenido exitosamente.",
                "No se pudieron obtener los historiales."
        );

        result.ifPresent(obj -> {
            List<DuenioHistorialDTO> historiales = (List<DuenioHistorialDTO>) obj;
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
                DuenioHistorialDTO.class,
                "Historial encontrado:",
                "No se encontró el historal con ID " + id + "."
        );

        result.ifPresent(obj -> {
            DuenioHistorialDTO historial = (DuenioHistorialDTO) obj;
            FlipTableHelper.imprimir(List.of(historial));
        });
    }

}
