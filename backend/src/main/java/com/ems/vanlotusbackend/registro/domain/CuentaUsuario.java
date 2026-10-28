package com.ems.vanlotusbackend.registro.domain;

/**
 * Entidad raíz del agregado "Cuenta de usuario" en el contexto de Registro.
 * Se identifica por su nombre de usuario, que es único en el sistema.
 */
public record CuentaUsuario(NombreUsuario nombreUsuario, ContrasenaCifrada contrasenaCifrada, Rol rol) {

    /**
     * Regla de negocio: la primera cuenta del sistema es la de la dueña (ADMIN);
     * todas las siguientes nacen como TRABAJADOR.
     */
    public static CuentaUsuario nueva(NombreUsuario nombreUsuario, ContrasenaCifrada contrasenaCifrada,
                                      boolean esPrimeraDelSistema) {
        return new CuentaUsuario(nombreUsuario, contrasenaCifrada, esPrimeraDelSistema ? Rol.ADMIN : Rol.TRABAJADOR);
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }
}
