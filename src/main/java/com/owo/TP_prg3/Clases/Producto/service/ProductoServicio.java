package com.owo.TP_prg3.Clases.Producto.service;

import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Inventario.dto.FormInventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioServicio;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ReglaNegocioException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductoServicio implements I_CRUD<Producto, ProductoDTO, FormProductoDTO> {
    
    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    private static final String ENTIDAD = "Producto";
    
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    @Lazy
    private InventarioServicio inventarioService;
    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;

     
    @Autowired
    private ExcelExportService excelExportService;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Producto convertir_a_Obj(FormProductoDTO fDTO){
        validarDatosProducto(fDTO);
        return new Producto(
            null,
            fDTO.getNombre(),
            fDTO.getCategoria(),
            fDTO.getUrl()==null? null : fDTO.getUrl()
        );
    }
    @Override
    public ProductoDTO convertir_a_DTO(Producto producto){
        return new ProductoDTO(
                producto.getProductoId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getUrl()==null? null : producto.getUrl()
        );
    }
    

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Transactional
    public Producto obtenerOCrearProducto(FormProductoDTO dto) {
        validarDatosProducto(dto);
        Optional<Producto> existente = productoRepositorio.findByNombreAndCategoria(dto.getNombre(), dto.getCategoria());
        
        if (existente.isPresent()) {
            return existente.get(); 
        } else {
            if (this.cargar(dto)) {
                return productoRepositorio.findByNombreAndCategoria(dto.getNombre(), dto.getCategoria())
                       .orElseThrow(() -> new RuntimeException("Error al crear el producto."));
            }
            throw new RuntimeException("Error al intentar cargar el nuevo producto.");
        }
    }

    // GET
    @Override
    public Set<ProductoDTO> obtenerTodos() {
        return productoRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ProductoDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return productoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ProductoDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) throw new CampoRequeridoException("campo");
        if (valor == null) throw new CampoRequeridoException("valor");

        // 1. Obtenemos todos los productos
        Stream<Producto> stream = productoRepositorio.findAll().stream();

        // 2. Creamos un filtro dependiendo del campo
        Predicate<Producto> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "categoria" -> filtro = p -> p.getCategoria().equals(valor);
            default -> throw new IngresoInvalidoException(
                "campo", 
                "debe ser 'nombre' o 'categoria'"
            );
        }

        // 3. Aplicamos el filtro al Stream, mapeamos a DTO
        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ProductoDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) throw new CampoRequeridoException("campo");
        
        // 1. Obtenemos todos los productos
        Stream<Producto> stream = productoRepositorio.findAll().stream();

        // 2. Creamos un Comparator dependiendo del campo
        Comparator<Producto> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Producto::getNombre);
            case "categoria"-> comparador = Comparator.comparing(Producto::getNombre);
            default -> comparador = Comparator.comparing(Producto::getProductoId);
        }
        // 3. Lo da vuelta si no es ascendente
        if (!ascendente) comparador = comparador.reversed();

        // 4. Aplicamos el comparador al Stream, mapeamos a DTO
        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    
    public byte[] exportar() {
        List<ProductoDTO> lista = List.copyOf(obtenerTodos());
        byte[] excel = excelExportService.generarExcel(lista, "Productos");
        return excel;
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormProductoDTO createDTO) {
        validarDatosProducto(createDTO);
        validarProductoNoExiste(createDTO.getNombre().trim(), createDTO.getCategoria().trim());

        Producto p = productoRepositorio.save(convertir_a_Obj(createDTO));
        //Crea un inventario con los valores recibidos (stockMin, precioVenta) o defaults
        return inventarioService.cargar(new FormInventarioDTO(
            0,
            p.getProductoId(),
            createDTO.getStockMin() != null ? createDTO.getStockMin() : 0,
            createDTO.getPrecioVenta() != null ? createDTO.getPrecioVenta() : BigDecimal.ZERO,
            BigDecimal.ZERO
        ));
    }

    // PUT
    @Override
    @Transactional
    public boolean actualizar(Long id, FormProductoDTO updateDTO) {
        validarDatosProducto(updateDTO);
        Producto producto = obtenerProductoPorId(id);
        
        // Validar que el nuevo nombre/categoría no exista en OTRO producto
        String nuevoNombre = updateDTO.getNombre().trim();
        String nuevaCategoria = updateDTO.getCategoria().trim();
        
        Optional<Producto> duplicado = productoRepositorio.findByNombreAndCategoria(nuevoNombre, nuevaCategoria);
        
        if (duplicado.isPresent() && !duplicado.get().getProductoId().equals(id)) {
            throw new EntidadDuplicadaException(
                ENTIDAD,
                String.format("nombre '%s' y categoría '%s'", nuevoNombre, nuevaCategoria)
            );
        }
        
        producto.setNombre(nuevoNombre);
        producto.setCategoria(nuevaCategoria);
        producto.setUrl(updateDTO.getUrl());
        productoRepositorio.save(producto);
        return true;
    }

    // DELETE
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        validarProductoPuedeEliminarse(id);
        Producto producto = obtenerProductoPorId(id);
        productoRepositorio.delete(producto);
        return true;
    }

    // VALIDACIONES PRIVADAS ===================================================================
    
    private void validarDatosProducto(FormProductoDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        
        String nombre = dto.getNombre();
        String categoria = dto.getCategoria();

        if (nombre == null || nombre.trim().isEmpty()) throw new CampoRequeridoException("nombre");
        if (categoria == null || categoria.trim().isEmpty()) throw new CampoRequeridoException("categoria");
        
        ValidacionGeneral.sinNumeros(nombre, "nombre");
        ValidacionGeneral.sinNumeros(categoria, "categoria");
    }
    
    private void validarProductoNoExiste(String nombre, String categoria) {
        Optional<Producto> existente = productoRepositorio.findByNombreAndCategoria(nombre, categoria);
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException(
                ENTIDAD, 
                String.format("nombre '%s' y categoría '%s'", nombre, categoria)
            );
        }
    }
    
    public Producto obtenerProductoPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    private void validarProductoPuedeEliminarse(Long productoId) {
        if (detallePedidoRepositorio.existsByProducto_ProductoId(productoId)) {
            throw new ReglaNegocioException(
                "No se puede eliminar " + ENTIDAD + " porque tiene DetallePedido asociados"
            );
        }
    }
}
