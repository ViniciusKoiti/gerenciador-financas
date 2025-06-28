package com.vinicius.gerenciamento_financeiro.domain.model.auditoria;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Auditoria {

    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private Auditoria(LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        validarInvariantes();
    }

    public static Auditoria criarNova() {
        LocalDateTime agora = LocalDateTime.now();
        return new Auditoria(agora, null);
    }

    public static Auditoria reconstituir(LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new Auditoria(criadoEm, atualizadoEm);
    }

    public Auditoria marcarComoAtualizado() {
        return new Auditoria(this.criadoEm, LocalDateTime.now());
    }

    public static Auditoria criarCom(LocalDateTime criadoEm) {
        return new Auditoria(criadoEm, null);
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public boolean foiAtualizada() {
        return atualizadoEm != null;
    }

    public boolean foiCriadaRecentemente() {
        return criadoEm != null &&
                criadoEm.isAfter(LocalDateTime.now().minusDays(1));
    }

    public boolean foiAtualizadaRecentemente() {
        return atualizadoEm != null &&
                atualizadoEm.isAfter(LocalDateTime.now().minusDays(1));
    }

    public LocalDateTime getUltimaModificacao() {
        if (atualizadoEm != null) {
            return atualizadoEm;
        }
        return criadoEm;
    }

    private void validarInvariantes() {
        if (criadoEm == null) {
            throw new IllegalStateException("Data de criação é obrigatória");
        }

        if (atualizadoEm != null && atualizadoEm.isBefore(criadoEm)) {
            throw new IllegalStateException("Data de atualização não pode ser anterior à criação");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Auditoria auditoria = (Auditoria) o;
        return Objects.equals(criadoEm, auditoria.criadoEm) &&
                Objects.equals(atualizadoEm, auditoria.atualizadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm, atualizadoEm);
    }

    @Override
    public String toString() {
        return String.format("Auditoria{criadoEm=%s, atualizadoEm=%s}",
                criadoEm,
                atualizadoEm != null ? atualizadoEm : "não atualizado");
    }
}