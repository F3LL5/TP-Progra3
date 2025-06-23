package com.owo.TP_prg3.Clases.Entidad.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Excepciones.ConflictoDeDatosException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EntidadServicioImpl implements EntidadServicio {

    @Autowired
    private EntidadRepositorio entidadRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;

    // Conversión
    private EntidadDTO convertirA_DTO(Entidad entidad) {
        return new EntidadDTO(
                entidad.getEntidad_id(),
                entidad.getDni(),
                entidad.getNombre(),
                entidad.getEdad(),
                entidad.getTipoEntidad(),
                entidad.getRolEntidad()
        );
    }

    private Entidad convertirA_Entidad(CreateEntidadDTO entidadDTO) {
        Entidad entidad = new Entidad();
        entidad.setNombre(entidadDTO.getNombre());
        entidad.setRolEntidad(entidadDTO.getRolEntidad());
        entidad.setTipoEntidad(entidadDTO.getTipoEntidad());
        if (entidadDTO.getEdad() < 0) throw new IngresoInvalidoException("La edad no puede ser negativa.");
        entidad.setEdad(entidadDTO.getEdad());
        entidad.setDni(entidadDTO.getDni());
        return entidad;
    }

    /// Métodos ------------------------------------------------------------------------------------------------------------------------------------------------
    /// GET ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public List<EntidadDTO> getAllEntidades() {
        return entidadRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<EntidadDTO> getEntidadById(Long id) {
        return Optional.of(entidadRepositorio.findById(id).map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entidad con ID " + id + " no encontrada.")));
    }

    @Override
    public Optional<EntidadDTO> findByDni(int dni) {
        Optional<Entidad> optional =
                entidadRepositorio.findAll()
                        .stream()
                        .filter(entidad -> entidad.getDni() == dni)
                        .findFirst();
        return optional.map(this::convertirA_DTO);
    }

    @Override
    public List<EntidadDTO> filtrarYOrdenar(
            @RequestParam(required = false) Long puestoId, @RequestParam(required = false) String rol_entidad,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        Stream<EntidadDTO> entidadDTOStream;

        if (puestoId != null) {
            // Si se proporciona un puestoId, comenzar con las entidades asociadas a ese puesto.
            entidadDTOStream = getEntidadesByPuestoId(puestoId).stream();
        } else {
            // Si no se proporciona puestoId, usar todas las entidades.
            entidadDTOStream = getAllEntidades().stream();
        }

        if (rol_entidad != null && !rol_entidad.isBlank()) {
            try {
                RolEntidad roleEnum = RolEntidad.valueOf(rol_entidad.toUpperCase());
                // Si se filtra por puesto, restringir los roles permitidos
                if (puestoId != null && (roleEnum == RolEntidad.DUENO_PUESTO || roleEnum == RolEntidad.ADMIN)) {
                    throw new IngresoInvalidoException("Cuando se filtra por puesto, el rol de entidad debe ser CLIENTE o PROVEEDOR.");
                }
                entidadDTOStream = entidadDTOStream.filter(entidad -> entidad.getRolEntidad().name().equalsIgnoreCase(rol_entidad));
            } catch (IllegalArgumentException e) {
                throw new IngresoInvalidoException("El rol de entidad '" + rol_entidad + "' no es válido. Valores permitidos: CLIENTE, PROVEEDOR, DUENO_PUESTO, ADMIN.");
            }
        }

        if (sortDir != null && !sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IngresoInvalidoException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        if(sortBy != null) {
            Comparator<EntidadDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "nombre" -> comparator = Comparator.comparing(EntidadDTO::getNombre);
                case "edad" -> comparator = Comparator.comparing(EntidadDTO::getEdad);
                case "dni" -> comparator = Comparator.comparing(EntidadDTO::getDni);
                case "tipoentidad" -> comparator = Comparator.comparing(EntidadDTO::getTipoEntidad);
                default -> throw new RuntimeException("Dicho criterio NO existe.");
            }
            if(comparator != null) if ("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            entidadDTOStream = entidadDTOStream.sorted(comparator);
        }

        return entidadDTOStream.toList();
    }

    // Obtener todas las entidades (CLIENTE, PROVEEDOR) asociadas a un puesto específico
    @Override
    public List<EntidadDTO> getEntidadesByPuestoId(Long puestoId) {
        List<EntidadDTO> allEntities = getAllEntidades(); // Obtener todas las entidades
        return allEntities.stream()
                .filter(entidad -> {
                    // Si la entidad es CLIENTE o PROVEEDOR
                    if (entidad.getRolEntidad() == RolEntidad.CLIENTE || entidad.getRolEntidad() == RolEntidad.PROVEEDOR) {
                        // Buscar si esta entidad tiene algún pedido asociado al puestoId
                        return pedidoRepositorio.findAll().stream()
                                .anyMatch(pedido -> pedido.getPuestoId().equals(puestoId) &&
                                        (pedido.getTransaccion().getCuentaOrigen().getEntidad().getEntidad_id().equals(entidad.getEntidad_id()) ||
                                                pedido.getTransaccion().getCuentaDestino().getEntidad().getEntidad_id().equals(entidad.getEntidad_id())));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // Filtrar Clientes con pedidos de un puesto específico
    @Override
    public List<EntidadDTO> getClientesConPedidosByPuestoId(Long puestoId) {
        List<Pedido> pedidosDePuesto = pedidoRepositorio.findAll().stream()
                .filter(pedido -> pedido.getPuestoId().equals(puestoId))
                .toList();

        List<Long> clienteIds = pedidosDePuesto.stream()
                .map(pedido -> pedido.getTransaccion().getCuentaOrigen().getEntidad().getEntidad_id())
                .distinct()
                .toList();

        return entidadRepositorio.findAll().stream()
                .filter(entidad -> clienteIds.contains(entidad.getEntidad_id()) && entidad.getRolEntidad() == RolEntidad.CLIENTE)
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    // Buscar entidad por ID y Puesto ID
    @Override
    public Optional<EntidadDTO> getEntidadByIdAndPuestoId(Long id, Long puestoId) {
        Optional<EntidadDTO> entidad = getEntidadById(id);

        if (entidad.isPresent()) {
            boolean isAssociated = getEntidadesByPuestoId(puestoId).stream()
                    .anyMatch(e -> e.getEntidad_id().equals(id));
            if (isAssociated) {
                return entidad;
            }
        }
        return Optional.empty();
    }

    /// POST ------------------------------------------------------------------------------------------------------------------------------------------------

    // Agregar Entidad para un Puesto específico
    @Transactional
    @Override
    public EntidadDTO createEntidadForPuesto(Long puestoId, CreateEntidadDTO createEntidadDTO) {
        if (createEntidadDTO.getRolEntidad() != RolEntidad.CLIENTE && createEntidadDTO.getRolEntidad() != RolEntidad.PROVEEDOR) {
            throw new IngresoInvalidoException("Solo se pueden crear entidades con rol CLIENTE o PROVEEDOR para un puesto.");
        }
        Entidad entidad = convertirA_Entidad(createEntidadDTO);
        Entidad entidadRegistrada = entidadRepositorio.save(entidad);
        return convertirA_DTO(entidadRegistrada);
    }

    @Transactional
    @Override
    public EntidadDTO createEntidadYCuentaBancaria(CreateEntidadDTO createEntidadDTO) {
        Entidad entidad = convertirA_Entidad(createEntidadDTO);
        Entidad entidadRegistrada = entidadRepositorio.save(entidad);

        //Creamos una cuenta bancaria automaticamente con un saldo random para agilizar el sistema
        if (entidadRegistrada.getEdad() >= 18) { // Solo si la entidad es mayor de edad
            CuentaBancaria cuentaBancaria = new CuentaBancaria();
            cuentaBancaria.setEntidad(entidadRegistrada);
            // Saldo random: 10,000 * (número aleatorio entre 1 y 10)
            double num = (int)(Math.random() * 10) + 1; // Número aleatorio entre 1 y 10
            cuentaBancaria.setSaldo(BigDecimal.valueOf(num * 10000.0));
            cuentaBancariaRepositorio.save(cuentaBancaria);
        }

        return convertirA_DTO(entidadRegistrada);
    }

    @Override
    public EntidadDTO createEntidad(CreateEntidadDTO createEntidadDTO) {
        Entidad entidad = convertirA_Entidad(createEntidadDTO);
        Entidad entidadRegistrada = entidadRepositorio.save(entidad);
        return convertirA_DTO(entidadRegistrada);
    }

    /// PATCH ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Optional<EntidadDTO> updateEntidad(Long id, UpdateEntidadDTO updateEntidadDTO) {
        return entidadRepositorio.findById(id)
                .map(entidad -> {
                    if (entidad == null) {
                        throw new RecursoNoEncontradoException("Item con ID " + id + " no encontrado para actualizar.");
                    }
                    if (updateEntidadDTO.getNombre() != null) entidad.setNombre(updateEntidadDTO.getNombre());
                    if (updateEntidadDTO.getRolEntidad() != null) entidad.setRolEntidad(updateEntidadDTO.getRolEntidad());
                    if (updateEntidadDTO.getTipoEntidad() != null) entidad.setTipoEntidad(updateEntidadDTO.getTipoEntidad());
                    if (updateEntidadDTO.getEdad() < 0)
                            throw new IngresoInvalidoException("La edad no puede ser negativa.");
                    entidad.setEdad(updateEntidadDTO.getEdad());

                    if (updateEntidadDTO.getDni() != null) {
                        Integer nuevoDni = updateEntidadDTO.getDni();
                        Optional<Entidad> dniYaExiste = entidadRepositorio.findByDni(nuevoDni);
                        if (dniYaExiste.isPresent()) throw new ConflictoDeDatosException("Ya existe otra entidad registrada con ese DNI.");
                        entidad.setDni(nuevoDni);
                    }

                    Entidad entidadModificada = entidadRepositorio.save(entidad);
                    return convertirA_DTO(entidadModificada);
                })
                .or(() -> {
                    throw new RecursoNoEncontradoException("Item con ID " + id + " no encontrado para actualizar.");
                });
    }

    // Modificar Entidad de un Puesto específico
    @Transactional
    @Override
    public Optional<EntidadDTO> updateEntidadForPuesto(Long id, Long puestoId, UpdateEntidadDTO updateEntidadDTO) {
        Optional<EntidadDTO> entidad = getEntidadByIdAndPuestoId(id, puestoId);
        if (entidad.isPresent()) {
            return updateEntidad(id, updateEntidadDTO);
        }
        throw new RecursoNoEncontradoException("La entidad con ID " + id + " no fue encontrada o no está asociada al puesto " + puestoId + " para ser modificada.");
    }

    /// DELETE ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteEntidad(Long id) {
        Entidad entidad = entidadRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La entidad de ID " + id + " no ha sido encontrada."));

        boolean tienePedidos = pedidoRepositorio.findAll().stream()
                .anyMatch(p ->
                        (p.getTransaccion().getCuentaOrigen().getEntidad().getEntidad_id().equals(id)) || (p.getTransaccion().getCuentaDestino().getEntidad().getEntidad_id().equals(id))
                );

        boolean tieneCuentas = cuentaBancariaRepositorio.findAll().stream().anyMatch(c -> c.getEntidad().getEntidad_id().equals(id));

        if (tienePedidos || tieneCuentas)
            throw new ConflictoDeDatosException("No se puede eliminar la entidad. Tiene pedidos o cuentas bancarias asociadas.");

        entidadRepositorio.delete(entidad);
        return true;
    }

    // Eliminar Entidad de un Puesto específico
    @Transactional
    @Override
    public boolean deleteEntidadFromPuesto(Long id, Long puestoId) {
        Optional<EntidadDTO> entidad = getEntidadByIdAndPuestoId(id, puestoId);
        if (entidad.isPresent()) {
            return deleteEntidad(id);
        } else throw new RecursoNoEncontradoException("La entidad con ID " + id + " no fue encontrada o no está asociada al puesto " + puestoId + " para ser eliminada.");
    }

}