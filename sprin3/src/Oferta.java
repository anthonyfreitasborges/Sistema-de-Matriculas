public class Oferta {
    public enum Status { PREVISTA, ATIVA, CANCELADA }
    private final Semestre semestre;
    private final Disciplina disciplina;
    private final Professor professor;
    private Status status = Status.PREVISTA;
    public Oferta(Semestre semestre, Disciplina disciplina, Professor professor) {
        this.semestre = semestre;
        this.disciplina = disciplina;
        this.professor = professor;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }
}
