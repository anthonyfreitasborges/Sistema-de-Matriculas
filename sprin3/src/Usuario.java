public class Usuario {
    private String login;
    private String senha;

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public boolean validarSenha(String senha) {
        return this.senha.equals(senha);
    }

    public String getLogin() {
        return login;
    }
}