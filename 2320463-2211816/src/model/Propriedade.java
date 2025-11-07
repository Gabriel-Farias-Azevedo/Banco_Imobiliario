package model;

import java.util.ArrayList;
import java.util.List;

public class Propriedade implements Observable {
    private String nome;
    private double preco;
    private Jogador dono;
    private int casas;
    private int hoteis;
    private boolean especial;
    private final List<Observer> observers;

    public Propriedade(String nome, double preco) {
        this(nome, preco, false);
    }

    public Propriedade(String nome, double preco, boolean especial) {
        this.nome = nome;
        this.preco = preco;
        this.especial = especial;
        this.casas = 0;
        this.hoteis = 0;
        this.observers = new ArrayList<>();
    }

    public boolean temDono() { return dono != null; }

    public void setDono(Jogador jogador) { this.dono = jogador; }

    public Jogador getDono() { return dono; }

    public double getPreco() { return preco; }

    public String getNome() { return nome; }

    public boolean isEspecial() { return especial; }

    public int getCasas() { return casas; }

    public int getHoteis() { return hoteis; }

    public boolean comprar(Jogador jogador) {
        if (!temDono() && !especial && jogador.getSaldo() >= preco) {
            Banco.getInstance().receber(jogador, preco);
            this.dono = jogador;
            notifyObservers("propriedadeComprada");
            return true;
        }
        return false;
    }

    public boolean construirCasa() {
        if (dono == null || especial) return false;
        if (casas < 4 && hoteis == 0) {
            casas++;
            notifyObservers("casaConstruida");
            return true;
        }
        return false;
    }

    public boolean construirHotel() {
        if (dono == null || especial) return false;
        if (casas == 4 && hoteis < 1) {
            hoteis = 1;
            casas = 0;
            notifyObservers("hotelConstruido");
            return true;
        }
        return false;
    }

    public double calcularAluguel() {
        if (hoteis > 0) return 500;
        if (casas > 0) return casas * 100;
        return 0;
    }

    public void cobrarAluguel(Jogador jogador) {
        if (dono != null && dono != jogador && !especial) {
            double aluguel = calcularAluguel();
            Banco.getInstance().receber(jogador, aluguel);
            Banco.getInstance().pagar(dono, aluguel);
            notifyObservers("aluguelCobrado");
        }
    }

    public void resetar() {
        dono = null;
        casas = 0;
        hoteis = 0;
    }

    @Override
    public void addObserver(Observer o) { observers.add(o); }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers(String evento) {
        for (Observer o : observers) {
            o.update(this, evento);
        }
    }
}
