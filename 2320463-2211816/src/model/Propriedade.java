package model;

import java.util.ArrayList;
import java.util.List;

public class Propriedade implements Observable {

    private String nome;
    private double preco;
    private Jogador dono;
    private int casas;
    private int hoteis;
    private boolean especial; // sorte, revés, prisão, partida, imposto etc.
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

    // ---------------- GETTERS ----------------

    public boolean temDono() { return dono != null; }

    public void setDono(Jogador jogador) { this.dono = jogador; }

    public Jogador getDono() { return dono; }

    public double getPreco() { return preco; }

    public String getNome() { return nome; }

    public boolean isEspecial() { return especial; }

    public int getCasas() { return casas; }

    public int getHoteis() { return hoteis; }

    // Companhia = qualquer casa cujo nome contenha "companhia"
    public boolean isCompanhia() {
        return nome != null && nome.toLowerCase().contains("companhia");
    }

    // ---------------- COMPRA ----------------

    public boolean comprar(Jogador jogador) {
        if (!temDono() && !especial && jogador.getSaldo() >= preco) {
            Banco.getInstance().receber(jogador, preco);
            this.dono = jogador;
            notifyObservers("propriedadeComprada");
            return true;
        }
        return false;
    }

    // ---------------- CONSTRUÇÃO ----------------
    // Companhias NÃO podem construir

   public boolean construirCasa() {
        if (dono == null || especial) return false;
        if (casas < 4) {
            casas++;
            notifyObservers("casaConstruida");
            return true;
        }
        return false;
    }


    public boolean construirHotel() {
        if (dono == null || especial) return false;
        if (casas >= 1 && hoteis == 0) { 
            hoteis = 1;
            casas = casas - 1; // opcional: algumas versões mantêm 1 casa, outras zeram
            notifyObservers("hotelConstruido");
            return true;
        }
        return false;
    }


    // ---------------- ALUGUEL ----------------

    // Terrenos normais (sem dados)
    public double calcularAluguel() {

        if (especial) return 0;

        // COMPANHIAS são tratadas separadamente
        if (isCompanhia()) return 0; 

        double base = preco * 0.15;

        if (hoteis > 0) return preco * 1.00;

        if (casas > 0) return preco * (0.15 + casas * 0.35);

        return base;
    }

    public double calcularAluguel(int valorDados) {
        if (!isCompanhia() || dono == null) return calcularAluguel();

        long qtd = dono.getPropriedades().stream()
                    .filter(Propriedade::isCompanhia)
                    .count();

        return qtd == 1 ? valorDados * 4 : valorDados * 10;
    }


    public void cobrarAluguel(Jogador jogador) {
        if (dono != null && dono != jogador && !especial) {

            double aluguel;
            if (isCompanhia()) {
                aluguel = 0;
            } else {
                aluguel = calcularAluguel();
            }

            if (aluguel > 0) {
                Banco.getInstance().receber(jogador, aluguel);
                Banco.getInstance().pagar(dono, aluguel);
                notifyObservers("aluguelCobrado");
            }
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
