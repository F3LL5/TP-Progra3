package com.owo.TP_prg3.Auntenticacion;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Utilidades.HttpService;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//Clase para realizar auntenticaciones
//Bibliografía: https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
public class AuthService {

    private static final String PROFILE_URL = "http://localhost:8080/api/auth/profile";
    private static final String PUESTO_URL = "http://localhost:8080/api/puestos";
    private static final String ENTIDAD_URL = "http://localhost:8080/api/entidades";
    private final Scanner scanner = new Scanner(System.in);
    @Getter
    private String authHeader = null;

    public Map<String, Object> iniciarSesion() throws IOException, InterruptedException {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasenia = scanner.nextLine();

        String auth = dni + ":" + contrasenia;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        try {
            HttpResponse<String> response = HttpService.realizarPeticion("GET", PROFILE_URL, this.authHeader, null);

            if (response != null) {
                int responseCode = response.statusCode();

                if (responseCode == 200) {
                    String respuesta = response.body();
                    System.out.println("¡Inicio de sesión exitoso!");

                    UsuarioAutenticado usuarioAutenticado = parsearUsuario(respuesta);
                    Puesto puesto = null;
                    Entidad entidad = null;

                    //Busca en los puestos si el usuario tiene un puesto
                    HttpResponse<String> puestoResponse = HttpService.realizarPeticion("GET", PUESTO_URL + "/dni/" + dni, this.authHeader, null);
                    if (puestoResponse.statusCode() == 200 && !puestoResponse.body().equals("null")) {
                        puesto = parsearPuesto(puestoResponse.body());
                        if (puesto != null) {
                            entidad = puesto.getDuenio();
                        }
                    } else {
                        System.out.println("El DNI: " + dni + " no tiene puestos asociados.");
                    }

                    // Si no tiene puesto, se busca si el usuario tiene una entidad asociada
                    if (entidad == null) {
                        HttpResponse<String> entidadResponse = HttpService.realizarPeticion("GET", ENTIDAD_URL + "/dni/" + dni, this.authHeader, null);
                        if (entidadResponse.statusCode() == 200 && !entidadResponse.body().isEmpty() && !entidadResponse.body().isBlank()) {
                            entidad = parsearEntidad(entidadResponse.body());
                        } else {
                            System.out.println("No se encontró una entidad para el DNI: " + dni + " (Código de estado: " + entidadResponse.statusCode() + ")");
                        }
                    }

                    //Se retornan todos los objetos.
                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("usuario", usuarioAutenticado);
                    resultado.put("puesto", puesto);
                    resultado.put("entidad", entidad);

                    return resultado;
                } else {
                    System.out.println("Error de autenticación: " + responseCode + " " + response.body());
                    this.authHeader = null; // Resetea el header si falla
                    return null;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error de conexión al intentar iniciar sesión: " + e.getMessage());
            this.authHeader = null;
            return null;
        }
        return null;
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

    private Puesto parsearPuesto(String jsonResponse) throws IOException, InterruptedException {
        JSONObject json = new JSONObject(jsonResponse);

        Long puestoId = json.getLong("puestoId");
        String nombre = json.getString("nombre");
        BigDecimal comision = json.getBigDecimal("comision");
        Long duenioId = json.getLong("duenioId");

        Entidad duenio = null;
        HttpResponse<String> entidadResponse = HttpService.realizarPeticion("GET", ENTIDAD_URL + "/" + duenioId,this.authHeader, null);

        if (entidadResponse.statusCode() == 200) {
            duenio = parsearEntidad(entidadResponse.body());
        } else {
            System.out.println("Error al obtener detalles de la Entidad con ID " + duenioId + ": " + entidadResponse.statusCode() + " " + entidadResponse.body());
        }

        return new Puesto(puestoId, nombre, duenio, comision);
    }

    private Entidad parsearEntidad(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);

        Long entidadId = json.getLong("entidad_id");
        String nombre = json.getString("nombre");
        String rolEntidadString = json.getString("rolEntidad");
        RolEntidad rolEntidad = RolEntidad.valueOf(rolEntidadString);
        String tipoEntidad = json.getString("tipoEntidad");
        Integer edad = json.getInt("edad");
        Integer dni = json.getInt("dni");

        return new Entidad(entidadId, nombre, rolEntidad, tipoEntidad, edad, dni);
    }

}