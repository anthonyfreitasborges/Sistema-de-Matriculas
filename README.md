# Histórias de Usuário

As histórias de usuário descrevem as principais funcionalidades do Sistema de Matrículas a partir da perspectiva dos usuários envolvidos.

## Secretaria

### HU01 — Manter cursos
**Como** secretário(a),  
**quero** cadastrar e manter as informações dos cursos,  
**para** que os cursos disponíveis na universidade estejam atualizados no sistema.

### HU02 — Manter disciplinas
**Como** secretário(a),  
**quero** cadastrar e manter as informações das disciplinas,  
**para** que elas possam ser disponibilizadas para matrícula.

### HU03 — Manter professores
**Como** secretário(a),  
**quero** cadastrar e manter as informações dos professores,  
**para** associá-los às disciplinas oferecidas.

### HU04 — Manter alunos
**Como** secretário(a),  
**quero** cadastrar e manter as informações dos alunos,  
**para** que eles possam utilizar o sistema de matrículas.

### HU05 — Gerar currículo do semestre
**Como** secretário(a),  
**quero** gerar o currículo de cada semestre,  
**para** definir as disciplinas que serão oferecidas aos alunos.

### HU06 — Definir período de matrícula
**Como** secretário(a),  
**quero** definir o período em que as matrículas estarão disponíveis,  
**para** controlar quando os alunos poderão realizar ou cancelar suas matrículas.

### HU07 — Encerrar período de matrícula
**Como** secretário(a),  
**quero** encerrar o período de matrículas,  
**para** verificar quais disciplinas poderão ser realizadas no próximo semestre.

### HU08 — Verificar quantidade mínima de alunos
**Como** secretário(a),  
**quero** verificar se cada disciplina possui pelo menos 3 alunos matriculados,  
**para** determinar se a disciplina será realizada no próximo semestre.

### HU09 — Cancelar disciplina
**Como** secretário(a),  
**quero** cancelar disciplinas que tenham menos de 3 alunos matriculados ao final do período,  
**para** evitar a realização de disciplinas que não atingiram o número mínimo de alunos.

---

## Aluno

### HU10 — Fazer login
**Como** aluno,  
**quero** realizar login utilizando minha senha,  
**para** acessar com segurança as funcionalidades do sistema.

### HU11 — Consultar disciplinas disponíveis
**Como** aluno,  
**quero** consultar as disciplinas disponíveis para matrícula,  
**para** escolher as disciplinas que desejo cursar no próximo semestre.

### HU12 — Realizar matrícula
**Como** aluno,  
**quero** realizar minha matrícula nas disciplinas disponíveis,  
**para** cursar as disciplinas no próximo semestre.

### HU13 — Escolher disciplinas obrigatórias e optativas
**Como** aluno,  
**quero** selecionar até 4 disciplinas como primeira opção e até 2 disciplinas alternativas,  
**para** organizar minhas preferências de matrícula.

### HU14 — Cancelar matrícula
**Como** aluno,  
**quero** cancelar uma matrícula realizada anteriormente,  
**para** alterar minhas disciplinas durante o período de matrículas.

### HU15 — Consultar minhas matrículas
**Como** aluno,  
**quero** consultar as disciplinas em que estou matriculado,  
**para** acompanhar minha situação acadêmica no semestre.

---

## Professor

### HU16 — Fazer login
**Como** professor,  
**quero** realizar login utilizando minha senha,  
**para** acessar com segurança as funcionalidades destinadas aos professores.

### HU17 — Consultar alunos matriculados
**Como** professor,  
**quero** consultar os alunos matriculados em cada disciplina que ministro,  
**para** saber quais alunos participarão das minhas aulas.

---

## Sistema de Cobranças

### HU18 — Receber dados das matrículas
**Como** sistema de cobranças,  
**quero** receber os dados das matrículas realizadas pelos alunos,  
**para** que as disciplinas do semestre possam ser consideradas no processo de cobrança.

---

## Regras de Negócio

Além das histórias de usuário, o sistema deve respeitar as seguintes regras:

- Um aluno pode escolher até **4 disciplinas como primeira opção (obrigatórias)**.
- Um aluno pode escolher até **2 disciplinas alternativas (optativas)**.
- Matrículas e cancelamentos só podem ser realizados durante o **período de matrículas**.
- Uma disciplina deve possuir pelo menos **3 alunos matriculados** para ser realizada.
- Uma disciplina com menos de **3 alunos** ao final do período de matrículas deve ser cancelada.
- Uma disciplina pode possuir no máximo **60 alunos matriculados**.
- Ao atingir **60 alunos**, novas matrículas para a disciplina devem ser encerradas.
- Após a realização da matrícula, o **Sistema de Matrículas deve notificar o Sistema de Cobranças**.
- Todos os usuários devem possuir **login e senha** para acessar o sistema.

## diagrma casos de uso
<img src="/Diagrama sem nome.drawio.png">