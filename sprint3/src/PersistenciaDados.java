import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PersistenciaDados {
    private static final Path ARQUIVO =
            Path.of("..", "dados", "1sistema_matriculas.txt");

    private static String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodificar(String texto) {
        return new String(Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8);
    }

    public static void salvar(List<Curso> cursos, List<Aluno> alunos,
                              List<Professor> professores) {
        List<String> linhas = new ArrayList<>();

        for (int i = 0; i < cursos.size(); i++) {
            Curso curso = cursos.get(i);
            linhas.add("C|" + codificar(curso.getNome()) + "|" + curso.getCreditos());

            for (int j = 0; j < curso.getDisciplinas().size(); j++) {
                Disciplina disciplina = curso.getDisciplinas().get(j);
                String loginProfessor = disciplina.getProfessor() == null
                        ? "" : disciplina.getProfessor().getLogin();

                linhas.add("D|" + i + "|" + codificar(disciplina.getNome())
                        + "|" + disciplina.getCreditos()
                        + "|" + disciplina.isAtiva()
                        + "|" + codificar(loginProfessor));
            }
        }

        for (Professor professor : professores) {
            linhas.add("P|" + codificar(professor.getLogin())
                    + "|" + codificar(professor.getSenha())
                    + "|" + codificar(professor.getNome()));
        }

        for (Aluno aluno : alunos) {
            int indiceCurso = cursos.indexOf(aluno.getCurso());
            linhas.add("A|" + codificar(aluno.getMatricula())
                    + "|" + codificar(aluno.getLogin())
                    + "|" + codificar(aluno.getSenha())
                    + "|" + codificar(aluno.getNome())
                    + "|" + indiceCurso);

            for (Matricula matricula : aluno.getMatriculas()) {
                int indiceCursoMatricula = cursos.indexOf(aluno.getCurso());
                int indiceDisciplina = aluno.getCurso().getDisciplinas()
                        .indexOf(matricula.getDisciplina());

                linhas.add("M|" + codificar(aluno.getMatricula())
                        + "|" + indiceCursoMatricula
                        + "|" + indiceDisciplina
                        + "|" + matricula.getTipo().name());
            }
        }

        try {
            Files.createDirectories(ARQUIVO.getParent());
            Files.write(ARQUIVO, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    public static void carregar(List<Curso> cursos, List<Aluno> alunos,
                                List<Professor> professores,
                                SistemaMatriculas sistema) {
        if (!Files.exists(ARQUIVO)) {
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(ARQUIVO, StandardCharsets.UTF_8);

            // Primeiro carrega cursos e professores.
            for (String linha : linhas) {
                String[] partes = linha.split("\\|", -1);

                if (partes[0].equals("C")) {
                    cursos.add(new Curso(decodificar(partes[1]),
                            Integer.parseInt(partes[2])));
                } else if (partes[0].equals("P")) {
                    professores.add(new Professor(decodificar(partes[1]),
                            decodificar(partes[2]), decodificar(partes[3])));
                }
            }

            // Depois carrega disciplinas e suas associações com professores.
            for (String linha : linhas) {
                String[] partes = linha.split("\\|", -1);
                if (!partes[0].equals("D")) {
                    continue;
                }

                int indiceCurso = Integer.parseInt(partes[1]);
                Disciplina disciplina = new Disciplina(decodificar(partes[2]),
                        Integer.parseInt(partes[3]));
                disciplina.setAtiva(Boolean.parseBoolean(partes[4]));

                String loginProfessor = decodificar(partes[5]);
                if (!loginProfessor.isEmpty()) {
                    for (Professor professor : professores) {
                        if (professor.getLogin().equals(loginProfessor)) {
                            disciplina.setProfessor(professor);
                            break;
                        }
                    }
                }

                cursos.get(indiceCurso).adicionarDisciplina(disciplina);
                sistema.adicionarDisciplina(disciplina);
            }

            // Carrega alunos.
            for (String linha : linhas) {
                String[] partes = linha.split("\\|", -1);
                if (partes[0].equals("A")) {
                    int indiceCurso = Integer.parseInt(partes[5]);
                    alunos.add(new Aluno(decodificar(partes[1]),
                            decodificar(partes[2]), decodificar(partes[3]),
                            decodificar(partes[4]), cursos.get(indiceCurso)));
                }
            }

            // Por fim, restaura as matrículas.
            for (String linha : linhas) {
                String[] partes = linha.split("\\|", -1);
                if (!partes[0].equals("M")) {
                    continue;
                }

                String matriculaAluno = decodificar(partes[1]);
                int indiceCurso = Integer.parseInt(partes[2]);
                int indiceDisciplina = Integer.parseInt(partes[3]);
                TipoInscricao tipo = TipoInscricao.valueOf(partes[4]);

                for (Aluno aluno : alunos) {
                    if (aluno.getMatricula().equals(matriculaAluno)) {
                        Disciplina disciplina = cursos.get(indiceCurso)
                                .getDisciplinas().get(indiceDisciplina);
                        aluno.matricular(disciplina, tipo);
                        break;
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }
}