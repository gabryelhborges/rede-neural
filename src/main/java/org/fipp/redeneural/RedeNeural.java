package org.fipp.redeneural;

import java.util.List;

public class RedeNeural {
    private int qtdeEntradas;
    private int qtdeCamadasOcultas;
    private int qtdeSaidas;
    private double taxaAprendizagem;
    private List<Neuronio> neuroniosEntrada;
    private List<Neuronio> neuroniosOcultos;
    private List<Neuronio> neuroniosSaida;
    //definir criterio de parada: por epocas ou erro

    /*
    - A rede neural eh estimulada por um ambiente
    - A rede neural sofre modificacoes nos seus parametros livres
    - A rede neural responde de uma nova maneira ao ambiente

    BACKPROPAGATION
    1- Inicializa os pesos(valores aleatorios?[qual intervalo?])
    2- Aplica as entradas
    3- Calcula os nets da camada oculta
    4- Aplica função de transferencia na camada oculta
    5- Calcula os nets da camada oculta
    6- Calculas as saidas da camada de saida
    7- Calcula os erros da camada de saida
    8- Calcula os erros da camada oculta
    9- Atualiza os pesos da camada de saida
    10- Atualiza os pesos da camada oculta
    11- Calcula o erro da rede
    - Aplicar os passos 2 a 11 ate que o erro da rede seja menor que o limiar
    */


    public RedeNeural(int qtdeEntradas, int qtdeSaidas, double taxaAprendizagem) {
        this.qtdeEntradas = qtdeEntradas;
        this.qtdeSaidas = qtdeSaidas;
        this.taxaAprendizagem = taxaAprendizagem;
        this.qtdeCamadasOcultas = calcularQtdeCamadasOcultas(true);
        inicializarNeuronios();
    }

    private void inicializarNeuronios() {
        //inicializa os neuronios de entrada
        for (int i = 0; i < qtdeEntradas; i++) {
            neuroniosEntrada.add(new Neuronio());
        }

        //inicializa os neuronios da camada oculta
        for (int i = 0; i < qtdeCamadasOcultas; i++) {
            neuroniosOcultos.add(new Neuronio());
        }

        //inicializa os neuronios de saida
        for (int i = 0; i < qtdeSaidas; i++) {
            neuroniosSaida.add(new Neuronio());
        }
    }

    private void funcaoSaida(String funcao){
        //olhar pag 4 do pdf
        //f(net) eh a saida i para os neuronios da camada oculta ou as saidas da camada de saida
        if(funcao.equals("linear")){

        }
        else if(funcao.equals("logistica")){

        }
        else{
            //tangente hiperbolica

        }
    }

    private int calcularQtdeCamadasOcultas(boolean aritmetica) {
        if(aritmetica){
            return (qtdeEntradas + qtdeSaidas) / 2;//aritmetica
        } else {
            return (int) Math.sqrt(qtdeEntradas * qtdeSaidas);//geometrica
        }
    }

    public double getTaxaAprendizagem() {
        return taxaAprendizagem;
    }

    public void setTaxaAprendizagem(double taxaAprendizagem) {
        this.taxaAprendizagem = taxaAprendizagem;
    }

    public int getQtdeEntradas() {
        return qtdeEntradas;
    }

    public void setQtdeEntradas(int qtdeEntradas) {
        this.qtdeEntradas = qtdeEntradas;
    }

    public int getQtdeCamadasOcultas() {
        return qtdeCamadasOcultas;
    }

    public void setQtdeCamadasOcultas(int qtdeCamadasOcultas) {
        this.qtdeCamadasOcultas = qtdeCamadasOcultas;
    }

    public int getQtdeSaidas() {
        return qtdeSaidas;
    }

    public void setQtdeSaidas(int qtdeSaidas) {
        this.qtdeSaidas = qtdeSaidas;
    }

    public List<Neuronio> getNeuroniosEntrada() {
        return neuroniosEntrada;
    }

    public void setNeuroniosEntrada(List<Neuronio> neuroniosEntrada) {
        this.neuroniosEntrada = neuroniosEntrada;
    }

    public List<Neuronio> getNeuroniosOcultos() {
        return neuroniosOcultos;
    }

    public void setNeuroniosOcultos(List<Neuronio> neuroniosOcultos) {
        this.neuroniosOcultos = neuroniosOcultos;
    }

    public List<Neuronio> getNeuroniosSaida() {
        return neuroniosSaida;
    }

    public void setNeuroniosSaida(List<Neuronio> neuroniosSaida) {
        this.neuroniosSaida = neuroniosSaida;
    }
}
