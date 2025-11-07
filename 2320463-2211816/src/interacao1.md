# Trabalho de INF1636

**Data:** 26/09/2025  
**Professor:** Ivan Mathias Filho
**Grupo: Gabriel Farias Azevedo 2320463, Miguel de Athayde


## Instruções para a 1ª Iteração

O software para jogar partidas de **Banco Imobiliário** deve ser organizado segundo a **arquitetura MVC**.  
Nesta 1ª iteração, será iniciada a **especificação e codificação do componente Model** dessa arquitetura.

Esse componente será formado por uma ou mais classes que fornecerão uma **API (Application Programming Interface)** para que os demais componentes (**View** e **Controller**) solicitem, quando necessário, serviços ao componente **Model**.

---

## Implementação do Componente Model

O componente **Model** deve ser um **pacote Java**.  
Todas as classes que representem elementos básicos do jogo, como o tabuleiro e as cartas, **devem ser declaradas como não públicas**, para que não possam ser diretamente referenciadas por classes dos pacotes **View** e **Controller**.

Segundo o **princípio da ocultação da informação**, apenas as funções de um módulo que serão diretamente chamadas pelos demais módulos de uma aplicação devem ser públicas.

Isto é, o pacote **Model** deve oferecer uma ou mais **classes públicas** que disponibilizem uma **API** para que as regras do jogo possam ser acessadas pelas classes externas.  
Essa API será implementada segundo os padrões de design **Singleton** e **Façade** (isto será feito apenas na 3ª iteração).

---

## Sobre o Componente Controller

O componente **Controller** não será desenvolvido nesta 1ª iteração, mas é interessante antecipar algumas de suas características.

Todos os procedimentos que concernem à realização de uma jogada serão controlados por classes do **Controller**.

Por exemplo, no jogo **War**, cada jogador passa, em sua vez, pelas seguintes etapas:

1. Receber novos exércitos e os colocar de acordo com a sua estratégia;  
2. Se desejar, atacar os seus adversários;  
3. Deslocar seus exércitos se houver conveniência;  
4. Receber uma carta, se fizer jus a isto.

Assim, o **Controller** será responsável por controlar as etapas de uma jogada.  
Para que as regras do jogo sejam cumpridas, o **Controller** precisará solicitar serviços ao **Model**.

### Exemplo

O jogador de cor **preta** deseja atacar o território **Califórnia** a partir do território **Colômbia/Venezuela**.  
O **Controller** deverá verificar:

1. O território Colômbia/Venezuela pertence ao jogador de cor preta;  
2. O território Califórnia **não** pertence ao jogador de cor preta;  
3. O território Colômbia/Venezuela faz **fronteira** com o território Califórnia;  
4. O território Colômbia/Venezuela tem **pelo menos dois exércitos**.

---

## Estrutura da Jogada no Banco Imobiliário

Em uma partida de **Banco Imobiliário**, uma jogada é composta por três etapas:

1. **Lançamento dos dados**;  
2. **Deslocamento do pião** da vez, de acordo com os valores obtidos nos dados;  
3. **Ações** (pagamento de aluguel, compra de casa ou propriedade) de acordo com o estado da casa alcançada.

Para implementar essas etapas, a API do componente **Model** poderia ter:

- Um método `lancarDados()` que retorna um array com os valores dos dados.  
- Um método `deslocarPiao(valores)` que desloca o pião e retorna `true` se a jogada for completada, ou `false` caso contrário.

Este é apenas um exemplo de organização dos componentes **Model** e **Controller**.

---

## Objetivo da 1ª Iteração

O objetivo da 1ª iteração é **criar um conjunto de classes** do componente **Model** que implemente algumas das regras mais importantes do manual do **Banco Imobiliário**.

Quando **View** e **Controller** começarem a ser implementados, podem surgir novas demandas.  
Por isso, o componente **Model** deve permitir **futuras alterações**, seguindo o **princípio aberto/fechado** (de Bertrand Meyer, *Object Oriented Software Construction*).

### Princípio Aberto/Fechado

- Um módulo é **aberto** se ainda estiver disponível para **extensão** (ex.: adicionar campos ou novas funções).  
- Um módulo é **fechado** se estiver **disponível para uso** por outros módulos, com uma descrição estável e uma API bem definida.

---

## Regras que Devem Ser Implementadas

1. Realizar o **lançamento virtual dos dados** (gerar números randômicos).  
2. **Deslocar piões** de acordo com o jogador da vez e com os valores obtidos.  
3. **Comprar uma propriedade** que não tenha proprietário.  
4. **Construir uma casa** em uma propriedade do jogador da vez.  
5. **Pagar automaticamente aluguel** quando cair em propriedade de outro jogador (com ao menos uma casa).  
6. Tratar todas as situações de **entrada e saída da prisão**.  
7. **Pagar aluguel, falir e sair do jogo**.

Todas as coleções (tabuleiro, cartas etc.) **devem ser implementadas** usando o **framework de coleções** apresentado no Capítulo 15.

---

## Vídeo da 1ª Iteração

O vídeo deve apresentar:

- O **código Java** implementado e testado;  
- O código implementado mas **ainda não totalmente testado**;  
- Os **problemas encontrados** e os **motivos** de funcionalidades não implementadas/testadas;  
- As **funcionalidades planejadas** para a próxima iteração;  
- Os **responsáveis** por cada funcionalidade.

---

## Testes Unitários

Devem ser realizados **testes unitários** para cada unidade de código (função, método, classe, módulo, etc.), verificando se atende à especificação.

Todas as **7 funcionalidades** listadas no item anterior devem ser **testadas individualmente**.

Use o **framework JUnit 4**, conforme apresentado nas transparências do **Capítulo 18**.  
Os **casos de teste** devem ser elaborados e documentados de acordo com as diretrizes do mesmo capítulo.

---
