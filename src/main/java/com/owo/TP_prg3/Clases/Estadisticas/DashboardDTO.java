package com.owo.TP_prg3.Clases.Estadisticas;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardDTO {
    // KPIs Superiores
    private BigDecimal ventasTotales;
    private BigDecimal gastosTotales;
    private Long nuevosClientes;
    
    // Gráfico de Torta
    private List<DataPointDTO> ventasPorMetodo;
    
    // Gráfico de Líneas 
    private List<TrendPointDTO> tendenciaVentas;

    // KPIs avanzaduchos
    private List<DataPointDTO> topProductos;
    private DataPointDTO productoMenosVendido;
    private String mejorMes;
    private String categoriaTop;
    private BigDecimal gananciasPromedioDia;
    private Double incrementoVentasMesAnterior;
}

// Clases para ayudar a las librerías de gráficos
@Data @AllArgsConstructor 
class DataPointDTO { 
    private String label; 
    private BigDecimal value; 
}

@Data @AllArgsConstructor
class TrendPointDTO { 
    private String mes; 
    private BigDecimal ventas; 
    private BigDecimal ganancias; 
}