public class Disciplina {
    private final String id;
    private String nome;
    private int creditos;
    private final Curso curso;
    public Disciplina(String id, String nome, int creditos, Curso curso) {
        this.id = id;
        this.curso = curso;
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

    public Curso getCurso() {
        return curso;
    }

    public String toString() {
        return id + " - " + nome + " (" + creditos + " créditos; curso " + curso.getNome() + ")";
    }
}
