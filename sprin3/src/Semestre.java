import java.time.LocalDate;

public class Semestre {
    private final String id;
    private LocalDate inicio;
    private LocalDate fim;
    private boolean encerrado;
    public Semestre(String id, LocalDate inicio, LocalDate fim) {
        this.id = id;
        definirPeriodo(inicio, fim);
    }

    void definirPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final deve ser igual ou posterior à inicial.");
        }
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean permiteMatricula(LocalDate hoje) {
        return !encerrado && !hoje.isBefore(inicio) && !hoje.isAfter(fim);
    }

    void encerrar() {
        encerrado = true;
    }

    public String getId() {
        return id;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public String toString() {
        return id + " | " + inicio + " até " + fim + " | " + (encerrado ? "ENCERRADO" : "NÃO ENCERRADO");
    }
}
