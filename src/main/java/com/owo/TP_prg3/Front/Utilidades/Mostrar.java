package com.owo.TP_prg3.Front.Utilidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public abstract class Mostrar {
    public static <T> void mostrarArray(ArrayList<T> arrayList){
        StringBuilder s = new StringBuilder();
        arrayList.forEach(t -> s.append(t.toString()).append(",\n"));
        System.out.println(s);
    }
}
