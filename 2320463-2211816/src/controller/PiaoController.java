package controller;

import java.util.List;
import model.Jogador;
import model.Observable;
import model.Observer;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView.JogadorView;

public class PiaoController implements Observer, Observable {
    private final List<Piao> pioes;
    private final List<JogadorView> jogadoresView;
    private int jogadorVez = 0;
    private final Tabuleiro tabuleiro;
    private final List<Observer> observers = new java.util.ArrayList<>();

    public PiaoController(List<Piao> pioes, List<JogadorView> jogadoresView, Tabuleiro tabuleiro) {
        this.pioes = pioes;
        this.jogadoresView = jogadoresView;
        this.tabuleiro = tabuleiro;

        // Registra este controller como observador de todos os pioes
        for (Piao p : pioes) {
            p.addObserver(this);
        }
    }

    /** Movimenta o pião do jogador da vez */
    public void moverPiao(int dadoA, int dadoB) {
        int passos = dadoA + dadoB;
        Piao p = pioes.get(jogadorVez);
        int posicaoAnterior = p.getPosicao();

        // Move o pião (dispara notificação automática)
        p.mover(passos);

        // Atualiza JogadorView
        JogadorView jv = jogadoresView.get(jogadorVez);
        jv.pistaIndex = p.getPosicao();

        // Atualiza posição no Jogador
        Jogador jogador = JogoController.getInstancia().getJogadores().get(jogadorVez);
        jogador.setPosicao(p.getPosicao());

        // Passou pela partida
        if (posicaoAnterior + passos >= 40) {
            jogador.ajustarSaldo(200);
        }

        // Efeito da casa
        String mensagem = tabuleiro.verificarEfeito(jogador, JogoController.getInstancia().getJogo());
        if (mensagem != null && !mensagem.isEmpty()) {
            System.out.println(mensagem);
        }

        // Passa a vez
        jogadorVez = (jogadorVez + 1) % pioes.size();
    }

    /** Ajusta o jogador da vez manualmente */
    public void setJogadorVez(int idx) {
        if (idx >= 0 && idx < pioes.size()) {
            jogadorVez = idx;
        }
    }

    public int getJogadorVez() {
        return jogadorVez;
    }

    public List<Piao> getPioes() {
        return pioes;
    }

    public List<JogadorView> getJogadoresView() {
        return jogadoresView;
    }

   @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o)) observers.add(o);
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
    @Override
    public void update(Observable o, String evento) {
        // Pode propagar eventos de Piao para observers do controller
        if (evento.equals("piaoMovido") || evento.equals("piaoReposicionado")) {
            notifyObservers(evento);
        }
    }
}
