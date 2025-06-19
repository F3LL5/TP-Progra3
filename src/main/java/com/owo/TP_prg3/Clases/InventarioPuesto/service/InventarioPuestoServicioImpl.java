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
        InventarioPuestoDTO dto = new InventarioPuestoDTO();
        dto.setInventario_id(inventarioPuesto.getInventario_id()); // Usar el campo correcto del ID
        dto.setPuestoId(inventarioPuesto.getPuesto().getPuestoId()); // Asumo que Puesto.getId() es el ID del puesto

        // Asignar el itemId directamente desde la entidad
        dto.setItemId(inventarioPuesto.getItemId());

        dto.setCantidad(inventarioPuesto.getCantidad());
        dto.setCostoAdquisicion(inventarioPuesto.getCostoAdquisicion());
        dto.setPrecioVenta(inventarioPuesto.getPrecioVenta());
        dto.setStockMin(inventarioPuesto.getStockMin());
        return dto;
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
    @Transactional
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        return inventarioPuestoRepositorio.findById(id)
                .map(inventarioPuesto -> {
                    // Actualización de cantidad
                    if (updateInventarioPuestoDTO.getCantidad() != null) {
                        inventarioPuesto.setCantidad(updateInventarioPuestoDTO.getCantidad());
                    }
                    // Actualización de costoAdquisicion
                    if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }

                    // --- INICIO DE MANEJO DE ITEM_ID (AHORA SÍ, PARA TU MODELO) ---
                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        // Verificamos que el nuevo ItemId exista antes de asignarlo
                        if (!itemRepositorio.existsById(updateInventarioPuestoDTO.getItemId())) {
                            throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado.");
                        }
                        inventarioPuesto.setItemId(updateInventarioPuestoDTO.getItemId());
                    }

                    // Tu validación personalizada para ItemId no nulo.
                    // Si el itemId en la entidad es null (porque no se proporcionó uno nuevo,
                    // y la entidad no lo tenía, o se intentó poner a null), lanza tu excepción.
                    if (inventarioPuesto.getItemId() == null) {
                        throw new IllegalStateException("El itemId del inventario no puede ser nulo al intentar guardar.");
                    }
                    // --- FIN DE MANEJO DE ITEM_ID ---

                    // Actualización de Puesto
                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    // Actualización de Stock Mínimo
                    if (updateInventarioPuestoDTO.getStockMin() != null) {
                        inventarioPuesto.setStockMin(updateInventarioPuestoDTO.getStockMin());
                    }
                    // Actualización de Precio de Venta
                    if (updateInventarioPuestoDTO.getPrecioVenta() != null) {
                        inventarioPuesto.setPrecioVenta(updateInventarioPuestoDTO.getPrecioVenta());
                    }

                    if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }

                    System.out.println();
                    System.out.println(inventarioPuesto);
                    System.out.println(id);
                    System.out.println();

                    InventarioPuesto updatedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
                    return convertirA_DTO(updatedInventarioPuesto);
                });
    }


    @Transactional
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, Long puestoId, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        // Busca el inventario y verifica que pertenezca al puesto especificado
        return inventarioPuestoRepositorio.findById(id)
                .filter(inventarioPuesto -> inventarioPuesto.getPuesto().getPuestoId().equals(puestoId))
                .map(inventarioPuesto -> {
                    // Actualización de cantidad
                    if (updateInventarioPuestoDTO.getCantidad() != null) {
                        inventarioPuesto.setCantidad(updateInventarioPuestoDTO.getCantidad());
                    }
                    // Actualización de costoAdquisicion
                    if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }

                    // --- INICIO DE MANEJO DE ITEM_ID (AHORA SÍ, PARA TU MODELO) ---
                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        // Verificamos que el nuevo ItemId exista antes de asignarlo
                        if (!itemRepositorio.existsById(updateInventarioPuestoDTO.getItemId())) {
                            throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado.");
                        }
                        inventarioPuesto.setItemId(updateInventarioPuestoDTO.getItemId());
                    }

                    // Tu validación personalizada para ItemId no nulo.
                    if (inventarioPuesto.getItemId() == null) {
                        throw new IllegalStateException("El itemId del inventario no puede ser nulo al intentar guardar.");
                    }
                    // --- FIN DE MANEJO DE ITEM_ID ---

                    // Actualización de Puesto
                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    // Actualización de Stock Mínimo
                    if (updateInventarioPuestoDTO.getStockMin() != null) {
                        inventarioPuesto.setStockMin(updateInventarioPuestoDTO.getStockMin());
                    }
                    // Actualización de Precio de Venta
                    if (updateInventarioPuestoDTO.getPrecioVenta() != null) {
                        inventarioPuesto.setPrecioVenta(updateInventarioPuestoDTO.getPrecioVenta());
                    }

                    if (updateInventarioPuestoDTO.getCostoAdquisicion() != null) {
                        inventarioPuesto.setCostoAdquisicion(updateInventarioPuestoDTO.getCostoAdquisicion());
                    }


                    InventarioPuesto updatedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
                    return convertirA_DTO(updatedInventarioPuesto);
                });
    }









}
