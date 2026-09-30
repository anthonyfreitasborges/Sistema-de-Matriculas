import java.util.ArrayList;
import java.util.List;

public class Curso {
    private final String nome;
    private final int creditos;
    private final List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String nome, int creditos) {
        this.nome = nome;
        this.creditos = creditos;
    }

    public boolean adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null || disciplinas.contains(disciplina)) {
            return false;
        }
        disciplinas.add(disciplina);
        return true;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public List<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }
}