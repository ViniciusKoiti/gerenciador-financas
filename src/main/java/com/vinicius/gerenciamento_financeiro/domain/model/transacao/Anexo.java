package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoAnexo;
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
public class Anexo {
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
    private ComprovanteFiscal comprovanteFiscal;

    @Embedded
    private Auditoria auditoria;
}