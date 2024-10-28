package org.fipp.redeneural.entidades;

import java.util.ArrayList;
import java.util.List;

public class Neuronio {
    private double net;
    private double i;//saida
    private double erro;
    private List<Conexao> conexoes;//representa os neuronios destino

    public void adicionaConexao(Conexao c){
        this.conexoes.add(c);
    }

    public Neuronio(){
        this.net = 0;
        conexoes = new ArrayList<>();
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
        this.i = i;
    }

    public double getErro() {
        return erro;
    }

    public void setErro(double erro) {
        this.erro = erro;
    }

    public List<Conexao> getConexoes() {
        return conexoes;
    }

    public void setConexoes(List<Conexao> conexoes) {
        this.conexoes = conexoes;
    }
}
