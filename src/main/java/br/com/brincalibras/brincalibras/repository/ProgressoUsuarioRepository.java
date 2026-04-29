package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.ProgressoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressoUsuarioRepository extends JpaRepository<ProgressoUsuario, Long> {
    List<ProgressoUsuario> findByUserId(Long userId);
    Optional<ProgressoUsuario> findByUserIdAndLicaoId(Long userId, Long licaoId);
    List<ProgressoUsuario> findByUserIdIn(List<Long> userIds);
}