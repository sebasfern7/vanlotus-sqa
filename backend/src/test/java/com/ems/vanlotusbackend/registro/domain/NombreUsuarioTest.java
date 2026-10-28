package com.ems.vanlotusbackend.registro.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD — Value Object NombreUsuario.
 * Estas pruebas se escribieron ANTES que la clase (fase ROJA).
 */
class NombreUsuarioTest {

    @Test
    @DisplayName("Acepta un nombre válido y elimina espacios al inicio y al final")
    void aceptaNombreValidoYRecortaEspacios() {
        NombreUsuario nombre = new NombreUsuario("  lucia.vargas  ");

        assertThat(nombre.valor()).isEqualTo("lucia.vargas");
    }

    @Test
    @DisplayName("Rechaza un nombre con menos de 3 caracteres")
    void rechazaNombreMuyCorto() {
        assertThatThrownBy(() -> new NombreUsuario("ab"))
                .isInstanceOf(NombreUsuarioInvalidoException.class)
                .hasMessage("El nombre de usuario debe tener entre 3 y 50 caracteres");
    }

    @Test
    @DisplayName("Rechaza un nombre vacío o nulo")
    void rechazaNombreVacio() {
        assertThatThrownBy(() -> new NombreUsuario(null))
                .isInstanceOf(NombreUsuarioInvalidoException.class);
        assertThatThrownBy(() -> new NombreUsuario("   "))
                .isInstanceOf(NombreUsuarioInvalidoException.class);
    }

    @Test
    @DisplayName("Rechaza espacios internos o caracteres no permitidos")
    void rechazaCaracteresNoPermitidos() {
        assertThatThrownBy(() -> new NombreUsuario("lucia vargas"))
                .isInstanceOf(NombreUsuarioInvalidoException.class)
                .hasMessage("El nombre de usuario solo puede contener letras, números y los caracteres . _ - @");
    }

    @Test
    @DisplayName("Dos nombres con el mismo valor son iguales (igualdad por valor)")
    void igualdadPorValor() {
        assertThat(new NombreUsuario("ana")).isEqualTo(new NombreUsuario(" ana "));
    }
}
