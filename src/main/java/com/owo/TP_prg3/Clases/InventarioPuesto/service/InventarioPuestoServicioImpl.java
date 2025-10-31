package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.ItemStockDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Item.service.ItemServicioImpl;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.function.Function;
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
    public Optional<InventarioPuestoDTO> getInventarioPuestoById(Long idInv) {
        return inventarioPuestoRepositorio.findById(idInv).map(this::convertirA_DTO);
    }

    //devuelve todos los inventarios del puesto con esa id
    @Override
    public List<InventarioPuestoDTO> obtenerInventariosDeUnPuesto(Long puestoId){
        List<InventarioPuestoDTO> inventarioPuestoDTOS = getAllInventarioPuestos();
        return inventarioPuestoDTOS.stream()
                                .filter(inventarioPuestoDTO -> inventarioPuestoDTO.getPuestoId().equals(puestoId))
                                .toList();
    }

    public Optional<List<ItemStockDTO>> obtenerItemsEnStock(Long puestoId) {
        // Todos los items del mercado
        List<ItemDTO> listaItems = itemServicio.getAllProducts();
        if (listaItems.isEmpty()) throw new RecursoNoEncontradoException("No se han encontrado items registrados.");

        // Todos los inventarios del puesto
        List<InventarioPuestoDTO> inventariosPuesto = obtenerInventariosDeUnPuesto(puestoId);
        if (inventariosPuesto.isEmpty()) throw new RecursoNoEncontradoException("No se han encontrado inventarios en el puesto con ID " + puestoId + ".");

        // Filtrar inventarios con stock positivo
        List<InventarioPuestoDTO> inventarioConStock = inventariosPuesto.stream()
                .filter(inventariopuestodto->inventariopuestodto.getCantidad()>0)
                .toList();
        if ( inventarioConStock.isEmpty() ) return Optional.empty();

        // Crear un mapa para una búsqueda rápida de ItemDTO por itemId
        Map<Long, ItemDTO> itemMap = listaItems.stream()
                .collect(Collectors.toMap(ItemDTO::getItem_id, Function.identity()));

        // Mapear cada InventarioPuestoDTO a su correspondiente ItemStockDTO
        List<ItemStockDTO> result =
                inventarioConStock.stream()
                                .map(inventario -> {
                                    // Encontrar el ItemDTO específico para el itemId de este inventario
                                    ItemDTO itemDTO = itemMap.get(inventario.getItemId());
                                    if (itemDTO != null) {
                                        return new ItemStockDTO(
                                                inventario.getInventario_id(),
                                                inventario.getPuestoId(),
                                                inventario.getItemId(),
                                                itemDTO.getNombre(),
                                                inventario.getCantidad(),
                                                inventario.getStockMin()
                                        );
                                    }
                                    return null;})
                                .filter(Objects::nonNull) // Eliminar cualquier nulo si un item no fue encontrado
                                .toList();
        return Optional.of(result);
    }

    @Override
    public Optional<List<ItemStockDTO>> obtenerItemsEnStockBajo(Long puestoId) {
        // Todos los items del mercado
        List<ItemDTO> listaItems = itemServicio.getAllProducts();
        if (listaItems.isEmpty()) throw new RecursoNoEncontradoException("No se han encontrado items registrados.");

        // Todos los inventarios del puesto
        List<InventarioPuestoDTO> inventariosPuesto = obtenerInventariosDeUnPuesto(puestoId);
        if (inventariosPuesto.isEmpty()) throw new RecursoNoEncontradoException("No se han encontrado inventarios en el puesto con ID " + puestoId + ".");

        // Filtrar inventarios con stock bajo
        List<InventarioPuestoDTO> inventarioConStockBajo = inventariosPuesto.stream()
                .filter(inv->inv.getCantidad() <= inv.getStockMin())
                .toList();
        if ( inventarioConStockBajo.isEmpty() ) return Optional.empty();

        // Crear un mapa para una búsqueda rápida de ItemDTO por itemId
        Map<Long, ItemDTO> itemMap = listaItems.stream()
                .collect(Collectors.toMap(ItemDTO::getItem_id, Function.identity()));

        // Mapear cada InventarioPuestoDTO a su correspondiente ItemStockDTO
        List<ItemStockDTO> result = inventarioConStockBajo.stream()
                .map(inventario -> {
                    // Encontrar el ItemDTO específico para el itemId de este inventario
                    ItemDTO itemDTO = itemMap.get(inventario.getItemId());
                    if (itemDTO != null) {
                        return new ItemStockDTO(
                                inventario.getInventario_id(),
                                inventario.getPuestoId(),
                                inventario.getItemId(),
                                itemDTO.getNombre(),
                                inventario.getCantidad(),
                                inventario.getStockMin()
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull) // Eliminar cualquier nulo
                .toList();
        return Optional.of(result);
    }

    @Override
    public List<InventarioPuestoDTO> filtrarYordenar(
            @RequestParam(required = false) Long puestoId, @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {

        Stream<InventarioPuestoDTO> inventarioStream;

        inventarioStream = getAllInventarioPuestos().stream();

        //Filtrado por puesto
        if (puestoId != null) inventarioStream = inventarioStream.filter(inv -> inv.getPuestoId().equals(puestoId));

        //Filtrado por categoria del item
        if (categoria != null) {
            // Filtra todos los items que sean de la categoria ingresada
            List<Item> itemsCategoria = itemRepositorio.findAll().stream().filter( i -> i.getCategoria().equals(categoria) ).toList();

            // Filtra los inventarios que tengan un match con el stream de items filtrados
            inventarioStream = inventarioStream
                    .filter( inv -> itemsCategoria.stream().anyMatch(i -> i.getItem_id().equals( inv.getItemId() )) );
        }

        // Verificacion de direccion
        if (sortDir != null && !sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IngresoInvalidoException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        // Si se ingresa un ordenamiento lo crea y lo aplica
        if(sortBy != null) {
            Comparator<InventarioPuestoDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "iditem" -> comparator = Comparator.comparing(InventarioPuestoDTO::getInventario_id);
                case "precioventa" -> comparator = Comparator.comparing(InventarioPuestoDTO::getPrecioVenta);
                case "costoadquisicion" -> comparator = Comparator.comparing(InventarioPuestoDTO::getCostoAdquisicion);
                case "cantidad" -> comparator = Comparator.comparing(InventarioPuestoDTO::getCantidad);
                case "stockmin" -> comparator = Comparator.comparing(InventarioPuestoDTO::getStockMin);
                default -> throw new RuntimeException("Dicho criterio NO existe.");
            }
            if(comparator != null) if ("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            inventarioStream = inventarioStream.sorted(comparator);
        }
        return inventarioStream.toList();
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
        } else throw new RecursoNoEncontradoException("El inventario de ID " + id + " no ha sido encontrado.");
    }

    public boolean deleteInventarioPuesto(Long id, Long puestoId) {
        // Verifica que el inventario exista y que pertenezca al puesto especificado
        Optional<InventarioPuesto> inventarioPuestoOptional = inventarioPuestoRepositorio.findById(id);
        if (inventarioPuestoOptional.isPresent() && inventarioPuestoOptional.get().getPuesto().getPuestoId().equals(puestoId)) {
            inventarioPuestoRepositorio.deleteById(id);
            return true;
        } else throw new RecursoNoEncontradoException("El inventario de ID " + id + " no es del puesto: " + puestoId + ".");
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
