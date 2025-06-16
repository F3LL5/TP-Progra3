package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Front.Menu.MenuAuditoria.MenuHistorial_Item;
import com.owo.TP_prg3.Front.Utilidades.Escaner;
import com.owo.TP_prg3.Front.HttpService;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuItem {

    //Atributos
    private static final String API_URL = "http://localhost:8080/api/items";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public MenuItem(String authHeader) {this.authHeader = authHeader;}

    //Menu
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            mostrarMenu();
            opcion = Escaner.stringValido(scanner);
            switch (opcion) {
                case "1" -> obtenerTodas();
                case "2" -> buscarPorId();
                case "3" -> agregar();
                case "4" -> eliminar();
                case "5" -> modificar();

                case "1.2" -> listado();
                case "1.3" -> filtrarYordenar();

                case "6" -> {
                    MenuHistorial_Item auditoria = new MenuHistorial_Item(authHeader);
                    auditoria.gestionar();
                }

                case "0" -> {} // Salir
                default -> System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
    }


    private void mostrarMenu() {
    System.out.print("""
                \n--- MENÚ DE GESTIÓN DE ITEMS ---
                OPCIONES:
                1. Obtener todos
                2. Buscar por id
                3. Agregar
                4. Eliminar
                5. Modificar
                
                1.2 Listado
                1.3 filtrarYordenar
                
                6. Historial de costos
                
                0. Salir
                Ingrese la opción:""");
    }

    //Metodos
    //GET
    private void obtenerTodas() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todos los items... ---");

        // Realizar la petición HTTP
        HttpResponse<String> response = HttpService.realizarPeticion("GET", API_URL, authHeader, null);
        String respuestaJson = response.body();

        // Convertir JSON a lista de ItemDTO
        ObjectMapper mapper = new ObjectMapper();
        List<ItemDTO> items = Arrays.asList(mapper.readValue(respuestaJson, ItemDTO[].class));

        // Imprimir en formato tabla
        System.out.println(FlipTableConverters.fromIterable(items, ItemDTO.class));
    }

    private void buscarPorId() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID de la entidad: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("GET", API_URL + "/" + id, authHeader, null);
    }

    private void listado() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas las items... ---");
        HttpService.realizarPeticion("GET", API_URL + "/listado", authHeader, null);
    }

    private void filtrarYordenar() throws IOException, InterruptedException {

        System.out.println("---Filtrar y ordenar items---");
        System.out.println("Filtrar por categoria, sino dejar vacio");
        String categoria= scanner.nextLine();
        if (categoria.isBlank()) categoria = null;



        System.out.println("Ordenar por nombre o costo, sino dejar vacio");
        String orden=scanner.nextLine();
        String direccion=null;
        if (orden.isBlank())
        {
            orden=null;
        }else{
            System.out.println("Ordenar ascendentemente o descendentemente,sino dejar vacio");
            direccion=scanner.nextLine();
            if (direccion.isBlank()) direccion = null;
        }

        StringBuilder urlBuilder=new StringBuilder(API_URL+"/filtrarYordenar?");

        if(categoria!=null) urlBuilder.append("categoria=").append(categoria).append("&");
        if(orden!=null) urlBuilder.append("orden=").append(orden).append("&");
        if(direccion!=null) urlBuilder.append("direccion=").append(direccion);

        String finalurl=urlBuilder.toString();
        if(finalurl.endsWith("&")||finalurl.endsWith("?"))
        {
            finalurl=finalurl.substring(0,finalurl.length()-1);
        }

        System.out.println("\n->Consultando"+finalurl);
        HttpService.realizarPeticion("GET",finalurl,authHeader,null);



    }

    //POST
    private void agregar() throws IOException, InterruptedException {
        System.out.println("\n--- Agregar nuevo item ---");

        System.out.print("Nombre del item: ");
        String nombre = Escaner.stringValido(scanner);
        System.out.print("Categoria del item: ");
        String categoria= Escaner.stringValido(scanner);
        System.out.print("Costo del item: ");
        Double costo = Escaner.doubleValido(scanner);

        String jsonBody = "{" +
                "\"nombre\":\"" + nombre + "\",\n" +
                "\"categoria\":\"" + categoria + "\",\n" +
                "\"costo\":" + costo + "\n" +
                "}";

        HttpService.realizarPeticion("POST", API_URL, authHeader, jsonBody);
    }

    //DELETE
    private void eliminar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a eliminar: ");
        Integer id = Escaner.enteroValido(scanner);
        HttpService.realizarPeticion("DELETE", API_URL + "/" + id, authHeader, null);
    }

    //PATCH
    private void modificar() throws IOException, InterruptedException {
        System.out.print("Ingrese el ID del item a modificar: ");
        Integer id = Escaner.enteroValido(scanner);
        System.out.println();

        System.out.print("""
                ATRIBUTO A MODIFICAR:
                1. Nombre
                2. Categoria
                3. Costo
                0. Cancelar
                Ingrese una opcion:""");
        Integer opcion = Escaner.enteroValido(scanner);
        if (opcion != 0 ) System.out.print("Ingrese el nuevo valor: ");

        String jsonBody = "";
        switch (opcion){
            case 1 -> {
                String nombre = Escaner.stringValido(scanner);
                jsonBody = "{\"nombre\":\"" +  nombre + "\"}" ;
            }
            case 2 -> {
                String categoria = Escaner.stringValido(scanner);
                jsonBody = "{\"categoria\":\"" + categoria + "\"}";
            }
            case 3 -> {
                Double costo = Escaner.doubleValido(scanner);
                jsonBody = "{\"costo\":"  + costo + "}";
            }
            case 0 -> {}
            default -> {
                System.out.println("Opcion no valida.");
                return;
            }
        }

        HttpService.realizarPeticion("PATCH", API_URL + "/" + id, authHeader, jsonBody);
    }









}