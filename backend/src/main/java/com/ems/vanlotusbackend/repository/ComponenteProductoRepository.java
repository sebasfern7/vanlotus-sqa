package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.ComponenteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponenteProductoRepository extends JpaRepository<ComponenteProducto, Integer> {

    List<ComponenteProducto> findByVarianteId(Integer varianteId);
}
