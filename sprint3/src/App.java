import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Curso> cursos = new ArrayList<>();
    private static final List<Aluno> alunos = new ArrayList<>();
    private static final List<Professor> professores = new ArrayList<>();
    private static final SistemaCobrancas cobrancas = new SistemaCobrancas();
    private static final SistemaMatriculas sistema = new SistemaMatriculas(cobrancas);
    private static final Secretaria secretaria = new Secretaria("secretaria", "secretaria123");

    public static void main(String[] args) {
        PersistenciaDados.carregar(cursos, alunos, professores, sistema);
        boolean executando = true;
        
        while (executando) {
            System.out.println("\n=== Sistema de Matriculas ===");
            System.out.println("Acesso inicial da secretaria: login secretaria | senha secretaria123");
            System.out.println("0. Sair");
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            if (login.equals("0")) {
                break;
            }
            System.out.print("Senha: ");
            Usuario usuario = autenticar(login, scanner.nextLine());
            if (usuario == null) {
                System.out.println("Login ou senha invalidos.");
            } else if (usuario instanceof Secretaria) {
                menuSecretaria();
            } else if (usuario instanceof Aluno) {
                menuAluno((Aluno) usuario);
            } else if (usuario instanceof Professor) {
                menuProfessor((Professor) usuario);
            }
        }
        System.out.println("Encerrando o sistema.");
    }

    private static Usuario autenticar(String login, String senha) {
        if (secretaria.getLogin().equals(login) && secretaria.validarSenha(senha)) {
            return secretaria;
        }
        for (Aluno aluno : alunos) {
            if (aluno.getLogin().equals(login) && aluno.validarSenha(senha)) {
                return aluno;
            }
        }
        for (Professor professor : professores) {
            if (professor.getLogin().equals(login) && professor.validarSenha(senha)) {
                return professor;
            }
        }
        return null;
    }

    private static void menuSecretaria() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== Secretaria Academica ===");
            System.out.println("1. Cadastrar curso");
            System.out.println("2. Listar cursos");
            System.out.println("3. Cadastrar aluno");
            System.out.println("4. Listar alunos");
            System.out.println("5. Abrir periodo de matriculas");
            System.out.println("6. Encerrar periodo de matriculas");
            System.out.println("7. Cadastrar professor");
            System.out.println("8. Associar professor a disciplina");
            System.out.println("9. Consultar notificacoes de cobranca");
            System.out.println("10. Listar disciplinas e alunos inscritos");
            System.out.println("11. Listar perfis e logins");
            System.out.println("0. Sair da conta");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1:
                    cadastrarCurso();
                    break;
                case 2:
                    listarCursos();
                    break;
                case 3:
                    cadastrarAluno();
                    break;
                case 4:
                    listarAlunos();
                    break;
                case 5:
                    System.out.println(sistema.iniciarPeriodo()
                            ? "Periodo de matriculas aberto."
                            : "Nao foi possivel abrir o periodo.");
                    break;
                case 6:
                    encerrarPeriodo();
                    break;
                case 7:
                    cadastrarProfessor();
                    break;
                case 8:
                    associarProfessor();
                    break;
                case 9:
                    listarNotificacoesCobranca();
                    break;
                case 10:
                    listarDisciplinas();
                    break;
                case 11:
                    listarPerfis();
                    break;
                case 0:
                    executando = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void menuAluno(Aluno aluno) {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== Portal do Aluno: " + aluno.getNome() + " ===");
            System.out.println("1. Matricular ou cancelar disciplinas");
            System.out.println("0. Sair da conta");
            int opcao = lerInteiro("Escolha uma opcao: ");
            if (opcao == 1) {
                gerenciarMatriculas(aluno);
            } else if (opcao == 0) {
                executando = false;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void menuProfessor(Professor professor) {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== Portal do Professor: " + professor.getNome() + " ===");
            System.out.println("1. Consultar alunos matriculados");
            System.out.println("0. Sair da conta");
            int opcao = lerInteiro("Escolha uma opcao: ");
            if (opcao == 1) {
                professor.consultarAlunos();
                for (Disciplina disciplina : professor.getDisciplinas()) {
                    System.out.println("\n" + disciplina.getNome() + " | "
                            + (disciplina.isAtiva() ? "Ativa" : "Aguardando ou cancelada"));
                    if (disciplina.getAlunos().isEmpty()) {
                        System.out.println("Nenhum aluno inscrito.");
                    }
                    for (Aluno matriculado : disciplina.getAlunos()) {
                        System.out.println("- " + matriculado.getMatricula() + " - " + matriculado.getNome());
                    }
                }
            } else if (opcao == 0) {
                executando = false;
            } else {
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
        Curso curso = new Curso(nome, lerPositivo("Total de creditos do curso: "));
        System.out.println("Informe as disciplinas do curso. Deixe o nome vazio para concluir.");
        while (true) {
            System.out.print("Nome da disciplina: ");
            String nomeDisciplina = scanner.nextLine().trim();
            if (nomeDisciplina.isEmpty()) {
                if (curso.getDisciplinas().isEmpty()) {
                    System.out.println("Cadastre pelo menos uma disciplina.");
                    continue;
                }
                break;
            }
            Disciplina disciplina = new Disciplina(nomeDisciplina,
                    lerPositivo("Creditos da disciplina: "));
            curso.adicionarDisciplina(disciplina);
            PersistenciaDados.salvar(cursos, alunos, professores);
            sistema.adicionarDisciplina(disciplina);
        }
        cursos.add(curso);
        System.out.println("Curso cadastrado com sucesso.");
    }

    private static void listarCursos() {
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }
        for (Curso curso : cursos) {
            System.out.println("\n" + curso.getNome() + " | Creditos: " + curso.getCreditos());
            for (Disciplina disciplina : curso.getDisciplinas()) {
                String professor = disciplina.getProfessor() == null
                        ? "Sem professor" : disciplina.getProfessor().getNome();
                System.out.println("- " + disciplina.getNome() + " | Creditos: " + disciplina.getCreditos()
                        + " | Alunos: " + disciplina.getAlunos().size() + "/60"
                        + " | Professor: " + professor);
            }
        }
    }

    private static void listarDisciplinas() {
        boolean encontrouDisciplina = false;
        for (Curso curso : cursos) {
            for (Disciplina disciplina : curso.getDisciplinas()) {
                encontrouDisciplina = true;
                System.out.println("\n" + curso.getNome() + " - " + disciplina.getNome());
                System.out.println("Alunos inscritos: " + disciplina.getAlunos().size() + "/60");
                if (disciplina.getAlunos().isEmpty()) {
                    System.out.println("Nenhum aluno inscrito.");
                } else {
                    for (Aluno aluno : disciplina.getAlunos()) {
                        System.out.println("- " + aluno.getNome() + " (" + aluno.getMatricula() + ")");
                    }
                }
            }
        }
        if (!encontrouDisciplina) {
            System.out.println("Nenhuma disciplina cadastrada.");
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
        System.out.print("Login do aluno: ");
        String login = scanner.nextLine().trim();
        if (login.isEmpty() || !loginDisponivel(login)) {
            System.out.println("Login vazio ou ja cadastrado.");
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
        alunos.add(new Aluno(matricula, login, lerSenha(), nome, cursos.get(indiceCurso)));
       PersistenciaDados.salvar(cursos, alunos, professores); 
        System.out.println("Aluno cadastrado com sucesso.");
    }

    private static void listarAlunos() {
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        for (Aluno aluno : alunos) {
            System.out.println(aluno.getNome() + " | Matricula: " + aluno.getMatricula()
                + " | Login: " + aluno.getLogin() + " | Curso: " + aluno.getCurso().getNome());
        }
    }

    private static void listarPerfis() {
        System.out.println("Secretaria | Login: " + secretaria.getLogin());
        for (Aluno aluno : alunos) {
            System.out.println("Aluno: " + aluno.getNome() + " | Login: " + aluno.getLogin());
        }
        for (Professor professor : professores) {
            System.out.println("Professor: " + professor.getNome() + " | Login: " + professor.getLogin());
        }
    }

    private static void cadastrarProfessor() {
        System.out.print("Login do professor: ");
        String login = scanner.nextLine().trim();
        if (login.isEmpty() || !loginDisponivel(login)) {
            System.out.println("Login vazio ou ja cadastrado.");
            return;
        }
        System.out.print("Nome do professor: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("O nome do professor nao pode ficar vazio.");
            return;
        }
            professores.add(new Professor(login, lerSenha(), nome));
      PersistenciaDados.salvar(cursos, alunos, professores);
            System.out.println("Professor cadastrado com sucesso.");
    }

    private static void associarProfessor() {
        if (cursos.isEmpty() || professores.isEmpty()) {
            System.out.println("Cadastre pelo menos um curso e um professor antes da associacao.");
            return;
        }
        int indiceCurso = escolherCurso();
        if (indiceCurso < 0) {
            return;
        }
        List<Disciplina> disciplinas = cursos.get(indiceCurso).getDisciplinas();
        for (int indice = 0; indice < disciplinas.size(); indice++) {
            System.out.println((indice + 1) + ". " + disciplinas.get(indice).getNome());
        }
        int indiceDisciplina = lerInteiro("Escolha a disciplina: ") - 1;
        if (indiceDisciplina < 0 || indiceDisciplina >= disciplinas.size()) {
            System.out.println("Disciplina invalida.");
            return;
        }
        for (int indice = 0; indice < professores.size(); indice++) {
            Professor professor = professores.get(indice);
            System.out.println((indice + 1) + ". " + professor.getNome()
                    + " (" + professor.getLogin() + ")");
        }
        int indiceProfessor = lerInteiro("Escolha o professor: ") - 1;
        if (indiceProfessor < 0 || indiceProfessor >= professores.size()) {
            System.out.println("Professor invalido.");
            return;
        }
        disciplinas.get(indiceDisciplina).setProfessor(professores.get(indiceProfessor));
        System.out.println("Professor associado a disciplina.");
    }

    private static void gerenciarMatriculas(Aluno aluno) {
        if (!sistema.isPeriodoMatricula()) {
            System.out.println("Matriculas e cancelamentos so podem ser feitos durante o periodo aberto.");
            return;
        }
        boolean gerenciando = true;
        while (gerenciando) {
            System.out.println("\nMatriculas de " + aluno.getNome() + ":");
            listarMatriculas(aluno);
            System.out.println("1. Adicionar disciplina");
            System.out.println("2. Cancelar matricula");
            System.out.println("0. Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            if (opcao == 1) {
                adicionarMatricula(aluno);
            } else if (opcao == 2) {
                cancelarMatricula(aluno);
            } else if (opcao == 0) {
                gerenciando = false;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void adicionarMatricula(Aluno aluno) {
        List<Disciplina> disciplinas = aluno.getCurso().getDisciplinas();
        for (int indice = 0; indice < disciplinas.size(); indice++) {
            Disciplina disciplina = disciplinas.get(indice);
            System.out.println((indice + 1) + ". " + disciplina.getNome()
                    + " | Alunos: " + disciplina.getAlunos().size() + "/60");
        }
        int escolha = lerInteiro("Escolha a disciplina (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > disciplinas.size()) {
            System.out.println("Disciplina invalida.");
            return;
        }
        System.out.println("1. Primeira opcao obrigatoria");
        System.out.println("2. Alternativa optativa");
        int tipoEscolhido = lerInteiro("Tipo de inscricao: ");
        TipoInscricao tipo = tipoEscolhido == 1 ? TipoInscricao.OBRIGATORIA
                : tipoEscolhido == 2 ? TipoInscricao.ALTERNATIVA : null;
        if (tipo == null) {
            System.out.println("Tipo de inscricao invalido.");
            return;
        }
        if (sistema.matricular(aluno, disciplinas.get(escolha - 1), tipo)) {
           PersistenciaDados.salvar(cursos, alunos, professores);

            System.out.println("Inscricao realizada.");
        } else {
            System.out.println("Inscricao nao realizada: verifique limites, duplicidade e periodo.");
        }
    }

    private static void cancelarMatricula(Aluno aluno) {
        List<Matricula> matriculas = aluno.getMatriculas();
        if (matriculas.isEmpty()) {
            System.out.println("O aluno nao possui inscricoes para cancelar.");
            return;
        }
        for (int indice = 0; indice < matriculas.size(); indice++) {
            System.out.println((indice + 1) + ". " + matriculas.get(indice).getDisciplina().getNome());
        }
        int escolha = lerInteiro("Escolha a matricula para cancelar (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > matriculas.size()) {
            System.out.println("Matricula invalida.");
            return;
        }
        if (sistema.cancelarMatricular(aluno, matriculas.get(escolha - 1).getDisciplina())) {
           PersistenciaDados.salvar(cursos, alunos, professores);
            System.out.println("Matricula cancelada.");
        } else {
            System.out.println("Nao foi possivel cancelar a matricula.");
        }
    }

    private static void listarMatriculas(Aluno aluno) {
        if (aluno.getMatriculas().isEmpty()) {
            System.out.println("Nenhuma disciplina selecionada.");
            return;
        }
        for (Matricula matricula : aluno.getMatriculas()) {
            System.out.println("- " + matricula.getDisciplina().getNome()
                    + " (" + matricula.getTipo().getDescricao() + ")");
        }
    }

    private static void encerrarPeriodo() {
        if (!sistema.fimPeriodo()) {
           PersistenciaDados.salvar(cursos, alunos, professores);
            System.out.println("Nao ha periodo de matriculas aberto.");
            return;
        }
        System.out.println("Resultado das ofertas para o proximo semestre:");
        for (Disciplina disciplina : sistema.getDisciplinas()) {
            System.out.println(disciplina.getNome() + ": " + (disciplina.isAtiva() ? "Ativa" : "Cancelada")
                    + " (" + disciplina.getAlunos().size() + " alunos)");
        }
    }

    private static void listarNotificacoesCobranca() {
        List<String> notificacoes = cobrancas.getNotificacoes();
        if (notificacoes.isEmpty()) {
            System.out.println("Nenhuma notificacao de cobranca registrada.");
            return;
        }
        for (String notificacao : notificacoes) {
            System.out.println(notificacao);
        }
    }

    private static int escolherCurso() {
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

    private static boolean loginDisponivel(String login) {
        if (secretaria.getLogin().equals(login)) {
            return false;
        }
        for (Aluno aluno : alunos) {
            if (aluno.getLogin().equals(login)) {
                return false;
            }
        }
        for (Professor professor : professores) {
            if (professor.getLogin().equals(login)) {
                return false;
            }
        }
        return true;
    }

    private static Aluno buscarAluno(String matricula) {
        for (Aluno aluno : alunos) {
            if (aluno.getMatricula().equals(matricula)) {
                return aluno;
            }
        }
        return null;
    }

    private static String lerSenha() {
        while (true) {
            System.out.print("Senha (minimo 4 caracteres): ");
            String senha = scanner.nextLine();
            if (senha.length() >= 4) {
                return senha;
            }
            System.out.println("A senha deve ter pelo menos 4 caracteres.");
        }
    }

    private static int lerPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor > 0) {
                return valor;
            }
            System.out.println("Informe um numero maior que zero.");
        }
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
}
