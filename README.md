# Rede Neural MLP (Multilayer Perceptron) com Algoritmo Backpropagation
## Sobre o Projeto
Este projeto implementa uma Rede Neural do tipo MLP (Multilayer Perceptron) utilizando o algoritmo de backpropagation para aprendizado supervisionado. A aplicação possui uma interface intuitiva para treinar, visualizar, testar a rede neural e gerar a matriz de confusão correspondente.

## Pré-requisitos
    Java 17.0.10
    IDE Intellij (recomendado para melhor experiência).
## Como Executar
Clone este repositório em sua máquina local.
Abra o projeto na IDE Intellij.
Localize e execute o arquivo principal:

    src/main/java/org/fipp/redeneural/HelloApplication.java

Ao iniciar o programa, será exibido um menu com quatro opções:

* Treinamento
* Visualizar Rede Neural
* Teste
* Matriz de Confusão

### 1. Treinamento
Para iniciar, selecione a aba Treinamento. Nesta aba, você poderá configurar os seguintes parâmetros:
* Valor de Erro: O erro mínimo desejado para o treinamento.
* Número de Iterações: Quantidade máxima de épocas de treinamento.
* Taxa de Aprendizado (N): Define o quão rápido a rede ajusta seus pesos.

Passos para o Treinamento:
* Clique no botão Carregar Arquivo para selecionar o arquivo de dados de treinamento.
* Insira a porcentagem do arquivo que será usada para o treinamento no campo correspondente.
* Clique no botão Iniciar Treinamento (localizado no canto inferior esquerdo).

Após o término do treinamento, a aplicação automaticamente avançará para a aba Teste.

### 2. Teste
Se, na aba de treinamento, você utilizou 100% do arquivo para treinar, será necessário carregar um novo arquivo de teste.
Caso tenha utilizado uma porcentagem menor que 100%, os dados restantes serão usados para teste.
Clique em Iniciar Teste para começar.

### 3. Matriz de Confusão
Concluído o teste, você será redirecionado para a aba Matriz de Confusão, onde poderá visualizar:

A matriz de confusão gerada.
Os resultados detalhados do desempenho da rede neural.

## Observações
Certifique-se de utilizar conjuntos de dados bem formatados para evitar problemas de leitura.
Arquivos de treinamento e teste devem estar no formato adequado (ex.: CSV).
