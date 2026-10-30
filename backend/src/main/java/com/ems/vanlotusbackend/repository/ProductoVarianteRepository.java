package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.ProductoVariante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoVarianteRepository extends JpaRepository<ProductoVariante, Integer> {

    /**
     * Busca todas las variantes que pertenecen a un producto específico.
     */
    List<ProductoVariante> findByProductoId(Integer productoId);
}
