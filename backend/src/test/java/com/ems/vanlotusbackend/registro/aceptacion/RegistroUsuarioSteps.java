package com.ems.vanlotusbackend.registro.aceptacion;

import com.ems.vanlotusbackend.registro.application.RegistrarUsuario;
import com.ems.vanlotusbackend.registro.domain.CuentaUsuario;
import com.ems.vanlotusbackend.registro.infrastructure.CifradorContrasenasBCrypt;
import com.ems.vanlotusbackend.registro.support.RepositorioCuentasEnMemoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Definición de pasos (glue) que conecta el lenguaje del negocio (Gherkin)
 * con el caso de uso real RegistrarUsuario.
 * Se usa el cifrador BCrypt real y un repositorio en memoria (sin base de datos).
 */
public class RegistroUsuarioSteps {

    private final RepositorioCuentasEnMemoria repositorio = new RepositorioCuentasEnMemoria();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private boolean registroPublicoHabilitado;
    private RuntimeException errorDelRegistro;

    private RegistrarUsuario registrarUsuario() {
        return new RegistrarUsuario(
                repositorio, new CifradorContrasenasBCrypt(passwordEncoder), registroPublicoHabilitado);
    }

    @Dado("que el registro público está habilitado")
    public void registroPublicoHabilitado() {
        registroPublicoHabilitado = true;
    }

    @Dado("que ya existe la cuenta {string}")
    public void yaExisteLaCuenta(String nombreUsuario) {
        registrarUsuario().ejecutar(nombreUsuario, "Existente2026");
    }

    @Cuando("una persona se registra con el usuario {string} y la contraseña {string}")
    public void unaPersonaSeRegistra(String nombreUsuario, String contrasena) {
        try {
            registrarUsuario().ejecutar(nombreUsuario, contrasena);
        } catch (IllegalArgumentException | IllegalStateException e) {
            errorDelRegistro = e;
        }
    }

    @Entonces("la cuenta {string} queda registrada con el rol {string}")
    public void laCuentaQuedaRegistrada(String nombreUsuario, String rol) {
        assertThat(errorDelRegistro).isNull();
        CuentaUsuario cuenta = repositorio.buscar(nombreUsuario).orElseThrow();
        assertThat(cuenta.rol().name()).isEqualTo(rol);
    }

    @Y("la contraseña de {string} no se guarda en texto plano")
    public void laContrasenaNoSeGuardaEnTextoPlano(String nombreUsuario) {
        String guardada = repositorio.buscar(nombreUsuario).orElseThrow().contrasenaCifrada().valor();
        assertThat(guardada).isNotEqualTo("Joyas2026");
        assertThat(passwordEncoder.matches("Joyas2026", guardada)).isTrue();
    }

    @Entonces("el registro es rechazado con el mensaje {string}")
    public void elRegistroEsRechazado(String mensaje) {
        assertThat(errorDelRegistro).isNotNull();
        assertThat(errorDelRegistro.getMessage()).isEqualTo(mensaje);
    }

    @Y("en el sistema existe una sola cuenta {string}")
    public void existeUnaSolaCuenta(String nombreUsuario) {
        assertThat(repositorio.contarCon(nombreUsuario)).isEqualTo(1);
    }

    @Y("la cuenta {string} no queda registrada")
    public void laCuentaNoQuedaRegistrada(String nombreUsuario) {
        assertThat(repositorio.buscar(nombreUsuario)).isEmpty();
    }
}
