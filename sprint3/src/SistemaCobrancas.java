import java.util.ArrayList;
import java.util.List;

public class SistemaCobrancas {
    private final List<String> notificacoes = new ArrayList<>();

    public void notificar(Aluno aluno, Disciplina disciplina) {
        registrar("Matricula", aluno, disciplina);
    }

    public void notificarCancelamento(Aluno aluno, Disciplina disciplina) {
        registrar("Cancelamento", aluno, disciplina);
    }

    private void registrar(String operacao, Aluno aluno, Disciplina disciplina) {
        String notificacao = operacao + " | Aluno: " + aluno.getMatricula() + " - " + aluno.getNome()
                + " | Disciplina: " + disciplina.getNome();
        notificacoes.add(notificacao);
        System.out.println("Sistema de cobrancas notificado: " + notificacao);
    }

    public List<String> getNotificacoes() {
        return new ArrayList<>(notificacoes);
    }
}