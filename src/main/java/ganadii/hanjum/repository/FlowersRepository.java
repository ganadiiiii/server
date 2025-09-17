package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Flowers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowersRepository extends JpaRepository<Flowers, Long> {
    List<Flowers> findByKoreanNameContainingIgnoreCase(String keyword);
    List<Flowers> findByEnglishNameContainingIgnoreCase(String keyword);
}

