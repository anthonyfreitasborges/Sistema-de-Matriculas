# Sistema de Matrículas

Projeto Java com menus no terminal e dados salvos em TXT. O caminho do arquivo aparece ao iniciar. Ao executar pelo VS Code ou pelo script, o sistema usa o mesmo banco: se já existir `dados/banco.txt` na pasta principal `Sistema-de-Matriculas`, usa esse arquivo para preservar os cadastros; caso contrário, usa `sprin3/dados/banco.txt`.

## Executar

Instale o JDK 17 ou superior. Abra o terminal na pasta `sprin3` e digite:

```powershell
.\executar.bat
```

## Login

Use `secretaria`, `carlos`, `ana`, `bruno` ou `carla`. A senha inicial é `1234` para todos.

Em um arquivo novo, o exemplo tem um curso e duas disciplinas. Ana começa sem matrículas, D1 tem dois alunos e D2 tem um. Bancos existentes mantêm seus cadastros e matrículas. Mais dados podem ser cadastrados pelo menu da secretaria.

## Usar os menus

- **Secretaria:** cadastre ou edite cursos, disciplinas e usuários. Defina as datas do semestre na opção 6 e inclua disciplinas no currículo na opção 7. Para editar um curso ou disciplina, informe um ID já existente.
- **Aluno:** escolha a opção 2 para se matricular. Se houver um só semestre, ele será selecionado automaticamente. Escolha o número da disciplina na lista (ou seu código, como `D1`) e depois `1` para obrigatória ou `2` para optativa. A opção 4 mostra suas matrículas e, logo abaixo, oferece `1 Matricular em outra disciplina` ou `0 Voltar ao menu do aluno`. A opção 3 permite cancelar. São permitidas até quatro obrigatórias e duas optativas por semestre. As optativas são matrículas adicionais, sem substituição automática.
- **Professor:** consulte os alunos das turmas atribuídas ao seu login.

Disciplinas em que você já está matriculado são identificadas e não aparecem entre as opções de nova matrícula. Se não houver nenhuma disponível, a secretaria precisa incluir outra no currículo.

Digite `0` para sair da conta. Na tela de login, `0` fecha o programa.

As matrículas só podem ser alteradas dentro das datas definidas. Cada turma aceita até 60 alunos. A secretaria encerra o período na opção 9: turmas com menos de três alunos são canceladas. O encerramento não pode ser desfeito.

A opção 10 da secretaria mostra a simulação de cobranças. Inclusões e cancelamentos ficam registrados, sem pagamento real.

As alterações são salvas automaticamente no TXT. Abra apenas uma instância por arquivo. Não edite o arquivo enquanto o programa estiver aberto. As senhas são guardadas como hashes, portanto altere-as pelo menu da secretaria.

Se quiser começar uma simulação separada, use um nome de arquivo que ainda não exista:

```powershell
.\executar.bat dados/teste.txt
```

O novo arquivo recebe os exemplos com o período aberto por 30 dias. No banco existente, ajuste as datas pela secretaria se o período já tiver passado.

Para executar os testes:

```powershell
.\testar.bat
```
