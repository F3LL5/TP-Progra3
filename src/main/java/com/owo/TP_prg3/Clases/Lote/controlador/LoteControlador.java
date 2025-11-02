package com.owo.TP_prg3.Clases.Lote.controlador;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Lote.dto.FormLoteDTO;
import com.owo.TP_prg3.Clases.Lote.dto.LoteDTO;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;

@RestController
@RequestMapping("api/lotes")
public class LoteControlador implements I_Controlador<LoteDTO, FormLoteDTO>{

    @Autowired
    private LoteServicio loteServicio;

    @Override
    public ResponseEntity<Set<LoteDTO>> obtenerTodos() {
        return ResponseEntity.ok(loteServicio.obtenerTodos());
    }

    @Override
    public ResponseEntity<LoteDTO> buscarPorID(Long id) {
        return ResponseEntity.ok(loteServicio.buscarPorID(id).orElse(null));
    }

    @Override
    public ResponseEntity<Set<LoteDTO>> filtrar(String campo, Object valor) {
        return ResponseEntity.ok(loteServicio.filtrar(campo, valor));
    }

    @Override
    public ResponseEntity<Set<LoteDTO>> ordenar(String campo, boolean ascendente) {
        return ResponseEntity.ok(loteServicio.ordenar(campo, ascendente));
    }

    @Override
    public ResponseEntity<Boolean> cargar(FormLoteDTO dto) {
        boolean exito = loteServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true);
        else return ResponseEntity.badRequest().body(false);
    }

    @Override
    public ResponseEntity<Boolean> actualizar(Long id, FormLoteDTO dto) {
        return ResponseEntity.ok(loteServicio.actualizar(id, dto));
    }

    @Override
    public ResponseEntity<Boolean> eliminar(Long id) {
        return ResponseEntity.ok(loteServicio.eliminar(id));
    }

}
