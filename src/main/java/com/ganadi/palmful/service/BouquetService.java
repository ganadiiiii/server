package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.BouquetRequest;
import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.BouquetRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BouquetService {
    
    private final BouquetRepository bouquetRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public BouquetService(BouquetRepository bouquetRepository, UserRepository userRepository) {
        this.bouquetRepository = bouquetRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 꽃다발 생성
     * @param userId 생성자 ID
     * @param request 꽃다발 생성 요청
     * @return 생성된 꽃다발 정보
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 때
     */
    public BouquetResponse createBouquet(Long userId, BouquetRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        // Bouquet 엔티티 생성
        Bouquet bouquet = new Bouquet();
        bouquet.setOwner(user);
        bouquet.setTitle(request.getTitle());
        bouquet.setMood(request.getMood());
        bouquet.setOccasion(request.getOccasion());
        bouquet.setSize(request.getSize());
        bouquet.setMessage(request.getMessage());
        bouquet.setStatus("draft");
        bouquet.setPreviewUrl(null); // AI 생성 이력과 연결될 예정이므로 일단 null
        bouquet.setCreatedAt(LocalDateTime.now());
        bouquet.setUpdatedAt(LocalDateTime.now());
        
        // 데이터베이스에 저장
        Bouquet savedBouquet = bouquetRepository.save(bouquet);
        
        // BouquetResponse로 변환하여 반환
        return convertToBouquetResponse(savedBouquet);
    }
    
    /**
     * 모든 꽃다발 목록 조회
     * @return 모든 꽃다발 목록
     */
    @Transactional(readOnly = true)
    public List<BouquetResponse> getAllBouquets() {
        List<Bouquet> bouquets = bouquetRepository.findAll();
        
        return bouquets.stream()
                .map(this::convertToBouquetResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 꽃다발 단일 조회
     * @param id 꽃다발 ID
     * @return 꽃다발 정보
     * @throws IllegalArgumentException 꽃다발을 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public BouquetResponse getBouquet(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없습니다: " + id));
        
        return convertToBouquetResponse(bouquet);
    }
    
    /**
     * 특정 사용자의 꽃다발 목록 조회
     * @param userId 사용자 ID
     * @return 꽃다발 목록
     */
    @Transactional(readOnly = true)
    public List<BouquetResponse> getUserBouquets(Long userId) {
        List<Bouquet> bouquets = bouquetRepository.findByOwnerId(userId);
        
        return bouquets.stream()
                .map(this::convertToBouquetResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자의 아카이브된 꽃다발 목록 조회 (내가 만든 것 + 받은 것)
     * @param userId 사용자 ID
     * @return 아카이브된 꽃다발 목록
     */
    @Transactional(readOnly = true)
    public List<BouquetResponse> getUserArchives(Long userId) {
        // 내가 만든 아카이브된 꽃다발
        List<Bouquet> myArchivedBouquets = bouquetRepository.findByOwnerIdAndArchivedAtIsNotNull(userId);
        
        // TODO: 받은 꽃다발도 포함 (Gift 엔티티와 연동 필요)
        // 현재는 내가 만든 아카이브만 반환
        
        return myArchivedBouquets.stream()
                .map(this::convertToBouquetResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 사용자의 꽃다발 목록 조회 (상태 필터링)
     * @param userId 사용자 ID
     * @param status 필터링할 상태 ("active", "archived", "all")
     * @return 꽃다발 목록
     */
    @Transactional(readOnly = true)
    public List<BouquetResponse> getUserBouquets(Long userId, String status) {
        List<Bouquet> bouquets;
        
        switch (status != null ? status.toLowerCase() : "all") {
            case "active":
                bouquets = bouquetRepository.findByOwnerIdAndStatusNot(userId, "archived");
                break;
            case "archived":
                bouquets = bouquetRepository.findByOwnerIdAndStatus(userId, "archived");
                break;
            case "all":
            default:
                bouquets = bouquetRepository.findByOwnerId(userId);
                break;
        }
        
        return bouquets.stream()
                .map(this::convertToBouquetResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 꽃다발 수정
     * @param id 꽃다발 ID
     * @param userId 수정 요청자 ID (소유권 확인용)
     * @param request 수정 요청 정보
     * @return 수정된 꽃다발 정보
     * @throws IllegalArgumentException 꽃다발을 찾을 수 없거나 소유권이 없을 때
     */
    public BouquetResponse updateBouquet(Long id, Long userId, BouquetRequest request) {
        // 소유권 확인
        Bouquet bouquet = bouquetRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 수정 권한이 없습니다: " + id));
        
        // 정보 업데이트
        bouquet.setTitle(request.getTitle());
        bouquet.setMood(request.getMood());
        bouquet.setOccasion(request.getOccasion());
        bouquet.setSize(request.getSize());
        bouquet.setMessage(request.getMessage());
        bouquet.setUpdatedAt(LocalDateTime.now());
        
        // 데이터베이스에 저장
        Bouquet updatedBouquet = bouquetRepository.save(bouquet);
        
        return convertToBouquetResponse(updatedBouquet);
    }
    
    /**
     * 꽃다발 삭제
     * @param id 꽃다발 ID
     * @param userId 삭제 요청자 ID (소유권 확인용)
     * @throws IllegalArgumentException 꽃다발을 찾을 수 없거나 소유권이 없을 때
     */
    public void deleteBouquet(Long id, Long userId) {
        // 소유권 확인
        Bouquet bouquet = bouquetRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 삭제 권한이 없습니다: " + id));
        
        // 삭제
        bouquetRepository.delete(bouquet);
    }
    
    /**
     * 꽃다발 아카이브
     * @param id 꽃다발 ID
     * @param userId 아카이브 요청자 ID (소유권 확인용)
     * @return 아카이브된 꽃다발 정보
     * @throws IllegalArgumentException 꽃다발을 찾을 수 없거나 소유권이 없을 때
     */
    public BouquetResponse archiveBouquet(Long id, Long userId) {
        // 소유권 확인
        Bouquet bouquet = bouquetRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 아카이브 권한이 없습니다: " + id));
        
        // 아카이브 처리
        bouquet.setStatus("archived");
        bouquet.setArchivedAt(LocalDateTime.now());
        bouquet.setUpdatedAt(LocalDateTime.now());
        
        // 데이터베이스에 저장
        Bouquet archivedBouquet = bouquetRepository.save(bouquet);
        
        return convertToBouquetResponse(archivedBouquet);
    }
    
    /**
     * Bouquet 엔티티를 BouquetResponse로 변환
     * @param bouquet 변환할 Bouquet 엔티티
     * @return BouquetResponse
     */
    private BouquetResponse convertToBouquetResponse(Bouquet bouquet) {
        // UserResponse 생성
        UserResponse ownerResponse = new UserResponse(
                bouquet.getOwner().getId(),
                bouquet.getOwner().getEmail(),
                bouquet.getOwner().getFirstName(),
                bouquet.getOwner().getLastName(),
                bouquet.getOwner().getProvider(),
                bouquet.getOwner().getCreatedAt()
        );
        
        // BouquetResponse 생성
        BouquetResponse response = new BouquetResponse();
        response.setId(bouquet.getId());
        response.setOwner(ownerResponse);
        response.setTitle(bouquet.getTitle());
        response.setMood(bouquet.getMood());
        response.setOccasion(bouquet.getOccasion());
        response.setSize(bouquet.getSize());
        response.setMessage(bouquet.getMessage());
        response.setStatus(bouquet.getStatus());
        response.setPreviewUrl(bouquet.getPreviewUrl());
        response.setCreatedAt(bouquet.getCreatedAt());
        response.setUpdatedAt(bouquet.getUpdatedAt());
        response.setArchivedAt(bouquet.getArchivedAt());
        
        // TODO: BouquetFlower 목록도 추가할 예정
        response.setFlowers(null);
        
        return response;
    }
}
