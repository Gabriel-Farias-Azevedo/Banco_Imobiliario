package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jogo {
    private List<Jogador> jogadores;
    private Tabuleiro tabuleiro;
    private Banco banco;
    private Prisao prisao;
    private int jogadorDaVez;
    private BaralhoCartas baralhoCartas;
    private List<Carta> deckSorte;
    private List<Carta> deckReves;
    private Dado dado;

    public Jogo(int numJogadores) {
        this.jogadores = new ArrayList<>();
        this.tabuleiro = new Tabuleiro();
        this.banco = new Banco();
        this.baralhoCartas = new BaralhoCartas();
        this.prisao = new Prisao();
        this.deckSorte = baralhoCartas.getCartasSorte();
        this.deckReves = baralhoCartas.getCartasReves();
        this.jogadorDaVez = 0;
        this.dado = new Dado(); // inicializa o objeto Dado

        String[] cores = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Preto"};

        for (int i = 0; i < numJogadores; i++) {
            jogadores.add(new Jogador("Jogador " + (i + 1), cores[i]));
        }
    }

    /**
     * Rola os dados usando o objeto Dado e retorna os valores
     */
    public int[] lancarDados() {
        dado.rolar();
        return new int[]{dado.getValorA(), dado.getValorB()};
    }

    /**
     * Move o peão do jogador da vez de acordo com os valores passados
     */
    public int deslocarPiao(int[] valores) {
        Jogador atual = jogadores.get(jogadorDaVez);
        int soma = Arrays.stream(valores).sum();
        int moveu = tabuleiro.moverPiao(atual, soma);

        tabuleiro.verificarEfeito(atual, this);
        return moveu;
    }

    /**
     * Compra a propriedade em que o jogador da vez está
     */
    public boolean comprarPropriedade() {
        Jogador atual = jogadores.get(jogadorDaVez);
        return tabuleiro.comprarPropriedade(atual, banco);
    }

    /**
     * Constrói uma casa na propriedade do jogador da vez
     */
    public boolean construirCasa() {
        Jogador atual = jogadores.get(jogadorDaVez);
        Propriedade prop = tabuleiro.getPropriedadeNaPosicao(atual.getPiao().getPosicao());
        if (prop != null && prop.getDono() == atual) {
            return prop.construirCasa();
        }
        return false;
    }

    /**
     * Passa a vez para o próximo jogador
     */
    public void proximoJogador() {
        jogadorDaVez = (jogadorDaVez + 1) % jogadores.size();
    }

    /**
     * Verifica falência do jogador e libera propriedades se necessário
     */
    public void verificarFalencia(Jogador jogador) {
        if (jogador.getSaldo() < 0) {
            jogadores.remove(jogador);
            for (Propriedade p : jogador.getPropriedades()) {
                p.setDono(null);
            }
        }
    }

    // Getters
    public BaralhoCartas getBaralhoCartas() { return baralhoCartas; }
    public Prisao getPrisao() { return prisao; }
    public Banco getBanco() { return banco; }
    public List<Carta> getDeckSorte() { return deckSorte; }
    public List<Carta> getDeckReves() { return deckReves; }
    public Jogador getJogadorDaVez() { return jogadores.get(jogadorDaVez); }
    public Dado getDado() { return dado; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public List<Jogador> getJogadores() { return jogadores; }
}
