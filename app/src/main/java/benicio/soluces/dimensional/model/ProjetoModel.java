package benicio.soluces.dimensional.model;

public class ProjetoModel {
    String nomeProjeto, dataCriacao, caminhoProjeto;

    public ProjetoModel() {
    }

    public ProjetoModel(String nomeProjeto, String dataCriacao, String caminhoProjeto) {
        this.nomeProjeto = nomeProjeto;
        this.dataCriacao = dataCriacao;
        this.caminhoProjeto = caminhoProjeto;
    }

    @Override
    public String toString() {
        StringBuilder toHtml = new StringBuilder();
        toHtml.append("<b>").append("Nome: ").append("</b>").append(nomeProjeto).append("<br>");
        toHtml.append("<b>").append("Data Criação: ").append("</b>").append(dataCriacao).append("<br>");
        return toHtml.toString();
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getCaminhoProjeto() {
        return caminhoProjeto;
    }

    public void setCaminhoProjeto(String caminhoProjeto) {
        this.caminhoProjeto = caminhoProjeto;
    }
}
