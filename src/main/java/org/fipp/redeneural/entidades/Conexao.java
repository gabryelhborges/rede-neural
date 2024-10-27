package org.fipp.redeneural.entidades;

public class Conexao {
    private double peso;
    private Neuronio origem;
    private Neuronio destino;

    public Conexao(Neuronio origem, Neuronio destino, double peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Neuronio getOrigem() {
        return origem;
    }

    public void setOrigem(Neuronio origem) {
        this.origem = origem;
    }

    public Neuronio getDestino() {
        return destino;
    }

    public void setDestino(Neuronio destino) {
        this.destino = destino;
    }

}
