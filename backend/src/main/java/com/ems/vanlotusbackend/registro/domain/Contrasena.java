package com.ems.vanlotusbackend.registro.domain;

/**
 * Value Object: contraseña en texto plano que cumple la política de seguridad.
 * Solo vive en memoria durante el registro; lo que se persiste es {@link ContrasenaCifrada}.
 */
public record Contrasena(String valor) {

    private static final int LONGITUD_MINIMA = 8;

    public Contrasena {
        if (valor == null || valor.length() < LONGITUD_MINIMA) {
            throw new ContrasenaInseguraException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres");
        }
        boolean tieneLetra = valor.chars().anyMatch(Character::isLetter);
        boolean tieneNumero = valor.chars().anyMatch(Character::isDigit);
        if (!tieneLetra || !tieneNumero) {
            throw new ContrasenaInseguraException("La contraseña debe contener al menos una letra y un número");
        }
    }

    /** Nunca exponer la contraseña en logs o mensajes de error. */
    @Override
    public String toString() {
        return "Contrasena[********]";
    }
}
