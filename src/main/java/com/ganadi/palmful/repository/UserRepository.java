package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 존재 여부 확인
     * @param email 확인할 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * Provider와 ProviderId로 사용자 조회 (소셜 로그인용)
     * @param provider 로그인 제공자
     * @param providerId 제공자 ID
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    
    /**
     * 친구 검색용: ID 목록에 포함되고 이름이나 이메일로 검색
     * @param ids 검색할 사용자 ID 목록
     * @param firstName 이름 검색어
     * @param lastName 성 검색어
     * @param email 이메일 검색어
     * @return 검색된 사용자 목록
     */
    List<User> findByIdInAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            Set<Long> ids, String firstName, String lastName, String email);
}
