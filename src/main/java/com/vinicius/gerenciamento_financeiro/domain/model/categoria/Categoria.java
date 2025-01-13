package com.vinicius.gerenciamento_financeiro.domain.model.categoria;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private boolean ativa = true;

    private String icone;

    @OneToMany(mappedBy = "categoria")
    private List<Transacao> transacoes;

    @ManyToOne
    @JoinColumn(name = "categoria_pai_id")
    @JsonBackReference
    private Categoria categoriaPai;

    @OneToMany(mappedBy = "categoriaPai")
    @JsonManagedReference
    private List<Categoria> subcategorias;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Embedded
    private Auditoria auditoria;


}