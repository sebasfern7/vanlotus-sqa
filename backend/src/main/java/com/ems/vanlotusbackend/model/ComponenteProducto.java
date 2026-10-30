package com.ems.vanlotusbackend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "componentes_producto")
public class ComponenteProducto {

    public ComponenteProducto() {
    }

    public ComponenteProducto(ProductoVariante variante, Insumo insumo, BigDecimal cantidadUtilizada) {
        this.variante = variante;
        this.insumo = insumo;
        this.cantidadUtilizada = cantidadUtilizada;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private ProductoVariante variante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Insumo insumo;

    @Column(name = "cantidad_utilizada", nullable = false, precision = 14, scale = 3)
    private BigDecimal cantidadUtilizada;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductoVariante getVariante() {
        return variante;
    }

    public void setVariante(ProductoVariante variante) {
        this.variante = variante;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(BigDecimal cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
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
