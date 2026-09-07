package com.owo.TP_prg3.Excepciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public final class ValidacionGeneral {

    private static final Pattern PATTERN_DIGITS = Pattern.compile(".*\\d.*");
    private static final Pattern PATTERN_SOLO_LETRAS = Pattern.compile("^[\\p{L} ]+$");
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private ValidacionGeneral() {
    }

    public static void validarIdValido(Long id) {
        if (id == null || id <= 0) throw new IngresoInvalidoException("ID", "debe ser un número positivo");
    }
    
    public static void mayorACero(Number valor, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.doubleValue() <= 0) throw new IngresoInvalidoException(campo, "debe ser un número positivo (mayor a cero)");
    }
      public static void mayorACero(BigDecimal valor, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new IngresoInvalidoException(campo, "debe ser un número positivo (mayor a cero)");
    }

    public static void noNegativo(Number valor, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.doubleValue() < 0) throw new IngresoInvalidoException(campo, "no puede ser un número negativo");
    }
    public static void noNegativo(BigDecimal valor, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new IngresoInvalidoException(campo, "no puede ser un número negativo");
    }

    public static void enRango(Number valor, Number min, Number max, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        double dValor = valor.doubleValue();
        double dMin = min.doubleValue();
        double dMax = max.doubleValue();

        if (dValor < dMin || dValor > dMax) {
            throw new IngresoInvalidoException(campo, String.format("debe estar entre %s y %s", min, max));
        }
    }

    public static void mayorQue(Number valor, Number limite, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.doubleValue() <= limite.doubleValue()) {
            throw new IngresoInvalidoException(campo, String.format("debe ser estrictamente mayor que %s", limite));
        }
    }
    
    public static void menorQue(Number valor, Number limite, String campo) {
        if (valor == null) throw new IngresoInvalidoException(campo, "no puede ser nulo");
        if (valor.doubleValue() >= limite.doubleValue()) {
            throw new IngresoInvalidoException(campo, String.format("debe ser estrictamente menor que %s", limite));
        }
    }

    public static void sinNumeros(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) return; 
        if (PATTERN_DIGITS.matcher(valor).matches()) {
            throw new IngresoInvalidoException(campo, "no debe contener números");
        }
    }

    public static void validarStringNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) throw new CampoRequeridoException(campo);
    }

    public static void validarDni(Long dni) {
        if (dni == null) throw new CampoRequeridoException("DNI");
        mayorACero(dni, "DNI");
        // Asumiendo un rango típico de DNI de 7 a 9 dígitos
        if (dni < 100000 || dni > 999999999) {
            throw new IngresoInvalidoException("DNI", "debe ser un número válido (e.g., entre 6 y 9 dígitos)");
        }
    }

    public static void validarEmailFormat(String email, String campo) {
        validarStringNoVacio(email, campo);
        if (!Pattern.matches(EMAIL_REGEX, email.trim())) {
            throw new IngresoInvalidoException(campo, "no tiene un formato de correo electrónico válido");
        }
    }
    
    public static void validarContrasenia(String contrasenia, String campo) {
        validarStringNoVacio(contrasenia, campo);
        if (contrasenia.length() < 1) {
            throw new IngresoInvalidoException(campo, "debe tener al menos 1 caracter");
        }
    }

    public static void validarFechaNac(LocalDate fechaNac, String campo) {
        if (fechaNac == null) throw new CampoRequeridoException(campo);
        int edad = LocalDate.now().getYear() - fechaNac.getYear();

        if (edad > 120 || edad < 18) {
            throw new IngresoInvalidoException(campo, "la edad debe ser mayor a 18 o menos a 120 años");
        }
    }

    public static void validarEdad(LocalDate fechaNac, int edadMinima, String campo) {
        if (fechaNac == null) throw new CampoRequeridoException(campo);
        long edad = ChronoUnit.YEARS.between(fechaNac, LocalDate.now());

        if (edad < edadMinima || edad > 120) {
            throw new IngresoInvalidoException(
                campo,
                "la edad debe ser mayor o igual a " + edadMinima + " y menor o igual a 120 años");
        }
    }

    public static void soloLetras(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) return;
        if (!PATTERN_SOLO_LETRAS.matcher(valor).matches()) {
            throw new IngresoInvalidoException(campo, "solo debe contener letras, sin números ni caracteres especiales");
        }
    }
}