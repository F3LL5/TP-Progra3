package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Item.service.ItemServicioImpl;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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




    private InventarioPuestoDTO convertirA_DTO(InventarioPuesto inventarioPuesto) {
        return new InventarioPuestoDTO(
                inventarioPuesto.getInventario_id(),
                inventarioPuesto.getCantidad(),
                inventarioPuesto.getPuesto() != null ? inventarioPuesto.getPuesto().getPuestoId() : null,
                inventarioPuesto.getItem() != null ? inventarioPuesto.getItem().getItem_id() : null,
                inventarioPuesto.getStockMin(),
                inventarioPuesto.getPrecioVenta()
        );
    }

    private InventarioPuesto convertirA_InventarioPuesto(CreateInventarioPuestoDTO inventarioPuestoDTO) {
        InventarioPuesto inventarioPuesto = new InventarioPuesto();
        inventarioPuesto.setCantidad(inventarioPuestoDTO.getCantidad());

        puestoRepositorio.findById(inventarioPuestoDTO.getPuestoId())
                .ifPresentOrElse(
                        inventarioPuesto::setPuesto,
                        () -> { throw new EntityNotFoundException("Puesto con ID " + inventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                );

        itemRepositorio.findById(inventarioPuestoDTO.getItemId())
                .ifPresentOrElse(
                        inventarioPuesto::setItem,
                        () -> { throw new EntityNotFoundException("Item con ID " + inventarioPuestoDTO.getItemId() + " no encontrado."); }
                );

        inventarioPuesto.setStockMin(inventarioPuestoDTO.getStockMin());
        inventarioPuesto.setPrecioVenta(inventarioPuestoDTO.getPrecioVenta());
        return inventarioPuesto;
    }

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

    @Override
    public InventarioPuestoDTO createInventarioPuesto(CreateInventarioPuestoDTO createInventarioPuestoDTO) {
        InventarioPuesto inventarioPuesto = convertirA_InventarioPuesto(createInventarioPuestoDTO);
        InventarioPuesto savedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
        return convertirA_DTO(savedInventarioPuesto);
    }

    @Override
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        return inventarioPuestoRepositorio.findById(id)
                .map(inventarioPuesto -> {
                    if (updateInventarioPuestoDTO.getCantidad() != null) {
                        inventarioPuesto.setCantidad(updateInventarioPuestoDTO.getCantidad());
                    }
                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        itemRepositorio.findById(updateInventarioPuestoDTO.getItemId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setItem,
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado."); }
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

    @Override
    public boolean deleteInventarioPuesto(Long id) {
        if (inventarioPuestoRepositorio.existsById(id)) {
            inventarioPuestoRepositorio.deleteById(id);
            return true;
        }
        return false;
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

    public List<Map<String,Object>> mostrarItemsEnStock(Long puestoId)
    {
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
                    fila.put("costo", item.getCosto());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad()); // se agrega stock

                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }

    public List<Map<String,Object>> obtenerItemsEnInventario(Long puestoId)
    {
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
                    fila.put("costo", item.getCosto());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad()); // Agregás el stock


                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }
    public List<Map<String,Object>> mostrarItemsEnStockBajo(Long puestoId)
    {
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
                    fila.put("costo", item.getCosto());
                    fila.put("categoria",item.getCategoria());
                    fila.put("cantidad", inv.getCantidad()); // Agregás el stock

                    respuesta.add(fila);
                    break;
                }
            }
        }
        return respuesta;
    }

    public List<Map<String,Object>> filtrarYordenar(Long puestoId,String categoria,String orden,String direccion)
    {

        List<Map<String,Object>> itemsMap=obtenerItemsEnInventario(puestoId);

        if (categoria != null && !categoria.isEmpty()) {
            itemsMap = itemsMap.stream()
                    .filter(item -> categoria.equalsIgnoreCase((String) item.get("categoria")))
                    .collect(Collectors.toList());
        }

        String campoOrden = (orden == null || orden.isEmpty()) ? "nombre" : orden;
        String campoDireccion = (direccion == null || direccion.isEmpty()) ? "asc" : direccion;



        itemsMap.sort((a, b) -> {

            Comparable valorA = (Comparable) a.get(campoOrden);
            Comparable valorB = (Comparable) b.get(campoOrden);


            int comparacion = valorA.compareTo(valorB);


            if ("desc".equalsIgnoreCase(campoDireccion)) {
                return -comparacion;
            } else {
                return comparacion;
            }
        });
        return itemsMap;
    }



























}
