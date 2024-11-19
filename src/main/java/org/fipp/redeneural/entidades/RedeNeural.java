package org.fipp.redeneural.entidades;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class RedeNeural{
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
    private List<String> listaClasses;
    /*
    - A rede neural eh estimulada por um ambiente
    - A rede neural sofre modificacoes nos seus parametros livres
    - A rede neural responde de uma nova maneira ao ambiente

    BACKPROPAGATION
    1- Inicializa os pesos(valores aleatorios[qual intervalo?])
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
    public RedeNeural(int qtdeEntradas, int qtdeSaidas, int qtdeCamadasOcultas, double limiar, int epocas, String funcaoTransferencia, double taxaAprendizagem, boolean aritmetica, boolean criterioParada, List<String> listaClasses) {
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
        this.listaClasses = listaClasses;
        inicializarNeuronios();
        criarConexoes();
    }

    public void treinar(TableView<ObservableList<String>> tabela){
        double erroRede;
        boolean haPlato, continuarTreino = true, mudarTaxaAprendizagem = true;
        List<Double> listaErrosRede = new ArrayList<>();
        int k = 0, aux = 15;
        do {
            for (ObservableList<String> linha : tabela.getItems()) {
                executarBackPropagation(linha);
            }
            erroRede = calculaErroRede();
            System.out.println("Erro da rede: " + erroRede);
            listaErrosRede.add(erroRede);

            if(k > aux && mudarTaxaAprendizagem) {
                haPlato = verificaPlato(listaErrosRede, k);
                if (haPlato) {
                    Util.exibirGraficoErros(listaErrosRede);
                    continuarTreino = Util.exibirMensagemConfirmacao("PLATO!",
                            "Plato detectado! Gostaria de continuar?");
                    if (continuarTreino) {
                        aux = k + 15;
                        mudarTaxaAprendizagem = Util.exibirMensagemConfirmacao("TAXA DE APRENDIZAGEM",
                                "Deseja mudar a taxa de aprendizagem?");
                        if (mudarTaxaAprendizagem) {
                            //perguntar a nova taxa de aprendizagem
                            setTaxaAprendizagem(Util.solicitarNovaTaxaAprendizagem(this.taxaAprendizagem));
                        }
                    }
                }
            }
            k++;
        }while(erroRede > this.limiar &&  k < this.epocas && continuarTreino);

        if(erroRede < this.limiar){
            System.out.println("Erro final rede: "+ erroRede);
        }
        else{
            System.out.println("Número de epocas atingido");
        }
        if(continuarTreino) {
            Util.exibirGraficoErros(listaErrosRede);
        }
    }


    private boolean verificaPlato(List<Double> listaErrosRede, int posAtual) {
        //calcula media, soma as diferencas da media e erro apresentado, calcula media (da soma) e verifica se é menor igual ao limiar
        int intervalo = 15;
        if (posAtual >= intervalo) {
            int posInicialIntervalo = posAtual - intervalo;
            double somaErros = 0.0;
            double somaDiferencas = 0.0;

            for (int i = posInicialIntervalo; i < posAtual; i++) {
                somaErros += listaErrosRede.get(i);
            }

            double media = somaErros / intervalo;

            for (int i = posInicialIntervalo; i < posAtual; i++) {
                somaDiferencas += Math.abs(listaErrosRede.get(i) - media); // Diferença absoluta
            }

            double mediaDiferencas = somaDiferencas / intervalo;

            if (mediaDiferencas <= this.limiar) {
                return true;
            }
        }
        return false;
    }

    public void testar(TableView<ObservableList<String>> tabelaTestes) {
        int numClasses = listaClasses.size();
        int[][] confusionMatrix = new int[numClasses][numClasses];
        int totalTestes = 0;
        int acertos = 0;
        int ultimaPos = tabelaTestes.getItems().get(0).size() - 1;
        for (ObservableList<String> linha : tabelaTestes.getItems()) {
            aplicaEntradas(linha);
            calculaNetCamadaOculta();
            calculaICamada(neuroniosOcultos);
            calculaNetCamadaSaida();
            calculaICamada(neuroniosSaida);

            double maiorSaida = neuroniosSaida.get(0).getI();
            int pos = 0;

            for (int k = 1; k < neuroniosSaida.size(); k++) {
                Neuronio n = neuroniosSaida.get(k);
                if (n.getI() > maiorSaida) {
                    maiorSaida = n.getI();
                    pos = k;
                }
            }
            int classeDesejada = Integer.parseInt(linha.get(ultimaPos).toString()) - 1; // Classe desejada da linha de teste
            int classeIdentificada = pos;

            confusionMatrix[classeDesejada][classeIdentificada]++;
            totalTestes++;
            if (classeDesejada == classeIdentificada) {
                acertos++;
            }
        }
        exibeMatriz(confusionMatrix);

        // calcula acurácia global
        double acuraciaGlobal = (double) acertos / totalTestes * 100;
        System.out.println("Acurácia Global: " + acuraciaGlobal + "%");

        // calcula acurácia por classe
        for (int i = 0; i < numClasses; i++) {
            int totalClasse = 0;
            int acertosClasse = 0;
            for (int j = 0; j < numClasses; j++) {
                totalClasse += confusionMatrix[i][j];
                if (i == j) {
                    acertosClasse += confusionMatrix[i][j];
                }
            }
            double acuraciaClasse = (totalClasse > 0) ? (double) acertosClasse / totalClasse * 100 : 0;
            System.out.println("Acurácia da classe " + (i + 1) + ": " + acuraciaClasse + "%");//no lugar de i+1, pode ser listaClasses.get(i)
        }
    }

    public static void exibeMatriz(int[][] matrix) {
        // Determina a largura máxima dos elementos da matriz
        int maxWidth = 0;
        for (int[] row : matrix) {
            for (int element : row) {
                int elementWidth = String.valueOf(element).length();
                if (elementWidth > maxWidth) {
                    maxWidth = elementWidth;
                }
            }
        }

        // Imprime a matriz com espaçamento igual
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.printf("%" + maxWidth + "d ", element);
            }
            System.out.println();
        }
    }

    private void executarBackPropagation(ObservableList<String> linha) {
        String classeDesejada = linha.get(linha.size() - 1);
        aplicaEntradas(linha);
        calculaNetCamadaOculta();
        calculaICamada(neuroniosOcultos);//calculaI da camada oculta
        calculaNetCamadaSaida();
        calculaICamada(neuroniosSaida);//calculaI da camada de saida
        calculaErroCamadaSaida(classeDesejada);
        calculaErroCamadaOculta();
        atualizaPesosCamadaSaida();//atualiza pesos da camada de saida
        atualizaPesosCamadaOculta();//atualiza pesos da camada oculta
        //calculaErroRede();
    }

    private void aplicaEntradas(ObservableList<String> linha) {
        for (int k = 0; k < linha.size() - 1; k++) {
            double entrada = Double.parseDouble(linha.get(k));
            Neuronio n = neuroniosEntrada.get(k);
            n.setI(entrada);
        }
        //System.out.println("Terminou de aplicar as entradas");
    }

    private void calculaNetCamadaOculta() {
        for (Neuronio nOculto : neuroniosOcultos) {
            double net = 0.0;
            for (Neuronio nEntrada : neuroniosEntrada) {
                double entrada = nEntrada.getI();
                for (Conexao c : nEntrada.getConexoes()) {
                    if (c.getDestino().equals(nOculto)) {
                        net += entrada * c.getPeso();
                    }
                }
            }
            nOculto.setNet(net);
        }
        //System.out.println("Terminou de calcular os nets da camada oculta");
    }

    private void calculaICamada(List<Neuronio> neuronios) {
        for(Neuronio n : neuronios){
            //f(net) eh a saida i para os neuronios da camada oculta ou as saidas da camada de saida
            double res = funcaoSaida(n.getNet(),funcaoTransferencia);
            n.setI(res);
        }
        //System.out.println("Terminou de calcular os I da camada");
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
        //System.out.println("Terminou de calcular os nets da camada de saida");
    }

    private void calculaErroCamadaSaidaAntigo() {
        for (int k = 0; k < neuroniosSaida.size(); k++) {
            Neuronio n = neuroniosSaida.get(k);
            int desejado = k + 1;
            double erro = (desejado - n.getI()) * derivadaFuncaoSaida(n.getNet(),funcaoTransferencia);
            n.setErro(erro);
        }
        //System.out.println("Terminou de calcular os erros da camada de saida");
    }

    private void calculaErroCamadaSaida(String classeDesejada) {
        int posDesejada = Integer.parseInt(classeDesejada) - 1;

        for (int i = 0; i < neuroniosSaida.size(); i++) {
            Neuronio n = neuroniosSaida.get(i);

            // Se o neurônio atual é o neurônio da classe desejada, o valor desejado é 1
            double desejado = (i == posDesejada) ? 1.0 : 0.0;

            double erro = (desejado - n.getI()) * derivadaFuncaoSaida(n.getNet(), funcaoTransferencia);
            n.setErro(erro);
        }

        //System.out.println("Terminou de calcular os erros da camada de saída");
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
        //System.out.println("Terminou de calcular os erros da camada oculta");
    }

    private void atualizaPesosCamadaSaida(){
        for(Neuronio n : neuroniosOcultos){
            for(Conexao c : n.getConexoes()){
                Neuronio nSaida = c.getDestino();
                double novopeso = c.getPeso() + taxaAprendizagem * nSaida.getErro() * n.getI();
                c.setPeso(novopeso);
            }
        }
        //System.out.println("Terminou de atualizar os pesos da camada de saida");
    }

    private void atualizaPesosCamadaOculta() {
        for (Neuronio nEntrada : neuroniosEntrada) {
            double entrada = nEntrada.getI();
            for (Conexao c : nEntrada.getConexoes()) {
                Neuronio nOculto = c.getDestino();
                double novopeso = c.getPeso() + taxaAprendizagem * nOculto.getErro() * entrada;
                c.setPeso(novopeso);
            }
        }
        //System.out.println("Terminou de atualizar os pesos da camada oculta");
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
            i = 0.1;
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

    public int calcularQtdeCamadasOcultas(boolean aritmetica) {
        //Math.ceil, arredonda o numero sempre pra cima
        if (aritmetica) {
            return (int) Math.ceil((qtdeEntradas + qtdeSaidas) / 2.0);
        } else {
            return (int) Math.ceil(Math.sqrt(qtdeEntradas * qtdeSaidas));
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

    public List<String> getListaClasses() {
        return listaClasses;
    }

    public void setListaClasses(List<String> listaClasses) {
        this.listaClasses = listaClasses;
    }
}
