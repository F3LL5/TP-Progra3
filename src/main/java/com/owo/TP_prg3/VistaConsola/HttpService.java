package com.owo.TP_prg3.VistaConsola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase de utilidad para manejar la comunicación HTTP.
 */
public class HttpService {

    /**
     * Crea y configura una conexión HTTP.
     */
    public static HttpURLConnection crearConexion(String method, String urlString, String authHeader) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        if (authHeader != null) {
            con.setRequestProperty("Authorization", authHeader);
        }
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        return con;
    }

    /**
     * Lee la respuesta de una conexión HTTP.
     */
    public static String leerRespuesta(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        BufferedReader in;
        if (responseCode >= 200 && responseCode < 300) {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    /**
     * Realiza una petición completa y muestra la respuesta en consola.
     */
    public static void realizarPeticion(String method, String urlString, String authHeader, String jsonBody) throws IOException {
        HttpURLConnection con = null;
        try {
            con = crearConexion(method, urlString, authHeader);
            if (jsonBody != null && !jsonBody.isEmpty()) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            System.out.println("Respuesta del servidor: " + con.getResponseCode() + " " + con.getResponseMessage());
            System.out.println("Cuerpo de la respuesta:");
            System.out.println(leerRespuesta(con));

        } catch (Exception e) {
            System.err.println("Error durante la petición HTTP: " + e.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
