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
import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Tienda.dto.FormTiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.TiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;

@Service
public class TiendaServicio implements I_CRUD<Tienda, TiendaDTO, FormTiendaDTO> {

    private static final String ENTIDAD = "Tienda";

    @Autowired
    private TiendaRepositorio tiendaRepositorio;
    @Autowired
    private DuenioRepositorio duenioRepositorio;

    @Override
    public Tienda convertir_a_Obj(FormTiendaDTO tiendaDTO) {
        validarDatosTienda(tiendaDTO);
        validarDuenioExiste(tiendaDTO.getDuenioDni());

        Tienda tienda = new Tienda();
        tienda.setNombre(tiendaDTO.getNombre());
        tienda.setDireccion(tiendaDTO.getDireccion());
        tienda.setCaja(new Caja(tiendaDTO.getCaja()));
        
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
        validarIdValido(id);
        return tiendaRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormTiendaDTO createDTO) {
        validarDatosTienda(createDTO);
        validarTiendaNoExiste(createDTO.getNombre());
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
        tienda.setCaja(new Caja(updateDTO.getCaja()));

        //Atributos que pueden ser modificados pero entran null desde el dto porque no es viable mostrarlos
        
        if(updateDTO.getDuenioDni()!=null){
            Integer duenioDni = updateDTO.getDuenioDni();
            tienda.setDuenio(duenioRepositorio.findByPersona_Dni(duenioDni).orElse(new Duenio(null, new Persona(), new Usuario())));
        }

        tiendaRepositorio.save(tienda);
        return true;
    }


    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Tienda tienda = obtenerTiendaPorId(id);
        tiendaRepositorio.delete(tienda);
        return true;
    }

    @Override
    public Set<TiendaDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de filtrado");
        }
        if (valor == null) {
            throw new CampoRequeridoException("valor de filtrado");
        }

        Stream<Tienda> stream = tiendaRepositorio.findAll().stream();
        Predicate<Tienda> filtro;
        String campoTrim = campo.toLowerCase().trim();

        switch (campoTrim) {
            case "nombre" -> filtro = t -> t.getNombre().equalsIgnoreCase(valor.toString().trim());
            case "direccion" -> filtro = t -> t.getDireccion().equalsIgnoreCase(valor.toString().trim());
            case "duenioid" -> {
                 try {
                     Long duenioId = Long.parseLong(valor.toString());
                     filtro = t -> t.getDuenio() != null && t.getDuenio().getDuenioId().equals(duenioId);
                 } catch (NumberFormatException e) {
                     throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'duenioId'");
                 }
            }
            default -> throw new IngresoInvalidoException(
                "campo de filtrado",
                "debe ser 'nombre', 'direccion' o 'duenioId'"
            );
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<TiendaDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento");
        }

        Stream<Tienda> stream = tiendaRepositorio.findAll().stream();
        Comparator<Tienda> comparador;
        String campoTrim = campo.toLowerCase().trim();

        switch (campoTrim) {
            case "nombre" -> comparador = Comparator.comparing(Tienda::getNombre);
            case "duenioid" -> comparador = Comparator.comparing(t -> t.getDuenio() != null ? t.getDuenio().getDuenioId() : Long.MIN_VALUE);
            default -> comparador = Comparator.comparing(Tienda::getTiendaId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Transactional
    public void acreditarMontoCaja(Long tiendaId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IngresoInvalidoException("monto", "debe ser un valor positivo.");
        }

        Tienda tienda = obtenerTiendaPorId(tiendaId);

        Caja caja = tienda.getCaja();
        
        // Si la caja es nula, aunque no debería serlo, la re hace
        if (caja == null) { 
            caja = new Caja(BigDecimal.ZERO);
            tienda.setCaja(caja);
        }

        // Sumar el monto al saldo actual
        caja.setSaldo(caja.getSaldo().add(monto));
        tiendaRepositorio.save(tienda);
    }

    @Transactional
    public void debitarMontoCaja(Long tiendaId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IngresoInvalidoException("monto", "debe ser un valor positivo.");
        }

        Tienda tienda = obtenerTiendaPorId(tiendaId);
        Caja caja = tienda.getCaja();
        
        if (caja == null) {
            throw new EntidadNoEncontradaException("La caja de la Tienda no existe para debitar.");
        }
        
        if (caja.getSaldo().compareTo(monto) < 0) {
            throw new IngresoInvalidoException("saldo", "insuficiente para debitar el monto solicitado.");
        }

        caja.setSaldo(caja.getSaldo().subtract(monto)); // Restar monto
        tiendaRepositorio.save(tienda);
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    private void validarIdValido(Long id) {
        if (id == null || id <= 0) {
            throw new IngresoInvalidoException("ID", "debe ser un número positivo para la entidad " + ENTIDAD);
        }
    }

    private void validarDatosTienda(FormTiendaDTO dto) {
        if (dto == null) {
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new CampoRequeridoException("nombre");
        }
        
        if (dto.getDireccion() == null || dto.getDireccion().trim().isEmpty()) {
            throw new CampoRequeridoException("dirección");
        }
    }

    private void validarDuenioExiste(int dni) {
        if (duenioRepositorio.findByPersona_Dni(dni).isEmpty()) {
            throw new EntidadNoEncontradaException("Dueño no encontrado con el dni: " + dni);
        }
    }

    private void validarTiendaNoExiste(String nombre) {
        Optional<Tienda> existente = tiendaRepositorio.findByNombre(nombre.trim());
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "nombre", nombre.trim());
        }
    }

    private Tienda obtenerTiendaPorId(Long id) {
        validarIdValido(id);
        return tiendaRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
