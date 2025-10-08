package ganadii.hanjum.repository;

import ganadii.hanjum.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@SuppressWarnings("unused")
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUserId(UUID userId);

    @Query("select u from User u where u.userId <> :userId and (lower(u.email) like lower(concat('%', :q, '%')) or lower(concat(u.firstName, ' ', u.lastName)) like lower(concat('%', :q, '%'))) order by u.lastName asc, u.firstName asc")
    Page<User> searchAll(@Param("userId") UUID userId, @Param("q") String q, Pageable pageable);

    @Query("select u from User u where u.userId <> :userId and (lower(u.email) like lower(concat('%', :q, '%')) or lower(concat(u.firstName, ' ', u.lastName)) like lower(concat('%', :q, '%'))) order by case when u.userId in :friendIds then 0 else 1 end, u.lastName asc, u.firstName asc")
    Page<User> searchAllOrderFriendFirst(@Param("userId") UUID userId, @Param("q") String q, @Param("friendIds") java.util.List<UUID> friendIds, Pageable pageable);

    @Query("SELECT u FROM User u " +
           "WHERE u.userId IN :friendIds OR u.userId IN :pendingRequestSenderIds " +
           "ORDER BY CASE WHEN u.userId IN :pendingRequestSenderIds THEN 0 ELSE 1 END, " +
           "u.lastName ASC, u.firstName ASC")
    Page<User> findFriendsAndRequestersSorted(
            @Param("friendIds") Collection<UUID> friendIds,
            @Param("pendingRequestSenderIds") Set<UUID> pendingRequestSenderIds,
            Pageable pageable
    );

    @Query("SELECT u FROM User u " +
           "WHERE u.userId <> :userId AND " +
           "(LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY CASE " +
           "    WHEN u.userId IN :friendIds THEN 0 " +
           "    WHEN u.userId IN :pendingReceiverIds THEN 1 " +
           "    ELSE 2 " +
           "END, u.lastName ASC, u.firstName ASC")
    Page<User> searchUsersWithCustomSort(
            @Param("userId") UUID userId,
            @Param("q") String q,
            @Param("friendIds") Set<UUID> friendIds,
            @Param("pendingReceiverIds") Set<UUID> pendingReceiverIds,
            Pageable pageable
    );
}
