
package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.pessoa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.pessoa.entity.EnderecoJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;
import org.springframework.stereotype.Component;


@Component
public class EnderecoJpaMapper {
    public EnderecoJpaEntity toJpaEmbeddable(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        return new EnderecoJpaEntity(
                endereco.getLogradouro(),
                endereco.getNumeroEndereco(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep()
        );
    }

    public Endereco toDomainObject(EnderecoJpaEntity enderecoJpa) {
        if (enderecoJpa == null || enderecoJpa.getLogradouro() == null) {
            return null;
        }

        return Endereco.criarCompleto(
                enderecoJpa.getLogradouro(),
                enderecoJpa.getNumeroEndereco(),
                enderecoJpa.getComplemento(),
                enderecoJpa.getBairro(),
                enderecoJpa.getCidade(),
                enderecoJpa.getEstado(),
                enderecoJpa.getCep()
        );
    }

    public void updateJpaEmbeddable(EnderecoJpaEntity target, Endereco source) {
        if (target == null || source == null) {
            return;
        }

        target.setLogradouro(source.getLogradouro());
        target.setNumeroEndereco(source.getNumeroEndereco());
        target.setComplemento(source.getComplemento());
        target.setBairro(source.getBairro());
        target.setCidade(source.getCidade());
        target.setEstado(source.getEstado());
        target.setCep(source.getCep());
    }
}
