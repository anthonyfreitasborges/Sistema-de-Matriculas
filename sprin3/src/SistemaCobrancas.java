import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SistemaCobrancas {
    private final List<String> eventos = new ArrayList<>();
    public void notificar(String acao, Matricula m) {
        eventos.add(LocalDateTime.now() + "|" + acao + "|" + m.getAluno().getLogin() + "|"
                + m.getOferta().getSemestre().getId() + "|" + m.getDisciplina().getId());
    }

    public List<String> getEventos() {
        return List.copyOf(eventos);
    }

    void restaurarEvento(String evento) {
        eventos.add(evento);
    }
}
