package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoAnexo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "anexos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnexoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeOriginal;
    private String nomeArquivo;
    private String contentType;
    private Long tamanho;
    private String caminho;

    @Enumerated(EnumType.STRING)
    private TipoAnexo tipo;

    private LocalDateTime dataUpload;
    private String hashArquivo;

    @ManyToOne
    @JoinColumn(name = "comprovante_fiscal_id")
    private ComprovanteFiscalJpaEntity comprovanteFiscal;

    @Embedded
    private AuditoriaJpa auditoria;
}