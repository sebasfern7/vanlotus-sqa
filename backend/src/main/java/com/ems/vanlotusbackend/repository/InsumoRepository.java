package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    @Modifying
    @Query("UPDATE Insumo i SET i.costoUnitario = :costo WHERE i.id = :id")
    void updateCostoUnitario(@Param("id") Integer id, @Param("costo") BigDecimal costo);
}
