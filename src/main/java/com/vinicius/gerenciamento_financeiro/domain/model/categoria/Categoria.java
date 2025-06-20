package com.vinicius.gerenciamento_financeiro.domain.model.categoria;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.util.List;

@Entity
@Table(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
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

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
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


    public Categoria(Long id,String nome, String descricao, String icone, Usuario usuario){
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo ao criar uma Categoria");
        }
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.icone = icone;
        this.auditoria = new Auditoria();
        this.usuario = usuario;
    }
    public Categoria(String nome, String descricao, String icone, Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo ao criar uma Categoria");
        }
        this.id = null;
        this.nome = nome;
        this.descricao = descricao;
        this.icone = icone;
        this.auditoria = new Auditoria();
        this.usuario = usuario;
    }

}