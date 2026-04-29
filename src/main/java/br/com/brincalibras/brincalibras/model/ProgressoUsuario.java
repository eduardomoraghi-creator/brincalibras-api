package br.com.brincalibras.brincalibras.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "progresso_usuario",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "licao_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean concluido;

    @Column(nullable = false)
    private Integer pontuacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "licao_id", nullable = false)
    private Licao licao;
}
