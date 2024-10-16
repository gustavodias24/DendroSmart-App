package benicio.soluces.dimensional.model;

public class CredencialModel {
    String chave, data_ativacao, dispositivo;
    UsuarioModel usuario;

    public CredencialModel(String chave, String dataAtivacao, UsuarioModel usuario, String dispositivo) {
        this.chave = chave;
        this.data_ativacao = dataAtivacao;
        this.usuario = usuario;
        this.dispositivo = dispositivo;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDipositivo(String dipositivo) {
        this.dispositivo = dipositivo;
    }

    public String getDataAtivacao() {
        return data_ativacao;
    }

    public void setDataAtivacao(String dataAtivacao) {
        this.data_ativacao = dataAtivacao;
    }

    public CredencialModel() {
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }
}
