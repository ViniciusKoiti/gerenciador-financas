package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.PixInfoJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.pessoa.EnderecoJpaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.PixInfo;
import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Cpf;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.PessoaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteJpaMapper {

    private final EnderecoJpaMapper enderecoJpaMapper;

    public ClienteJpaEntity toJpaEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteJpaEntity clienteJpa = ClienteJpaEntity.builder()
                .id(cliente.getClienteId() != null ? cliente.getClienteId().getValue() : null)
                .pixInfo(mapPixInfoToJpa(cliente.getPixInfo()))
                .usuario(criarUsuarioJpaMinimo(cliente.getUsuarioId()))
                .auditoria(mapAuditoriaToJpa(cliente.getAuditoria()))
                .build();
        // Campos da classe pai PessoaJpaEntity
        clienteJpa.setNome(cliente.getNome());
        clienteJpa.setCpf(cliente.getCpf().getNumero());
        clienteJpa.setEmail(cliente.getEmail().getEndereco());
        clienteJpa.setTelefone(cliente.getTelefone());
        clienteJpa.setDataNascimento(cliente.getDataNascimento());
        clienteJpa.setEnderecoJpaEntity(enderecoJpaMapper.toJpaEmbeddable(cliente.getEndereco()));

        return clienteJpa;
    }

    public Cliente toDomainEntity(ClienteJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Endereco endereco = enderecoJpaMapper.toDomainObject(jpaEntity.getEnderecoJpaEntity());
        PixInfo pixInfo = mapPixInfoToDomain(jpaEntity.getPixInfo());
        Auditoria auditoria = mapAuditoriaToDomain(jpaEntity.getAuditoria());

        if (jpaEntity.getId() != null) {
            return Cliente.reconstituir(
                    jpaEntity.getId(),
                    jpaEntity.getNome(),
                    new Cpf(jpaEntity.getCpf()),
                    new Email(jpaEntity.getEmail()),
                    jpaEntity.getTelefone(),
                    jpaEntity.getDataNascimento(),
                    endereco,
                    UsuarioId.of(jpaEntity.getUsuario().getId()),
                    pixInfo,
                    true, // assumindo que está ativo por padrão
                    auditoria
            );
        } else {
            return Cliente.criarCompleto(
                    jpaEntity.getNome(),
                    new Cpf(jpaEntity.getCpf()),
                    new Email(jpaEntity.getEmail()),
                    jpaEntity.getTelefone(),
                    jpaEntity.getDataNascimento(),
                    endereco,
                    UsuarioId.of(jpaEntity.getUsuario().getId()),
                    pixInfo
            );
        }
    }

    public void updateJpaEntity(ClienteJpaEntity jpaEntity, Cliente cliente) {
        if (jpaEntity == null || cliente == null) {
            return;
        }

        // Atualizar campos da classe pai PessoaJpaEntity
        jpaEntity.setNome(cliente.getNome());
        jpaEntity.setCpf(cliente.getNome());
        jpaEntity.setEmail(cliente.getEmail().getEndereco());
        jpaEntity.setTelefone(cliente.getTelefone());
        jpaEntity.setDataNascimento(cliente.getDataNascimento());

        if (cliente.getEndereco() != null) {
            if (jpaEntity.getEnderecoJpaEntity() == null) {
                jpaEntity.setEnderecoJpaEntity(enderecoJpaMapper.toJpaEmbeddable(cliente.getEndereco()));
            } else {
                enderecoJpaMapper.updateJpaEmbeddable(jpaEntity.getEnderecoJpaEntity(), cliente.getEndereco());
            }
        }

        // Atualizar campos específicos de ClienteJpaEntity
        jpaEntity.setPixInfo(mapPixInfoToJpa(cliente.getPixInfo()));

        if (jpaEntity.getAuditoria() == null) {
            jpaEntity.setAuditoria(new AuditoriaJpa());
        }
    }

    private PixInfoJpa mapPixInfoToJpa(PixInfo pixInfo) {
        if (pixInfo == null) {
            return null;
        }

        PixInfoJpa.TipoChavePixJpa tipoJpa = switch (pixInfo.getTipo()) {
            case CPF -> PixInfoJpa.TipoChavePixJpa.CPF;
            case EMAIL -> PixInfoJpa.TipoChavePixJpa.EMAIL;
            case TELEFONE -> PixInfoJpa.TipoChavePixJpa.TELEFONE;
            case ALEATORIA -> PixInfoJpa.TipoChavePixJpa.ALEATORIA;
        };

        return new PixInfoJpa(
                pixInfo.getChave(),
                tipoJpa,
                pixInfo.getBanco(),
                pixInfo.isAtivo()
        );
    }

    private PixInfo mapPixInfoToDomain(PixInfoJpa pixInfoJpa) {
        if (pixInfoJpa == null || pixInfoJpa.getChave() == null) {
            return null;
        }

        PixInfo.TipoChavePix tipoDomain = switch (pixInfoJpa.getTipo()) {
            case CPF -> PixInfo.TipoChavePix.CPF;
            case EMAIL -> PixInfo.TipoChavePix.EMAIL;
            case TELEFONE -> PixInfo.TipoChavePix.TELEFONE;
            case ALEATORIA -> PixInfo.TipoChavePix.ALEATORIA;
        };

        if (pixInfoJpa.isAtivo()) {
            return PixInfo.criar(pixInfoJpa.getChave(), tipoDomain, pixInfoJpa.getBanco());
        } else {
            return PixInfo.desativado(pixInfoJpa.getChave(), tipoDomain, pixInfoJpa.getBanco());
        }
    }

    private UsuarioJpaEntity criarUsuarioJpaMinimo(UsuarioId usuarioId) {
        return UsuarioJpaEntity.builder()
                .id(usuarioId.getValue())
                .build();
    }

    private AuditoriaJpa mapAuditoriaToJpa(Auditoria auditoria) {
        if (auditoria == null) {
            return new AuditoriaJpa();
        }

        AuditoriaJpa auditoriaJpa = new AuditoriaJpa();
        auditoriaJpa.setCriadoEm(auditoria.getCriadoEm());
        auditoriaJpa.setAtualizadoEm(auditoria.getAtualizadoEm());
        return auditoriaJpa;
    }

    private Auditoria mapAuditoriaToDomain(AuditoriaJpa auditoriaJpa) {
        if (auditoriaJpa == null || auditoriaJpa.getCriadoEm() == null) {
            return Auditoria.criarNova();
        }

        return Auditoria.reconstituir(
                auditoriaJpa.getCriadoEm(),
                auditoriaJpa.getAtualizadoEm()
        );
    }
}