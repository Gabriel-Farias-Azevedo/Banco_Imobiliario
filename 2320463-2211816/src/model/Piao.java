package model;

import java.util.ArrayList;
import java.util.List;

public class Piao implements Observable {

    private int posicao; // de 0 a 39 (tabuleiro com 40 casas)
    private String cor;
    private final List<Observer> observers;

    public Piao(String cor) {
        this.cor = cor;
        this.posicao = 0; // posição inicial
        this.observers = new ArrayList<>();
    }

    public void mover(int passos) {
        posicao = (posicao + passos) % 40;
        if (posicao < 0) posicao += 40;
        notifyObservers("piaoMovido");
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao % 40;
        notifyObservers("piaoReposicionado");
    }

    public int getPosicao() {
        return posicao;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
        notifyObservers("corAlterada");
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
