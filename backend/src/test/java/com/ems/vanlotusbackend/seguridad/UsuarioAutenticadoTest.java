package com.ems.vanlotusbackend.seguridad;

import com.ems.vanlotusbackend.model.Usuario;
import com.ems.vanlotusbackend.repository.UsuarioRepository;
import com.ems.vanlotusbackend.security.CurrentUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * HU-05: Iniciar sesión según el rol.
 * Cada venta, gasto o movimiento guarda quién lo hizo: CurrentUserService identifica al usuario de la sesión.
 * Sin sesión válida no hay operación posible.
 */
@DisplayName("HU-05 Usuario autenticado")
class UsuarioAutenticadoTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final CurrentUserService currentUser = new CurrentUserService(usuarioRepository);

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    private void iniciarSesion(String username, String rol) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol))));
    }

    @Test
    @DisplayName("Con sesión iniciada devuelve el usuario dueño de la sesión")
    void conSesionDevuelveElUsuario() {
        Usuario lucia = new Usuario();
        lucia.setUsername("lucia.vargas");
        lucia.setRol("TRABAJADOR");
        when(usuarioRepository.findByUsername("lucia.vargas")).thenReturn(Optional.of(lucia));
        iniciarSesion("lucia.vargas", "TRABAJADOR");

        assertThat(currentUser.getCurrentUser()).isSameAs(lucia);
    }

    @Test
    @DisplayName("Sin sesión no se puede operar")
    void sinSesionSeRechaza() {
        assertThatThrownBy(currentUser::getCurrentUser)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No hay usuario autenticado");
    }

    @Test
    @DisplayName("Un visitante anónimo no cuenta como usuario autenticado")
    void anonimoSeRechaza() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "clave", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThatThrownBy(currentUser::getCurrentUser)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No hay usuario autenticado");
    }

    @Test
    @DisplayName("Una sesión de un usuario que ya no existe se rechaza")
    void usuarioInexistenteSeRechaza() {
        when(usuarioRepository.findByUsername("borrado")).thenReturn(Optional.empty());
        iniciarSesion("borrado", "TRABAJADOR");

        assertThatThrownBy(currentUser::getCurrentUser)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Usuario no encontrado: borrado");
    }

    @Test
    @DisplayName("La variante opcional devuelve null en vez de fallar cuando no hay sesión")
    void varianteOpcionalSinSesion() {
        assertThat(currentUser.getCurrentUserOrNull()).isNull();
    }
}
