package com.owo.TP_prg3.Clases.Herramientas;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int MAX_COL_WIDTH = 15000;
    private static final int FILAS_ENCABEZADO = 4;

    @Autowired
    private TiendaRepositorio tiendaRepositorio;

    public <T> byte[] generarExcel(List<T> datos, String nombreHoja) {
        if (datos == null || datos.isEmpty()) return new byte[0];

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(nombreHoja);
            Field[] campos = obtenerTodosLosCampos(datos.get(0).getClass());

            Optional<Tienda> tienda = tiendaRepositorio.findById(1L);
            tienda.ifPresent(t -> renderizarEncabezadoEmpresa(sheet, workbook, t, campos.length));

            int filaInicio = tienda.isPresent() ? FILAS_ENCABEZADO : 0;

            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloDato(workbook, false);
            CellStyle altDataStyle = crearEstiloDato(workbook, true);

            Row headerRow = sheet.createRow(filaInicio);
            headerRow.setHeightInPoints(30);
            IntStream.range(0, campos.length).forEach(i -> {
                Cell cell = headerRow.createCell(i);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(formatearNombreColumna(campos[i].getName()));
            });

            IntStream.range(0, datos.size()).forEach(dataIdx -> {
                T objeto = datos.get(dataIdx);
                Row row = sheet.createRow(filaInicio + 1 + dataIdx);
                row.setHeightInPoints(20);
                CellStyle rowStyle = dataIdx % 2 == 0 ? dataStyle : altDataStyle;

                IntStream.range(0, campos.length).forEach(colIdx -> {
                    Cell cell = row.createCell(colIdx);
                    cell.setCellStyle(rowStyle);
                    try {
                        campos[colIdx].setAccessible(true);
                        Object valor = campos[colIdx].get(objeto);
                        asignarValorCelda(cell, valor);
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("Error");
                    }
                });
            });

            int ultimaFila = filaInicio + datos.size();
            IntStream.range(0, campos.length).forEach(i -> {
                sheet.autoSizeColumn(i);
                int ancho = sheet.getColumnWidth(i) + 512;
                sheet.setColumnWidth(i, Math.min(ancho, MAX_COL_WIDTH));
            });

            if (!datos.isEmpty()) {
                sheet.setAutoFilter(new CellRangeAddress(filaInicio, ultimaFila, 0, campos.length - 1));
            }
            sheet.createFreezePane(0, filaInicio + 1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    private void renderizarEncabezadoEmpresa(Sheet sheet, Workbook workbook, Tienda tienda, int numCols) {
        CellStyle titleStyle = crearEstiloTitulo(workbook);
        CellStyle infoStyle = crearEstiloInfo(workbook);

        int cols = Math.max(numCols, 2);

        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(36);
        for (int i = 0; i < cols; i++) {
            Cell c = row0.createCell(i);
            c.setCellStyle(titleStyle);
        }
        String nombre = tienda.getNombreFantasia() != null ? tienda.getNombreFantasia() : tienda.getRazonSocial();
        row0.getCell(0).setCellValue(nombre != null ? nombre.toUpperCase() : "MI EMPRESA");
        if (cols > 1) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols - 1));
        }

        Row row1 = sheet.createRow(1);
        row1.setHeightInPoints(22);
        String[] datos1 = new String[cols];
        datos1[0] = tienda.getCuit() != null ? "CUIT: " + tienda.getCuit() : "";
        datos1[1] = tienda.getCondicion() != null ? "IVA: " + tienda.getCondicion().name().replace("_", " ") : "";
        if (cols > 2) datos1[2] = tienda.getIngresosBrutos() != null ? "IIBB: " + tienda.getIngresosBrutos() : "";
        if (cols > 3) datos1[3] = tienda.getPuntoDeVenta() != null ? "PTO VTA: " + tienda.getPuntoDeVenta() : "";
        for (int i = 0; i < cols; i++) {
            Cell c = row1.createCell(i);
            c.setCellStyle(infoStyle);
            c.setCellValue(datos1[i]);
        }

        Row row2 = sheet.createRow(2);
        row2.setHeightInPoints(22);
        String direccion = "";
        if (tienda.getDireccion() != null) {
            var d = tienda.getDireccion();
            direccion = (d.getCalle() != null ? d.getCalle() : "")
                + (d.getAltura() != null ? " " + d.getAltura() : "")
                + (d.getLocalidad() != null ? ", " + d.getLocalidad() : "")
                + (d.getProvincia() != null ? ", " + d.getProvincia() : "")
                + (d.getPais() != null ? ", " + d.getPais() : "");
        }
        String fechaInicio = tienda.getFechaInicioActividades() != null
            ? "INICIO: " + tienda.getFechaInicioActividades().format(DATE_FMT) : "";

        String[] datos2 = new String[cols];
        datos2[0] = !direccion.isEmpty() ? "DIR: " + direccion : "";
        if (cols > 1) datos2[1] = fechaInicio;
        for (int i = 0; i < cols; i++) {
            Cell c = row2.createCell(i);
            c.setCellStyle(infoStyle);
            c.setCellValue(datos2[i] != null ? datos2[i] : "");
        }

        Row row3 = sheet.createRow(3);
        row3.setHeightInPoints(6);
        CellStyle separatorStyle = crearEstiloSeparador(workbook);
        for (int i = 0; i < cols; i++) {
            Cell c = row3.createCell(i);
            c.setCellStyle(separatorStyle);
        }
    }

    private Field[] obtenerTodosLosCampos(Class<?> clazz) {
        List<Field> campos = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Collections.addAll(campos, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return campos.toArray(new Field[0]);
    }

    private String formatearNombreColumna(String name) {
        return name
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("_", " ")
            .toUpperCase();
    }

    private void asignarValorCelda(Cell cell, Object valor) {
        if (valor == null) {
            cell.setCellValue("");
            return;
        }
        if (valor instanceof String s) {
            cell.setCellValue(s);
        } else if (valor instanceof Number n) {
            cell.setCellValue(n.doubleValue());
        } else if (valor instanceof Boolean b) {
            cell.setCellValue(b);
        } else if (valor instanceof LocalDate d) {
            cell.setCellValue(d.format(DATE_FMT));
        } else if (valor instanceof LocalDateTime dt) {
            cell.setCellValue(dt.format(DATETIME_FMT));
        } else if (valor instanceof Date d) {
            cell.setCellValue(d);
        } else if (valor instanceof Enum<?> e) {
            cell.setCellValue(e.name());
        } else {
            cell.setCellValue(valor.toString());
        }
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName("Calibri");
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{(byte) 0x8C, (byte) 0x1B, (byte) 0x4A}, null));
        font.setFontHeightInPoints((short) 18);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xFD, (byte) 0xE8, (byte) 0xEE}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloInfo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setColor(IndexedColors.GREY_80_PERCENT.getIndex());
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xFD, (byte) 0xE8, (byte) 0xEE}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloSeparador(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xFD, (byte) 0xE8, (byte) 0xEE}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setBold(true);
        font.setColor(IndexedColors.GREY_80_PERCENT.getIndex());
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xF5, (byte) 0xB7, (byte) 0xC2}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloDato(Workbook workbook, boolean alternar) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (alternar) {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xFD, (byte) 0xF0, (byte) 0xF5}, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }
}
