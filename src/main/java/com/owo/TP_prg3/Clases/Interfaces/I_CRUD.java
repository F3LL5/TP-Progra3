package com.owo.TP_prg3.Clases.Interfaces;

import java.util.Optional;
import java.util.Set;

public interface I_CRUD<T, Dto, FormDto, Id> {
    // Conversión
    T convertir_a_Obj(FormDto pDTO);
    Dto convertir_a_DTO(T producto);
    
    // Lectura
    Set<Dto> obtenerTodos();
    Optional<Dto> buscarPorID(Id id);
    Set<Dto> filtrar(String campo, Object valor);
    Set<Dto> ordenar(String campo, boolean ascendente);

    // Escritura
    boolean cargar(FormDto createItemDTO);
    boolean actualizar(Id id, FormDto formItemDTO);
    boolean eliminar(Id id);
}
