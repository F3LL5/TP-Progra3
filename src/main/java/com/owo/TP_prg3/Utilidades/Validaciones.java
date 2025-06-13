package com.owo.TP_prg3.Utilidades;

import java.util.function.Predicate;

public abstract class Validaciones {
    public static Predicate<Integer> isEnteroPositivo = n -> n > 0;
}
