package com.owo.TP_prg3.Clases.Interfaces;

import org.springframework.http.ResponseEntity;
import java.util.Set;

public interface I_Controlador<Dto, FormDto> {
    
    // Operaciones de lectura y consulta
    ResponseEntity<Set<Dto>> obtenerTodos();
    ResponseEntity<Dto> buscarPorID(Long id);
    ResponseEntity<Set<Dto>> filtrar(String campo, Object valor);
    ResponseEntity<Set<Dto>> ordenar(String campo, boolean ascendente);
    
    // Operaciones de escritura
    ResponseEntity<Boolean> cargar(FormDto dto);
    ResponseEntity<Boolean> actualizar(Long id, FormDto dto);
    ResponseEntity<Boolean> eliminar(Long id);
}