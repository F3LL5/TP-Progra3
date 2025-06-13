package com.owo.TP_prg3.Front.Auntenticacion;

import com.owo.TP_prg3.Front.HttpService;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Scanner;

//Clase para realizar auntenticaciones
//Bibliografía: https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
public class AuthService {

    private static final String PROFILE_URL = "http://localhost:8080/api/auth/profile";
    private final Scanner scanner = new Scanner(System.in);
    @Getter
    private String authHeader = null;

    /**
     * Solicita DNI y contraseña, intenta autenticar contra el endpoint de perfil.
     * @return Un objeto UsuarioAutenticado si es exitoso, de lo contrario null.
     */
    public UsuarioAutenticado iniciarSesion() throws IOException, InterruptedException {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasenia = scanner.nextLine();

        String auth = dni + ":" + contrasenia;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        try {
            HttpResponse<String> response = HttpService.realizarPeticion("GET", PROFILE_URL, this.authHeader, null);

            int responseCode = response.statusCode();

            if (responseCode == 200) {
                String respuesta = response.body();
                System.out.println("¡Inicio de sesión exitoso!");
                return parsearUsuario(respuesta);
            } else {
                System.err.println("Error de autenticación: " + responseCode + " " + response.body());
                this.authHeader = null; // Resetea el header si falla
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error de conexión al intentar iniciar sesión: " + e.getMessage());
            this.authHeader = null;
            return null;
        }
    }

    /**
     * Analiza la respuesta JSON del perfil para obtener el rol del usuario.
     * @param jsonResponse La respuesta JSON del servidor.
     * @return Un objeto UsuarioAutenticado.
     */
    private UsuarioAutenticado parsearUsuario(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        String dni = json.getString("username");
        JSONArray rolesJson = json.getJSONArray("roles");

        // Asumimos que el usuario tiene un solo rol principal.
        String rol = "Usuario"; // Rol por defecto
        if (!rolesJson.isEmpty()) {
            // Spring Security agrega el prefijo "ROLE_"
            rol = rolesJson.getString(0).replace("ROLE_", "");
        }

        return new UsuarioAutenticado(dni, rol);
    }

}