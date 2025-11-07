package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Prisao implements Observable {

    private final Set<Jogador> presos;
    private final List<Observer> observers;

    public Prisao() {
        this.presos = new HashSet<>();
        this.observers = new ArrayList<>();
    }

    public void prender(Jogador j) {
        presos.add(j);
        j.setPreso(true);
        notifyObservers("jogadorPreso");
    }

    public void soltarDado(Jogador j, int dado1, int dado2) {
        if (dado1 == dado2 && presos.contains(j)) {
            presos.remove(j);
            j.setPreso(false);
            notifyObservers("soltoPorDado");
        }
    }

    public void soltarCarta(Jogador j) {
        if (presos.contains(j)) {
            presos.remove(j);
            j.setPreso(false);
            notifyObservers("soltoPorCarta");
        }
    }

    public boolean estaPreso(Jogador j) {
        return presos.contains(j);
    }

    public int getQuantidadePresos() {
        return presos.size();
    }

    public Set<Jogador> getPresos() {
        return Collections.unmodifiableSet(presos);
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
