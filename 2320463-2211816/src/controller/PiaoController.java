package controller;

import java.util.List;
import java.util.ArrayList;

import model.Jogador;
import model.Observable;
import model.Observer;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView.JogadorView;

public class PiaoController implements Observer, Observable {

    private final List<Piao> pioes;
    private final List<JogadorView> jogadoresView;
    private final Tabuleiro tabuleiro;

    private int jogadorVez = 0;

    private final List<Observer> observers = new ArrayList<>();

    public PiaoController(List<Piao> pioes, List<JogadorView> jogadoresView, Tabuleiro tabuleiro) {
        this.pioes = pioes;
        this.jogadoresView = jogadoresView;
        this.tabuleiro = tabuleiro;

        // Observa cada pião (somente se emitirem eventos internos)
        for (Piao p : pioes) {
            p.addObserver(this);
        }
    }

    // ======================================================
    //               MOVIMENTAÇÃO DO PIÃO
    // ======================================================

    public void moverPiao(int dadoA, int dadoB) {

        if (jogadorVez < 0 || jogadorVez >= pioes.size()) {
            System.err.println("ERRO: jogadorVez fora do intervalo!");
            return;
        }

        int passos = dadoA + dadoB;

        Piao piao = pioes.get(jogadorVez);
        int posAnterior = piao.getPosicao();

        // Move o pião no model
        piao.mover(passos);

        // Atualiza view do pião
        JogadorView jv = jogadoresView.get(jogadorVez);
        jv.pistaIndex = piao.getPosicao();

        // Atualiza model do jogador
        Jogador jogador = JogoController.getInstancia().getJogadores().get(jogadorVez);
        jogador.setPosicao(piao.getPosicao());

        // Passou pela saída?
        if (posAnterior + passos >= 40) {
            jogador.ajustarSaldo(200);
        }

        // Aplica efeitos da casa (Sorte / Revés / Propriedade)
        String msg = tabuleiro.verificarEfeito(jogador, JogoController.getInstancia().getJogo());
        if (msg != null && !msg.isEmpty()) {
            System.out.println(msg);
        }

        // Notifica view uma única vez
        notifyObservers("PiaoMovido");
    }

    // ======================================================
    //                    TURNO
    // ======================================================

    public void setJogadorVez(int idx) {
        if (idx >= 0 && idx < pioes.size()) {
            this.jogadorVez = idx;
        }
    }

    public int getJogadorVez() {
        return jogadorVez;
    }

    // ======================================================
    //              OBSERVER (escuta o Pião)
    // ======================================================

    @Override
    public void update(Observable observado, String evento) {

        // Se o modelo Piao emitir eventos internos,
        // NÃO chamamos moverPiao novamente, só propagamos para a View.
        if ("PiaoMovido".equals(evento) || "PiaoReposicionado".equals(evento)) {
            notifyObservers(evento);
        }
    }

    // ======================================================
    //                     OBSERVABLE
    // ======================================================

    @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o))
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
