package com.owo.TP_prg3.Clases.Tienda.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Tienda.dto.FormTiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.TiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;

@Service
public class TiendaServicio implements I_CRUD<Tienda, TiendaDTO, FormTiendaDTO> {

    @Autowired
    private TiendaRepositorio tiendaRepositorio;
    @Autowired
    private DuenioRepositorio duenioRepositorio;
    @Autowired
    private CuentaBancariaRepositorio cbRepositorio;

    @Override
    public Tienda convertir_a_Obj(FormTiendaDTO tiendaDTO) {
        Tienda tienda = new Tienda();
        tienda.setNombre(tiendaDTO.getNombre());
        tienda.setDireccion(tiendaDTO.getDireccion());
        
        //Buscar y asignar la CB
        Integer cbu = tiendaDTO.getCbu();
        if (cbu !=null) {
            Optional<CuentaBancaria> optionalCb = cbRepositorio.findByCbu(cbu);
            CuentaBancaria cuenta;
            if (optionalCb.isPresent()) cuenta = optionalCb.get();
            else cuenta = new CuentaBancaria(null, cbu, BigDecimal.ZERO, tienda);
            cuenta.setTienda(tienda);
            tienda.getCuentaBancaria().add(cuenta);
        }
        
        //Buscar y asignar el duenio
        Integer duenioDni = tiendaDTO.getDuenioDni();
        tienda.setDuenio(duenioRepositorio.findByPersona_Dni(duenioDni).orElse(new Duenio(null, new Persona(), new Usuario())));
        
        return tienda;
    }

    @Override
    public TiendaDTO convertir_a_DTO(Tienda tienda){
        return new TiendaDTO(
            tienda.getTiendaId(),
            tienda.getNombre(),
            tienda.getDireccion(),
            tienda.getCaja().getSaldo(), 
            tienda.getDuenio().getNombre()
        );
    }

    @Override
    public Set<TiendaDTO> obtenerTodos() {
        return tiendaRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<TiendaDTO> buscarPorID(Long id) {
        return tiendaRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormTiendaDTO createDTO) {
        tiendaRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
    @Override
    public boolean actualizar(Long id, FormTiendaDTO updateDTO) {
        Optional<Tienda> optional = tiendaRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Tienda tienda = optional.get();
        tienda.setNombre(updateDTO.getNombre());
        tienda.setDireccion(updateDTO.getDireccion());
        //TODO tienda.setCaja()
        
        Integer duenioDni = updateDTO.getDuenioDni();
        tienda.setDuenio(duenioRepositorio.findByPersona_Dni(duenioDni).orElse(new Duenio(null, new Persona(), new Usuario())));

        tiendaRepositorio.save(tienda);
        return true;
    }


    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Optional<Tienda> optional = tiendaRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else tiendaRepositorio.delete(optional.get());
        return true;
    }

    @Override
    public Set<TiendaDTO> filtrar(String campo, Object valor) {
        Stream<Tienda> stream = tiendaRepositorio.findAll().stream();

        Predicate<Tienda> filtro;
        switch (campo.toLowerCase()) {
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<TiendaDTO> ordenar(String campo, boolean ascendente) {
        Stream<Tienda> stream = tiendaRepositorio.findAll().stream();

        Comparator<Tienda> comparador;
        switch (campo.toLowerCase()) {
            default -> comparador = Comparator.comparing(Tienda::getTiendaId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }
}
