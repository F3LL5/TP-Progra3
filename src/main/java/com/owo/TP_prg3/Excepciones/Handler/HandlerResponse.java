package com.owo.TP_prg3.Excepciones.Handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class HandlerResponse {
    private static ObjectMapper mapper;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
    }

    public static Optional<Object> handleResponse(HttpResponse<String> response, Class<?> clazz, String successMessage, String errorMessage) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode >= 200 && statusCode < 300) {
            System.out.println(successMessage);
            if (responseBody != null && !responseBody.isBlank()) {
                try {
                    if (responseBody.startsWith("[")) {

                        List<?> items = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
                        return Optional.of(items);
                    } else {
                        Object item = mapper.readValue(responseBody, clazz);
                        return Optional.of(item);
                    }
                } catch (IOException e) {
                    System.out.println("Error al parsear la respuesta JSON: " + e.getMessage());
                    return Optional.empty();
                }
            } else {
                System.out.println("La respuesta del servidor está vacía.");
                return Optional.empty(); // Retorna Optional vacío si la respuesta está vacía
            }
        } else { // Códigos de error
            handleErrorResponse(statusCode, responseBody);
            System.out.println(errorMessage);
            return Optional.empty(); // Retorna Optional vacío en caso de error
        }
    }

    public static void handleErrorResponse(int statusCode, String responseBody) {

        try {

            Map<String, Object> errorMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
            });

            if (errorMap.containsKey("errores")) {
                List<String> errors = (List<String>) errorMap.get("errores");
                System.out.println("Detalles de la validación:");
                errors.forEach(System.err::println);
            } else if (errorMap.containsKey("mensaje")) {
                System.out.println("Mensaje: " + errorMap.get("mensaje"));
            } else if (errorMap.containsKey("error")) {
                System.out.println("Error: " + errorMap.get("error"));
                System.out.println("Ruta: " + errorMap.get("path"));
            } else {
                System.out.println("Respuesta de error no reconocida: " + responseBody);
            }
        } catch (IOException e) {
            System.out.println("Error al parsear el cuerpo del error: " + e.getMessage());
            System.out.println("Cuerpo de la respuesta original: " + responseBody);
        }
    }
}
