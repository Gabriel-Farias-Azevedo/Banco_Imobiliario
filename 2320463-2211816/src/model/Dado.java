package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dado implements Observable {

    private int valorA;
    private int valorB;
    private static final Random random = new Random();
    private final List<Observer> observers;

    public Dado() {
        this.valorA = 1;
        this.valorB = 1;
        this.observers = new ArrayList<>();
    }

    public void rolar() {
        valorA = random.nextInt(6) + 1;
        valorB = random.nextInt(6) + 1;
        notifyObservers("dadoRolado");
    }

    public int getValorA() {
        return valorA;
    }

    public int getValorB() {
        return valorB;
    }

    public void setValorA(int valorA) {
        this.valorA = Math.max(1, Math.min(6, valorA));
    }

    public void setValorB(int valorB) {
        this.valorB = Math.max(1, Math.min(6, valorB));
    }

    public int getSoma() {
        return valorA + valorB;
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
