public class Curso {
    private final String id;
    private String nome;
    private int creditos;
    public Curso(String id, String nome, int creditos) {
        this.id = id;
        atualizar(nome, creditos);
    }

    void atualizar(String nome, int creditos) {
        if (creditos <= 0) {
            throw new IllegalArgumentException("Créditos devem ser positivos.");
        }
        this.nome = nome;
        this.creditos = creditos;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public String toString() {
        return id + " - " + nome + " (" + creditos + " créditos)";
    }
}
