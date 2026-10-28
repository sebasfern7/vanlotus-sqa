package com.ems.vanlotusbackend.registro.support;

import com.ems.vanlotusbackend.registro.domain.CuentaUsuario;
import com.ems.vanlotusbackend.registro.domain.NombreUsuario;
import com.ems.vanlotusbackend.registro.domain.RepositorioCuentas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Doble de prueba (fake) del repositorio: guarda las cuentas en una lista.
 * Permite probar el dominio sin base de datos (pruebas rápidas y aisladas).
 */
public class RepositorioCuentasEnMemoria implements RepositorioCuentas {

    private final List<CuentaUsuario> cuentas = new ArrayList<>();

    @Override
    public boolean existe(NombreUsuario nombreUsuario) {
        return cuentas.stream().anyMatch(c -> c.nombreUsuario().equals(nombreUsuario));
    }

    @Override
    public long contar() {
        return cuentas.size();
    }

    @Override
    public void guardar(CuentaUsuario cuenta) {
        cuentas.add(cuenta);
    }

    public Optional<CuentaUsuario> buscar(String nombreUsuario) {
        return cuentas.stream()
                .filter(c -> c.nombreUsuario().valor().equals(nombreUsuario))
                .findFirst();
    }

    public long contarCon(String nombreUsuario) {
        return cuentas.stream()
                .filter(c -> c.nombreUsuario().valor().equals(nombreUsuario))
                .count();
    }
}
