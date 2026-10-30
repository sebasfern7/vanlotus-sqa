package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Obtener todos los detalles de una venta
    List<DetalleVenta> findByVentaId(Integer ventaId);
}
