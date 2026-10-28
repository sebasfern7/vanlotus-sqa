package com.ems.vanlotusbackend.registro.domain;

/**
 * Regla de negocio violada durante el registro (se traduce a HTTP 400).
 */
public class ContrasenaInseguraException extends IllegalArgumentException {

    public ContrasenaInseguraException(String mensaje) {
        super(mensaje);
    }
}
