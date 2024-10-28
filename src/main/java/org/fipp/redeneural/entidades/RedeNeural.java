package org.fipp.redeneural.entidades;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class RedeNeural {
    private int qtdeEntradas;
    private int qtdeCamadasOcultas;
    private int qtdeSaidas;
    private double taxaAprendizagem;
    private double limiar;//erro desejado
    private int epocas;
    private String funcaoTransferencia;//(saida) linear, logistica, tangente hiperbolica
    private List<Neuronio> neuroniosEntrada;
    private List<Neuronio> neuroniosOcultos;
    private List<Neuronio> neuroniosSaida;
    private boolean criterioParada;//true para epocas e false para erro
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
    - Aplicar os passos 2 a 11 ate que o erro da rede seja menor que o limiar ou o numero de epocas seja atingido
    */
    public RedeNeural(int qtdeEntradas, int qtdeSaidas, int qtdeCamadasOcultas, double limiar, int epocas, String funcaoTransferencia, double taxaAprendizagem, boolean aritmetica, boolean criterioParada) {
        this.qtdeEntradas = qtdeEntradas;
        this.qtdeSaidas = qtdeSaidas;
        this.limiar = limiar;
        this.epocas = epocas;
        this.funcaoTransferencia = funcaoTransferencia;
        this.taxaAprendizagem = taxaAprendizagem;
        if(qtdeCamadasOcultas == 0){
            this.qtdeCamadasOcultas = calcularQtdeCamadasOcultas(aritmetica);
        }
        else{
            this.qtdeCamadasOcultas = qtdeCamadasOcultas;
        }
        this.criterioParada = criterioParada;

        inicializarNeuronios();
        criarConexoes();
    }

    public void treinar(TableView<ObservableList<String>> tabela){
        if(criterioParada){
            treinarPorEpocas(tabela);
            System.out.println("Erro final rede com epoca: "+calculaErroRede());
        }
        else{
            treinarPorErro(tabela);
        }
    }

    public void testar(TableView<ObservableList<String>> tabelaTestes) {
        // matriz de confusão
        int[][] matrizConfusao = new int[qtdeSaidas][qtdeSaidas];
        for (ObservableList<String> linha : tabelaTestes.getItems()) {
            // Local variables to hold net and I values
            double[] netOcultos = new double[neuroniosOcultos.size()];
            double[] iOcultos = new double[neuroniosOcultos.size()];
            double[] netSaidas = new double[neuroniosSaida.size()];
            double[] iSaidas = new double[neuroniosSaida.size()];

            // Calculate net values for hidden neurons
            for (int k = 0; k < linha.size() - 1; k++) {
                double entrada = Double.parseDouble(linha.get(k));
                Neuronio nEntrada = neuroniosEntrada.get(k);
                for (Conexao c : nEntrada.getConexoes()) {
                    Neuronio nOculto = c.getDestino();
                    int index = neuroniosOcultos.indexOf(nOculto);
                    netOcultos[index] += entrada * c.getPeso();
                }
            }

            // Calculate I values for hidden neurons
            for (int i = 0; i < neuroniosOcultos.size(); i++) {
                iOcultos[i] = funcaoSaida(netOcultos[i], funcaoTransferencia);
            }

            // Calculate net values for output neurons
            for (int i = 0; i < neuroniosSaida.size(); i++) {
                Neuronio nSaida = neuroniosSaida.get(i);
                for (Conexao c : nSaida.getConexoes()) {
                    int index = neuroniosOcultos.indexOf(c.getOrigem());
                    netSaidas[i] += iOcultos[index] * c.getPeso();
                }
            }

            // Calculate I values for output neurons
            for (int i = 0; i < neuroniosSaida.size(); i++) {
                iSaidas[i] = funcaoSaida(netSaidas[i], funcaoTransferencia);
            }

            // Determine the predicted class
            int classe = Integer.parseInt(linha.get(linha.size() - 1));
            int maior = 0;
            for (int i = 0; i < qtdeSaidas; i++) {
                if (iSaidas[i] > iSaidas[maior]) {
                    maior = i;
                }
            }
            matrizConfusao[classe - 1][maior]++;
        }

        System.out.println("\n\n\nMatriz de confusão: ");
        for (int i = 0; i < qtdeSaidas; i++) {
            for (int j = 0; j < qtdeSaidas; j++) {
                System.out.print(matrizConfusao[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void treinarPorEpocas(TableView<ObservableList<String>> tabela) {
//        for (int i = 0; i < this.epocas; i++) {
//            //fazer metodos genericos para que possa ser utilizado no treinamento por erro
//            executarBackPropagation(tabela);
//        }
        int k = 0;
        do{
            for (ObservableList<String> linha : tabela.getItems()) {
                executarBackPropagation2(linha);
            }
            k++;
        }while(k < this.epocas);
    }

    private void executarBackPropagation2(ObservableList<String> linha) {
        calculaNetCamadaOculta2(linha);
        calculaICamada(neuroniosOcultos);//calculaI da camada oculta
        calculaNetCamadaSaida();
        calculaICamada(neuroniosSaida);//calculaI da camada de saida
        calculaErroCamadaSaida();
        calculaErroCamadaOculta();
        atualizaPesosCamadaSaida();//atualiza pesos da camada de saida
        atualizaPesosCamadaOculta2(linha);//atualiza pesos da camada oculta
        //calculaErroRede();//somente para treinamento por erro
    }

    private void atualizaPesosCamadaOculta2(ObservableList<String> linha) {
        for (int k = 0; k < linha.size() - 1; k++) { // -1 para não incluir a coluna da classe
            double entrada = Double.parseDouble(linha.get(k));
            Neuronio nEntrada = neuroniosEntrada.get(k);
            for (Conexao c : nEntrada.getConexoes()) {
                Neuronio nOculto = c.getDestino();
                double novopeso = c.getPeso() + taxaAprendizagem * nOculto.getErro() * entrada;
                c.setPeso(novopeso);
            }
        }
        System.out.println("Terminou de atualizar os pesos da camada oculta2");
    }

    private void calculaNetCamadaOculta2(ObservableList<String> linha) {
        zeraNetNeuronios(neuroniosOcultos);
        for (int k = 0; k < linha.size()-1; k++) {//-1 tira a classe // iterar sobre as colunas da linha
            double entrada = Double.parseDouble(linha.get(k));
            Neuronio nEntrada = neuroniosEntrada.get(k);
            for(Conexao c : nEntrada.getConexoes()){
                Neuronio nOculto = c.getDestino();
                double net = entrada * c.getPeso();
                nOculto.setNet(nOculto.getNet() + net);//neuronio deve comecar com net = 0 para ir incrementando o valor
            }
        }
        System.out.println("Terminou de calcular os nets da camada oculta2");
    }

    private void treinarPorErro(TableView<ObservableList<String>> tabela) {
        double erroRede;
        do {
            executarBackPropagation(tabela);
            erroRede = calculaErroRede();
            System.out.println("Erro da rede: " + erroRede);
        } while (this.limiar < erroRede);//enquanto erro da rede for maior que o erro estipulado(limiar)
    }

    private void executarBackPropagation(TableView<ObservableList<String>> tabela) {//nao deveria mandar a tabela, somente a linha?
        calculaNetCamadaOculta(tabela);
        calculaICamada(neuroniosOcultos);//calculaI da camada oculta
        calculaNetCamadaSaida();
        calculaICamada(neuroniosSaida);//calculaI da camada de saida
        calculaErroCamadaSaida();
        calculaErroCamadaOculta();
        atualizaPesosCamadaSaida();//atualiza pesos da camada de saida
        atualizaPesosCamadaOculta(tabela);//atualiza pesos da camada oculta
        //calculaErroRede();//somente para treinamento por erro
    }

    private void atualizaPesosCamadaSaida(){
        for(Neuronio n : neuroniosOcultos){
            for(Conexao c : n.getConexoes()){
                Neuronio nSaida = c.getDestino();
                double novopeso = c.getPeso() + taxaAprendizagem * nSaida.getErro() * n.getI();
                c.setPeso(novopeso);
            }
        }
        System.out.println("Terminou de atualizar os pesos da camada de saida");
    }

    private void atualizaPesosCamadaOculta(TableView<ObservableList<String>> tabela) {
        for (ObservableList<String> linha : tabela.getItems()) {
            for (int k = 0; k < linha.size() - 1; k++) { // -1 para não incluir a coluna da classe
                double entrada = Double.parseDouble(linha.get(k));
                Neuronio nEntrada = neuroniosEntrada.get(k);
                for (Conexao c : nEntrada.getConexoes()) {
                    Neuronio nOculto = c.getDestino();
                    double novopeso = c.getPeso() + taxaAprendizagem * nOculto.getErro() * entrada;
                    c.setPeso(novopeso);
                }
            }
        }
        System.out.println("Terminou de atualizar os pesos da camada oculta");
    }

    private void calculaErroCamadaOculta() {
        for(Neuronio n : neuroniosOcultos){
            double erro = 0.0;
            for(Conexao c : n.getConexoes()){
                erro += c.getDestino().getErro() * c.getPeso();
            }
            erro = erro * derivadaFuncaoSaida(n.getNet(),funcaoTransferencia);
            n.setErro(erro);
        }
        System.out.println("Terminou de calcular os erros da camada oculta");
    }

    private void calculaErroCamadaSaida() {
        for (int k = 0; k < neuroniosSaida.size(); k++) {
            Neuronio n = neuroniosSaida.get(k);
            int desejado = k + 1;//é isso mesmo?
            double erro = (desejado - n.getI()) * derivadaFuncaoSaida(n.getNet(),funcaoTransferencia);
            n.setErro(erro);
        }
        System.out.println("Terminou de calcular os erros da camada de saida");
    }

    private void calculaNetCamadaSaida() {
        for(Neuronio nSaida : neuroniosSaida){
            double net= 0.0;
            for(Neuronio nOculto : neuroniosOcultos){
                for(Conexao c : nOculto.getConexoes()){
                    if(c.getDestino().equals(nSaida)){
                        net += nOculto.getI() * c.getPeso();
                    }
                }
            }
            nSaida.setNet(net);
        }
        System.out.println("Terminou de calcular os nets da camada de saida");
    }

    private void calculaICamada(List<Neuronio> neuronios) {
        for(Neuronio n : neuronios){
            //f(net) eh a saida i para os neuronios da camada oculta ou as saidas da camada de saida
            double res = funcaoSaida(n.getNet(),funcaoTransferencia);
            n.setI(res);
        }
        System.out.println("Terminou de calcular os I da camada");
    }

    private void calculaNetCamadaOculta(TableView<ObservableList<String>> tabela){
        zeraNetNeuronios(neuroniosOcultos);
        for (ObservableList<String> linha : tabela.getItems()){//iterar sobre as linhas da tabela
            for (int k = 0; k < linha.size()-1; k++) {//-1 tira a classe // iterar sobre as colunas da linha
                double entrada = Double.parseDouble(linha.get(k));
                Neuronio nEntrada = neuroniosEntrada.get(k);
                for(Conexao c : nEntrada.getConexoes()){
                    Neuronio nOculto = c.getDestino();
                    double net = entrada * c.getPeso();
                    nOculto.setNet(nOculto.getNet() + net);//neuronio deve comecar com net = 0 para ir incrementando o valor
                }
            }
        }
        System.out.println("Terminou de calcular os nets da camada oculta");
    }

    private void zeraNetNeuronios(List<Neuronio> neuronios) {
        for(Neuronio n : neuronios){
            n.setNet(0);
        }
    }

    private double calculaErroRede() {
        double erroRede = 0.0;
        for(Neuronio n : neuroniosSaida){
            erroRede += Math.pow(n.getErro(),2);
        }
        erroRede = erroRede * 0.5;
        return erroRede;
    }

    private void criarConexoes() {
        //criar conexoes entre os neuronios
        //conexoes entre os neuronios de entrada e os neuronios da camada oculta
        for (Neuronio nEntrada : neuroniosEntrada) {
            for (Neuronio nOculto : neuroniosOcultos) {
                Conexao c = new Conexao(nEntrada, nOculto, (Math.random() - 0.5) / 10);//Math.random() em peso?
                nEntrada.adicionaConexao(c);
            }
        }
        //conexoes entre os neuronios da camada oculta e os neuronios de saida
        for (Neuronio nOculto : neuroniosOcultos) {
            for (Neuronio nSaida : neuroniosSaida) {
                Conexao c = new Conexao(nOculto, nSaida, (Math.random() - 0.5) / 10);//Math.random() em peso?
                nOculto.adicionaConexao(c);
            }
        }
    }

    public List<Neuronio> criaListaNeuronio(int qtde){
        List<Neuronio> neuronios = new ArrayList<>();
        for (int i = 0; i < qtde; i++) {
            neuronios.add(new Neuronio());
        }
        return neuronios;
    }

    private void inicializarNeuronios() {
        //inicializa os neuronios de entrada
        neuroniosEntrada = criaListaNeuronio(qtdeEntradas);
        neuroniosOcultos = criaListaNeuronio(qtdeCamadasOcultas);
        neuroniosSaida = criaListaNeuronio(qtdeSaidas);
    }

    private double funcaoSaida(double net, String funcao){
        //f(net) eh a saida i para os neuronios da camada oculta ou as saidas da camada de saida
        double i;
        if(funcao.equals("linear")){
            i = net / 10;
        }
        else if(funcao.equals("logistica")){
            i = 1 / (1 + Math.exp(-net));//  ->    1 / (1 + e^(-net))
        }
        else{
            //tangente hiperbolica
            i = Math.tanh(net); // -> (e^net - e^(-net)) / (e^net + e^(-net))
        }
        return i;
    }

    private double derivadaFuncaoSaida(double net, String funcao){
        double i, fnet;
        if(funcao.equals("linear")){
            i = 1.0 / 10;
        }
        else if(funcao.equals("logistica")){
            fnet = funcaoSaida(net,funcao);
            i = fnet * (1 - fnet);
        }
        else{
            //tangente hiperbolica
            fnet = funcaoSaida(net,funcao);
            i = 1 - Math.pow(fnet,2); // -> 1 - (fnet^2)
        }
        return i;
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

    public double getLimiar() {
        return limiar;
    }

    public void setLimiar(double limiar) {
        this.limiar = limiar;
    }

    public int getEpocas() {
        return epocas;
    }

    public void setEpocas(int epocas) {
        this.epocas = epocas;
    }

    public boolean isCriterioParada() {
        return criterioParada;
    }

    public void setCriterioParada(boolean criterioParada) {
        this.criterioParada = criterioParada;
    }

    public String getFuncaoTransferencia() {
        return funcaoTransferencia;
    }

    public void setFuncaoTransferencia(String funcaoTransferencia) {
        this.funcaoTransferencia = funcaoTransferencia;
    }
}
