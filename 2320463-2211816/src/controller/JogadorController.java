package controller;

import java.util.Collections;
import java.util.List;
import model.Jogador;
import model.Observer;

public class JogadorController {

    private final List<Jogador> jogadores;
    private int jogadorAtualIndex = 0;

    public JogadorController(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public void sortearOrdemJogadores() {
        Collections.shuffle(jogadores);
        for (int i = 0; i < jogadores.size(); i++) {
            jogadores.get(i).setOrdem(i + 1);
        }
        jogadores.sort((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem()));
    }

    public Jogador getJogadorAtual() {
        if (jogadores.isEmpty()) return null;
        return jogadores.get(jogadorAtualIndex);
    }

    public void proximoJogador() {
        if (jogadores.isEmpty()) return;
        jogadorAtualIndex = (jogadorAtualIndex + 1) % jogadores.size();
    }

    public void removerJogador(Jogador jogador) {
        int index = jogadores.indexOf(jogador);
        if (index >= 0) {
            jogadores.remove(index);
            if (jogadorAtualIndex >= jogadores.size()) {
                jogadorAtualIndex = 0;
            }
        }
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void addObserverAosJogadores(Observer o) {
        for (Jogador j : jogadores) {
            j.addObserver(o);
        }
    }

    public void removeObserverDosJogadores(Observer o) {
        for (Jogador j : jogadores) {
            j.removeObserver(o);
        }
    }
}
