public enum TipoInscricao {
    OBRIGATORIA("1a opcao"),
    ALTERNATIVA("Alternativa");

    private final String descricao;

    TipoInscricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}