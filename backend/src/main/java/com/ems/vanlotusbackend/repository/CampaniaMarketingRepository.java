package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.CampaniaMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaniaMarketingRepository extends JpaRepository<CampaniaMarketing, Integer> {
}
