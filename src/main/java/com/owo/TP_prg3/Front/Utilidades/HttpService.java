package com.owo.TP_prg3.Front.Utilidades;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Clase para realizar consultas http desde la vista
// Bibliografía: https://certidevs.com/tutorial-java-httpclient
public class HttpService {

    //Builder
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    //Metodos
    public static HttpResponse<String> realizarPeticion(String method, String urlString, String authHeader, String jsonBody) throws IOException, InterruptedException {
        System.out.println("Peticion:\""+urlString+"\"");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(10));

        if (!authHeader.isEmpty()) requestBuilder.header("Authorization", authHeader);

        requestBuilder
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json");

        switch (method.toUpperCase()) {
            case "GET" -> requestBuilder.GET();
            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : "")); //
            case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
            case "PATCH" -> requestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
            case "DELETE" -> requestBuilder.DELETE();
            default -> throw new IllegalArgumentException("Método HTTP no soportado: " + method);
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = null;
        try {
            //Se envía la solicitud y se recibe la respuesta como un HttpResponse String
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println( "Código de estado: " + response.statusCode() + " " + (response.body().isEmpty() ? "No Message" : "OK") );
            System.out.println( "Cuerpo de la respuesta:\n" + response.body() );

        } catch (IOException | InterruptedException e) {
            System.out.println("Error durante la petición HTTP: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
