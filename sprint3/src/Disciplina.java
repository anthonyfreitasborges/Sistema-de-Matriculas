import java.util.ArrayList;
import java.util.List;

public class Disciplina {
    private final String nome;
    private final int creditos;
    private final List<Aluno> alunos = new ArrayList<>();
    private boolean ativa;
    private Professor professor;

    public Disciplina(String nome, int creditos) {
        this.nome = nome;
        this.creditos = creditos;
    }

    public boolean adicionarAluno(Aluno aluno) {
        if (aluno == null || alunos.contains(aluno) || alunos.size() >= 60) {
            return false;
        }
        alunos.add(aluno);
        return true;
    }

    public boolean removerAluno(Aluno aluno) {
        return alunos.remove(aluno);
    }

    public boolean verificar() {
        return alunos.size() >= 3;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public List<Aluno> getAlunos() {
        return new ArrayList<>(alunos);
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        if (this.professor != null) {
            this.professor.removerDisciplina(this);
        }
        this.professor = professor;
        if (professor != null) {
            professor.adicionarDisciplina(this);
        }
    }
}