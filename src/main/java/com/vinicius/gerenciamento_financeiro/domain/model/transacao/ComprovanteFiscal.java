package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoComprovante;
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
public class ComprovanteFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Transacao transacao;

    private TipoComprovante tipo;
    private String numeroDocumento;
    private String hash;

    @OneToMany(mappedBy = "comprovanteFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anexo> anexos;

    @Embedded
    private Auditoria auditoria;
}
