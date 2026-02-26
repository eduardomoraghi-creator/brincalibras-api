package br.com.brincalibras.brincalibras.repository;

import br.com.brincalibras.brincalibras.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório: camada de acesso ao banco.
 * Spring Data cria as queries pelo nome do método.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Ajuda no update: verificar se já existe outro usuário com o mesmo email
    boolean existsByEmailAndIdNot(String email, Long id);
}
