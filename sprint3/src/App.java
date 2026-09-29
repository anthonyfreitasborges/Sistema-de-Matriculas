import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Curso> cursos = new ArrayList<>();
    private static final List<Aluno> alunos = new ArrayList<>();
    private static final List<Inscricao> inscricoes = new ArrayList<>();
    private static EstadoPeriodo estadoPeriodo = EstadoPeriodo.NAO_INICIADO;

    public static void main(String[] args) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Secretaria Academica ===");
            System.out.println("1. Cadastrar curso");
            System.out.println("2. Listar cursos");
            System.out.println("3. Cadastrar aluno");
            System.out.println("4. Listar alunos");
            System.out.println("5. Abrir periodo de matriculas");
            System.out.println("6. Matricular ou cancelar matriculas");
            System.out.println("7. Encerrar periodo de matriculas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1":
                    cadastrarCurso();
                    break;
                case "2":
                    listarCursos();
                    break;
                case "3":
                    cadastrarAluno();
                    break;
                case "4":
                    listarAlunos();
                    break;
                case "5":
                    abrirPeriodo();
                    break;
                case "6":
                    gerenciarMatriculas();
                    break;
                case "7":
                    encerrarPeriodo();
                    break;
                case "0":
                    executando = false;
                    System.out.println("Encerrando o sistema.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void cadastrarCurso() {
        System.out.print("Nome do curso: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("O nome do curso nao pode ficar vazio.");
            return;
        }

        int creditos = lerCreditos();
        List<Disciplina> disciplinas = new ArrayList<>();
        System.out.println("Informe as disciplinas. Deixe vazio para concluir.");
        while (true) {
            System.out.print("Disciplina: ");
            String disciplina = scanner.nextLine().trim();
            if (disciplina.isEmpty()) {
                if (disciplinas.isEmpty()) {
                    System.out.println("Cadastre pelo menos uma disciplina.");
                    continue;
                }
                break;
            }
            disciplinas.add(new Disciplina(disciplina));
        }

        cursos.add(new Curso(nome, creditos, disciplinas));
        System.out.println("Curso cadastrado com sucesso.");
    }

    private static int lerCreditos() {
        while (true) {
            System.out.print("Total de creditos do curso: ");
            String entrada = scanner.nextLine().trim();
            try {
                int creditos = Integer.parseInt(entrada);
                if (creditos > 0) {
                    return creditos;
                }
            } catch (NumberFormatException exception) {
                // Solicita novamente uma entrada numerica positiva.
            }
            System.out.println("Informe um numero inteiro maior que zero.");
        }
    }

    private static void listarCursos() {
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }

        for (int indice = 0; indice < cursos.size(); indice++) {
            Curso curso = cursos.get(indice);
            System.out.println("\n" + (indice + 1) + ". " + curso.getNome());
            System.out.println("   Creditos: " + curso.getCreditos());
            for (Disciplina disciplina : curso.getDisciplinas()) {
                System.out.println("   - " + disciplina.getNome() + " | Inscritos: "
                        + contarInscritos(disciplina) + " | Situacao: " + disciplina.getEstado().getDescricao());
            }
        }
    }

    private static void cadastrarAluno() {
        if (cursos.isEmpty()) {
            System.out.println("Cadastre um curso antes de cadastrar alunos.");
            return;
        }

        System.out.print("Matricula do aluno: ");
        String matricula = scanner.nextLine().trim();
        if (matricula.isEmpty() || buscarAluno(matricula) != null) {
            System.out.println("Matricula vazia ou ja cadastrada.");
            return;
        }

        System.out.print("Nome do aluno: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("O nome do aluno nao pode ficar vazio.");
            return;
        }

        int indiceCurso = escolherCurso();
        if (indiceCurso < 0) {
            return;
        }
        alunos.add(new Aluno(matricula, nome, cursos.get(indiceCurso)));
        System.out.println("Aluno cadastrado com sucesso.");
    }

    private static void listarAlunos() {
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }

        for (Aluno aluno : alunos) {
            System.out.println(aluno.getMatricula() + " - " + aluno.getNome()
                    + " | Curso: " + aluno.getCurso().getNome());
        }
    }

    private static int escolherCurso() {
        System.out.println("Cursos:");
        for (int indice = 0; indice < cursos.size(); indice++) {
            System.out.println((indice + 1) + ". " + cursos.get(indice).getNome());
        }
        int escolha = lerInteiro("Escolha o curso: ");
        if (escolha < 1 || escolha > cursos.size()) {
            System.out.println("Curso invalido.");
            return -1;
        }
        return escolha - 1;
    }

    private static void abrirPeriodo() {
        if (estadoPeriodo != EstadoPeriodo.NAO_INICIADO) {
            System.out.println("O periodo de matriculas ja foi iniciado ou encerrado.");
            return;
        }
        estadoPeriodo = EstadoPeriodo.ABERTO;
        System.out.println("Periodo de matriculas aberto.");
    }

    private static void gerenciarMatriculas() {
        if (estadoPeriodo != EstadoPeriodo.ABERTO) {
            System.out.println("Matriculas e cancelamentos so podem ser feitos durante o periodo aberto.");
            return;
        }

        System.out.print("Matricula do aluno: ");
        Aluno aluno = buscarAluno(scanner.nextLine().trim());
        if (aluno == null) {
            System.out.println("Aluno nao encontrado.");
            return;
        }

        boolean gerenciando = true;
        while (gerenciando) {
            System.out.println("\nMatriculas de " + aluno.getNome() + ":");
            listarInscricoes(aluno);
            System.out.println("1. Adicionar disciplina");
            System.out.println("2. Cancelar matricula");
            System.out.println("0. Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1:
                    adicionarMatricula(aluno);
                    break;
                case 2:
                    cancelarMatricula(aluno);
                    break;
                case 0:
                    gerenciando = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void adicionarMatricula(Aluno aluno) {
        List<Disciplina> disciplinas = aluno.getCurso().getDisciplinas();
        System.out.println("Disciplinas do curso:");
        for (int indice = 0; indice < disciplinas.size(); indice++) {
            Disciplina disciplina = disciplinas.get(indice);
            System.out.println((indice + 1) + ". " + disciplina.getNome()
                    + " | Inscritos: " + contarInscritos(disciplina) + "/60");
        }

        int escolha = lerInteiro("Escolha a disciplina (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > disciplinas.size()) {
            System.out.println("Disciplina invalida.");
            return;
        }

        Disciplina disciplina = disciplinas.get(escolha - 1);
        if (buscarInscricao(aluno, disciplina) != null) {
            System.out.println("O aluno ja selecionou essa disciplina.");
            return;
        }
        if (disciplina.getEstado() == EstadoDisciplina.CANCELADA) {
            System.out.println("Essa disciplina foi cancelada.");
            return;
        }
        if (contarInscritos(disciplina) >= 60) {
            System.out.println("A disciplina atingiu o limite de 60 alunos.");
            return;
        }

        System.out.println("1. 1a opcao (obrigatoria) - " + contarInscricoes(aluno, TipoInscricao.OBRIGATORIA) + "/4");
        System.out.println("2. Alternativa (optativa) - " + contarInscricoes(aluno, TipoInscricao.ALTERNATIVA) + "/2");
        int tipo = lerInteiro("Tipo de inscricao: ");
        TipoInscricao tipoInscricao;
        if (tipo == 1 && contarInscricoes(aluno, TipoInscricao.OBRIGATORIA) < 4) {
            tipoInscricao = TipoInscricao.OBRIGATORIA;
        } else if (tipo == 2 && contarInscricoes(aluno, TipoInscricao.ALTERNATIVA) < 2) {
            tipoInscricao = TipoInscricao.ALTERNATIVA;
        } else {
            System.out.println("Opcao invalida ou limite de disciplinas dessa categoria atingido.");
            return;
        }

        inscricoes.add(new Inscricao(aluno, disciplina, tipoInscricao));
        System.out.println("Inscricao realizada.");
    }

    private static void cancelarMatricula(Aluno aluno) {
        List<Inscricao> doAluno = obterInscricoes(aluno);
        if (doAluno.isEmpty()) {
            System.out.println("O aluno nao possui inscricoes para cancelar.");
            return;
        }
        for (int indice = 0; indice < doAluno.size(); indice++) {
            Inscricao inscricao = doAluno.get(indice);
            System.out.println((indice + 1) + ". " + inscricao.getDisciplina().getNome()
                    + " (" + inscricao.getTipo().getDescricao() + ")");
        }

        int escolha = lerInteiro("Escolha a matricula para cancelar (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > doAluno.size()) {
            System.out.println("Matricula invalida.");
            return;
        }
        inscricoes.remove(doAluno.get(escolha - 1));
        System.out.println("Matricula cancelada.");
    }

    private static void encerrarPeriodo() {
        if (estadoPeriodo != EstadoPeriodo.ABERTO) {
            System.out.println("Nao ha periodo de matriculas aberto.");
            return;
        }

        estadoPeriodo = EstadoPeriodo.ENCERRADO;
        System.out.println("Resultado das ofertas para o proximo semestre:");
        for (Curso curso : cursos) {
            for (Disciplina disciplina : curso.getDisciplinas()) {
                if (contarInscritos(disciplina) >= 3) {
                    disciplina.setEstado(EstadoDisciplina.ATIVA);
                } else {
                    disciplina.setEstado(EstadoDisciplina.CANCELADA);
                }
                System.out.println(curso.getNome() + " - " + disciplina.getNome() + ": "
                        + disciplina.getEstado().getDescricao() + " (" + contarInscritos(disciplina) + " alunos)");
            }
        }
    }

    private static void listarInscricoes(Aluno aluno) {
        List<Inscricao> doAluno = obterInscricoes(aluno);
        if (doAluno.isEmpty()) {
            System.out.println("Nenhuma disciplina selecionada.");
            return;
        }
        for (Inscricao inscricao : doAluno) {
            System.out.println("- " + inscricao.getDisciplina().getNome() + " ("
                    + inscricao.getTipo().getDescricao() + ")");
        }
    }

    private static List<Inscricao> obterInscricoes(Aluno aluno) {
        List<Inscricao> resultado = new ArrayList<>();
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getAluno() == aluno) {
                resultado.add(inscricao);
            }
        }
        return resultado;
    }

    private static Inscricao buscarInscricao(Aluno aluno, Disciplina disciplina) {
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getAluno() == aluno && inscricao.getDisciplina() == disciplina) {
                return inscricao;
            }
        }
        return null;
    }

    private static int contarInscritos(Disciplina disciplina) {
        int total = 0;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getDisciplina() == disciplina) {
                total++;
            }
        }
        return total;
    }

    private static int contarInscricoes(Aluno aluno, TipoInscricao tipo) {
        int total = 0;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getAluno() == aluno && inscricao.getTipo() == tipo) {
                total++;
            }
        }
        return total;
    }

    private static Aluno buscarAluno(String matricula) {
        for (Aluno aluno : alunos) {
            if (aluno.getMatricula().equals(matricula)) {
                return aluno;
            }
        }
        return null;
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Informe um numero inteiro.");
            }
        }
    }

    private static class Curso {
        private final String nome;
        private final int creditos;
        private final List<Disciplina> disciplinas;

        private Curso(String nome, int creditos, List<Disciplina> disciplinas) {
            this.nome = nome;
            this.creditos = creditos;
            this.disciplinas = new ArrayList<>(disciplinas);
        }

        private String getNome() {
            return nome;
        }

        private int getCreditos() {
            return creditos;
        }

        private List<Disciplina> getDisciplinas() {
            return disciplinas;
        }
    }

    private static class Disciplina {
        private final String nome;
        private EstadoDisciplina estado = EstadoDisciplina.AGUARDANDO;

        private Disciplina(String nome) {
            this.nome = nome;
        }

        private String getNome() {
            return nome;
        }

        private EstadoDisciplina getEstado() {
            return estado;
        }

        private void setEstado(EstadoDisciplina estado) {
            this.estado = estado;
        }
    }

    private static class Aluno {
        private final String matricula;
        private final String nome;
        private final Curso curso;

        private Aluno(String matricula, String nome, Curso curso) {
            this.matricula = matricula;
            this.nome = nome;
            this.curso = curso;
        }

        private String getMatricula() {
            return matricula;
        }

        private String getNome() {
            return nome;
        }

        private Curso getCurso() {
            return curso;
        }
    }

    private static class Inscricao {
        private final Aluno aluno;
        private final Disciplina disciplina;
        private final TipoInscricao tipo;

        private Inscricao(Aluno aluno, Disciplina disciplina, TipoInscricao tipo) {
            this.aluno = aluno;
            this.disciplina = disciplina;
            this.tipo = tipo;
        }

        private Aluno getAluno() {
            return aluno;
        }

        private Disciplina getDisciplina() {
            return disciplina;
        }

        private TipoInscricao getTipo() {
            return tipo;
        }
    }

    private enum EstadoPeriodo {
        NAO_INICIADO,
        ABERTO,
        ENCERRADO
    }

    private enum EstadoDisciplina {
        AGUARDANDO("Aguardando fechamento"),
        ATIVA("Ativa no proximo semestre"),
        CANCELADA("Cancelada");

        private final String descricao;

        EstadoDisciplina(String descricao) {
            this.descricao = descricao;
        }

        private String getDescricao() {
            return descricao;
        }
    }

    private enum TipoInscricao {
        OBRIGATORIA("1a opcao"),
        ALTERNATIVA("Alternativa");

        private final String descricao;

        TipoInscricao(String descricao) {
            this.descricao = descricao;
        }

        private String getDescricao() {
            return descricao;
        }
    }
}
