# BANCO IMOBILIÁRIO

---

## COMPONENTES
- 32 casas plásticas  
- 12 hotéis plásticos  
- 22 títulos de propriedades  
- 30 cartões Sorte/Reves  
- 6 piões  
- 2 dados  
- 380 notas  
- 1 tabuleiro  

---

## OBJETIVO
Tornar-se o mais rico jogador, através de **compra, aluguel ou venda de propriedades**.

---

## JOGADORES
Podem jogar de **2 a 6 pessoas**, as quais escolhem a cor de seus piões, colocando-os no ponto de partida.

Em seguida, embaralham-se as **cartas de Sorte e Revés**, que são colocadas de cabeça para baixo no local indicado, no centro do tabuleiro.

Cada jogador deve receber:
- 8 notas de $1  
- 10 de $5  
- 10 de $10  
- 10 de $50  
- 8 de $100  
- 2 notas de $500  

O restante do dinheiro fica com o **Banco**, junto com os títulos de propriedade.  
É aconselhável que uma pessoa jogue como **banqueiro**, mas que também possa participar do jogo, tomando cuidado para **não misturar suas notas e propriedades** com as do banco.

---

## COMEÇO DO JOGO
O primeiro jogador lança os dados e, conforme o número de pontos obtidos, avança seu pião pelo tabuleiro.

- Se o espaço atingido for **comprável**, ele poderá **comprar** a propriedade pagando o preço indicado.
- Se cair em um **terreno ou empresa com dono**, deve **pagar automaticamente o aluguel ou taxa** correspondente.
- Se cair em **Sorte ou Revés**, deve **retirar um cartão** do monte e seguir as instruções.

Se tirar **dupla** (ex.: dois 2, dois 3 etc.), tem direito a **novo lançamento**.  
Uma segunda dupla dá direito a outro lançamento, mas **três duplas seguidas** enviam o jogador **à prisão**.

---

## PRISÃO
O jogador irá à prisão se cair no campo “VÁ PARA A PRISÃO” ou se tirar **três duplas seguidas**.

Estando preso:
- Poderá **sair** se **tirar uma dupla** nas três próximas jogadas;  
- Ou se **pagar $50** ao banqueiro antes de jogar;  
- Ou se possuir o **cartão “Saída Livre da Prisão”**, que deve ser devolvido ao baralho após o uso.

Enquanto estiver preso, o jogador **não recebe aluguel**, mas pode **vender ou negociar propriedades**.

---

## HONORÁRIOS
Cada vez que o jogador **alcançar o ponto de partida** ou **passar por ele**, receberá do banqueiro **$200** como **honorários**.

---

## TERRENO OU EMPRESA COM DONO
Se o jogador cair em um terreno ou empresa já pertencente a outro jogador, deverá **pagar automaticamente** o **aluguel** ou **taxa** conforme o título da propriedade.

O dono da propriedade deve **cobrar antes do próximo jogador lançar os dados**, caso contrário perde o direito ao recebimento.

---

## CONSTRUÇÕES
Assim que o jogador possuir **todo um grupo de propriedades da mesma cor**, poderá construir casas e hotéis.

- **Casas:** até **4 casas** por terreno, pagando ao banqueiro o preço indicado no título.  
- **Hotel:** pode ser construído após 4 casas, substituindo-as.  
- O jogador pode construir **em cada propriedade de forma equilibrada**, não sendo permitido colocar mais de uma casa em uma propriedade antes de colocar pelo menos uma nas outras do mesmo grupo.  

---

## TROCAS E VENDAS ENTRE JOGADORES
É permitido que os jogadores **negociem trocas ou vendas** entre si, combinando preços livremente.

Caso o jogador deseje vender casas ou hotéis, deverá **vender ao banco** pela **metade do valor** pago originalmente.

---

## HIPOTECAS
Terrenos **sem construção** podem ser hipotecados ao banco pelo valor indicado no título.  
Caso haja casas ou hotéis, é necessário **vendê-los primeiro ao banco** pela metade do preço antes de hipotecar.

Empresas também podem ser hipotecadas pelos valores determinados em seus títulos.

---

## PAGAMENTOS
Pagamentos devem ser feitos **sempre em dinheiro**.

Se o jogador **não tiver dinheiro** suficiente:
1. Deve **vender casas e hotéis** (pela metade do preço);
2. Se ainda não for suficiente, pode **hipotecar ou vender propriedades**.

Caso ninguém queira comprar, o **banco compra pelo valor nominal**.

---

## FALÊNCIA
Se mesmo após vender casas, hotéis e hipotecar propriedades o jogador **não conseguir pagar suas dívidas**, ele **vai à falência** e **sai do jogo**.

- O dinheiro obtido com vendas vai para o credor.  
- Propriedades hipotecadas do jogador falido serão leiloadas pelo banco.  

---

## OBSERVAÇÃO
Durante o jogo **nenhum jogador pode emprestar ou doar dinheiro** a outro jogador.

---

## TÉRMINO DO JOGO
O jogo termina quando **restar apenas um jogador** (os outros foram à falência).  
Apuram-se então os valores:
- Notas  
- Terrenos  
- Propriedades  
- Casas e hotéis  

Caso algum jogador possua **propriedades hipotecadas**, deve computar **metade do valor pago por elas** no total de seus bens.

---


Entretanto, para adaptar as regras do jogo físico à simulação em computador, algumas **modificações** são necessárias.  
A seguir estão as adaptações que devem ser consideradas:

---

### COMPONENTES

- As **32 casas plásticas**, os **12 hotéis plásticos** e as **380 notas** **não serão representadas graficamente**.  
- O programa deve **armazenar internamente**:
  - O **montante de dinheiro** de cada jogador;
  - As **quantidades de casas e hotéis** existentes em cada propriedade.
- Os **dados**, **títulos de propriedade**, **piões** e **cartões de sorte/revés** serão representados por **imagens** (já publicadas na página da disciplina).

---

### JOGADORES

- O **número de jogadores** (entre **2 e 6**) será definido no **frame inicial**.  
- Cada jogador **iniciará com $4.000** unidades monetárias.  
- O **banco** iniciará com **$200.000** unidades monetárias.

---

### BANQUEIRO

- O **papel de banqueiro** será exercido **pelo próprio software**.

---

### COMEÇO DO JOGO

- **Não é necessário** realizar sorteio para definir a ordem dos jogadores.  
- A **ordem pode ser fixa** e associada às **cores dos piões**.

---

### PRISÃO

- Um jogador **sairá da prisão** caso:
  1. Obtenha **dois números iguais** ao lançar os dados; **ou**
  2. Possua o **cartão “Saída Livre da Prisão”**.

- Nesse caso, a **saída será automática**, e o **cartão deverá ser devolvido** ao baralho de cartas.
- O **pagamento de multa ao banqueiro não será considerado**.

---

### TERRENO OU EMPRESA COM DONO

- O **pagamento de aluguel ou taxa** deve ser feito **automaticamente**.

---

### TROCAS E VENDAS ENTRE JOGADORES

- **Não serão implementadas.**

---

### CONSTRUÇÕES

Como **trocas e vendas** não serão implementadas, as regras de construção foram adaptadas:

1. Na **1ª vez** que um jogador **cair em uma propriedade**, ele **poderá comprá-la**, caso esteja disponível.  
2. Nas **vezes subsequentes**, será possível **construir um único imóvel por vez**.  
3. Para **construir um hotel**, a propriedade deve ter **pelo menos uma casa**.  
4. Cada propriedade pode ter **até 4 casas e 1 hotel**.

---

### HIPOTECAS

- **Não serão implementadas.**

---

### PAGAMENTOS

- Devem ser feitos **automaticamente** (débito e crédito).  
- Caso um jogador **precise de dinheiro**, ele poderá **vender uma propriedade ao banco**, recebendo **90% do valor total** (terreno + construções).

---

### FALÊNCIA

- **Empréstimos e doações** **não serão implementados**.  
- Se um jogador **falir**, ele **sairá do jogo**.

---

### TÉRMINO DO JOGO

- O jogo **terminará quando os jogadores decidirem**.  
- Nesse momento:
  - Será **apurado o capital acumulado** por cada jogador;
  - Será **definida a posição (ranking)** de cada um.

---

### Observação Final

Caso, no decorrer do desenvolvimento, sejam detectadas **dificuldades excessivas** para implementar alguma regra, este documento **poderá ser atualizado** com a lista das regras que **ficarão de fora do trabalho**.

---