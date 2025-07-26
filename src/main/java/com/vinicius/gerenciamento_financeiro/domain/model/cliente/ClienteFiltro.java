package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Value Object que representa os filtros de busca de Cliente no domínio.
 * Encapsula as regras de negócio relacionadas à filtragem de clientes.
 */
public record ClienteFiltro(
        Optional<String> nome,
        Optional<String> cpf,
        Optional<String> email,
        Optional<String> telefone,
        Optional<Boolean> ativo,
        Optional<String> buscaGeral,
        Optional<LocalDate> dataNascimentoInicio,
        Optional<LocalDate> dataNascimentoFim
) {
    public static ClienteFiltro vazio() {
        return new ClienteFiltro(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()
        );
    }
    public static Builder builder() {
        return new Builder();
    }
    public boolean temFiltros() {
        return nome.isPresent() || cpf.isPresent() || email.isPresent() ||
                telefone.isPresent() || ativo.isPresent() || buscaGeral.isPresent() ||
                dataNascimentoInicio.isPresent() || dataNascimentoFim.isPresent();
    }
    public boolean temBuscaGeral() {
        return buscaGeral.isPresent() &&
                !buscaGeral.get().trim().isEmpty() &&
                buscaGeral.get().trim().length() >= 2;
    }
    public boolean temFiltroNome() {
        return nome.isPresent() && !nome.get().trim().isEmpty();
    }
    public boolean temFiltroEmail() {
        return email.isPresent() && !email.get().trim().isEmpty();
    }
    public boolean temFiltroCpf() {
        return cpf.isPresent() && !cpf.get().trim().isEmpty();
    }

    public boolean temFiltroTelefone() {
        return telefone.isPresent() && !telefone.get().trim().isEmpty();
    }

    public boolean temPeriodoNascimento() {
        return dataNascimentoInicio.isPresent() || dataNascimentoFim.isPresent();
    }

    public boolean isPeriodoNascimentoValido() {
        if (dataNascimentoInicio.isEmpty() || dataNascimentoFim.isEmpty()) {
            return true; // Permitir apenas uma das datas
        }

        return !dataNascimentoInicio.get().isAfter(dataNascimentoFim.get());
    }
    public boolean isValido() {
        // Validar busca geral mínima
        if (buscaGeral.isPresent() &&
                !buscaGeral.get().trim().isEmpty() &&
                buscaGeral.get().trim().length() < 2) {
            return false;
        }

        return isPeriodoNascimentoValido();
    }

    public ClienteFiltro limpar() {
        return new ClienteFiltro(
                filtrarString(nome),
                filtrarString(cpf),
                filtrarString(email),
                filtrarString(telefone),
                ativo,
                filtrarString(buscaGeral),
                dataNascimentoInicio,
                dataNascimentoFim
        );
    }

    private Optional<String> filtrarString(Optional<String> valor) {
        return valor.filter(s -> s != null && !s.trim().isEmpty());
    }
    public static class Builder {
        private Optional<String> nome = Optional.empty();
        private Optional<String> cpf = Optional.empty();
        private Optional<String> email = Optional.empty();
        private Optional<String> telefone = Optional.empty();
        private Optional<Boolean> ativo = Optional.empty();
        private Optional<String> buscaGeral = Optional.empty();
        private Optional<LocalDate> dataNascimentoInicio = Optional.empty();
        private Optional<LocalDate> dataNascimentoFim = Optional.empty();

        public Builder nome(String nome) {
            this.nome = Optional.ofNullable(nome)
                    .filter(s -> !s.trim().isEmpty());
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = Optional.ofNullable(cpf)
                    .filter(s -> !s.trim().isEmpty());
            return this;
        }

        public Builder email(String email) {
            this.email = Optional.ofNullable(email)
                    .filter(s -> !s.trim().isEmpty());
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = Optional.ofNullable(telefone)
                    .filter(s -> !s.trim().isEmpty());
            return this;
        }

        public Builder ativo(Boolean ativo) {
            this.ativo = Optional.ofNullable(ativo);
            return this;
        }

        public Builder buscaGeral(String buscaGeral) {
            this.buscaGeral = Optional.ofNullable(buscaGeral)
                    .filter(s -> !s.trim().isEmpty())
                    .filter(s -> s.trim().length() >= 2);
            return this;
        }

        public Builder dataNascimentoInicio(LocalDate data) {
            this.dataNascimentoInicio = Optional.ofNullable(data);
            return this;
        }

        public Builder dataNascimentoFim(LocalDate data) {
            this.dataNascimentoFim = Optional.ofNullable(data);
            return this;
        }

        public Builder periodo(LocalDate inicio, LocalDate fim) {
            this.dataNascimentoInicio = Optional.ofNullable(inicio);
            this.dataNascimentoFim = Optional.ofNullable(fim);
            return this;
        }

        public ClienteFiltro build() {
            ClienteFiltro filtro = new ClienteFiltro(
                    nome, cpf, email, telefone, ativo,
                    buscaGeral, dataNascimentoInicio, dataNascimentoFim
            );

            if (!filtro.isValido()) {
                throw new IllegalArgumentException(
                        "Filtro inválido: " + obterMensagemErro(filtro)
                );
            }

            return filtro;
        }

        private String obterMensagemErro(ClienteFiltro filtro) {
            if (filtro.buscaGeral().isPresent() &&
                    !filtro.buscaGeral().get().trim().isEmpty() &&
                    filtro.buscaGeral().get().trim().length() < 2) {
                return "Busca geral deve ter pelo menos 2 caracteres";
            }

            if (!filtro.isPeriodoNascimentoValido()) {
                return "Data de início não pode ser posterior à data de fim";
            }

            return "Dados inválidos";
        }
    }
}