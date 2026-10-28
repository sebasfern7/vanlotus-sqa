package com.ems.vanlotusbackend.registro.infrastructure;

import com.ems.vanlotusbackend.registro.domain.CifradorContrasenas;
import com.ems.vanlotusbackend.registro.domain.Contrasena;
import com.ems.vanlotusbackend.registro.domain.ContrasenaCifrada;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador: implementa el cifrado del dominio con el PasswordEncoder (BCrypt) de Spring Security.
 */
@Component
public class CifradorContrasenasBCrypt implements CifradorContrasenas {

    private final PasswordEncoder passwordEncoder;

    public CifradorContrasenasBCrypt(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ContrasenaCifrada cifrar(Contrasena contrasena) {
        return new ContrasenaCifrada(passwordEncoder.encode(contrasena.valor()));
    }
}
