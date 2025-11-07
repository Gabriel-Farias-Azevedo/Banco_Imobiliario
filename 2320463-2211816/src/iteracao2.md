# Trabalho de INF1636  
**Data:** 13/10/2025  
**Professor:** Ivan Mathias Filho  

---

## Instruções para a 2ª Iteração

A 2ª iteração tem como objetivo iniciar a implementação de uma **interface gráfica** por meio da qual se possa jogar **Banco Imobiliário**.

Nem todas as funcionalidades precisarão ser acionadas pela interface nesta iteração.  
O **salvamento e a recuperação** de uma partida, por exemplo, deverão ser implementados apenas a partir da **3ª iteração**.  
Assim, o resultado da 2ª iteração será uma versão executável do jogo **com algumas limitações**.

---

## Interface Gráfica

A interface gráfica do jogo **deve usar componentes Java Swing e Java2D**.  
O **tabuleiro**, os **piões** e as **cartas** devem ser exibidos **OBRIGATORIAMENTE** por meio da **API Java2D**.

> Foram disponibilizados arquivos com imagens desses elementos de jogo na página da disciplina no EAD.

⚠️ **Não serão aceitas soluções** que utilizem componentes Swing (como `JPanel`, `JButton`, `JLabel`, etc.) para exibir as imagens.  
As imagens devem ser exibidas **apenas com** o método `drawImage()` da classe `Graphics2D`.

As janelas criadas devem ter **dimensões máximas de 1280x800 pixels**.

---

## Janela Inicial

A primeira janela exibida deve permitir:

- Incluir dados dos jogadores (3 a 6) de uma nova partida;  
- Continuar uma partida salva (a partir da 3ª iteração).

Após a escolha, a janela inicial deve ser fechada e a janela do **tabuleiro** aberta.

### Dados dos Jogadores

- Cada jogador deve ter:
  - Um **identificador** (string de 1 a 8 caracteres alfanuméricos);
  - Uma **cor de pião** (não podem existir piões repetidos).

> Na 2ª iteração, implementar **apenas** a definição do número de jogadores.  
> A continuação de partidas será tratada na 3ª iteração.

---

## O Tabuleiro

O tabuleiro **não pode conter nenhum componente Swing**.  
Deve ser construído **somente** com os métodos `fill()`, `draw()` e `drawImage()` da classe `Graphics2D`.

### Exceções Permitidas

1. Uso de `JPopupMenu`, `JMenu` ou `JButton` para **salvar ou carregar** um jogo;  
2. Uso de `JButton` para **simular o lançamento dos dados**.

---

## As Jogadas

Operações que não exigem intervenção do jogador devem ser **automatizadas**.  
A interface deve indicar **claramente** a cor do jogador da vez.

> Exemplo: pintar a área onde os dados são exibidos com a cor do jogador atual.

Evite o uso de caixas de diálogo (`JDialog`, `JOptionPane`, etc.) para mensagens simples, pois elas interrompem o fluxo do jogo.

O resultado do lançamento dos dados deve ser exibido **com figuras** representando os números (1 a 6).  
Essas figuras estão disponíveis no EAD.

### Testes de Lançamento

Para facilitar os testes, o **valor dos dados** deve poder ser definido manualmente (por exemplo, com dois combo boxes de 1 a 6).

---

## Deslocamento dos Piões

Sugestão:  
Crie **6 pistas imaginárias** ao redor do tabuleiro.  
Assim, não será necessário desenvolver um algoritmo complexo para lidar com múltiplos piões na mesma casa.

---

## Deck de Cartas de Sorte/Revés

O **deck** não precisa ser exibido o tempo todo, mas a **carta recebida** deve ser mostrada a todos.  
Pode ser exibida:
- Em uma área do painel do tabuleiro; ou  
- Em uma caixa de diálogo.

### Arquitetura MVC

- As **imagens** das cartas fazem parte da **View**.  
- O **Model** deve representar o **significado** de cada carta.  
- Cada carta no Model deve possuir um **identificador (chave)** que permita à View recuperar a imagem rapidamente (ex: `HashMap`).

---

## Informações Sobre o Estado do Jogo

Durante a partida, as seguintes informações devem estar disponíveis:

### Propriedades

Quando um pião para em uma propriedade, exibir:
- A **carta da propriedade**;
- A **cor do proprietário** (se houver);
- O **preço de compra**;
- Se for um terreno, o **número de casas e hotéis**.

### Propriedades e Finanças do Jogador

Durante a jogada de um jogador:
- Mostrar **seu dinheiro atual** e **suas propriedades**.  
- O dinheiro pode ser mostrado como texto, e as propriedades em uma **combo box**.  
- Ao selecionar uma propriedade, exibir seus detalhes no painel ou em outra janela.

---

## Operações do Jogador da Vez

Durante sua jogada, o jogador pode:

- **Construir** casas ou hotéis;  
- **Vender** propriedades ao banco.

Essas operações exigem **diálogos específicos** (por exemplo, `JButton` ou menus).

Quando ocorrer uma operação de **débito/crédito**, o programa deve informar:
- O **montante**;
- Quem **pagou** e quem **recebeu**;
- O **saldo atualizado** de ambos.

Pode ser necessário um botão (ou equivalente) para **encerrar a jogada** e **passar a vez**.

---

## Funcionalidades da 2ª Iteração

A interface gráfica deve implementar:

- Exibição da janela inicial;  
- Definição dos jogadores da partida;  
- Exibição do tabuleiro;  
- Definição da ordem dos jogadores (sorteio);  
- Lançamento dos dados e movimentação dos piões;  
- Exibição das cartas das propriedades.

---

## Design e Implementação

A avaliação considerará o **uso correto de técnicas de design** e **boas práticas de programação**.  
Isso inclui:
- Baixo acoplamento e alta coesão;  
- Organização em pacotes;  
- Uso **OBRIGATÓRIO** dos seguintes **Design Patterns**:

  - **Observer**  
  - **Façade**  
  - **Singleton**

### Restrições de Arquitetura

- Nenhum método no **Model** pode fazer referência direta à **View**.  
- A atualização das janelas deve ocorrer **somente via padrão Observer**.  
- O **Controller** é responsável por:
  - Abrir `JOptionPane` (mensagens);
  - Abrir `JFileChooser` (salvar/abrir jogo).

---

## Observações Finais

Estas instruções fornecem uma **visão global da arquitetura** a ser implementada.  
Não há exigência de que todos os pontos sejam plenamente atendidos nesta 2ª iteração.

---
