import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    private Scanner entrada = new Scanner(System.in);
    private BancoTxt banco;
    private SistemaMatriculas sistema;

    public App(BancoTxt banco, SistemaMatriculas sistema) {
        this.banco = banco;
        this.sistema = sistema;
    }

    public static void main(String[] args) {
        Path caminho = caminhoPadrao();
        if (args.length > 0) {
            caminho = Path.of(args[0]);
        }
        BancoTxt banco = new BancoTxt(caminho);
        try {
            SistemaMatriculas sistema = banco.carregar();
            App app = new App(banco, sistema);
            app.executar();
        } catch (IOException e) {
            System.out.println("Erro no arquivo: " + e.getMessage());
            System.out.println("O programa foi encerrado. A última alteração pode não ter sido salva.");
        }
    }

    static Path caminhoPadrao() {
        try {
            Path classes = Path.of(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path projeto = classes.getParent();
            Path bancoExistente = projeto.getParent().resolve("dados/banco.txt");
            if (Files.exists(bancoExistente)) {
                return bancoExistente;
            }
            return projeto.resolve("dados/banco.txt");
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível localizar a pasta do projeto.", e);
        }
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine().trim();
    }

    private String senha(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }

    private int numero(String mensagem) {
        try {
            return Integer.parseInt(ler(mensagem));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número inteiro.");
        }
    }

    private LocalDate data(String mensagem) {
        try {
            return LocalDate.parse(ler(mensagem + " (AAAA-MM-DD): "));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use AAAA-MM-DD.");
        }
    }

    private void executar() throws IOException {
        System.out.println("SISTEMA DE MATRÍCULAS");
        System.out.println("Arquivo de dados: " + banco.getArquivo());
        try {
            while (true) {
                String login = ler("\nLogin (0 para sair): ");
                if (login.equals("0")) {
                    return;
                }
                Usuario usuario = sistema.login(login, senha("Senha: "));
                if (usuario == null) {
                    System.out.println("Login ou senha incorretos.");
                } else {
                    System.out.println("Bem-vindo, " + usuario.getNome());
                    menu(usuario);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nPrograma encerrado.");
        }
    }

    private void menu(Usuario usuario) throws IOException {
        while (true) {
            if (usuario instanceof Secretaria) {
                System.out.println("\n1 Consultar cadastros\n2 Cadastrar/editar curso\n3 Cadastrar/editar disciplina\n4 Cadastrar usuário\n5 Editar usuário\n6 Definir período\n7 Montar currículo\n8 Consultar currículo\n9 Encerrar período\n10 Consultar cobranças\n0 Sair da conta");
            } else if (usuario instanceof Aluno) {
                System.out.println("\n1 Consultar disciplinas\n2 Matricular\n3 Cancelar matrícula\n4 Minhas matrículas\n0 Sair da conta");
            } else {
                System.out.println("\n1 Consultar minhas turmas e alunos\n0 Sair da conta");
            }
            try {
                int opcao = numero("Opção: ");
                if (opcao == 0) {
                    return;
                }
                boolean alterou = false;
                if (usuario instanceof Secretaria) {
                    alterou = menuSecretaria(usuario, opcao);
                } else if (usuario instanceof Aluno) {
                    alterou = menuAluno((Aluno) usuario, opcao);
                } else if (opcao == 1) {
                    listarProfessor((Professor) usuario);
                } else {
                    System.out.println("Opção inválida.");
                }
                if (alterou) {
                    banco.salvar(sistema);
                    System.out.println("Dados salvos.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean menuSecretaria(Usuario usuario, int opcao) {
        switch (opcao) {
            case 1:
                listarCadastros();
                return false;
            case 2:
                String curso = ler("ID do curso (existente para editar): ");
                String nomeCurso = ler("Nome: ");
                int creditosCurso = numero("Créditos: ");
                sistema.salvarCurso(usuario, curso, nomeCurso, creditosCurso);
                break;
            case 3:
                String disciplina = ler("ID da disciplina (existente para editar): ");
                String nomeDisciplina = ler("Nome: ");
                int creditosDisciplina = numero("Créditos: ");
                String cursoId = ler("ID do curso: ");
                sistema.salvarDisciplina(usuario, disciplina, nomeDisciplina, creditosDisciplina, cursoId);
                break;
            case 4:
                int tipo = numero("1 Aluno / 2 Professor / 3 Secretaria: ");
                String perfil;
                if (tipo == 1) {
                    perfil = "ALUNO";
                } else if (tipo == 2) {
                    perfil = "PROFESSOR";
                } else if (tipo == 3) {
                    perfil = "SECRETARIA";
                } else {
                    throw new IllegalArgumentException("Perfil inválido.");
                }
                String nome = ler("Nome: ");
                String login = ler("Login: ");
                String senha = senha("Senha: ");
                sistema.cadastrarUsuario(usuario, perfil, nome, login, senha);
                break;
            case 5:
                String loginUsuario = ler("Login do usuário: ");
                String novoNome = ler("Nome atualizado: ");
                String novaSenha = senha("Nova senha (Enter para manter): ");
                sistema.atualizarUsuario(usuario, loginUsuario, novoNome, novaSenha);
                break;
            case 6:
                String semestre = ler("Semestre (ex.: 2026.2): ");
                LocalDate inicio = data("Início");
                LocalDate fim = data("Fim");
                sistema.definirPeriodo(usuario, semestre, inicio, fim);
                break;
            case 7:
                String semestreOferta = escolherSemestre();
                String disciplinaOferta = ler("ID da disciplina: ");
                String professor = ler("Login do professor: ");
                sistema.oferecerDisciplina(usuario, semestreOferta, disciplinaOferta, professor);
                break;
            case 8:
                listarCurriculo(escolherSemestre());
                return false;
            case 9:
                String periodo = escolherSemestre();
                String confirmar = ler("Encerrar definitivamente este período? (s/n): ");
                if (!confirmar.equalsIgnoreCase("s")) {
                    return false;
                }
                sistema.encerrarPeriodo(usuario, periodo);
                listarCurriculo(periodo);
                break;
            case 10:
                for (String evento : sistema.getCobrancas().getEventos()) {
                    System.out.println(evento);
                }
                return false;
            default:
                throw new IllegalArgumentException("Opção inválida.");
        }
        return true;
    }

    private boolean menuAluno(Aluno aluno, int opcao) {
        switch (opcao) {
            case 1:
                listarCurriculo(escolherSemestre());
                return false;
            case 2:
                matricularAluno(aluno);
                break;
            case 3:
                String semestreCancelar = escolherSemestre();
                String disciplinaCancelar = escolherDisciplina(aluno, semestreCancelar, true);
                sistema.cancelarMatricula(aluno, semestreCancelar, disciplinaCancelar);
                break;
            case 4:
                List<Matricula> matriculas = sistema.matriculasDoAluno(aluno);
                if (matriculas.isEmpty()) {
                    System.out.println("Nenhuma matrícula.");
                }
                for (Matricula matricula : matriculas) {
                    System.out.println(matricula.getOferta().getSemestre().getId() + " | "
                            + matricula.getDisciplina().getNome() + " | " + matricula.getTipo() + " | " + matricula.getStatus());
                }
                System.out.println("\n1 Matricular em outra disciplina\n0 Voltar ao menu do aluno");
                int escolha = numero("Opção: ");
                if (escolha == 0) {
                    return false;
                }
                if (escolha != 1) {
                    throw new IllegalArgumentException("Opção inválida.");
                }
                matricularAluno(aluno);
                break;
            default:
                throw new IllegalArgumentException("Opção inválida.");
        }
        return true;
    }

    private void matricularAluno(Aluno aluno) {
        String semestre = escolherSemestre();
        String disciplina = escolherDisciplina(aluno, semestre, false);
        int escolha = numero("1 Obrigatória / 2 Optativa: ");
        Matricula.Tipo tipo;
        if (escolha == 1) {
            tipo = Matricula.Tipo.OBRIGATORIA;
        } else if (escolha == 2) {
            tipo = Matricula.Tipo.OPTATIVA;
        } else {
            throw new IllegalArgumentException("Tipo inválido.");
        }
        sistema.matricular(aluno, semestre, disciplina, tipo);
        System.out.println("Matrícula realizada em " + sistema.buscarDisciplina(disciplina).getNome() + ".");
    }

    private void listarCadastros() {
        for (Curso curso : sistema.getCursos()) {
            System.out.println(curso);
        }
        for (Disciplina disciplina : sistema.getDisciplinas()) {
            System.out.println(disciplina);
        }
        for (Usuario usuario : sistema.getUsuarios()) {
            System.out.println(usuario.getLogin() + " - " + usuario.getNome() + " - " + usuario.getPerfil());
        }
        for (Semestre semestre : sistema.getSemestres()) {
            System.out.println(semestre);
        }
    }

    private String escolherSemestre() {
        List<Semestre> semestres = sistema.getSemestres();
        if (semestres.isEmpty()) {
            throw new IllegalArgumentException("A secretaria ainda não cadastrou um semestre.");
        }
        if (semestres.size() == 1) {
            System.out.println("Semestre: " + semestres.get(0));
            return semestres.get(0).getId();
        }
        for (int i = 0; i < semestres.size(); i++) {
            System.out.println((i + 1) + " - " + semestres.get(i));
        }
        String escolha = ler("Número ou código do semestre: ");
        for (int i = 0; i < semestres.size(); i++) {
            if (escolha.equals(String.valueOf(i + 1)) || escolha.equals(semestres.get(i).getId())) {
                return semestres.get(i).getId();
            }
        }
        throw new IllegalArgumentException("Escolha um dos semestres listados.");
    }

    private String escolherDisciplina(Aluno aluno, String semestreId, boolean cancelar) {
        Semestre semestre = sistema.buscarSemestre(semestreId);
        sistema.verificarPeriodo(semestre);
        List<Oferta> opcoes = new ArrayList<>();
        for (Oferta oferta : sistema.getOfertas()) {
            if (oferta.getSemestre() != semestre) {
                continue;
            }
            boolean matriculado = false;
            for (Matricula matricula : sistema.matriculasDoAluno(aluno)) {
                if (matricula.getOferta() == oferta && matricula.isVigente()) {
                    matriculado = true;
                }
            }
            if (cancelar && matriculado) {
                opcoes.add(oferta);
            } else if (!cancelar) {
                if (matriculado) {
                    System.out.println(oferta.getDisciplina().getNome() + " - você já está matriculado.");
                } else if (oferta.getStatus() == Oferta.Status.CANCELADA) {
                    System.out.println(oferta.getDisciplina().getNome() + " - turma cancelada.");
                } else if (sistema.inscritos(oferta).size() >= 60) {
                    System.out.println(oferta.getDisciplina().getNome() + " - turma lotada.");
                } else {
                    opcoes.add(oferta);
                }
            }
        }
        if (opcoes.isEmpty()) {
            if (cancelar) {
                throw new IllegalArgumentException("Você não tem matrículas para cancelar neste semestre.");
            }
            throw new IllegalArgumentException("Não há novas disciplinas disponíveis para você neste semestre. A secretaria pode incluir outras no currículo.");
        }
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println((i + 1) + " - " + opcoes.get(i).getDisciplina());
        }
        String escolha = ler("Número ou código da disciplina: ");
        for (int i = 0; i < opcoes.size(); i++) {
            String id = opcoes.get(i).getDisciplina().getId();
            if (escolha.equals(String.valueOf(i + 1)) || escolha.equalsIgnoreCase(id)) {
                return id;
            }
        }
        throw new IllegalArgumentException("Escolha uma das disciplinas listadas.");
    }

    private void listarCurriculo(String semestreId) {
        Semestre semestre = sistema.buscarSemestre(semestreId);
        boolean encontrou = false;
        for (Oferta oferta : sistema.getOfertas()) {
            if (oferta.getSemestre() == semestre) {
                encontrou = true;
                int total = sistema.inscritos(oferta).size();
                String situacao;
                if (!semestre.permiteMatricula(LocalDate.now())) {
                    situacao = "Inscrições fechadas";
                } else if (total == 60) {
                    situacao = "Turma lotada";
                } else {
                    situacao = "Vagas: " + (60 - total);
                }
                System.out.println(oferta.getDisciplina() + " | " + oferta.getProfessor().getNome()
                        + " | " + total + " alunos | " + oferta.getStatus() + " | " + situacao);
            }
        }
        if (!encontrou) {
            System.out.println("Nenhuma disciplina neste currículo.");
        }
    }

    private void listarProfessor(Professor professor) {
        boolean encontrou = false;
        for (Oferta oferta : sistema.getOfertas()) {
            if (oferta.getProfessor() == professor) {
                encontrou = true;
                System.out.println("\n" + oferta.getSemestre().getId() + " - " + oferta.getDisciplina().getNome() + " - " + oferta.getStatus());
                List<Matricula> alunos = sistema.consultarAlunos(professor, oferta);
                if (alunos.isEmpty()) {
                    System.out.println("Sem alunos matriculados.");
                }
                for (Matricula matricula : alunos) {
                    System.out.println(matricula.getAluno().getNome());
                }
            }
        }
        if (!encontrou) {
            System.out.println("Nenhuma turma atribuída.");
        }
    }
}
