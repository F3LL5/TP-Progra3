package com.owo.TP_prg3.Clases.Interfaces;

import java.util.Optional;
import java.util.Set;

public interface I_CRUD<T, Dto, FormDto> {
    // Conversión
    T convertir_a_Obj(FormDto pDTO);
    Dto convertir_a_DTO(T producto);
    
    // Lectura
    Set<Dto> obtenerTodos();
    Optional<Dto> buscarPorID(Long id);
    Set<Dto> filtrar(String campo, Object valor);
    Set<Dto> ordenar(String campo, boolean ascendente);

    // Escritura
    boolean cargar(FormDto createItemDTO);
    boolean actualizar(Long id, FormDto formItemDTO);
    boolean eliminar(Long id);
}
