package com.ems.vanlotusbackend.registro.application;

import com.ems.vanlotusbackend.registro.domain.CifradorContrasenas;
import com.ems.vanlotusbackend.registro.domain.ContrasenaCifrada;
import com.ems.vanlotusbackend.registro.domain.CuentaUsuario;
import com.ems.vanlotusbackend.registro.domain.Rol;
import com.ems.vanlotusbackend.registro.domain.RegistroDeshabilitadoException;
import com.ems.vanlotusbackend.registro.domain.UsuarioYaRegistradoException;
import com.ems.vanlotusbackend.registro.support.RepositorioCuentasEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD — Caso de uso "Registrar usuario".
 * Cubre los criterios de aceptación CA-01 y CA-02 a nivel unitario.
 */
class RegistrarUsuarioTest {

    /** Cifrador falso: suficiente para verificar que no se guarda el texto plano. */
    private final CifradorContrasenas cifradorFalso =
            contrasena -> new ContrasenaCifrada("{hash}" + contrasena.valor().hashCode());

    private RepositorioCuentasEnMemoria repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioCuentasEnMemoria();
    }

    private RegistrarUsuario casoDeUso(boolean registroPublicoHabilitado) {
        return new RegistrarUsuario(repositorio, cifradorFalso, registroPublicoHabilitado);
    }

    @Test
    @DisplayName("CA-01: registra una cuenta nueva con rol TRABAJADOR")
    void registraCuentaConRolTrabajador() {
        casoDeUso(true).ejecutar("admin", "Admin2026");

        CuentaUsuario cuenta = casoDeUso(true).ejecutar("lucia.vargas", "Joyas2026");

        assertThat(cuenta.nombreUsuario().valor()).isEqualTo("lucia.vargas");
        assertThat(cuenta.rol()).isEqualTo(Rol.TRABAJADOR);
        assertThat(repositorio.buscar("lucia.vargas")).isPresent();
    }

    @Test
    @DisplayName("CA-01: la contraseña se guarda cifrada, nunca en texto plano")
    void guardaLaContrasenaCifrada() {
        CuentaUsuario cuenta = casoDeUso(true).ejecutar("lucia.vargas", "Joyas2026");

        assertThat(cuenta.contrasenaCifrada().valor()).isNotEqualTo("Joyas2026");
    }

    @Test
    @DisplayName("La primera cuenta del sistema se crea como ADMIN")
    void primeraCuentaEsAdmin() {
        CuentaUsuario cuenta = casoDeUso(false).ejecutar("duena", "Lotus2026");

        assertThat(cuenta.rol()).isEqualTo(Rol.ADMIN);
    }

    @Test
    @DisplayName("CA-02: rechaza un nombre de usuario ya registrado y no crea otra cuenta")
    void rechazaUsuarioDuplicado() {
        casoDeUso(true).ejecutar("lucia.vargas", "Joyas2026");

        assertThatThrownBy(() -> casoDeUso(true).ejecutar("lucia.vargas", "OtraClave99"))
                .isInstanceOf(UsuarioYaRegistradoException.class)
                .hasMessage("El nombre de usuario ya está en uso");
        assertThat(repositorio.contarCon("lucia.vargas")).isEqualTo(1);
    }

    @Test
    @DisplayName("Rechaza el registro si ya hay cuentas y el registro público está deshabilitado")
    void rechazaCuandoRegistroDeshabilitado() {
        casoDeUso(false).ejecutar("duena", "Lotus2026");

        assertThatThrownBy(() -> casoDeUso(false).ejecutar("lucia.vargas", "Joyas2026"))
                .isInstanceOf(RegistroDeshabilitadoException.class);
        assertThat(repositorio.contar()).isEqualTo(1);
    }
}
