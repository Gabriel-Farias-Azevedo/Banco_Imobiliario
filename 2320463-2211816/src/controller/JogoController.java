package controller;

import java.util.ArrayList;
import java.util.List;

import model.Jogador;
import model.Jogo;
import model.Piao;
import model.Tabuleiro;
import model.Observable;
import model.Observer;

public class JogoController implements Observable {

    private static JogoController instancia;

    private final List<Jogador> jogadores = new ArrayList<>();
    private int jogadorAtualIndex = 0;

    private PiaoController piaoController;
    private JogadorController jogadorController;

    private Jogo jogo;
    private Tabuleiro tabuleiroModel;

    private final List<Observer> observers = new ArrayList<>();

    private JogoController() {
        jogo = new Jogo(4);
        tabuleiroModel = jogo.getTabuleiro();
    }

    public static JogoController getInstancia() {
        if (instancia == null) instancia = new JogoController();
        return instancia;
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
        for (Observer o : observers) o.update(this, evento);
    }

    public void adicionarJogador(String nome, String cor) {
        Jogador j = new Jogador(nome, cor);
        jogadores.add(j);
        jogo.getJogadores().add(j);
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Jogador getJogadorAtual() {
        return jogadores.get(jogadorAtualIndex);
    }

    public int getJogadorAtualIndex() {
        return jogadorAtualIndex;
    }

    public void reiniciar() {
        jogadores.clear();
        jogadorAtualIndex = 0;
        jogo = new Jogo(4);
        tabuleiroModel = jogo.getTabuleiro();
    }

    public void jogarDados(int d1, int d2) {

    Jogador jogador = getJogadorAtual();
    var prisao = jogo.getPrisao();

    if (jogador.isPreso()) {

        // 1.1 — Sai automaticamente se tiver carta
        if (jogador.temCartaSairPrisao()) {
            prisao.tentarSairPorCarta(jogador);
            notifyObservers("jogadorSolto");
        }

        // 1.2 — Tenta sair por dados iguais
        else if (d1 == d2) {
            prisao.tentarSairPorDado(jogador, d1, d2);
            notifyObservers("jogadorSolto");
        }

        // 1.3 — Se ainda estiver preso, perde o turno
        if (jogador.isPreso()) {
            jogador.incrementarTurnoPreso();
            notifyObservers("permanecePreso");
            return;     // 🔥 TURN0 TERMINA AQUI
        }
    }

    if (piaoController == null) return;

    piaoController.setJogadorVez(jogadorAtualIndex);
    piaoController.moverPiao(d1, d2);

    // Verifica falência após mover
    jogo.verificarFalencia(jogador);

    if (jogador.isFalido()) {
        jogadorController.removerJogador(jogador);

        if (jogadores.size() <= 1) {
            System.out.println("Jogo encerrado!");
        }
    }
}


    public void passarTurno() {
        jogadorAtualIndex++;
        jogadorAtualIndex %= jogadores.size();
        notifyObservers("VezMudou");
    }

    public Jogo getJogo() { return jogo; }
    public Tabuleiro getTabuleiro() { return tabuleiroModel; }

    public void setPiaoController(PiaoController pc) {
        this.piaoController = pc;
    }

    public void setJogadorController(JogadorController jc) {
        this.jogadorController = jc;
    }
}
