package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Item.service.ItemServicioImpl;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventarioPuestoServicioImpl implements InventarioPuestoServicio {

    @Autowired
    private InventarioPuestoRepositorio inventarioPuestoRepositorio;
    @Autowired
    private PuestoRepositorio puestoRepositorio;
    @Autowired
    private ItemRepositorio itemRepositorio;
    @Autowired
    private ItemServicioImpl itemServicio;

    /// CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------

    private InventarioPuestoDTO convertirA_DTO(InventarioPuesto inventarioPuesto) {
        return new InventarioPuestoDTO(
                inventarioPuesto.getInventario_id(),
                inventarioPuesto.getCantidad(),
                inventarioPuesto.getPuesto().getPuestoId(),
                inventarioPuesto.getItemId(),
                inventarioPuesto.getStockMin(),
                inventarioPuesto.getPrecioVenta(),
                inventarioPuesto.getCostoAdquisicion()
        );
    }

    private InventarioPuesto convertirA_InventarioPuesto(CreateInventarioPuestoDTO inventarioPuestoDTO) {
        InventarioPuesto inventarioPuesto = new InventarioPuesto();

        // 1. Busca la entidad Puesto y asígnala.
        puestoRepositorio.findById(inventarioPuestoDTO.getPuestoId())
                .ifPresentOrElse(
                        inventarioPuesto::setPuesto,
                        () -> { throw new EntityNotFoundException("Puesto con ID " + inventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                );

        // 2. Busca la entidad Item.
        Item item = itemRepositorio.findById(inventarioPuestoDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item con ID " + inventarioPuestoDTO.getItemId() + " no encontrado."));

        // 3. Asigna la entidad Item encontrada
        inventarioPuesto.setItemId(item.getItem_id());

        // 4. Asigna el resto de los campos.
        inventarioPuesto.setCantidad(inventarioPuestoDTO.getCantidad());
        inventarioPuesto.setStockMin(inventarioPuestoDTO.getStockMin());
        inventarioPuesto.setPrecioVenta(inventarioPuestoDTO.getPrecioVenta());
        inventarioPuesto.setCostoAdquisicion(inventarioPuestoDTO.getCostoAdquisicion());

        return inventarioPuesto;
    }

    /// GET ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<InventarioPuestoDTO> getAllInventarioPuestos() {
        return inventarioPuestoRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<InventarioPuestoDTO> getInventarioPuestoById(Long id) {
        return inventarioPuestoRepositorio.findById(id).map(this::convertirA_DTO);
    }

    public Optional<InventarioPuestoDTO> getInventarioPuestoByIdAndPuestoId(Long id, Long puestoId) {
        return inventarioPuestoRepositorio.findById(id)
                .filter(inventario -> inventario.getPuesto().getPuestoId().equals(puestoId)) // Filtra por puestoId
                .map(this::convertirA_DTO);
    }

    //devuelve todos los inventarios del puesto con esa id
    public List<InventarioPuestoDTO> obtenerInventariosDeUnPuesto(Long puestoId){
        List<InventarioPuestoDTO> inventarioPuestoDTOS=getAllInventarioPuestos();
        List<InventarioPuestoDTO> inventarioPuestosDTOSconStock;
        inventarioPuestosDTOSconStock=inventarioPuestoDTOS.stream()
                .filter(inventarioPuestoDTO ->inventarioPuestoDTO.getPuestoId()==puestoId)
                .toList();

        return  inventarioPuestosDTOSconStock;
    }

    public List<Map<String,Object>> mostrarItemsEnStock(Long puestoId) {
        List<ItemDTO> ListaItems=itemServicio.getAllProducts();
        List<InventarioPuestoDTO> inventariosPuesto=obtenerInventariosDeUnPuesto(puestoId);

        List<InventarioPuestoDTO> inventarioConStock=inventariosPuesto.stream()
                .filter(inventariopuestodto->inventariopuestodto.getCantidad()>0)
                .toList();



        List<ItemDTO> itemsConStock = ListaItems.stream()
                .filter(item -> inventarioConStock.stream()
                        .anyMatch(inv -> (inv.getItemId() == item.getItem_id())))
                .toList();
        System.out.println(itemsConStock);

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (ItemDTO item : itemsConStock) {
            for (InventarioPuestoDTO inv : inventarioConStock) {
                if (inv.getItemId() == item.getItem_id()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_Item", item.getItem_id());
                    fila.put("nombre", item.getNombre());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad());
                    fila.put("costoAdquisicion", inv.getCostoAdquisicion());

                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }

    public List<Map<String,Object>> obtenerItemsEnInventario(Long puestoId) {
        List<ItemDTO> ListaItems=itemServicio.getAllProducts();
        List<InventarioPuestoDTO> inventarioPuesto=obtenerInventariosDeUnPuesto(puestoId);


        List<ItemDTO> itemsEnInventario = ListaItems.stream()
                .filter(item -> inventarioPuesto.stream()
                        .anyMatch(inv -> (inv.getItemId() == item.getItem_id())))
                .toList();
        System.out.println(itemsEnInventario);

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (ItemDTO item : itemsEnInventario) {
            for (InventarioPuestoDTO inv : inventarioPuesto) {
                if (inv.getItemId() == item.getItem_id()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_Item", item.getItem_id());
                    fila.put("nombre", item.getNombre());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad());
                    fila.put("costoAdquisicion", inv.getCostoAdquisicion());

                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }

    public List<Map<String,Object>> mostrarItemsEnStockBajo(Long puestoId) {
        List<ItemDTO> ListaItems=itemServicio.getAllProducts();
        List<InventarioPuestoDTO> inventarioConStock=obtenerInventariosDeUnPuesto(puestoId);


        List<ItemDTO> itemsConStockBajo = ListaItems.stream()
                .filter(item -> inventarioConStock.stream()
                        .anyMatch(inv -> (inv.getItemId() == item.getItem_id()) && (inv.getCantidad() <= inv.getStockMin())))
                .toList();
        System.out.println(itemsConStockBajo);

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (ItemDTO item : itemsConStockBajo) {
            for (InventarioPuestoDTO inv : inventarioConStock) {
                if (inv.getItemId() == item.getItem_id()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_Item", item.getItem_id());
                    fila.put("nombre", item.getNombre());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad());
                    fila.put("costoAdquisicion", inv.getCostoAdquisicion());
                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }

    public List<Map<String,Object>> filtrarYordenar(Long puestoId,String categoria,String orden,String direccion) {
        // Obtener todos los inventarios de un puesto
        List<Map<String,Object>> itemsMap=obtenerItemsEnInventario(puestoId);

        // Filtro por categoria
        if (categoria != null && !categoria.isEmpty()) {
            itemsMap = itemsMap.stream()
                    .filter(item -> categoria.equalsIgnoreCase((String) item.get("categoria")))
                    .collect(Collectors.toList());
        }

        // Orden y direccion por defecto
        String campoOrden = (orden == null || orden.isEmpty()) ? "nombre" : orden;
        String campoDireccion = (direccion == null || direccion.isEmpty()) ? "asc" : direccion;


        itemsMap.sort((a, b) -> {
            Comparable valorA;
            Comparable valorB;

            // Manejo de valores nulos para el ordenamiento
            if (a.get(campoOrden) == null && b.get(campoOrden) == null) return 0;
            if (a.get(campoOrden) == null) return "desc".equalsIgnoreCase(campoDireccion) ? -1 : 1;
            if (b.get(campoOrden) == null) return "desc".equalsIgnoreCase(campoDireccion) ? 1 : -1;

            // Convertir a BigDecimal si el campo es numérico para comparar correctamente
            if ("costoAdquisicion".equalsIgnoreCase(campoOrden) || "precioVenta".equalsIgnoreCase(campoOrden) || "cantidad".equalsIgnoreCase(campoOrden)) {
                valorA = new BigDecimal(a.get(campoOrden).toString());
                valorB = new BigDecimal(b.get(campoOrden).toString());
            } else {
                valorA = (Comparable) a.get(campoOrden);
                valorB = (Comparable) b.get(campoOrden);
            }

            int comparacion = valorA.compareTo(valorB);

            if ("desc".equalsIgnoreCase(campoDireccion)) {
                return -comparacion;
            } else {
                return comparacion;
            }
        });
        return itemsMap;
    }

    /// POST ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public InventarioPuestoDTO createInventarioPuesto(CreateInventarioPuestoDTO createInventarioPuestoDTO) {
        // Verifica si ya existe ese inventario del itemId
        Optional<InventarioPuesto> inventarioPuestoOptional = inventarioPuestoRepositorio
                .findByPuesto_PuestoIdAndItemId(createInventarioPuestoDTO.getPuestoId(), createInventarioPuestoDTO.getItemId());


        // Si ya existe, lanza un error en lugar de actualizar
        if (inventarioPuestoOptional.isPresent()){
            System.out.println("DEBUG: Inventario ya existe para Puesto ID " + createInventarioPuestoDTO.getPuestoId() + " e Item ID " + createInventarioPuestoDTO.getItemId() + ". Lanzando excepción.");
            throw new IllegalArgumentException("Ya existe un inventario para el puesto con ID " + createInventarioPuestoDTO.getPuestoId() + " y el item con ID " + createInventarioPuestoDTO.getItemId() + ".");
        } else {
            InventarioPuesto inventarioPuesto = convertirA_InventarioPuesto(createInventarioPuestoDTO);
            InventarioPuesto savedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
            return convertirA_DTO(savedInventarioPuesto);
        }
    }

    /// DELETE ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteInventarioPuesto(Long id) {
        if (inventarioPuestoRepositorio.existsById(id)) {
            inventarioPuestoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean deleteInventarioPuesto(Long id, Long puestoId) {
        // Verifica que el inventario exista y que pertenezca al puesto especificado
        Optional<InventarioPuesto> inventarioPuestoOptional = inventarioPuestoRepositorio.findById(id);
        if (inventarioPuestoOptional.isPresent() && inventarioPuestoOptional.get().getPuesto().getPuestoId().equals(puestoId)) {
            inventarioPuestoRepositorio.deleteById(id);
            return true;
        }
        return false; // Retorna false si no existe o no pertenece al puesto
    }

    /// PATCH ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        return inventarioPuestoRepositorio.findById(id)
                .map(inventarioPuesto -> {
                    Integer cantidadActual = inventarioPuesto.getCantidad();
                    BigDecimal costoActual = inventarioPuesto.getCostoAdquisicion();

                    // Si el DTO trae una nueva cantidad Y es diferente a la actual
                    if (updateInventarioPuestoDTO.getCantidad() != null && !updateInventarioPuestoDTO.getCantidad().equals(cantidadActual)) {
                        Integer nuevaCantidadEnDTO = updateInventarioPuestoDTO.getCantidad();

                        // Si la cantidad en el DTO es MAYOR a la actual y trae un costo de adquisición (significa un ingreso)
                        if (nuevaCantidadEnDTO > cantidadActual && updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                            Integer cantidadIngreso = nuevaCantidadEnDTO - cantidadActual;
                            BigDecimal costoIngreso = updateInventarioPuestoDTO.getCostoAdquisicion();

                            BigDecimal nuevoCostoPonderado = calcularCostoPromedioPonderado(
                                    costoActual, cantidadActual,
                                    costoIngreso, cantidadIngreso);

                            inventarioPuesto.setCostoAdquisicion(nuevoCostoPonderado);
                            inventarioPuesto.setCantidad(nuevaCantidadEnDTO); // Actualiza la cantidad total
                        } else {
                            // Casos de salida de stock o aumento sin costo explícito
                            // El costo promedio existente se mantiene, a menos que se sobrescriba explícitamente el costo.
                            inventarioPuesto.setCantidad(nuevaCantidadEnDTO);
                            if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                                // Si se actualiza solo el costo sin un ingreso de cantidad, se sobrescribe directamente.
                                inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                            }
                        }
                    } else if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        // Si la cantidad NO cambia, pero el costo de adquisición SÍ se proporciona, lo actualizamos directamente.
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }

                    // Otros campos
                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        // Si el DTO proporciona un nuevo itemId, lo buscamos y asignamos.
                        itemRepositorio.findById(updateInventarioPuestoDTO.getItemId())
                                .ifPresentOrElse(
                                        item -> inventarioPuesto.setItemId(item.getItem_id()),
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado."); }
                                );
                    } else {
                        if (inventarioPuesto.getItemId() == null) {
                            throw new IllegalStateException("El itemId del inventario es nulo y no se proporcionó un nuevo itemId en el DTO.");
                        }
                    }

                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    if (updateInventarioPuestoDTO.getStockMin() != null) {
                        inventarioPuesto.setStockMin(updateInventarioPuestoDTO.getStockMin());
                    }
                    if (updateInventarioPuestoDTO.getPrecioVenta() != null) {
                        inventarioPuesto.setPrecioVenta(updateInventarioPuestoDTO.getPrecioVenta());
                    }

                    InventarioPuesto updatedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
                    return convertirA_DTO(updatedInventarioPuesto);
                });
    }

    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, Long puestoId, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        // Busca el inventario y verifica que pertenezca al puesto especificado
        return inventarioPuestoRepositorio.findById(id)
                .filter(inventarioPuesto -> inventarioPuesto.getPuesto().getPuestoId().equals(puestoId)) // Filtra para asegurar que pertenece al puesto
                .map(inventarioPuesto -> {
                    Integer cantidadActual = inventarioPuesto.getCantidad();
                    BigDecimal costoActual = inventarioPuesto.getCostoAdquisicion();

                    if (updateInventarioPuestoDTO.getCantidad() != null && !updateInventarioPuestoDTO.getCantidad().equals(cantidadActual)) {
                        Integer nuevaCantidadEnDTO = updateInventarioPuestoDTO.getCantidad();

                        if (nuevaCantidadEnDTO > cantidadActual && updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                            Integer cantidadIngreso = nuevaCantidadEnDTO - cantidadActual;
                            BigDecimal costoIngreso = updateInventarioPuestoDTO.getCostoAdquisicion();

                            BigDecimal nuevoCostoPonderado = calcularCostoPromedioPonderado(
                                    costoActual, cantidadActual,
                                    costoIngreso, cantidadIngreso);

                            inventarioPuesto.setCostoAdquisicion(nuevoCostoPonderado);
                            inventarioPuesto.setCantidad(nuevaCantidadEnDTO);
                        } else {
                            inventarioPuesto.setCantidad(nuevaCantidadEnDTO);
                            if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                                inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                            }
                        }
                    } else if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }

                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        itemRepositorio.findById(updateInventarioPuestoDTO.getItemId())
                                .ifPresentOrElse(
                                        item -> inventarioPuesto.setItemId(item.getItem_id()),
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado."); }
                                );
                    } else {
                        if (inventarioPuesto.getItemId() == null) {
                            throw new IllegalStateException("El itemId del inventario es nulo y no se proporcionó un nuevo itemId en el DTO.");
                        }
                    }

                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    if (updateInventarioPuestoDTO.getStockMin() != null) {
                        inventarioPuesto.setStockMin(updateInventarioPuestoDTO.getStockMin());
                    }
                    if (updateInventarioPuestoDTO.getPrecioVenta() != null) {
                        inventarioPuesto.setPrecioVenta(updateInventarioPuestoDTO.getPrecioVenta());
                    }

                    InventarioPuesto updatedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
                    return convertirA_DTO(updatedInventarioPuesto);
                });
    }

    // Otro
    private BigDecimal calcularCostoPromedioPonderado(
            BigDecimal costoExistente, Integer cantidadExistente, BigDecimal costoNuevoIngreso, Integer cantidadNuevoIngreso) {

        // Valor total del stock actual (cantidad existente * costo existente)
        BigDecimal valorTotalExistente = costoExistente.multiply(new BigDecimal(cantidadExistente));

        // Valor total de las unidades que ingresan (cantidad nueva * costo nuevo)
        BigDecimal valorTotalNuevoIngreso = costoNuevoIngreso.multiply(new BigDecimal(cantidadNuevoIngreso));

        // Cantidad total de unidades después del ingreso
        Integer cantidadTotal = cantidadExistente + cantidadNuevoIngreso;

        // Si no hay unidades en total, el costo promedio es cero
        if (cantidadTotal == 0) {
            return BigDecimal.ZERO;
        }

        // Nuevo costo promedio ponderado = (valor total combinado) / (cantidad total)
        BigDecimal nuevoCostoPonderado = (valorTotalExistente.add(valorTotalNuevoIngreso))
                .divide(new BigDecimal(cantidadTotal), 2, RoundingMode.HALF_UP); // Redondeo a 2 decimales

        return nuevoCostoPonderado;
    }
}
