package com.owo.TP_prg3.Front.Utilidades;

import com.jakewharton.fliptables.FlipTable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        System.out.println(FlipTable.of(headers, datos));
    }


}
