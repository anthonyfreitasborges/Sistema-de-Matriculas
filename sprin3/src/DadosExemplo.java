import java.time.LocalDate;

public class DadosExemplo {
    public static SistemaMatriculas criar() {
        SistemaMatriculas sistema = new SistemaMatriculas(new SistemaCobrancas());
        Secretaria secretaria = new Secretaria("Secretaria", "secretaria", "1234");
        sistema.adicionarUsuario(secretaria);
        sistema.cadastrarUsuario(secretaria, "PROFESSOR", "Carlos", "carlos", "1234");
        sistema.cadastrarUsuario(secretaria, "ALUNO", "Ana", "ana", "1234");
        sistema.cadastrarUsuario(secretaria, "ALUNO", "Bruno", "bruno", "1234");
        sistema.cadastrarUsuario(secretaria, "ALUNO", "Carla", "carla", "1234");
        sistema.salvarCurso(secretaria, "C1", "Engenharia de Software", 160);
        sistema.salvarDisciplina(secretaria, "D1", "Programação", 4, "C1");
        sistema.salvarDisciplina(secretaria, "D2", "Banco de Dados", 4, "C1");

        LocalDate hoje = LocalDate.now();
        String semestre = hoje.getYear() + ".1";
        if (hoje.getMonthValue() > 6) {
            semestre = hoje.getYear() + ".2";
        }
        sistema.definirPeriodo(secretaria, semestre, hoje, hoje.plusDays(30));
        sistema.oferecerDisciplina(secretaria, semestre, "D1", "carlos");
        sistema.oferecerDisciplina(secretaria, semestre, "D2", "carlos");

        Aluno bruno = (Aluno) sistema.buscarUsuario("bruno");
        Aluno carla = (Aluno) sistema.buscarUsuario("carla");
        sistema.matricular(bruno, semestre, "D1", Matricula.Tipo.OBRIGATORIA);
        sistema.matricular(carla, semestre, "D1", Matricula.Tipo.OBRIGATORIA);
        sistema.matricular(bruno, semestre, "D2", Matricula.Tipo.OBRIGATORIA);
        return sistema;
    }
}
