package br.com.brincalibras.brincalibras.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "top_gamers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopGamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pontuacao_total", nullable = false)
    @Builder.Default
    private Integer pontuacaoTotal = 0;

    @Column(name = "melhor_pontuacao", nullable = false)
    @Builder.Default
    private Integer melhorPontuacao = 0;

    @Column(name = "total_partidas", nullable = false)
    @Builder.Default
    private Integer totalPartidas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer forca = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer memoria = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer pares = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer soletrando = 0;

    @Column(name = "montar_sinal_libras", nullable = false)
    @Builder.Default
    private Integer montarSinalLibras = 0;

    @Column(name = "ultimo_jogo")
    private String ultimoJogo;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}