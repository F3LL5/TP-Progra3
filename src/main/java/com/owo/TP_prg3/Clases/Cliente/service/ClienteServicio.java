package com.owo.TP_prg3.Clases.Cliente.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.Persona.service.PersonaServicio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import jakarta.transaction.Transactional;
import com.owo.TP_prg3.Clases.Cliente.dto.ClienteDTO;
import com.owo.TP_prg3.Clases.Cliente.modelo.Cliente;
import com.owo.TP_prg3.Clases.Cliente.modelo.ClienteRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;

@Service
public class ClienteServicio implements I_CRUD<Cliente, ClienteDTO, FormPersonaDTO>{

    private static final String ENTIDAD = "Cliente";

    @Autowired
    private PersonaServicio personaServicio;
    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    // Conversión
    @Override
    public Cliente convertir_a_Obj(FormPersonaDTO fDTO) {
        validarDatosCliente(fDTO);

        Cliente cliente = new Cliente();
        cliente.setNombre(fDTO.getNombre());
        cliente.setEdad(fDTO.getEdad());
        cliente.setDni(fDTO.getDni());
        return cliente;
    }
    
    @Override
    public ClienteDTO convertir_a_DTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getPersonaId(), 
            cliente.getDni(),
            cliente.getNombre(),
            cliente.getEdad(),
            cliente.getClienteId()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<ClienteDTO> obtenerTodos() {
        return clienteRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'clientes'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClienteDTO> buscarPorID( Long id) {
        ValidacionGeneral.validarIdValido(id);
        return clienteRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ClienteDTO> buscarPorDNI(int dni) {
        return clienteRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ClienteDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) throw new CampoRequeridoException("campo de filtrado");
        if (valor == null) throw new CampoRequeridoException("valor de filtrado");
        
        Stream<Cliente> stream = clienteRepositorio.findAll().stream();

        Predicate<Cliente> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equalsIgnoreCase(valor.toString().trim());
            case "edad" -> {
                try {
                     int edad = Integer.parseInt(valor.toString());
                     filtro = p -> p.getEdad().equals(edad);
                 } catch (NumberFormatException e) {
                     throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'edad'");
                 }
            }
            case "dni" -> {
                try {
                    int dni = Integer.parseInt(valor.toString());
                    filtro = p -> p.getDni() == dni;
                } catch (NumberFormatException e) {
                    throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'dni'");
                }
            }
            default -> throw new IngresoInvalidoException( 
                "campo de filtrado",
                "debe ser 'nombre', 'edad' o 'dni'"
            ); 
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ClienteDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento");
        }
        Stream<Cliente> stream = clienteRepositorio.findAll().stream();

        Comparator<Cliente> comparador;
        switch (campo.toLowerCase().trim()) {
            case "nombre"-> comparador = Comparator.comparing(Cliente::getNombre);
            case "edad"-> comparador = Comparator.comparing(Cliente::getEdad);
            case "dni"-> comparador = Comparator.comparing(Cliente::getDni);
            default -> comparador = Comparator.comparing(Cliente::getClienteId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormPersonaDTO createDTO) {
        validarDatosCliente(createDTO);
        validarClienteNoExiste(createDTO.getDni());
        // JPA automáticamente realiza el INSERT en la tabla 'personas' y luego en 'clientes'
        clienteRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
   @Transactional
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        validarDatosCliente(updateDTO);
        Cliente cliente = obtenerClientePorId(id);
        validarClienteNoExisteOtro(updateDTO.getDni(), cliente.getPersonaId());
        
        cliente.setNombre(updateDTO.getNombre());
        cliente.setEdad(updateDTO.getEdad());
        cliente.setDni(updateDTO.getDni());

        clienteRepositorio.save(cliente);
        return true;
    }

    // DELETE 
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Cliente cliente = obtenerClientePorId(id);
        clienteRepositorio.delete(cliente);
        return true;
    }

    // VALIDACIONES PRIVADAS ==================================================================================================================================

    /** @param dto El DTO a validar. @throws IngresoInvalidoException si el DTO es nulo. 
        @throws CampoRequeridoException si algún campo obligatorio es nulo o vacío. */
    private void validarDatosCliente(FormPersonaDTO dto) {
        personaServicio.validarDatosPersona(dto);
    }

    /** @param dni El DNI a verificar.
        @throws EntidadDuplicadaException si ya existe un Cliente con ese DNI. */
    private void validarClienteNoExiste(int dni) {
        // Usamos el método existente 'buscarPorDNI' para chequear
        Optional<ClienteDTO> existente = this.buscarPorDNI(dni);
        Optional<Persona> existentePersona = personaRepositorio.findByDni(dni);

        if (existente.isPresent() || existentePersona.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "dni", dni);
        }
    }

    /** @param dni El DNI a verificar. @param id El ID del cliente que se está actualizando.
        @throws EntidadDuplicadaException si ya existe otro Cliente con ese DNI. */
    private void validarClienteNoExisteOtro(int dni, Long id) {
        Optional<Cliente> existente = clienteRepositorio.findByPersona_Dni(dni);
        // Si existe y su ID de persona es diferente al que estamos actualizando (entidad.getPersonaId()), es duplicado.
        if (existente.isPresent() && !existente.get().getPersonaId().equals(id)) {
            throw new EntidadDuplicadaException(ENTIDAD, "dni", dni);
        }
    }

    private Cliente obtenerClientePorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

}
