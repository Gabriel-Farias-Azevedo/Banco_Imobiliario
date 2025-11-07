package controller;

import java.util.Collections;
import java.util.List;
import model.Jogador;

public class JogadorController {

    private final List<Jogador> jogadores;

    public JogadorController(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    /** Sorteia a ordem dos jogadores aleatoriamente */
    public void sortearOrdemJogadores() {
        Collections.shuffle(jogadores);
        for (int i = 0; i < jogadores.size(); i++) {
            jogadores.get(i).setOrdem(i + 1);
        }
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }
}
