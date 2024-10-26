package org.fipp.redeneural;

public class RedeNeural {
    private int qtdeEntradas;
    private int qtdeCamadasOcultas;
    private int qtdeSaidas;
    private double taxaAprendizagem;
    //definir criterio de parada: por epocas ou erro

    public RedeNeural(int qtdeEntradas, int qtdeSaidas, double taxaAprendizagem) {
        this.qtdeEntradas = qtdeEntradas;
        this.qtdeSaidas = qtdeSaidas;
        this.taxaAprendizagem = taxaAprendizagem;
        this.qtdeCamadasOcultas = calcularQtdeCamadasOcultas(true);
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
}
