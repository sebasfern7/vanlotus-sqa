package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByClienteId(Integer clienteId);

    @Query("""
            SELECT v FROM Venta v LEFT JOIN FETCH v.cliente c
            WHERE (:canal IS NULL OR v.canalVenta = :canal)
            AND (:genero IS NULL OR c.genero = :genero)
            AND v.fechaPedido >= :fechaDesde
            AND v.fechaPedido < :fechaHasta
            AND (:fechaNacimientoMax IS NULL OR c.fechaNacimiento <= :fechaNacimientoMax)
            AND (:fechaNacimientoMin IS NULL OR c.fechaNacimiento >= :fechaNacimientoMin)
            """)
    List<Venta> findForReporte(
            @Param("canal") String canal,
            @Param("genero") String genero,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            @Param("fechaNacimientoMin") LocalDate fechaNacimientoMin,
            @Param("fechaNacimientoMax") LocalDate fechaNacimientoMax);
}
