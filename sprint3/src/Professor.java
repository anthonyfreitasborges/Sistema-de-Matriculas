import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {
    private final String nome;
    private final List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(String login, String senha, String nome) {
        super(login, senha);
        this.nome = nome;
    }

    public boolean consultarAlunos() {
        for (Disciplina disciplina : disciplinas) {
            if (!disciplina.getAlunos().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null || disciplinas.contains(disciplina)) {
            return false;
        }
        disciplinas.add(disciplina);
        return true;
    }

    public void removerDisciplina(Disciplina disciplina) {
        disciplinas.remove(disciplina);
    }

    public String getNome() {
        return nome;
    }

    public List<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }
}