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

        if (statusCode >= 200 && statusCode < 300) { /// UNA RESPUESTA ENTRE 200 Y 300 ES EXITOSA. EJ: 204 para respuesta EXITOSA sin cuerpo
            System.out.println(successMessage);
            if (responseBody != null && !responseBody.isBlank()) { /// RETORNA MENSAJE SI HAY LA RESPUESTA TIENE CUERPO
                try {
                    if (responseBody.startsWith("[")) { /// SI ES UNA LISTA DE OBJETOS (YA QUE EN EL JSON EMPIEZAN CON [])
                        List<?> items = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
                        return Optional.of(items);
                    } else {
                        Object item = mapper.readValue(responseBody, clazz); /// SI NO EMPIEZA CON "[", ES SÓLO UN OBJETO
                        return Optional.of(item);
                    }
                } catch (IOException e) {
                    System.out.println("Error al parsear la respuesta JSON: " + e.getMessage()); /// ERROR EL LEER JSON
                    return Optional.empty();
                }
            } else {
                System.out.println("La respuesta del servidor está vacía.");
                return Optional.empty(); /// OPTIONAL VACÍO SI LA RESPUESTA ES VACÍA
            }
        } else { ///  SI EL SV DEVUELVE +300, ES ERROR (400, 404, 500, ETC)
            handleErrorResponse(statusCode, responseBody); /// ES OTRO MÉTOD0 QUE DEVUELVE LOS DETALLES DEL ERROR
            System.out.println(errorMessage);
            return Optional.empty(); /// OPTIONAL VACÍO EN CASO DE ERROR
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
