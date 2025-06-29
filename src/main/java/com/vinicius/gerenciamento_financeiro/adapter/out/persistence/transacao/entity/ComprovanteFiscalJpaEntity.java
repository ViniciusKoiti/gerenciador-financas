package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoComprovante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "comprovante_fiscais")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprovanteFiscalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private TransacaoJpaEntity transacao;

    private TipoComprovante tipo;
    private String numeroDocumento;
    private String hash;

    @OneToMany(mappedBy = "comprovanteFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoJpaEntity> anexos;

    @Embedded
    private AuditoriaJpa auditoria;
}
