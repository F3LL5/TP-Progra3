package com.owo.TP_prg3.VistaConsola;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Scanner;

public class AuthService {

    private static final String PROFILE_URL = "http://localhost:8080/api/auth/profile";
    private final Scanner scanner = new Scanner(System.in);
    private String authHeader = null;

    /**
     * Solicita DNI y contraseña, intenta autenticar contra el endpoint de perfil.
     * @return Un objeto UsuarioAutenticado si es exitoso, de lo contrario null.
     */
    public UsuarioAutenticado iniciarSesion() throws IOException {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasenia = scanner.nextLine();

        String auth = dni + ":" + contrasenia;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        HttpURLConnection con = HttpService.crearConexion("GET", PROFILE_URL, this.authHeader);
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String respuesta = HttpService.leerRespuesta(con);
            System.out.println("¡Inicio de sesión exitoso!");
            return parsearUsuario(respuesta);
        } else {
            System.err.println("Error de autenticación: " + responseCode + " " + con.getResponseMessage());
            this.authHeader = null; // Resetea el header si falla
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
        if (rolesJson.length() > 0) {
            // Spring Security agrega el prefijo "ROLE_"
            rol = rolesJson.getString(0).replace("ROLE_", "");
        }

        return new UsuarioAutenticado(dni, rol);
    }

    public String getAuthHeader() {
        return authHeader;
    }
}