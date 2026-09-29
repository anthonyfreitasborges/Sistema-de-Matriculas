import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TesteMenu {
    private static String executar(Path banco, String entrada) {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
            App.main(new String[]{banco.toString()});
            return saida.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }
    }

    private static void conferir(boolean resultado, String mensagem) {
        if (!resultado) {
            throw new AssertionError(mensagem);
        }
    }

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory(Path.of("build"), "menu-");
        Path arquivo = pasta.resolve("banco.txt");
        BancoTxt banco = new BancoTxt(arquivo);
        SistemaMatriculas sistema = banco.carregar();
        Aluno ana = (Aluno) sistema.buscarUsuario("ana");
        conferir(sistema.matriculasDoAluno(ana).isEmpty(), "Ana deve começar sem matrículas no exemplo novo.");

        String saida = executar(arquivo, "ana\n1234\n2\n1\n1\n4\n0\n0\n0\n");
        conferir(saida.contains("Matrícula realizada em"), "Deve permitir matrícula pela opção numérica.");
        conferir(saida.contains("MATRICULADA"), "Deve mostrar a matrícula no histórico do aluno.");
        sistema = banco.carregar();
        ana = (Aluno) sistema.buscarUsuario("ana");
        conferir(sistema.matriculasDoAluno(ana).size() == 1, "Deve salvar a matrícula no TXT.");
        conferir(sistema.getCobrancas().getEventos().size() == 4, "Deve salvar a notificação de cobrança.");

        saida = executar(arquivo, "ana\n1234\n4\n1\nd2\n2\n2\n0\n0\n");
        conferir(saida.contains("1 Matricular em outra disciplina"), "Deve oferecer nova matrícula após listar as disciplinas do aluno.");
        conferir(saida.contains("Matrícula realizada em"), "Deve aceitar o código da disciplina em minúsculas.");
        conferir(saida.contains("Não há novas disciplinas disponíveis"), "Deve explicar quando todas já estão matriculadas.");
        sistema = banco.carregar();
        ana = (Aluno) sistema.buscarUsuario("ana");
        conferir(sistema.matriculasDoAluno(ana).size() == 2, "Não deve duplicar matrículas.");

        saida = executar(arquivo, "ana\n1234\n3\n1\n2\n1\n1\n0\n0\n");
        conferir(saida.contains("Matrícula realizada em"), "Deve permitir cancelar e matricular novamente.");
        sistema = banco.carregar();
        ana = (Aluno) sistema.buscarUsuario("ana");
        conferir(sistema.matriculasDoAluno(ana).size() == 3, "Deve manter o histórico do cancelamento.");
        System.out.println("SUCESSO: fluxo de matrícula pelo menu verificado.");
    }
}
