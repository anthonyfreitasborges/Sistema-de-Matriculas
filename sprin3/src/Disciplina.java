import java.util.ArrayList;

public class Disciplina {
    private String nome;
    private int creditos;
    private ArrayList<Aluno> alunos = new ArrayList<>();
    private boolean ativa;

    public Disciplina(String nome, int creditos) {
        this.nome = nome;
        this.creditos = creditos;
    }

    public boolean adicionarAluno(Aluno aluno) {
        if (alunos.size() >= 60) {
            return false;
        }

        if (alunos.contains(aluno)) {
            return false;
        }

        alunos.add(aluno);
        return true;
    }

    public void removerAluno(Aluno aluno) {
        alunos.remove(aluno);
    }

    public void verificarAtivacao() {
        ativa = alunos.size() >= 3;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public ArrayList<Aluno> getAlunos() {
        return alunos;
    }

    public boolean isAtiva() {
        return ativa;
    }
}