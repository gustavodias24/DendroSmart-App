package benicio.soluces.dimensional.model;

public class CredencialModel {
    String chave, dataAtivacao, dispositivo;
    UsuarioModel usuario;

    public CredencialModel(String chave, String dataAtivacao, UsuarioModel usuario, String dispositivo) {
        this.chave = chave;
        this.dataAtivacao = dataAtivacao;
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
        return dataAtivacao;
    }

    public void setDataAtivacao(String dataAtivacao) {
        this.dataAtivacao = dataAtivacao;
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
