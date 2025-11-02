package com.owo.TP_prg3.Clases.Persona.Cliente.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.Cliente.dto.ClienteDTO;
import com.owo.TP_prg3.Clases.Persona.Cliente.modelo.Cliente;
import com.owo.TP_prg3.Clases.Persona.Cliente.modelo.ClienteRepositorio;

@Service
public class ClienteServicio implements I_CRUD<Cliente, ClienteDTO, FormPersonaDTO>{

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    // Conversión
    @Override
    public Cliente convertir_a_Obj(FormPersonaDTO fDTO) {
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
    public Optional<ClienteDTO> buscarPorID(Long id) {
        return clienteRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ClienteDTO> buscarPorDNI(int dni) {
        return clienteRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ClienteDTO> filtrar(String campo, Object valor) {

        Stream<Cliente> stream = clienteRepositorio.findAll().stream();

        Predicate<Cliente> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> filtro = p -> p.getEdad().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ClienteDTO> ordenar(String campo, boolean ascendente) {
        Stream<Cliente> stream = clienteRepositorio.findAll().stream();

        Comparator<Cliente> comparador;
        switch (campo.toLowerCase()) {
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

    @Override
    public boolean cargar(FormPersonaDTO createDTO) {
        // JPA automáticamente realiza el INSERT en la tabla 'personas' y luego en 'clientes'
        clienteRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        Optional<Cliente> optional = clienteRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Cliente cliente = optional.get();
        cliente.setNombre(updateDTO.getNombre());
        cliente.setEdad(updateDTO.getEdad());
        cliente.setDni(updateDTO.getDni());

        clienteRepositorio.save(cliente);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Cliente> optional = clienteRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else clienteRepositorio.delete(optional.get());
        return true;
    }
}
