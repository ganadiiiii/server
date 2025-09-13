package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.Bouquet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BouquetRepository extends JpaRepository<Bouquet, Long> {
    
    /**
     * 소유자 ID로 꽃다발 목록 조회
     * @param ownerId 소유자 ID
     * @return 꽃다발 목록
     */
    List<Bouquet> findByOwnerId(Long ownerId);
    
    /**
     * 소유자 ID와 꽃다발 ID로 조회 (소유권 확인용)
     * @param id 꽃다발 ID
     * @param ownerId 소유자 ID
     * @return 꽃다발 정보 (Optional)
     */
    Optional<Bouquet> findByIdAndOwnerId(Long id, Long ownerId);
    
    /**
     * 소유자 ID로 꽃다발 개수 조회
     * @param ownerId 소유자 ID
     * @return 꽃다발 개수
     */
    long countByOwnerId(Long ownerId);
    
    /**
     * 소유자 ID와 상태로 꽃다발 목록 조회
     * @param ownerId 소유자 ID
     * @param status 상태
     * @return 꽃다발 목록
     */
    List<Bouquet> findByOwnerIdAndStatus(Long ownerId, String status);
    
    /**
     * 소유자 ID와 상태가 아닌 꽃다발 목록 조회
     * @param ownerId 소유자 ID
     * @param status 제외할 상태
     * @return 꽃다발 목록
     */
    List<Bouquet> findByOwnerIdAndStatusNot(Long ownerId, String status);
}
