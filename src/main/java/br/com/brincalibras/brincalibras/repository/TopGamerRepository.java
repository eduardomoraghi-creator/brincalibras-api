package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.TopGamer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopGamerRepository extends JpaRepository<TopGamer, Long> {

    Optional<TopGamer> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tg from TopGamer tg where tg.user.id = :userId")
    Optional<TopGamer> findByUserIdForUpdate(@Param("userId") Long userId);

    List<TopGamer> findTop10ByOrderByPontuacaoTotalDescMelhorPontuacaoDescTotalPartidasAscIdAsc();
}