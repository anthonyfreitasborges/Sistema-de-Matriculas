public abstract class Usuario {
    private final String login;
    private final String senha;

    protected Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public boolean validarSenha(String senhaInformada) {
        return senha.equals(senhaInformada);
    }
    public String getSenha() {
    return senha;
}
}