package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByRequester_IdAndAddressee_IdAndStatusIn(Long requesterId, Long addresseeId, Collection<String> statuses);
    List<FriendRequest> findByAddressee_IdAndStatusOrderByCreatedAtDesc(Long addresseeId, String status);
    List<FriendRequest> findByRequester_IdOrderByCreatedAtDesc(Long requesterId);
}


