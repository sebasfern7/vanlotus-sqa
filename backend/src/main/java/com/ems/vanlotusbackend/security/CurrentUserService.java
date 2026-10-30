package com.ems.vanlotusbackend.security;

import com.ems.vanlotusbackend.model.Usuario;
import com.ems.vanlotusbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + auth.getName()));
    }

    public Usuario getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (IllegalStateException ex) {
            return null;
        }
    }
}
