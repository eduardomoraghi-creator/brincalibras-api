package br.com.brincalibras.brincalibras.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "licoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Licao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 1000)
    private String conteudo;

    @Column(nullable = false)
    private Integer ordem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLicao tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;
}