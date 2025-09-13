package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByReceiver_IdOrderBySentAtDesc(Long receiverId);
}



