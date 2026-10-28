package com.ems.vanlotusbackend.registro.domain;

/**
 * Repositorio (puerto) del agregado CuentaUsuario.
 * El dominio define QUÉ necesita; la infraestructura decide CÓMO (JPA, memoria, etc.).
 */
public interface RepositorioCuentas {

    boolean existe(NombreUsuario nombreUsuario);

    long contar();

    void guardar(CuentaUsuario cuenta);
}
