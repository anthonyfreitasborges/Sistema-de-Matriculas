import java.nio.file.*;
import java.time.LocalDate;
import java.io.IOException;
public class TesteSistema {
    private static int verificacoes;
    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        verificacoes++;
    }
    private static void rejeitar(Runnable operacao, String mensagem) {
        try { operacao.run(); } catch (IllegalArgumentException e) { verificacoes++; return; }
        throw new AssertionError("Deveria rejeitar: " + mensagem);
    }
    private static SistemaMatriculas criarBaseTeste() {
        SistemaMatriculas sistema = DadosExemplo.criar();
        Usuario secretaria = sistema.buscarUsuario("secretaria");
        String semestre = sistema.getSemestres().get(0).getId();
        Aluno ana = (Aluno) sistema.buscarUsuario("ana");
        sistema.matricular(ana, semestre, "D1", Matricula.Tipo.OBRIGATORIA);
        sistema.matricular(ana, semestre, "D2", Matricula.Tipo.OBRIGATORIA);
        sistema.cadastrarUsuario(secretaria, "PROFESSOR", "Mariana", "mariana", "1234");
        for (int i = 3; i <= 7; i++) {
            String id = "D" + i;
            sistema.salvarDisciplina(secretaria, id, "Disciplina " + i, 4, "C1");
            String professor = "carlos";
            if (i >= 5) {
                professor = "mariana";
            }
            sistema.oferecerDisciplina(secretaria, semestre, id, professor);
        }
        return sistema;
    }

    public static void main(String[] args) throws Exception {
        SistemaMatriculas s = criarBaseTeste();
        Usuario sec = s.login("secretaria", "1234");
        Aluno ana = (Aluno) s.login("ana", "1234");
        String sem = s.getSemestres().get(0).getId();
        conferir(sec instanceof Secretaria && ana != null, "Login correto");
        conferir(s.login("ana", "errada") == null && s.login("inexistente", "1234") == null, "Login inválido");
        rejeitar(() -> s.salvarCurso(ana, "X", "Inválido", 10), "permissão secretaria");
        rejeitar(() -> s.cadastrarUsuario(sec, "ALUNO", "Outra Ana", "ana", "1234"), "login duplicado");
        rejeitar(() -> s.salvarCurso(sec, "X", "Nome|inválido", 10), "separador TXT");
        rejeitar(() -> s.salvarCurso(sec, "X", "Curso", 0), "créditos inválidos");
        rejeitar(() -> s.definirPeriodo(sec, "ERRADO", LocalDate.now(), LocalDate.now().minusDays(1)), "datas invertidas");
        rejeitar(() -> s.oferecerDisciplina(sec, sem, "D1", "carlos"), "oferta duplicada");
        rejeitar(() -> s.oferecerDisciplina(sec, sem, "D3", "ana"), "professor inválido");
        int eventos = s.getCobrancas().getEventos().size();
        rejeitar(() -> s.matricular(ana, sem, "D1", Matricula.Tipo.OBRIGATORIA), "matrícula duplicada");
        conferir(s.getCobrancas().getEventos().size() == eventos, "Falha não gera cobrança");
        s.matricular(ana, sem, "D3", Matricula.Tipo.OBRIGATORIA);
        s.matricular(ana, sem, "D4", Matricula.Tipo.OBRIGATORIA);
        rejeitar(() -> s.matricular(ana, sem, "D5", Matricula.Tipo.OBRIGATORIA), "quinta obrigatória");
        s.matricular(ana, sem, "D5", Matricula.Tipo.OPTATIVA);
        s.matricular(ana, sem, "D6", Matricula.Tipo.OPTATIVA);
        rejeitar(() -> s.matricular(ana, sem, "D7", Matricula.Tipo.OPTATIVA), "terceira optativa");
        s.cancelarMatricula(ana, sem, "D6");
        conferir(s.inscritos(s.buscarOferta(sem, "D6")).isEmpty(), "Cancelamento libera vaga");
        s.matricular(ana, sem, "D7", Matricula.Tipo.OPTATIVA);
        rejeitar(() -> s.cancelarMatricula(ana, sem, "D6"), "cancelamento repetido");
        Professor carlos = (Professor) s.buscarUsuario("carlos");
        conferir(s.consultarAlunos(carlos, s.buscarOferta(sem, "D1")).size() == 3, "Professor consulta sua turma");
        rejeitar(() -> s.consultarAlunos(carlos, s.buscarOferta(sem, "D5")), "turma de outro professor");
        s.definirPeriodo(sec, "FUTURO", LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));
        s.oferecerDisciplina(sec, "FUTURO", "D1", "carlos");
        rejeitar(() -> s.matricular(ana, "FUTURO", "D1", Matricula.Tipo.OBRIGATORIA), "antes do início");
        rejeitar(() -> s.cancelarMatricula(ana, "FUTURO", "D1"), "cancelamento fora do período");
        s.definirPeriodo(sec, "FUTURO", LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
        rejeitar(() -> s.matricular(ana, "FUTURO", "D1", Matricula.Tipo.OBRIGATORIA), "após o fim");
        s.definirPeriodo(sec, "FUTURO", LocalDate.now(), LocalDate.now());
        s.matricular(ana, "FUTURO", "D1", Matricula.Tipo.OBRIGATORIA);
        conferir(s.inscritos(s.buscarOferta("FUTURO", "D1")).size() == 1, "Datas inclusivas e limites independentes por semestre");
        s.encerrarPeriodo(sec, sem);
        conferir(s.buscarOferta(sem, "D1").getStatus() == Oferta.Status.ATIVA, "Mínimo de 3 ativa turma");
        conferir(s.buscarOferta(sem, "D2").getStatus() == Oferta.Status.CANCELADA, "2 alunos cancela turma");
        conferir(s.inscritos(s.buscarOferta(sem, "D2")).isEmpty(), "Cancela matrículas da turma cancelada");
        conferir(s.getCobrancas().getEventos().stream().anyMatch(e -> e.contains("|CANCELAMENTO_DISCIPLINA|")), "Notifica cancelamento à cobrança");
        rejeitar(() -> s.matricular(ana, sem, "D6", Matricula.Tipo.OPTATIVA), "semestre encerrado");
        rejeitar(() -> s.cancelarMatricula(ana, sem, "D1"), "cancelamento em semestre encerrado");
        rejeitar(() -> s.encerrarPeriodo(sec, sem), "encerramento repetido");
        rejeitar(() -> s.definirPeriodo(sec, sem, LocalDate.now(), LocalDate.now()), "reabrir semestre");
        SistemaMatriculas lotacao = criarBaseTeste();
        Usuario secretaria = lotacao.buscarUsuario("secretaria");
        String periodo = lotacao.getSemestres().get(0).getId();
        for (int i = 1; i <= 61; i++) {
            lotacao.cadastrarUsuario(secretaria, "ALUNO", "Aluno " + i, "teste" + i, "1234");
            if (i <= 60) lotacao.matricular((Aluno) lotacao.buscarUsuario("teste" + i), periodo, "D7", Matricula.Tipo.OBRIGATORIA);
        }
        Aluno excedente = (Aluno) lotacao.buscarUsuario("teste61");
        rejeitar(() -> lotacao.matricular(excedente, periodo, "D7", Matricula.Tipo.OBRIGATORIA), "61º aluno");
        lotacao.cancelarMatricula((Aluno) lotacao.buscarUsuario("teste1"), periodo, "D7");
        lotacao.matricular(excedente, periodo, "D7", Matricula.Tipo.OBRIGATORIA);
        conferir(lotacao.inscritos(lotacao.buscarOferta(periodo, "D7")).size() == 60, "Vaga reaberta até 60");

        Path pasta = Files.createTempDirectory(Path.of("build"), "teste-");
        BancoTxt banco = new BancoTxt(pasta.resolve("banco.txt"));
        banco.salvar(s);
        SistemaMatriculas carregado = banco.carregar();
        conferir(carregado.login("ana", "1234") != null, "Senha funciona após recarga");
        conferir(carregado.getMatriculas().size() == s.getMatriculas().size(), "Histórico persistido");
        conferir(carregado.getCobrancas().getEventos().equals(s.getCobrancas().getEventos()), "Cobranças persistidas sem duplicação");
        conferir(carregado.buscarOferta(sem, "D2").getStatus() == Oferta.Status.CANCELADA, "Situação da turma persistida");
        conferir(carregado.buscarSemestre(sem).isEncerrado(), "Período persistido");
        carregado.atualizarUsuario(carregado.buscarUsuario("secretaria"), "ana", "Ana Atualizada", "nova123");
        banco.salvar(carregado);
        conferir(banco.carregar().login("ana", "nova123").getNome().equals("Ana Atualizada"), "Edição de usuário persistida");
        Path invalido = pasta.resolve("invalido.txt");
        Files.writeString(invalido, "BANCO|1\nCURSO|C1|Curso|abc\n");
        String antes = Files.readString(invalido);
        try { new BancoTxt(invalido).carregar(); throw new AssertionError("Banco inválido deveria falhar"); }
        catch (IOException e) { conferir(Files.readString(invalido).equals(antes), "Banco inválido preservado"); }
        System.out.println("SUCESSO: " + verificacoes + " verificações passaram.");
    }
}
