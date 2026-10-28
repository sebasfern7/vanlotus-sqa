package com.ems.vanlotusbackend.registro.domain;

/**
 * Servicio de dominio (puerto) para cifrar contraseñas antes de guardarlas.
 */
@FunctionalInterface
public interface CifradorContrasenas {

    ContrasenaCifrada cifrar(Contrasena contrasena);
}
