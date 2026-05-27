package com.owo.TP_prg3.Clases.Estadisticas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.owo.TP_prg3.Clases.Enum.TipoPedido;

@Service
public class EstadisticasServicio {

    @Autowired
    private EstadisticasRepository statsRepo;

    public DashboardDTO obtenerResumen(LocalDate inicio, LocalDate fin) {
        // -------------------------------------------------------------------
        // VARIABLES
        // -------------------------------------------------------------------        

        // Convertimos LocalDate a LocalDateTime para la bdd
        var inicioDT = inicio.atStartOfDay();
        var finDT = fin.atTime(LocalTime.MAX);

        long dias = ChronoUnit.DAYS.between(inicio, fin);
        List<Object[]> datosTrend;

        if (inicio.isEqual(fin)) { 
            // CASO HOY:
            datosTrend = statsRepo.getTendenciaPorHora(inicioDT, finDT);
        } else if (dias > 0 && dias <= 31) {
            // CASO MES:
            datosTrend = statsRepo.getTendenciaDiaria(inicioDT, finDT);
        } else {
            // CASO AÑO o MÁS:
            datosTrend = statsRepo.getTendenciaMensual(inicioDT, finDT);
        }

        // -------------------------------------------------------------------
        // ESTADISTICAS
        // -------------------------------------------------------------------
        
        // Total ventas
        BigDecimal ventas = statsRepo.sumMontoPorTipoEnRango(TipoPedido.VENTA, inicioDT, finDT);
        if (ventas == null) ventas = BigDecimal.ZERO;
        
        // Total gastos
        BigDecimal gastos = statsRepo.sumMontoPorTipoEnRango(TipoPedido.COMPRA, inicioDT, finDT);
        if (gastos == null) gastos = BigDecimal.ZERO;

        // Cant clientes
        Long clientes = statsRepo.countNuevosClientesHistorial(inicioDT, finDT);
        if (clientes == null) clientes = 0L;
        
        BigDecimal gananciaTotal = ventas.subtract(gastos);

        // Grafico de torta de total ventas vs total compras
        List<DataPointDTO> metodos = statsRepo.getVentasPorMetodoPago(inicioDT, finDT)
                .stream()
                .map(obj -> new DataPointDTO(obj[0].toString(), (BigDecimal) obj[1]))
                .collect(Collectors.toList());

        // Grafico de lineas de tendendia mensual
        List<TrendPointDTO> tendencia = datosTrend.stream()
            .map(obj -> new TrendPointDTO(obj[0].toString(), (BigDecimal) obj[1], (BigDecimal) obj[2]))
            .collect(Collectors.toList());

        // TOP 5 BEST PRODUCTOS DEL MUNDO MUNDIAL (TOP 5 MÁS VENDIDOS)
        List<DataPointDTO> topProductos = statsRepo.getTopProductosEnRango(inicioDT, finDT).stream()
                .limit(5)
                .map(obj -> new DataPointDTO(
                        obj[0].toString(), 
                        BigDecimal.valueOf(Long.parseLong(obj[1].toString()))
                ))
                .collect(Collectors.toList());

        // B. Producto Menos Vendido
        List<Object[]> menosVendidosRaw = statsRepo.getProductoMenosVendidoEnRango(inicioDT, finDT);
        DataPointDTO menosVendido = null;
        if (menosVendidosRaw != null && !menosVendidosRaw.isEmpty()) {
            Object[] obj = menosVendidosRaw.get(0);
            menosVendido = new DataPointDTO(
                    obj[0].toString(), 
                    BigDecimal.valueOf(Long.parseLong(obj[1].toString()))
            );
        } else {
            menosVendido = new DataPointDTO("Sin ventas", BigDecimal.ZERO);
        }

        // CATEGORÍA MÁS VENDIDA 
        List<Object[]> categoriasRaw = statsRepo.getCategoriaTopEnRango(inicioDT, finDT);
        String categoriaTop = (categoriasRaw != null && !categoriasRaw.isEmpty()) 
                ? categoriasRaw.get(0)[0].toString() 
                : "Sin datos";

        // GANANCIA PROMEDIO DEL DÍA
        BigDecimal promedioDia = gananciaTotal.divide(
                BigDecimal.valueOf(dias > 0 ? dias : 1), 
                2, 
                RoundingMode.HALF_UP
        );

        // MES DE MAYOR VENTAS
        String mejorMes = "Sin datos";
        BigDecimal maxGananciaMes = BigDecimal.valueOf(-Double.MAX_VALUE);
        List<Object[]> tendenciaMensualRaw = statsRepo.getTendenciaMensual(inicioDT.minusYears(1), finDT);
        
        if (tendenciaMensualRaw != null) {
            for (Object[] row : tendenciaMensualRaw) {
                BigDecimal gananciaMes = (BigDecimal) row[2];
                if (gananciaMes != null && gananciaMes.compareTo(maxGananciaMes) > 0) {
                    maxGananciaMes = gananciaMes;
                    mejorMes = row[0].toString();
                }
            }
        }

        // VS MES ANTERIOR
        LocalDateTime inicioMesPasado = inicioDT.minusMonths(1);
        LocalDateTime finMesPasado = finDT.minusMonths(1);
        BigDecimal ventasMesPasado = statsRepo.sumMontoPorTipoEnRango(TipoPedido.VENTA, inicioMesPasado, finMesPasado);
        Double incrementoVentas = 0.0;
        if (ventasMesPasado != null && ventasMesPasado.compareTo(BigDecimal.ZERO) > 0) {
            incrementoVentas = ((ventas.subtract(ventasMesPasado))
                    .divide(ventasMesPasado, 4, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        // DEVOLVEMOS DTO
        return DashboardDTO.builder()
                .ventasTotales(ventas)
                .gastosTotales(gastos)
                .nuevosClientes(clientes)
                .ventasPorMetodo(metodos)
                .tendenciaVentas(tendencia)
                // Variables avanzaduchas
                .topProductos(topProductos)
                .productoMenosVendido(menosVendido)
                .mejorMes(mejorMes)
                .categoriaTop(categoriaTop)
                .gananciasPromedioDia(promedioDia) 
                .incrementoVentasMesAnterior(incrementoVentas)
                .build();
    }

    public CierreCajaDTO obtenerMovimientosCajaDiaria(LocalDate fecha) {
        // Convertimos el día (ej: 2026-05-19) en el rango completo de horas de ese día
        var inicioDT = fecha.atStartOfDay();              // 2026-05-19 00:00:00
        var finDT = fecha.atTime(LocalTime.MAX);          // 2026-05-19 23:59:59.999

        BigDecimal ingresos = statsRepo.sumVentasEfectivoEnRango(inicioDT, finDT);
        BigDecimal egresos = statsRepo.sumGastosEnRango(inicioDT, finDT);

        return CierreCajaDTO.builder()
                .ingresosEfectivo(ingresos != null ? ingresos : BigDecimal.ZERO)
                .egresosGastos(egresos != null ? egresos : BigDecimal.ZERO)
                .build();
    }
}