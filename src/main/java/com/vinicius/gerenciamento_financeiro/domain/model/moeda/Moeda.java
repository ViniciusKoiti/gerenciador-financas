package com.vinicius.gerenciamento_financeiro.domain.model.moeda;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;

import java.util.Objects;

public final class Moeda {

    private final MoedaId id;
    private final String codigo;
    private final String simbolo;
    private final String nome;

    private final int casasDecimais;
    private final boolean ativo;
    private final Auditoria auditoria;

    public MoedaId getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getNome() {
        return nome;
    }

    public int getCasasDecimais() {
        return casasDecimais;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public Moeda(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "CurrencyId não pode ser nulo");
        this.codigo = validarCodigo(builder.codigo);
        this.simbolo = validarSimbolo(builder.simbolo);
        this.nome = validarNome(builder.nome);
        this.casasDecimais = validarCasasDecimais(builder.casasDecimais);
        this.ativo = builder.ativo;
        this.auditoria = builder.auditoria != null ? builder.auditoria : Auditoria.criarNova();
    }


    public static Moeda criar(String code, String symbol, String displayName, int decimalPlaces) {
        return new Builder()
                .id(MoedaId.of(code))
                .codigo(code)
                .simbolo(symbol)
                .nome(displayName)
                .casasDecimais(decimalPlaces)
                .ativo(true)
                .auditoria(Auditoria.criarNova())
                .build();
    }

    public static Moeda reconstituir(String code, String symbol, String displayName,
                                        int decimalPlaces, boolean active, Auditoria auditoria) {
        return new Builder()
                .id(MoedaId.of(code))
                .codigo(code)
                .simbolo(symbol)
                .nome(displayName)
                .casasDecimais(decimalPlaces)
                .ativo(active)
                .auditoria(auditoria)
                .build();
    }

    public Moeda ativar() {
        if (this.ativo) {
            return this;
        }

        return new Builder()
                .from(this)
                .ativo(true)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public Moeda desativar() {
        if (!this.ativo) {
            return this;
        }

        return new Builder()
                .from(this)
                .ativo(false)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public boolean isEuro() {
        return "EUR".equals(this.codigo);
    }

    public boolean isDolar() {
        return "USD".equals(this.codigo);
    }

    public boolean isReal() {
        return "BRL".equals(this.codigo);
    }


    private String validarCodigo(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo não pode ser vazio");
        }
        if (code.length() != 3) {
            throw new IllegalArgumentException("Codigo deve ter exatamente 3 caracteres");
        }
        return code.toUpperCase().trim();
    }

    private String validarSimbolo(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Simbolo não pode ser vazio");
        }
        if (symbol.length() > 10) {
            throw new IllegalArgumentException("Simbolo não pode ter mais de 10 caracteres");
        }
        return symbol.trim();
    }

    private String validarNome(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Moeda - nome não pode ser vazio");
        }
        if (displayName.length() > 100) {
            throw new IllegalArgumentException("Moeda  - name não pode ter mais de 100 caracteres");
        }
        return displayName.trim();
    }

    private int validarCasasDecimais(int decimalPlaces) {
        if (decimalPlaces < 0 || decimalPlaces > 8) {
            throw new IllegalArgumentException("Decimal places deve estar entre 0 e 8");
        }
        return decimalPlaces;
    }


    public static class Builder {
        private MoedaId id;
        private String codigo;
        private String simbolo;
        private String nome;
        private int casasDecimais = 2;
        private boolean ativo = true;
        private Auditoria auditoria;

        public Builder id(MoedaId id) { this.id = id; return this; }
        public Builder codigo(String code) { this.codigo = code; return this; }
        public Builder simbolo(String symbol) { this.simbolo = symbol; return this; }
        public Builder nome(String displayName) { this.nome = displayName; return this; }
        public Builder casasDecimais(int decimalPlaces) { this.casasDecimais = decimalPlaces; return this; }
        public Builder ativo(boolean active) { this.ativo = active; return this; }
        public Builder auditoria(Auditoria auditoria) { this.auditoria = auditoria; return this; }

        public Builder from(Moeda currency) {
            this.id = currency.id;
            this.codigo = currency.codigo;
            this.simbolo = currency.simbolo;
            this.nome = currency.nome;
            this.casasDecimais = currency.casasDecimais;
            this.ativo = currency.ativo;
            this.auditoria = currency.auditoria;
            return this;
        }

        public Moeda build() {
            return new Moeda(this);
        }

    }


}
