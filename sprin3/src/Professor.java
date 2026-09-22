public class Professor extends Usuario {
    private String nome;

    public Professor(String nome, String login, String senha) {
        super(login, senha);
        this.nome = nome;
    }

    public void consultarAlunos(Disciplina disciplina) {
        System.out.println("Alunos da disciplina " + disciplina.getNome() + ":");

        for (Aluno aluno : disciplina.getAlunos()) {
            System.out.println(aluno.getNome());
        }
    }
}