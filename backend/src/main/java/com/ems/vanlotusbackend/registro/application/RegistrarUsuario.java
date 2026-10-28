package com.ems.vanlotusbackend.registro.application;

import com.ems.vanlotusbackend.registro.domain.CifradorContrasenas;
import com.ems.vanlotusbackend.registro.domain.Contrasena;
import com.ems.vanlotusbackend.registro.domain.CuentaUsuario;
import com.ems.vanlotusbackend.registro.domain.NombreUsuario;
import com.ems.vanlotusbackend.registro.domain.RegistroDeshabilitadoException;
import com.ems.vanlotusbackend.registro.domain.RepositorioCuentas;
import com.ems.vanlotusbackend.registro.domain.UsuarioYaRegistradoException;

/**
 * Caso de uso (servicio de aplicación): registrar una cuenta de usuario.
 * Orquesta el dominio; las reglas viven en los Value Objects y en CuentaUsuario.
 */
public class RegistrarUsuario {

    private final RepositorioCuentas repositorio;
    private final CifradorContrasenas cifrador;
    private final boolean registroPublicoHabilitado;

    public RegistrarUsuario(RepositorioCuentas repositorio, CifradorContrasenas cifrador,
                            boolean registroPublicoHabilitado) {
        this.repositorio = repositorio;
        this.cifrador = cifrador;
        this.registroPublicoHabilitado = registroPublicoHabilitado;
    }

    public CuentaUsuario ejecutar(String nombreUsuario, String contrasena) {
        long cuentasExistentes = repositorio.contar();
        if (cuentasExistentes > 0 && !registroPublicoHabilitado) {
            throw new RegistroDeshabilitadoException();
        }

        NombreUsuario nombre = new NombreUsuario(nombreUsuario);
        Contrasena contrasenaSegura = new Contrasena(contrasena);
        if (repositorio.existe(nombre)) {
            throw new UsuarioYaRegistradoException("El nombre de usuario ya está en uso");
        }

        CuentaUsuario cuenta = CuentaUsuario.nueva(
                nombre, cifrador.cifrar(contrasenaSegura), cuentasExistentes == 0);
        repositorio.guardar(cuenta);
        return cuenta;
    }
}
