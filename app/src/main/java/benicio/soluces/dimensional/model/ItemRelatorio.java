package benicio.soluces.dimensional.model;

import java.io.Serializable;

public class ItemRelatorio implements Serializable {

    String imagemArvore;
    String tamanhoCadaTora;
    String dh;

    // informações exibição
    String dadosGps;
    String dadosTora;
    String dadosVolume;

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getTamanhoCadaTora() {
        return tamanhoCadaTora;
    }

    public void setTamanhoCadaTora(String tamanhoCadaTora) {
        this.tamanhoCadaTora = tamanhoCadaTora;
    }

    public String getImagemArvore() {
        return imagemArvore;
    }

    public void setImagemArvore(String imagemArvore) {
        this.imagemArvore = imagemArvore;
    }

    public String getDadosGps() {
        return dadosGps;
    }

    public void setDadosGps(String dadosGps) {
        this.dadosGps = dadosGps;
    }

    public String getDadosTora() {
        return dadosTora;
    }

    public void setDadosTora(String dadosTora) {
        this.dadosTora = dadosTora;
    }

    public String getDadosVolume() {
        return dadosVolume;
    }

    public void setDadosVolume(String dadosVolume) {
        this.dadosVolume = dadosVolume;
    }
}
