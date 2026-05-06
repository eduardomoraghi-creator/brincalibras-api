package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.TopGamer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopGamerRepository extends JpaRepository<TopGamer, Long> {

    Optional<TopGamer> findByUserId(Long userId);

    List<TopGamer> findTop10ByOrderByPontuacaoTotalDescMelhorPontuacaoDescTotalPartidasAscIdAsc();
}