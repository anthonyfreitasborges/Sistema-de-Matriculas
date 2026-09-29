import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaMatriculas {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Semestre> semestres = new ArrayList<>();
    private final List<Oferta> ofertas = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();
    private final SistemaCobrancas cobrancas;
    public SistemaMatriculas(SistemaCobrancas cobrancas) {
        this.cobrancas = cobrancas;
    }

    static String texto(String valor) {
        if (valor == null || valor.isBlank() || valor.contains("|") || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException("Preencha o campo sem usar | ou quebras de linha.");
        }
        return valor.trim();
    }

    private void secretaria(Usuario usuario) {
        if (!(usuario instanceof Secretaria) || !usuarios.contains(usuario)) {
            throw new IllegalArgumentException("Acesso exclusivo da secretaria.");
        }
    }

    private void aluno(Aluno aluno) {
        if (!usuarios.contains(aluno)) {
            throw new IllegalArgumentException("Aluno não cadastrado.");
        }
    }

    public Usuario login(String login, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login) && usuario.validarSenha(senha)) {
                return usuario;
            }
        }
        return null;
    }

    public void cadastrarUsuario(Usuario operador, String perfil, String nome, String login, String senha) {
        secretaria(operador);
        adicionarUsuario(novoUsuario(perfil, texto(nome), texto(login), senha));
    }

    static Usuario novoUsuario(String perfil, String nome, String login, String senha) {
        if (perfil.equals("ALUNO")) {
            return new Aluno(nome, login, senha);
        }
        if (perfil.equals("PROFESSOR")) {
            return new Professor(nome, login, senha);
        }
        if (perfil.equals("SECRETARIA")) {
            return new Secretaria(nome, login, senha);
        }
        throw new IllegalArgumentException("Perfil inválido.");
    }

    void adicionarUsuario(Usuario usuario) {
        texto(usuario.getNome());
        texto(usuario.getLogin());
        for (Usuario cadastrado : usuarios) {
            if (cadastrado.getLogin().equals(usuario.getLogin())) {
                throw new IllegalArgumentException("Login já cadastrado.");
            }
        }
        usuarios.add(usuario);
    }

    public void atualizarUsuario(Usuario operador, String login, String nome, String senha) {
        secretaria(operador);
        Usuario usuario = buscarUsuario(login);
        String validado = texto(nome);
        if (!senha.isEmpty()) {
            usuario.alterarSenha(senha);
        }
        usuario.setNome(validado);
    }

    public void salvarCurso(Usuario operador, String id, String nome, int creditos) {
        secretaria(operador);
        id = texto(id);
        nome = texto(nome);
        for (Curso curso : cursos) {
            if (curso.getId().equals(id)) {
                curso.atualizar(nome, creditos);
                return;
            }
        }
        cursos.add(new Curso(id, nome, creditos));
    }

    public void salvarDisciplina(Usuario operador, String id, String nome, int creditos, String cursoId) {
        secretaria(operador);
        id = texto(id);
        nome = texto(nome);
        Curso curso = buscarCurso(cursoId);
        for (Disciplina disciplina : disciplinas) {
            if (disciplina.getId().equals(id)) {
                if (disciplina.getCurso() != curso) {
                    throw new IllegalArgumentException("Não é possível trocar o curso de uma disciplina existente.");
                }
                disciplina.atualizar(nome, creditos);
                return;
            }
        }
        disciplinas.add(new Disciplina(id, nome, creditos, curso));
    }

    public void definirPeriodo(Usuario operador, String id, LocalDate inicio, LocalDate fim) {
        secretaria(operador);
        id = texto(id);
        for (Semestre semestre : semestres) {
            if (semestre.getId().equals(id)) {
                if (semestre.isEncerrado()) {
                    throw new IllegalArgumentException("Semestre encerrado não pode ser reaberto. Cadastre outro semestre.");
                }
                semestre.definirPeriodo(inicio, fim);
                return;
            }
        }
        semestres.add(new Semestre(id, inicio, fim));
    }

    public void oferecerDisciplina(Usuario operador, String semestreId, String disciplinaId, String professorLogin) {
        secretaria(operador);
        Semestre semestre = buscarSemestre(semestreId);
        if (semestre.isEncerrado()) {
            throw new IllegalArgumentException("Semestre encerrado.");
        }
        Disciplina disciplina = buscarDisciplina(disciplinaId);
        Usuario professor = buscarUsuario(professorLogin);
        if (!(professor instanceof Professor)) {
            throw new IllegalArgumentException("Informe o login de um professor.");
        }
        for (Oferta oferta : ofertas) {
            if (oferta.getSemestre() == semestre && oferta.getDisciplina() == disciplina) {
                throw new IllegalArgumentException("Disciplina já incluída no currículo deste semestre.");
            }
        }
        ofertas.add(new Oferta(semestre, disciplina, (Professor) professor));
    }

    public void matricular(Aluno aluno, String semestreId, String disciplinaId, Matricula.Tipo tipo) {
        aluno(aluno);
        Oferta oferta = buscarOferta(semestreId, disciplinaId);
        verificarPeriodo(oferta);
        if (tipo == null) {
            throw new IllegalArgumentException("Escolha obrigatória ou optativa.");
        }
        int quantidade = 0;
        for (Matricula matricula : matriculas) {
            if (matricula.getAluno() == aluno && matricula.isVigente()) {
                if (matricula.getOferta() == oferta) {
                    throw new IllegalArgumentException("Você já está matriculado nesta disciplina.");
                }
                if (matricula.getOferta().getSemestre() == oferta.getSemestre()
                        && matricula.getTipo() == tipo) {
                    quantidade++;
                }
            }
        }
        int limite = 4;
        if (tipo == Matricula.Tipo.OPTATIVA) {
            limite = 2;
        }
        if (quantidade >= limite) {
            throw new IllegalArgumentException("Limite de " + limite + " disciplinas do tipo " + tipo + " atingido.");
        }
        if (inscritos(oferta).size() >= 60) {
            throw new IllegalArgumentException("Turma lotada: limite de 60 alunos.");
        }
        Matricula matricula = new Matricula(aluno, oferta, tipo);
        matriculas.add(matricula);
        cobrancas.notificar("INCLUSAO", matricula);
    }

    public void verificarPeriodo(Semestre semestre) {
        if (semestre.isEncerrado()) {
            throw new IllegalArgumentException("O semestre " + semestre.getId() + " já foi encerrado pela secretaria.");
        }
        LocalDate hoje = LocalDate.now();
        if (hoje.isBefore(semestre.getInicio())) {
            throw new IllegalArgumentException("As matrículas começam em " + semestre.getInicio() + ".");
        }
        if (hoje.isAfter(semestre.getFim())) {
            throw new IllegalArgumentException("O prazo terminou em " + semestre.getFim() + ". Procure a secretaria para ajustar o período.");
        }
    }

    private void verificarPeriodo(Oferta oferta) {
        verificarPeriodo(oferta.getSemestre());
        if (oferta.getStatus() == Oferta.Status.CANCELADA) {
            throw new IllegalArgumentException("Disciplina cancelada.");
        }
    }

    public void cancelarMatricula(Aluno aluno, String semestreId, String disciplinaId) {
        aluno(aluno);
        Oferta oferta = buscarOferta(semestreId, disciplinaId);
        verificarPeriodo(oferta);
        for (Matricula matricula : matriculas) {
            if (matricula.getAluno() == aluno && matricula.getOferta() == oferta && matricula.isVigente()) {
                matricula.setStatus(Matricula.Status.CANCELADA_ALUNO);
                cobrancas.notificar("CANCELAMENTO_ALUNO", matricula);
                return;
            }
        }
        throw new IllegalArgumentException("Matrícula não encontrada.");
    }

    public void encerrarPeriodo(Usuario operador, String semestreId) {
        secretaria(operador);
        Semestre semestre = buscarSemestre(semestreId);
        if (semestre.isEncerrado()) {
            throw new IllegalArgumentException("Semestre já encerrado.");
        }
        if (LocalDate.now().isBefore(semestre.getInicio())) {
            throw new IllegalArgumentException("O período ainda não começou.");
        }
        for (Oferta oferta : ofertas) {
            if (oferta.getSemestre() != semestre) {
                continue;
            }
            List<Matricula> inscritos = inscritos(oferta);
            if (inscritos.size() >= 3) {
                oferta.setStatus(Oferta.Status.ATIVA);
            } else {
                oferta.setStatus(Oferta.Status.CANCELADA);
                for (Matricula matricula : inscritos) {
                    matricula.setStatus(Matricula.Status.CANCELADA_DISCIPLINA);
                    cobrancas.notificar("CANCELAMENTO_DISCIPLINA", matricula);
                }
            }
        }
        semestre.encerrar();
    }

    public List<Matricula> consultarAlunos(Professor professor, Oferta oferta) {
        if (!usuarios.contains(professor) || oferta.getProfessor() != professor || !ofertas.contains(oferta)) {
            throw new IllegalArgumentException("O professor só pode consultar suas próprias turmas.");
        }
        return inscritos(oferta);
    }

    public List<Matricula> inscritos(Oferta oferta) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            if (matricula.getOferta() == oferta && matricula.isVigente()) {
                resultado.add(matricula);
            }
        }
        return resultado;
    }

    public List<Matricula> matriculasDoAluno(Aluno aluno) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            if (matricula.getAluno() == aluno) {
                resultado.add(matricula);
            }
        }
        return resultado;
    }

    public Usuario buscarUsuario(String login) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        throw new IllegalArgumentException("Usuário não encontrado.");
    }

    public Curso buscarCurso(String id) {
        for (Curso curso : cursos) {
            if (curso.getId().equals(id)) {
                return curso;
            }
        }
        throw new IllegalArgumentException("Curso não encontrado.");
    }

    public Disciplina buscarDisciplina(String id) {
        for (Disciplina disciplina : disciplinas) {
            if (disciplina.getId().equals(id)) {
                return disciplina;
            }
        }
        throw new IllegalArgumentException("Disciplina não encontrada.");
    }

    public Semestre buscarSemestre(String id) {
        for (Semestre semestre : semestres) {
            if (semestre.getId().equals(id)) {
                return semestre;
            }
        }
        throw new IllegalArgumentException("Semestre não encontrado.");
    }

    public Oferta buscarOferta(String semestre, String disciplina) {
        for (Oferta oferta : ofertas) {
            if (oferta.getSemestre().getId().equals(semestre)
                    && oferta.getDisciplina().getId().equals(disciplina)) {
                return oferta;
            }
        }
        throw new IllegalArgumentException("Disciplina não oferecida neste semestre.");
    }

    public List<Usuario> getUsuarios() {
        return List.copyOf(usuarios);
    }

    public List<Curso> getCursos() {
        return List.copyOf(cursos);
    }

    public List<Disciplina> getDisciplinas() {
        return List.copyOf(disciplinas);
    }

    public List<Semestre> getSemestres() {
        return List.copyOf(semestres);
    }

    public List<Oferta> getOfertas() {
        return List.copyOf(ofertas);
    }

    public List<Matricula> getMatriculas() {
        return List.copyOf(matriculas);
    }

    public SistemaCobrancas getCobrancas() {
        return cobrancas;
    }

    void restaurarCurso(Curso curso) {
        cursos.add(curso);
    }

    void restaurarDisciplina(Disciplina disciplina) {
        disciplinas.add(disciplina);
    }

    void restaurarSemestre(Semestre semestre) {
        semestres.add(semestre);
    }

    void restaurarOferta(Oferta oferta) {
        ofertas.add(oferta);
    }

    void restaurarMatricula(Matricula matricula) {
        matriculas.add(matricula);
    }
}
