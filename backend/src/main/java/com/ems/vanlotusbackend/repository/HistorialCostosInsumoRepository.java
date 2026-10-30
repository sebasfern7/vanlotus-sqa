package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.HistorialCostosInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialCostosInsumoRepository extends JpaRepository<HistorialCostosInsumo, Integer> {

    List<HistorialCostosInsumo> findByInsumoIdOrderByCreatedAtDesc(Integer insumoId);
}
