package com.owo.TP_prg3.Clases.Herramientas;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportService {

    public <T> byte[] generarExcel(List<T> datos, String nombreHoja) {
        if (datos == null || datos.isEmpty()) return new byte[0];

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(nombreHoja);
            
            // 1. Obtener atributos de la clase usando Refletion
            Field[] campos = datos.get(0).getClass().getDeclaredFields();

            // 2. Crear encabezados
            Row headerRow = sheet.createRow(0);
            IntStream.range(0, campos.length).forEach(i -> {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(campos[i].getName().toUpperCase());
            });

            // 3. Llenar datos
            IntStream.range(0, datos.size()).forEach(dataIdx -> {
                T objeto = datos.get(dataIdx);
                Row row = sheet.createRow(dataIdx + 1); // +1 para no reemplazar el encabezado

                IntStream.range(0, campos.length).forEach(colIdx -> {
                    try {
                        campos[colIdx].setAccessible(true);
                        Object valor = campos[colIdx].get(objeto);
                        row.createCell(colIdx).setCellValue(valor != null ? valor.toString() : "");
                    } catch (IllegalAccessException e) {
                        row.createCell(colIdx).setCellValue("Error");
                    }
                });
            });

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel genérico", e);
        }
    }
}
