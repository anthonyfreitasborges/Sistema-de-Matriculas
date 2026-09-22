import java.util.ArrayList;

public class Curso {
    private String nome;
    private int creditos;
    private ArrayList<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String nome, int creditos) {
        this.nome = nome;
        this.creditos = creditos;
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        disciplinas.add(disciplina);
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public ArrayList<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}