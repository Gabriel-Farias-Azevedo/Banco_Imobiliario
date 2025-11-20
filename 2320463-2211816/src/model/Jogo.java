package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import controller.CartaController;

/**
 * Representa o núcleo do jogo Banco Imobiliário.
 * Gerencia jogadores, tabuleiro, banco, dados, prisões e baralhos.
 * Implementa Observable para sincronização com a interface.
 */
public class Jogo implements Observable, Observer {

    private final List<Jogador> jogadores;
    private final Tabuleiro tabuleiro;
    private final Banco banco;
    private final Prisao prisao;
    private final CartaController baralhoCartas;
    private final Dado dado;
    private final List<Observer> observers;

    private int jogadorDaVez;

    public Jogo(int numJogadores) {
        this.jogadores = new ArrayList<>();
        this.tabuleiro = new Tabuleiro();
        this.banco = Banco.getInstance();
        this.prisao = new Prisao();
        this.baralhoCartas = new CartaController();
        this.dado = new Dado();
        this.jogadorDaVez = 0;
        this.observers = new ArrayList<>();

        String[] cores = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Preto"};

        for (int i = 0; i < numJogadores; i++) {
            Jogador jogador = new Jogador("Jogador " + (i + 1), cores[i]);
            jogador.addObserver(this);
            jogadores.add(jogador);
        }

        dado.addObserver(this);
    }

    public int[] lancarDados() {
        dado.rolar();
        int[] valores = {dado.getValorA(), dado.getValorB()};
        notifyObservers("dadosLancados");
        return valores;
    }

    public int deslocarPiao(int[] valores) {
        Jogador atual = jogadores.get(jogadorDaVez);
        int soma = Arrays.stream(valores).sum();
        int moveu = tabuleiro.moverPiao(atual, soma);

        tabuleiro.verificarEfeito(atual, this);
        notifyObservers("jogadorMoveu");

        return moveu;
    }

    public void atualizarPosicaoJogador(Jogador jogador) {
        tabuleiro.verificarEfeito(jogador, this);
        notifyObservers("posicaoAtualizada");
    }

    public boolean comprarPropriedade() {
        Jogador atual = jogadores.get(jogadorDaVez);

        Propriedade prop = tabuleiro.getPropriedadeNaPosicao(atual.getPosicao());

        if (prop == null || prop.isEspecial() || prop.temDono()) {
            return false;
        }

        if (atual.getSaldo() >= prop.getPreco()) {
            atual.ajustarSaldo(-prop.getPreco());
            banco.creditar(prop.getPreco());
            prop.setDono(atual);
            atual.getPropriedades().add(prop);
            notifyObservers("propriedadeComprada");
            return true;
        }

        return false;
    }

    public boolean construirCasa() {
        Jogador atual = jogadores.get(jogadorDaVez);
        Propriedade prop = tabuleiro.getPropriedadeNaPosicao(atual.getPiao().getPosicao());
        if (prop != null && prop.getDono() == atual && prop.construirCasa()) {
            notifyObservers("casaConstruida");
            return true;
        }
        return false;
    }

    public void proximoJogador() {
        do {
            jogadorDaVez = (jogadorDaVez + 1) % jogadores.size();
        } while (jogadores.get(jogadorDaVez).isFalido());

        notifyObservers("proximoJogador");
    }

    public void verificarFalencia(Jogador jogador) {
        if (jogador.isFalido()) {
            notifyObservers("falencia");

            for (Propriedade p : jogador.getPropriedades()) {
                p.setDono(null);
            }

            jogadores.remove(jogador);

            if (jogadores.size() == 1) {
                notifyObservers("jogoEncerrado");
            }
        }
    }

    public CartaController getBaralhoCartas() { return baralhoCartas; }
    public Prisao getPrisao() { return prisao; }
    public Banco getBanco() { return banco; }
    public Dado getDado() { return dado; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public List<Jogador> getJogadores() { return jogadores; }
    public Jogador getJogadorDaVez() { return jogadores.get(jogadorDaVez); }

    @Override
    public void update(Observable observado, String evento) {
        switch (evento) {
            case "dadoRolado" -> notifyObservers("dadosLancados");
            case "saldoAlterado" -> notifyObservers("saldoAtualizado");
            case "posicaoAlterada" -> notifyObservers("posicaoAtualizada");
            case "falencia" -> notifyObservers("falencia");
        }
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String evento) {
        for (Observer o : observers) {
            o.update(this, evento);
        }
    }
}
