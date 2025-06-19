package com.owo.TP_prg3.Front.Menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.fliptables.FlipTableConverters;
import com.owo.TP_prg3.Clases.User.dto.UsuarioDTO;
import com.owo.TP_prg3.Excepciones.Handler.HandlerResponse;
import com.owo.TP_prg3.Front.HttpService;
import com.owo.TP_prg3.Front.Utilidades.Escaner;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

import static com.owo.TP_prg3.Excepciones.Handler.HandlerResponse.handleResponse;

public class MenuUsuarios {
    /// ------------------------------------------ATRIBUTOS-------------------------------------------------------------
    private static final String API_URL = "http://localhost:8080/api/usuarios";
    private final String authHeader;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper mapper = new ObjectMapper();

    /// -----------------------------------------CONSTRUCTORES----------------------------------------------------------
    public MenuUsuarios(String authHeader) {
        HandlerResponse.setObjectMapper(new ObjectMapper());
        this.authHeader = authHeader;
    }

    /// --------------------------------------MENU----------------------------------------------------------------------
    public void gestionar() throws IOException, InterruptedException {
        String opcion;
        do {
            System.out.print("""
                    \n--- MENÚ DE GESTIÓN DE USUARIOS ---
                    1. Crear nuevo usuario para una entidad existente
                    2. Listar todos los usuarios.
                    
                    0. Volver al menú principal
                    Ingrese una opción:""");
            opcion = Escaner.stringValido(scanner);

            switch (opcion) {
                case "1" -> crearUsuario();
                case "2" -> obtenerTodos();
                case "0" -> {
                }
                default -> System.out.println("Opción no válida.");
            }
            Escaner.pausa(scanner);
        } while (!opcion.equals("0"));
    }

    /// ------------------------------------GET-------------------------------------------------------------------------
    private void obtenerTodos() throws IOException, InterruptedException {
        System.out.println("\n--- Obteniendo todas los usuarios... ---");

        Optional<Object> result = handleResponse(
                HttpService.realizarPeticion("GET", API_URL, authHeader, null),
                UsuarioDTO.class,
                "Usuarios obtenidos exitosamente.",
                "No se pudieron obtener los Usuarios."
        );

        result.ifPresent(obj -> {
            List<UsuarioDTO> items = (List<UsuarioDTO>) obj;
            if (!items.isEmpty()) {
                System.out.println(FlipTableConverters.fromIterable(items, UsuarioDTO.class));
            } else {
                System.out.println("No se encontraron resultados.");
            }
        });
    }

    /// ----------------------------------POST--------------------------------------------------------------------------
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