package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Flowers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface FlowersRepository extends JpaRepository<Flowers, Long> {
    List<Flowers> findByKoreanNameContainingIgnoreCase(String keyword);
    List<Flowers> findByEnglishNameContainingIgnoreCase(String keyword);
}
