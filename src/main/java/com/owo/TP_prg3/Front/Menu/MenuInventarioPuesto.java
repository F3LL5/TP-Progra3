package com.owo.TP_prg3.Front.Menu;

import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Utilidades.Escaner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class MenuInventarioPuesto {

    private static final String API_URL = "http://localhost:8080/api/inventarioPuestos";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    public MenuInventarioPuesto(String authHeader) {this.authHeader = authHeader;}

    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrar_menu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtener_todos();
                case "2" -> buscar_x_id();
                case "3" -> agregar();
                case "4" -> eliminar();
               // case "5" -> //modificar();
                case "0" -> {}
                default -> System.out.println("OPCIÓN INVÁLIDA. VUELVA A INTENTAR");
            }
        } while (!opcion.equals("0"));
    }

    private void mostrar_menu() {
        System.out.println("""
                --- MENÚ DE GESTIÓN DE INVENTARIO DE PUESTOS ---
                OPCIONES:
                1. Obtener todos los inventarios
                2. Buscar por ID
                3. Agregar inventario
                4. Eliminar inventario
                5. Modificar inventario
                0. Salir
                INGRESE LA OPCIÓN QUE DESEE:""");
    }

    private void obtener_todos() throws IOException, InterruptedException {
        System.out.println("OBTENIENDO inventarios...");
        HttpService.realizarPeticion("GET", API_URL, authHeader, null);
    }

    private void buscar_x_id() throws IOException, InterruptedException {
        System.out.println("INGRESE ID DEL INVENTARIO: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void agregar() throws IOException, InterruptedException {
        System.out.println("AGREGAR NUEVO INVENTARIO");
        System.out.print("Ingrese cantidad: "); String cantidad = Escaner.stringValido(scanner);
        System.out.println("Ingrese id del puesto"); Long puestoId=Long.valueOf(Escaner.enteroValido(scanner));
        System.out.print("Ingrese ID item: "); Long itemId = Long.valueOf(Escaner.enteroValido(scanner));
        System.out.println("Ingrese la cantidad de stock minimo que desea establecer: "); Integer stockMin=Escaner.enteroValido(scanner);
        System.out.println("Ingrese el precio del item"); BigDecimal precioVenta=BigDecimal.valueOf(Escaner.doubleValido(scanner));


        String jsonBody = "{" +
                "\"cantidad\":" +cantidad + ",\n" +
                "\"puestoId\":" + puestoId +",\n" +
                "\"itemId\":" + itemId + ",\n"+
                "\"stockMin\":" + stockMin + ",\n"+
                "\"precioVenta\":" + precioVenta + ",\n"+
        "}";
        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }




    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del inventario a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }












































}
