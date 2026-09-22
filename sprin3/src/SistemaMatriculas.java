import java.util.ArrayList;

public class SistemaMatriculas {
    private boolean periodoMatricula;
    private ArrayList<Disciplina> disciplinas = new ArrayList<>();
    private SistemaCobrancas cobrancas;

    public SistemaMatriculas(SistemaCobrancas cobrancas) {
        this.cobrancas = cobrancas;
    }

    public void iniciarPeriodo() {
        periodoMatricula = true;
    }

    public void encerrarPeriodo() {
        periodoMatricula = false;

        for (Disciplina disciplina : disciplinas) {
            disciplina.verificarAtivacao();
        }
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        disciplinas.add(disciplina);
    }

    public boolean matricular(Aluno aluno, Disciplina disciplina) {
        if (!periodoMatricula) {
            return false;
        }

        if (aluno.matricular(disciplina)) {
            cobrancas.notificar(aluno, disciplina);
            return true;
        }

        return false;
    }

    public void cancelarMatricula(Aluno aluno, Disciplina disciplina) {
        if (periodoMatricula) {
            aluno.cancelarMatricula(disciplina);
        }
    }
}/