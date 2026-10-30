package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.MovimientoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInsumoRepository extends JpaRepository<MovimientoInsumo, Integer> {

    List<MovimientoInsumo> findByInsumoIdOrderByCreatedAtDesc(Integer insumoId);
}
