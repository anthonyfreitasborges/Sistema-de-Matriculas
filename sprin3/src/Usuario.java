import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Usuario {
    private String nome;
    private final String login;
    private final String perfil;
    private String senhaHash;
    public Usuario(String nome, String login, String senha, String perfil) {
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
        alterarSenha(senha);
    }

    public void alterarSenha(String senha) {
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("A senha precisa ter pelo menos 4 caracteres.");
        }
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        String hex = HexFormat.of().formatHex(sal);
        senhaHash = hex + ":" + hash(senha, hex);
    }

    private static String hash(String senha, String sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), HexFormat.of().parseHex(sal), 65536, 256);
            byte[] resultado = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return HexFormat.of().formatHex(resultado);
        } catch (Exception e) {
            throw new IllegalStateException("Erro na validação de senha.", e);
        }
    }

    public boolean validarSenha(String senha) {
        String[] partes = senhaHash.split(":");
        return MessageDigest.isEqual(HexFormat.of().parseHex(partes[1]), HexFormat.of().parseHex(hash(senha, partes[0])));
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    void restaurarSenhaHash(String hash) {
        if (!hash.matches("[0-9a-f]{32}:[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Hash de senha inválido.");
        }
        senhaHash = hash;
    }

    void setNome(String nome) {
        this.nome = nome;
    }
}
