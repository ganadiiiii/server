package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.BouquetGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BouquetGenerationRepository extends JpaRepository<BouquetGeneration, Long> {
    List<BouquetGeneration> findByBouquetId(Long bouquetId);
}


