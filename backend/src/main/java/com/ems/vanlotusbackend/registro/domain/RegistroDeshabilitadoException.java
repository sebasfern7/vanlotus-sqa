package com.ems.vanlotusbackend.registro.domain;

/**
 * El registro público está cerrado y ya existe al menos una cuenta (se traduce a HTTP 409).
 */
public class RegistroDeshabilitadoException extends IllegalStateException {

    public RegistroDeshabilitadoException() {
        super("El registro público está deshabilitado. Contacte a un administrador.");
    }
}
