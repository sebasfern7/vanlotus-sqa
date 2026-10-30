package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.MovimientoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoProductoRepository extends JpaRepository<MovimientoProducto, Integer> {

    List<MovimientoProducto> findByVarianteIdOrderByCreatedAtDesc(Integer varianteId);
}
