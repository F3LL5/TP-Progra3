package com.owo.TP_prg3.Utilidades;

import com.jakewharton.fliptables.FlipTable;
import java.lang.reflect.Field;
import java.util.*;

public abstract class FlipTableHelper {

    ///  CLASE AUXILIAR DE SOPORTE INTERNO
    private static class ColumnaInfo {
        int orden;
        String nombre;
        Field campo;

        public ColumnaInfo(int orden, String nombre, Field campo) {
            this.orden = orden;
            this.nombre = nombre;
            this.campo = campo;
        }
    }

    public static <T> void imprimir(List<T> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        Class<?> clase = lista.getFirst().getClass();
        Field[] fields = clase.getDeclaredFields();
        List<ColumnaInfo> columnas = new ArrayList<>();

        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                Column col = f.getAnnotation(Column.class);
                columnas.add(new ColumnaInfo(col.order(), col.name(), f));
            }
        }

        columnas.sort(Comparator.comparingInt(c -> c.orden));
        String[] headers = columnas.stream().map(c -> c.nombre).toArray(String[]::new);

        String[][] datos = new String[lista.size()][columnas.size()];
        for (int i = 0; i < lista.size(); i++) {
            Object objeto = lista.get(i);
            for (int j = 0; j < columnas.size(); j++) {
                Field campo = columnas.get(j).campo;
                campo.setAccessible(true);
                try {
                    Object valor = campo.get(objeto);
                    datos[i][j] = (valor != null) ? valor.toString() : "";
                } catch (IllegalAccessException e) {
                    datos[i][j] = "ERROR";
                }
            }
        }

        System.out.print(FlipTable.of(headers, datos));
    }

    public static void imprimirMapa(List<Map<String, Object>> listaMapa) {
        if (listaMapa == null || listaMapa.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        // Obtenemos los encabezados de las claves del mapa.
        String[] headers = listaMapa.get(0).keySet().toArray(new String[0]);

        // Convertir el map en una matriz de Strings
        String[][] datos = listaMapa.stream()
                .map(filaMapa -> Arrays.stream(headers)
                        .map(header -> {
                            Object valor = filaMapa.get(header);
                            return (valor != null) ? valor.toString() : "";
                        })
                        .toArray(String[]::new)
                )
                .toArray(String[][]::new);

        // Imprimir la tabla usando FlipTable.
        System.out.print(FlipTable.of(headers, datos));
    }


}
