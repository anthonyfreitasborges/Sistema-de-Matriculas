import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class BancoTxt {
    private final Path arquivo;
    public BancoTxt(Path arquivo) {
        this.arquivo = arquivo.toAbsolutePath();
    }

    public Path getArquivo() {
        return arquivo;
    }

    public void salvar(SistemaMatriculas s) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("BANCO|1");
        for (Usuario u : s.getUsuarios()) {
            linhas.add(String.join("|", "USUARIO", u.getPerfil(), u.getLogin(), u.getNome(), u.getSenhaHash()));
        }
        for (Curso c : s.getCursos()) {
            linhas.add("CURSO|" + c.getId() + "|" + c.getNome() + "|" + c.getCreditos());
        }
        for (Disciplina d : s.getDisciplinas()) {
            linhas.add("DISCIPLINA|" + d.getId() + "|" + d.getNome() + "|" + d.getCreditos() + "|" + d.getCurso().getId());
        }
        for (Semestre p : s.getSemestres()) {
            linhas.add("SEMESTRE|" + p.getId() + "|" + p.getInicio() + "|" + p.getFim() + "|" + p.isEncerrado());
        }
        for (Oferta o : s.getOfertas()) {
            linhas.add("OFERTA|" + o.getSemestre().getId() + "|" + o.getDisciplina().getId() + "|" + o.getProfessor().getLogin() + "|" + o.getStatus());
        }
        for (Matricula m : s.getMatriculas()) {
            linhas.add("MATRICULA|" + m.getAluno().getLogin() + "|" + m.getOferta().getSemestre().getId() + "|" + m.getDisciplina().getId() + "|" + m.getTipo() + "|" + m.getStatus());
        }
        for (String evento : s.getCobrancas().getEventos()) {
            linhas.add("COBRANCA|" + evento);
        }
        Files.createDirectories(arquivo.getParent());
        Path temporario = Files.createTempFile(arquivo.getParent(), "banco-", ".tmp");
        try {
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    public SistemaMatriculas carregar() throws IOException {
        if (!Files.exists(arquivo)) {
            SistemaMatriculas exemplo = DadosExemplo.criar();
            salvar(exemplo);
            return exemplo;
        }
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        if (linhas.isEmpty() || !linhas.get(0).equals("BANCO|1")) {
            throw new IOException("Cabeçalho do banco TXT inválido. O arquivo foi preservado.");
        }
        SistemaMatriculas s = new SistemaMatriculas(new SistemaCobrancas());
        List<String> chaves = new ArrayList<>();
        int numero = 1;
        try {
            for (int i = 1; i < linhas.size(); i++) {
                numero = i + 1;
                String linha = linhas.get(i);
                if (linha.isBlank() || linha.startsWith("#")) {
                    continue;
                }
                String[] p = linha.split("\\|", -1);
                int esperado = 5;
                if (p[0].equals("CURSO")) {
                    esperado = 4;
                } else if (p[0].equals("MATRICULA") || p[0].equals("COBRANCA")) {
                    esperado = 6;
                }
                if (p.length != esperado) {
                    throw new IllegalArgumentException("Quantidade de campos inválida.");
                }
                for (String campo : p) {
                    SistemaMatriculas.texto(campo);
                }
                String chave = null;
                if (p[0].equals("USUARIO")) {
                    chave = "USUARIO|" + p[2];
                } else if (p[0].equals("OFERTA")) {
                    chave = "OFERTA|" + p[1] + "|" + p[2];
                } else if (p[0].equals("CURSO") || p[0].equals("DISCIPLINA") || p[0].equals("SEMESTRE")) {
                    chave = p[0] + "|" + p[1];
                }
                if (chave != null) {
                    if (chaves.contains(chave)) {
                        throw new IllegalArgumentException("Registro duplicado.");
                    }
                    chaves.add(chave);
                }
                switch (p[0]) {
                    case "USUARIO":
                        Usuario usuario = SistemaMatriculas.novoUsuario(p[1], p[3], p[2], "temporaria");
                        usuario.restaurarSenhaHash(p[4]);
                        s.adicionarUsuario(usuario);
                        break;
                    case "CURSO":
                        Curso curso = new Curso(p[1], p[2], Integer.parseInt(p[3]));
                        s.restaurarCurso(curso);
                        break;
                    case "DISCIPLINA":
                        Curso cursoDisciplina = s.buscarCurso(p[4]);
                        Disciplina disciplina = new Disciplina(p[1], p[2], Integer.parseInt(p[3]), cursoDisciplina);
                        s.restaurarDisciplina(disciplina);
                        break;
                    case "SEMESTRE":
                        if (!p[4].equals("true") && !p[4].equals("false")) {
                            throw new IllegalArgumentException("Situação do semestre inválida.");
                        }
                        Semestre semestre = new Semestre(p[1], LocalDate.parse(p[2]), LocalDate.parse(p[3]));
                        if (Boolean.parseBoolean(p[4])) {
                            semestre.encerrar();
                        }
                        s.restaurarSemestre(semestre);
                        break;
                    case "OFERTA":
                        Semestre semestreOferta = s.buscarSemestre(p[1]);
                        Disciplina disciplinaOferta = s.buscarDisciplina(p[2]);
                        Professor professor = (Professor) s.buscarUsuario(p[3]);
                        Oferta oferta = new Oferta(semestreOferta, disciplinaOferta, professor);
                        oferta.setStatus(Oferta.Status.valueOf(p[4]));
                        s.restaurarOferta(oferta);
                        break;
                    case "MATRICULA":
                        Aluno aluno = (Aluno) s.buscarUsuario(p[1]);
                        Oferta ofertaMatricula = s.buscarOferta(p[2], p[3]);
                        Matricula matricula = new Matricula(aluno, ofertaMatricula, Matricula.Tipo.valueOf(p[4]));
                        matricula.setStatus(Matricula.Status.valueOf(p[5]));
                        s.restaurarMatricula(matricula);
                        break;
                    case "COBRANCA":
                        s.getCobrancas().restaurarEvento(linha.substring("COBRANCA|".length()));
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo de registro desconhecido.");
                }
            }
            validar(s);
        } catch (RuntimeException e) {
            throw new IOException("Banco TXT inválido próximo à linha " + numero + ": " + e.getMessage() + ". O arquivo foi preservado.", e);
        }
        return s;
    }

    private void validar(SistemaMatriculas sistema) {
        boolean temSecretaria = false;
        for (Usuario usuario : sistema.getUsuarios()) {
            if (usuario instanceof Secretaria) {
                temSecretaria = true;
            }
        }
        if (!temSecretaria) {
            throw new IllegalArgumentException("Falta uma secretaria.");
        }
        for (Matricula matricula : sistema.getMatriculas()) {
            if (!matricula.isVigente()) {
                continue;
            }
            int quantidade = 0;
            for (Matricula outra : sistema.getMatriculas()) {
                if (outra.isVigente() && outra.getAluno() == matricula.getAluno()) {
                    if (outra != matricula && outra.getOferta() == matricula.getOferta()) {
                        throw new IllegalArgumentException("Matrícula duplicada.");
                    }
                    if (outra.getOferta().getSemestre() == matricula.getOferta().getSemestre()
                            && outra.getTipo() == matricula.getTipo()) {
                        quantidade++;
                    }
                }
            }
            int limite = 4;
            if (matricula.getTipo() == Matricula.Tipo.OPTATIVA) {
                limite = 2;
            }
            if (quantidade > limite) {
                throw new IllegalArgumentException("Limite de disciplinas excedido.");
            }
        }
        for (Oferta oferta : sistema.getOfertas()) {
            int alunos = sistema.inscritos(oferta).size();
            if (alunos > 60 || (oferta.getStatus() == Oferta.Status.CANCELADA && alunos > 0)) {
                throw new IllegalArgumentException("Quantidade de alunos inválida.");
            }
            if (oferta.getSemestre().isEncerrado()) {
                if (oferta.getStatus() == Oferta.Status.PREVISTA
                        || (oferta.getStatus() == Oferta.Status.ATIVA && alunos < 3)) {
                    throw new IllegalArgumentException("Turma encerrada inconsistente.");
                }
            } else if (oferta.getStatus() != Oferta.Status.PREVISTA) {
                throw new IllegalArgumentException("Turma de semestre aberto deve estar prevista.");
            }
        }
    }
}
