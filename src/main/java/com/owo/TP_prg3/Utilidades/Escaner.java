package com.owo.TP_prg3.Utilidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public abstract class Escaner {
    //Numeros
    public static Integer enteroValido(Scanner scanner){
        //Obliga al usuario a ingresar un entero y si apreta una letra no se rompe el programa
        while(true){
            try {
                Integer n = scanner.nextInt();
                scanner.nextLine();
                return n;
            } catch (InputMismatchException e) {
                System.out.print("Solo numeros enteros: ");
                scanner.nextLine();
            }
        }
    }

    public static Double doubleValido(Scanner scanner){
        while (true){
            try {
                Double d = scanner.nextDouble();
                scanner.nextLine();
                return d;
            } catch (InputMismatchException e){
                System.out.print("Formato no valido. Ej. 1,25: ");
                scanner.nextLine();
            }
        }
    }

    //String
    public static String stringValido(Scanner scanner){
        while(true){
            String s = scanner.nextLine();
            if (!s.isEmpty()) return s;
            System.out.print("Ingrese al menos un caracter: ");
        }
    }

    public static String stringSinNumeros(Scanner scanner){
        while(true){
            String s = stringValido(scanner);
            if (s.chars().allMatch(c -> Character.isAlphabetic(c) || Character.isWhitespace(c) || Character.isSpaceChar(c))){
                return s;
            } else System.out.print("No se permiten numeros o caracteres especiales. Intente nuevamente: ");
        }
    }

    //Otros
    public static void pausa(Scanner scanner){
        System.out.print("Ingrese un caracter para continuar... ");
        scanner.nextLine();
    }

    public static LocalDate fecha(Scanner scanner){
        while(true){
            try {
                String fechaString = stringValido(scanner);
                LocalDate fecha = LocalDate.parse(fechaString);
                return fecha;
            } catch (DateTimeParseException e){
                System.out.print("Ingrese un formato valido [yyyy-MM-dd]: ");
            }
        }
    }

    public static LocalDateTime fechaYhora(Scanner scanner){
        while(true){
            try {
                LocalDate fecha = fecha(scanner);

                System.out.print("Ingrese horario [hh-MM]: ");
                int hora = enteroValido(scanner);
                int minutos = enteroValido(scanner);

                return fecha.atTime(hora,minutos);
            } catch (DateTimeParseException e){
                System.out.print("Ingrese un formato valido [yyyy-MM-dd] o [hh-MM]: ");
            }
        }
    }
}
