package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.Licao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicaoRepository extends JpaRepository<Licao, Long> {
    List<Licao> findByUnidadeIdOrderByOrdemAsc(Long unidadeId);
    boolean existsByUnidadeId(Long unidadeId);
}
