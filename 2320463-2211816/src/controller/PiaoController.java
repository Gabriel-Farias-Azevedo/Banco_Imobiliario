package controller;

import java.util.ArrayList;
import java.util.List;
import model.Jogador;
import model.Observable;
import model.Piao;
import model.Tabuleiro;
import view.Observer;
import view.TabuleiroView.JogadorView;

public class PiaoController implements Observable {

    private final List<Piao> pioes;
    private final List<JogadorView> jogadoresView;
    private int jogadorVez = 0;
    private final Tabuleiro tabuleiro;

    private final List<Observer> observadores = new ArrayList<>();

    public PiaoController(List<Piao> pioes, List<JogadorView> jogadoresView, Tabuleiro tabuleiro) {
        this.pioes = pioes;
        this.jogadoresView = jogadoresView;
        this.tabuleiro = tabuleiro;
    }

    @Override
    public void addObserver(Observer o) {
        if (!observadores.contains(o)) observadores.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observadores.remove(o);
    }

    private void notificarObservadores(String evento) {
        for (Observer obs : observadores) {
            obs.atualizar(this, evento);
        }
    }

    public void moverPiao(int dadoA, int dadoB) {
        int passos = dadoA + dadoB;
        Piao p = pioes.get(jogadorVez);
        int posicaoAnterior = p.getPosicao();
        p.mover(passos);

        // Atualiza JogadorView
        JogadorView jv = jogadoresView.get(jogadorVez);
        jv.pistaIndex = p.getPosicao();

        Jogador jogador = JogoController.getInstancia().getJogadores().get(jogadorVez);
        jogador.setPosicao(p.getPosicao());

        // Verifica se passou pela casa PARTIDA (posição 0)
        if (posicaoAnterior + passos >= 40) {
            jogador.creditar(200);
            System.out.println(jogador.getNome() + " passou pela PARTIDA e recebeu $200!");
        }

        // Verifica efeito da casa onde caiu
        String mensagem = tabuleiro.verificarEfeito(jogador, 
            JogoController.getInstancia().getJogo());
        
        if (mensagem != null && !mensagem.isEmpty()) {
            System.out.println(mensagem);
        }

        // Notifica observadores
        notificarObservadores("PiaoMovido");
        notificarObservadores("AtualizarInfoJogador"); 

        // Passa a vez
        jogadorVez = (jogadorVez + 1) % pioes.size();
        notificarObservadores("VezMudou");
    }

    public void setJogadorVez(int idx) {
        if (idx >= 0 && idx < pioes.size()) {
            jogadorVez = idx;
            notificarObservadores("VezMudou");
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

}
