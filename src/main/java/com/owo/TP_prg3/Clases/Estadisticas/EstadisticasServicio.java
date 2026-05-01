package com.owo.TP_prg3.Clases.Estadisticas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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
        
        // Total gastos
        
        BigDecimal gastos = statsRepo.sumMontoPorTipoEnRango(TipoPedido.COMPRA, inicioDT, finDT);
        // Cant clientes
        Long clientes = statsRepo.countNuevosClientesHistorial(inicioDT, finDT);
        if (clientes == null) clientes = 0L;
        
        // Grafico de torta de total ventas vs total compras
        List<DataPointDTO> metodos = statsRepo.getVentasPorMetodoPago(inicioDT, finDT)
                .stream()
                .map(obj -> new DataPointDTO(obj[0].toString(), (BigDecimal) obj[1]))
                .collect(Collectors.toList());

        // Grafico de lineas de tendendia mensual
        List<TrendPointDTO> tendencia = datosTrend.stream()
            .map(obj -> new TrendPointDTO(obj[0].toString(), (BigDecimal) obj[1], (BigDecimal) obj[2]))
            .collect(Collectors.toList());


        //Devolvemos un dto que contiene las estadisticas solicitadas
        return DashboardDTO.builder()
                .ventasTotales(ventas != null ? ventas : BigDecimal.ZERO)
                .gastosTotales(gastos != null ? gastos : BigDecimal.ZERO)
                .nuevosClientes(clientes)
                .ventasPorMetodo(metodos)
                .tendenciaVentas(tendencia)
                .build();
    }
}