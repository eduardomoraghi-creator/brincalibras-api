package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByUserEmailAndCodigoAndUtilizadoFalseOrderByIdDesc(
            String email,
            String codigo
    );

    void deleteByUserId(Long userId);
}
