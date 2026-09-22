import java.util.ArrayList;

public class Aluno extends Usuario {
    private String nome;
    private ArrayList<Matricula> matriculas = new ArrayList<>();

    public Aluno(String nome, String login, String senha) {
        super(login, senha);
        this.nome = nome;
    }

    public boolean matricular(Disciplina disciplina) {
        if (matriculas.size() >= 6) {
            return false;
        }

        if (disciplina.adicionarAluno(this)) {
            matriculas.add(new Matricula(this, disciplina));
            return true;
        }

        return false;
    }

    public void cancelarMatricula(Disciplina disciplina) {
        disciplina.removerAluno(this);

        matriculas.removeIf(
            m -> m.getDisciplina() == disciplina
        );
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Matricula> getMatriculas() {
        return matriculas;
    }
}