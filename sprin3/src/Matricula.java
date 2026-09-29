public class Matricula {
    public enum Tipo { OBRIGATORIA, OPTATIVA }
    public enum Status { MATRICULADA, CANCELADA_ALUNO, CANCELADA_DISCIPLINA }
    private final Aluno aluno;
    private final Oferta oferta;
    private final Tipo tipo;
    private Status status = Status.MATRICULADA;
    public Matricula(Aluno aluno, Oferta oferta, Tipo tipo) {
        this.aluno = aluno;
        this.oferta = oferta;
        this.tipo = tipo;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public Disciplina getDisciplina() {
        return oferta.getDisciplina();
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isVigente() {
        return status == Status.MATRICULADA;
    }

    void setStatus(Status status) {
        this.status = status;
    }
}
