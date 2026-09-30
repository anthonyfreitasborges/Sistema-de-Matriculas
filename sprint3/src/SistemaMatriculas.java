import java.util.ArrayList;
import java.util.List;

public class SistemaMatriculas {
    private boolean periodoMatricula;
    private final SistemaCobrancas cobrancas;
    private final List<Disciplina> disciplinas = new ArrayList<>();

    public SistemaMatriculas(SistemaCobrancas cobrancas) {
        this.cobrancas = cobrancas;
    }

    public boolean iniciarPeriodo() {
        if (periodoMatricula) {
            return false;
        }
        periodoMatricula = true;
        return true;
    }

    public boolean fimPeriodo() {
        if (!periodoMatricula) {
            return false;
        }
        for (Disciplina disciplina : disciplinas) {
            disciplina.setAtiva(disciplina.verificar());
        }
        periodoMatricula = false;
        return true;
    }

    public boolean adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null || disciplinas.contains(disciplina)) {
            return false;
        }
        disciplinas.add(disciplina);
        return true;
    }

    public boolean matricular(Aluno aluno, Disciplina disciplina, TipoInscricao tipo) {
        if (!periodoMatricula || !disciplinas.contains(disciplina) || aluno == null || tipo == null) {
            return false;
        }
        if (!aluno.matricular(disciplina, tipo)) {
            return false;
        }
        cobrancas.notificar(aluno, disciplina);
        return true;
    }

    public boolean cancelarMatricular(Aluno aluno, Disciplina disciplina) {
        if (!periodoMatricula || aluno == null || disciplina == null || !disciplinas.contains(disciplina)) {
            return false;
        }
        if (!aluno.cancelarMatricula(disciplina)) {
            return false;
        }
        cobrancas.notificarCancelamento(aluno, disciplina);
        return true;
    }

    public boolean isPeriodoMatricula() {
        return periodoMatricula;
    }

    public List<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }
}