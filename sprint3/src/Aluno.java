import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private final String matricula;
    private final String nome;
    private final Curso curso;
    private final List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String matricula, String login, String senha, String nome, Curso curso) {
        super(login, senha);
        this.matricula = matricula;
        this.nome = nome;
        this.curso = curso;
    }

    public boolean matricular(Disciplina disciplina) {
        return matricular(disciplina, TipoInscricao.OBRIGATORIA);
    }

    public boolean matricular(Disciplina disciplina, TipoInscricao tipo) {
        if (disciplina == null || tipo == null || curso == null
                || !curso.getDisciplinas().contains(disciplina) || buscarMatricula(disciplina) != null) {
            return false;
        }
        if (tipo == TipoInscricao.OBRIGATORIA && contar(TipoInscricao.OBRIGATORIA) >= 4) {
            return false;
        }
        if (tipo == TipoInscricao.ALTERNATIVA && contar(TipoInscricao.ALTERNATIVA) >= 2) {
            return false;
        }
        if (!disciplina.adicionarAluno(this)) {
            return false;
        }
        matriculas.add(new Matricula(this, disciplina, tipo));
        return true;
    }

    public boolean cancelarMatricula(Disciplina disciplina) {
        Matricula matricula = buscarMatricula(disciplina);
        if (matricula == null || !disciplina.removerAluno(this)) {
            return false;
        }
        return matriculas.remove(matricula);
    }

    private Matricula buscarMatricula(Disciplina disciplina) {
        for (Matricula matricula : matriculas) {
            if (matricula.getDisciplina() == disciplina) {
                return matricula;
            }
        }
        return null;
    }

    private int contar(TipoInscricao tipo) {
        int total = 0;
        for (Matricula matricula : matriculas) {
            if (matricula.getTipo() == tipo) {
                total++;
            }
        }
        return total;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public Curso getCurso() {
        return curso;
    }

    public List<Matricula> getMatriculas() {
        return new ArrayList<>(matriculas);
    }
}