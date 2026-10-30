package com.ems.vanlotusbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad Usuario.
 * Implementa UserDetails para integrarse directamente con Spring Security.
 */
@Entity // Indica a JPA que esta clase mapea a una tabla
@Table(name = "usuarios") // Nombre exacto de la tabla en Postgres
public class Usuario implements UserDetails {

    public Usuario() {
    }

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL (Autoincremental en Postgres)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String rol = "TRABAJADOR";

    @CreationTimestamp // Actualizado automáticamente por Hibernate en INSERT
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Actualizado automáticamente por Hibernate en UPDATE
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Métodos de UserDetails (Spring Security) ---

    /**
     * Devuelve los roles o permisos de este usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleStr = (this.rol != null) ? this.rol.toUpperCase() : "TRABAJADOR";
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleStr));
    }

    /**
     * En este proyecto, indicaremos que las cuentas nunca expiran.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicamos que la cuenta nunca se bloquea automáticamente.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicamos que las credenciales (password) no expiran.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicamos si la cuenta está activa.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
