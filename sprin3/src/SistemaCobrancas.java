public class SistemaCobrancas {

    public void notificar(Aluno aluno, Disciplina disciplina) {
        System.out.println(
            "Cobrança: " + aluno.getNome() +
            " - disciplina: " + disciplina.getNome()
        );
    }
}