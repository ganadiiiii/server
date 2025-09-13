package com.ganadi.palmful.repository;

import com.ganadi.palmful.entity.Friendship;
import com.ganadi.palmful.entity.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    List<Friendship> findByUser_Id(Long userId);
    List<Friendship> findByFriend_Id(Long friendId);
    boolean existsByUser_IdAndFriend_Id(Long userId, Long friendId);
    void deleteByUser_IdAndFriend_Id(Long userId, Long friendId);
}


