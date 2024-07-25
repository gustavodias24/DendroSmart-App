package benicio.soluces.dimensional.model;

import java.io.Serializable;

public class PostagemModel implements Serializable {
    String _id, app, data, descricao, titulo;
    boolean tem_imagem;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>").append(titulo).append("</h1><br>");
        builder.append("<p>").append(descricao.replace("\n", "<br>")).append("</p><br>");
        builder.append("<p>").append("<b>Data:</b>").append(data).append("</p>");
        return builder.toString();
    }

    public PostagemModel(String _id, String app, String data, String descricao, String titulo, boolean tem_imagem) {
        this._id = _id;
        this.app = app;
        this.data = data;
        this.descricao = descricao;
        this.titulo = titulo;
        this.tem_imagem = tem_imagem;
    }

    public PostagemModel() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isTem_imagem() {
        return tem_imagem;
    }

    public void setTem_imagem(boolean tem_imagem) {
        this.tem_imagem = tem_imagem;
    }
}
