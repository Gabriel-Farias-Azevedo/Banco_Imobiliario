package model;

import java.util.ArrayList;
import java.util.List;

public class Banco implements Observable {

    private static Banco instancia;  // Singleton
    private double saldo;
    private final List<Observer> observers; // Observadores (ex: Controller ou View)

    public Banco() {
        this.saldo = 200000.0; // duzentos mil
        this.observers = new ArrayList<>();
    }

    // 🔒 Singleton thread-safe
    public static synchronized Banco getInstance() {
        if (instancia == null) {
            instancia = new Banco();
        }
        return instancia;
    }

    public double getSaldo() {
        return saldo;
    }

    public void creditar(double valor) {
        saldo += valor;
        notifyObservers("credito");
    }

    public void debitar(double valor) {
        if (saldo >= valor) {
            saldo -= valor;
            notifyObservers("debito");
        } else {
            throw new IllegalStateException("Saldo insuficiente no banco!");
        }
    }

    // Banco paga ao jogador (ex: sorte/revés positivo)
    public void pagar(Jogador jogador, double valor) {
        if (saldo >= valor) {
            jogador.ajustarSaldo(valor);
            this.debitar(valor);
            notifyObservers("pagamento");
        } else {
            throw new IllegalStateException("Banco sem saldo suficiente para pagamento!");
        }
    }

    // Banco recebe do jogador (ex: taxas, multas)
    public void receber(Jogador jogador, double valor) {
        if (jogador.getSaldo() >= valor) {
            jogador.ajustarSaldo(-valor);
            this.creditar(valor);
            notifyObservers("recebimento");
        } else {
            throw new IllegalStateException("Jogador sem saldo suficiente para pagar ao banco!");
        }
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
