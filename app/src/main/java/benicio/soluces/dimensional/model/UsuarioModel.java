package benicio.soluces.dimensional.model;

public class UsuarioModel {
    String nome,login,senha,email,nomeEmpresa,telefone;

    public UsuarioModel() {
    }

    public UsuarioModel(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public UsuarioModel(String nome, String login, String senha, String email, String nomeEmpresa, String telefone) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.nomeEmpresa = nomeEmpresa;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}