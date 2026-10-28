package com.ems.vanlotusbackend.registro.domain;

/**
 * Regla de negocio violada durante el registro (se traduce a HTTP 400).
 */
public class UsuarioYaRegistradoException extends IllegalArgumentException {

    public UsuarioYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
