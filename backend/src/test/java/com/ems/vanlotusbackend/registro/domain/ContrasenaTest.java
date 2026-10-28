package com.ems.vanlotusbackend.registro.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD — Value Object Contrasena (política de contraseñas segura).
 */
class ContrasenaTest {

    @Test
    @DisplayName("Acepta una contraseña de 8+ caracteres con letras y números")
    void aceptaContrasenaSegura() {
        Contrasena contrasena = new Contrasena("Joyas2026");

        assertThat(contrasena.valor()).isEqualTo("Joyas2026");
    }

    @Test
    @DisplayName("Rechaza contraseñas con menos de 8 caracteres")
    void rechazaContrasenaCorta() {
        assertThatThrownBy(() -> new Contrasena("abc12"))
                .isInstanceOf(ContrasenaInseguraException.class)
                .hasMessage("La contraseña debe tener al menos 8 caracteres");
    }

    @ParameterizedTest
    @ValueSource(strings = {"solamenteletras", "12345678"})
    @DisplayName("Rechaza contraseñas sin letras o sin números")
    void rechazaContrasenaSinLetrasONumeros(String insegura) {
        assertThatThrownBy(() -> new Contrasena(insegura))
                .isInstanceOf(ContrasenaInseguraException.class)
                .hasMessage("La contraseña debe contener al menos una letra y un número");
    }

    @Test
    @DisplayName("Rechaza una contraseña nula")
    void rechazaContrasenaNula() {
        assertThatThrownBy(() -> new Contrasena(null))
                .isInstanceOf(ContrasenaInseguraException.class);
    }

    @Test
    @DisplayName("toString no expone la contraseña en logs")
    void toStringNoExponeElValor() {
        assertThat(new Contrasena("Joyas2026").toString()).doesNotContain("Joyas2026");
    }
}
