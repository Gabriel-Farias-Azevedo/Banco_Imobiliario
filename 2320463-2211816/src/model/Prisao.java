package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Prisao implements Observable {

    private final Set<Jogador> presos = new HashSet<>();
    private final List<Observer> observers = new ArrayList<>();

    public void prender(Jogador j) {
        if (!presos.contains(j)) {
            presos.add(j);
            j.setPreso(true);
            j.incrementarTurnoPreso(); // começa a contar
            notifyObservers("jogadorPreso");
        }
    }

    public boolean tentarSairPorCarta(Jogador j) {
        if (presos.contains(j) && j.temCartaSairPrisao()) {
            j.usarCartaSairPrisao();
            presos.remove(j);
            j.setPreso(false);
            notifyObservers("soltoPorCarta");
            return true;
        }
        return false;
    }

    public boolean tentarSairPorDado(Jogador j, int d1, int d2) {
        if (presos.contains(j) && d1 == d2) {
            presos.remove(j);
            j.setPreso(false);
            notifyObservers("soltoPorDado");
            return true;
        }
        return false;
    }

    public boolean estaPreso(Jogador j) { return presos.contains(j); }

    @Override
    public void addObserver(Observer o) { observers.add(o); }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers(String evento) {
        for (Observer o : observers) o.update(this, evento);
    }
}
