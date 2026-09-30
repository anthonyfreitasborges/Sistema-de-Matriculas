public class Matricula {
    private final Aluno aluno;
    private final Disciplina disciplina;
    private final TipoInscricao tipo;

    public Matricula(Aluno aluno, Disciplina disciplina) {
        this(aluno, disciplina, TipoInscricao.OBRIGATORIA);
    }

    public Matricula(Aluno aluno, Disciplina disciplina, TipoInscricao tipo) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public TipoInscricao getTipo() {
        return tipo;
    }
}