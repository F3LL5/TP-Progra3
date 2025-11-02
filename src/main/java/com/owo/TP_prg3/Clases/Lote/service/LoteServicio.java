package com.owo.TP_prg3.Clases.Lote.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Lote.dto.FormLoteDTO;
import com.owo.TP_prg3.Clases.Lote.dto.LoteDTO;
import com.owo.TP_prg3.Clases.Lote.modelo.Lote;
import com.owo.TP_prg3.Clases.Lote.modelo.LoteRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;

public class LoteServicio implements I_CRUD<Lote, LoteDTO, FormLoteDTO> {
    
    // ATRIBUTOS ------------------------------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private LoteRepositorio loteRepositorio;

    // CONVERSION ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Lote convertir_a_Obj(FormLoteDTO fDTO){
        return new Lote(
            null,
            fDTO.getProducto(),
            fDTO.getCantidadDisponible(),
            fDTO.getCostoUnitario(),
            fDTO.getFechaIngreso()
        );
    }
    @Override
    public LoteDTO convertir_a_DTO(Lote lote){
        return new LoteDTO(
            lote.getLote_id(),
            lote.getProducto(),
            lote.getCantidadDisponible(),
            lote.getCostoUnitario(),
            lote.getFechaIngreso()
        );
    }
    

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<LoteDTO> obtenerTodos() {
        return loteRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<LoteDTO> buscarPorID(Long id) {
        return loteRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<LoteDTO> filtrar(String campo, Object valor) {

        Stream<Lote> stream = loteRepositorio.findAll().stream();

        Predicate<Lote> filtro;
        switch (campo.toLowerCase()) {
            case "cantidad"-> filtro = l -> l.getCantidadDisponible().equals(valor);
            case "producto"-> filtro = l -> l.getProducto().getProducto_id().equals(valor); //Filtra por id de producto
            case "costoUnitario"-> filtro = l -> l.getCostoUnitario().equals(valor);
            case "fechaIngreso"-> filtro = l -> l.getFechaIngreso().equals(valor);
            default-> filtro = l -> false;
        }
        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<LoteDTO> ordenar(String campo, boolean ascendente) {

        Stream<Lote> stream = loteRepositorio.findAll().stream();

        Comparator<Lote> comparador;
        switch (campo.toLowerCase()) {
            case "cantidad" -> comparador = Comparator.comparing(Lote::getCantidadDisponible);
            case "producto"-> comparador = Comparator.comparing(Lote::getProducto, Comparator.comparing(Producto::getProducto_id)); 
            case "costoUnitario"-> comparador = Comparator.comparing(Lote::getCostoUnitario);
            case "fechaIngreso"-> comparador = Comparator.comparing(Lote::getFechaIngreso);
            default-> comparador = Comparator.comparing(Lote::getLote_id);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormLoteDTO cDTO) {
        loteRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    // PUT
    @Override
    public boolean actualizar(Long id, FormLoteDTO updateDTO) {
        Optional<Lote> optional = loteRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Lote lote = optional.get();
        lote.setCantidadDisponible(updateDTO.getCantidadDisponible());
        lote.setCostoUnitario(updateDTO.getCostoUnitario());
        lote.setFechaIngreso(updateDTO.getFechaIngreso());

        loteRepositorio.save(lote);
        return true;
    }

    //DELETE
    @Override
    public boolean eliminar(Long id) {
        Optional<Lote> optional = loteRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else loteRepositorio.delete(optional.get());
        return true;
    }
}
