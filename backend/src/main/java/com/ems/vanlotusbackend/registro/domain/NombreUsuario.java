package com.ems.vanlotusbackend.registro.domain;

import java.util.regex.Pattern;

/**
 * Value Object: nombre con el que una persona se identifica en Van Lotus.
 * Inmutable, se compara por valor y no puede existir en un estado inválido.
 */
public record NombreUsuario(String valor) {

    private static final int LONGITUD_MINIMA = 3;
    private static final int LONGITUD_MAXIMA = 50;
    private static final Pattern CARACTERES_PERMITIDOS = Pattern.compile("^[A-Za-z0-9._@-]+$");

    public NombreUsuario {
        valor = valor == null ? "" : valor.trim();
        if (valor.length() < LONGITUD_MINIMA || valor.length() > LONGITUD_MAXIMA) {
            throw new NombreUsuarioInvalidoException(
                    "El nombre de usuario debe tener entre " + LONGITUD_MINIMA + " y " + LONGITUD_MAXIMA + " caracteres");
        }
        if (!CARACTERES_PERMITIDOS.matcher(valor).matches()) {
            throw new NombreUsuarioInvalidoException(
                    "El nombre de usuario solo puede contener letras, números y los caracteres . _ - @");
        }
    }
}
