package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoServicioImpl implements PedidoServicio {
    //ATRIBUTOS
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Autowired
    private DetallePedidoServicioImpl detallePedidoServicio;

    //CONVERSION
    private PedidoDTO convertirA_DTO(Pedido pedido) {
        return new PedidoDTO(
                pedido.getPedidoId(),
                pedido.getTransaccion().getTransaccionId(),
                pedido.getPuestoId()
        );
    }

    private Pedido convertirA_Pedido(CreatePedidoDTO createPedidoDTO){
        Pedido pedido = new Pedido();

        transaccionRepositorio.findById(createPedidoDTO.getTransaccionId())
                .ifPresentOrElse(
                        pedido::setTransaccion,
                        () -> { throw new EntityNotFoundException("Transaccion con ID " + createPedidoDTO.getTransaccionId() + " no encontrada."); }
                );

        pedido.setPuestoId(createPedidoDTO.getPuestoId());

        return pedido;
    }

    //METODOS
    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepositorio.findAll().stream()
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        return pedidoRepositorio.findById(id)
                .map(this::convertirA_DTO);
    }

    @Override
    @Transactional
    public PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO) {
        Pedido pedido = convertirA_Pedido(createPedidoDTO);
        Pedido savedPedido = pedidoRepositorio.save(pedido);
        return convertirA_DTO(savedPedido);
    }

    @Override
    @Transactional
    public Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepositorio.findById(id)
                .map(pedido -> {
                    if (updatePedidoDTO.getTransaccionId() != null) {
                        transaccionRepositorio
                                .findById(updatePedidoDTO.getTransaccionId())
                                .ifPresentOrElse( pedido::setTransaccion, () -> {
                                    throw new EntityNotFoundException("Transaccion con ID " + updatePedidoDTO.getTransaccionId() + " no encontrada.");
                                });
                    }
                    if (updatePedidoDTO.getPuestoId() != null) {
                        pedido.setPuestoId(updatePedidoDTO.getPuestoId());
                    }

                    Pedido updatedPedido = pedidoRepositorio.save(pedido);
                    return convertirA_DTO(updatedPedido);
                });
    }

    @Override
    public boolean deletePedido(Long id) {
        if (pedidoRepositorio.existsById(id)) {
            pedidoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PedidoDTO> filtrarYOrdenar(String tipo_transaccion, String sortBy, String sortDir) {
        List<PedidoDTO> allPedidosDTO = getAllPedidos();
        Stream<PedidoDTO> pedidoDTOStream = allPedidosDTO.stream();

        if (tipo_transaccion != null && !tipo_transaccion.isEmpty()) {
            pedidoDTOStream = pedidoDTOStream.filter(pedido -> transaccionRepositorio.getById(pedido.getTransaccionId()).getTipo().name().equalsIgnoreCase(tipo_transaccion) );
        }

        if (sortBy != null) {
            Comparator<PedidoDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "fecha" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getFecha());
                case "monto" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getMonto());
                case "id_cuenta_origen" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getCuentaOrigen().getCuentaBancariaId());
                case "id_cuenta_destino" -> comparator = Comparator.comparing((PedidoDTO pedido) -> transaccionRepositorio.getById(pedido.getTransaccionId()).getCuentaDestino().getCuentaBancariaId());
                default -> throw new RuntimeException("Dicho criterio de búsqueda NO existe.");
            }
            if(comparator != null) if("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            pedidoDTOStream = pedidoDTOStream.sorted(comparator);
        }

        return pedidoDTOStream.toList();
    }

}
