package model;

import java.util.ArrayList;
import java.util.List;

public class Jogador implements Observable {

    private String nome;
    private String cor;
    private double saldo;
    private Piao piao;
    private List<Propriedade> propriedades;
    private int posicao;

    // ------- CAMPOS RELACIONADOS À PRISÃO -------
    private boolean preso;                 // está preso?
    private int turnosPreso;               // quantos turnos já está preso
    private boolean temCartaSairPrisao;    // possui carta de saída livre?

    private int ordem;
    private final List<Observer> observers;

    public Jogador(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
        this.saldo = 4000.0;
        this.piao = new Piao(cor);
        this.propriedades = new ArrayList<>();
        this.posicao = 0;

        this.preso = false;
        this.turnosPreso = 0;
        this.temCartaSairPrisao = false;

        this.ordem = 0;
        this.observers = new ArrayList<>();
    }

    // ---------------- GETTERS ----------------
    public String getNome() { return nome; }
    public String getCor() { return cor; }
    public double getSaldo() { return saldo; }
    public Piao getPiao() { return piao; }
    public List<Propriedade> getPropriedades() { return propriedades; }
    public int getPosicao() { return posicao; }
    public boolean isPreso() { return preso; }
    public int getTurnosPreso() { return turnosPreso; }
    public boolean temCartaSairPrisao() { return temCartaSairPrisao; }
    public int getOrdem() { return ordem; }

    // ---------------- SETTERS ----------------

    public void setOrdem(int ordem) {
        this.ordem = ordem;
        notifyObservers("ordemDefinida");
    }

    public void setPreso(boolean preso) {
        this.preso = preso;
        this.turnosPreso = preso ? 0 : 0; // reset sempre que entra ou sai

        notifyObservers(preso ? "jogadorPreso" : "jogadorSolto");
    }

    public void incrementarTurnoPreso() {
        this.turnosPreso++;
        notifyObservers("turnoPresoIncrementado");
    }

    public void receberCartaSairPrisao() {
        this.temCartaSairPrisao = true;
        notifyObservers("recebeuCartaSairPrisao");
    }

    public void usarCartaSairPrisao() {
        this.temCartaSairPrisao = false;
        notifyObservers("usouCartaSairPrisao");
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao % 40;
        notifyObservers("posicaoAlterada");
    }

    public void deslocar(int casas) {
        this.posicao = (posicao + casas) % 40;
        notifyObservers("posicaoAlterada");
    }

    public void ajustarSaldo(double valor) {
        this.saldo += valor;
        notifyObservers("saldoAlterado");

        if (saldo <= 0) {
            notifyObservers("falencia");
        }
    }

    public void adicionarPropriedade(Propriedade propriedade) {
        this.propriedades.add(propriedade);
        notifyObservers("propriedadeAdquirida");
    }

    public void removerPropriedade(Propriedade propriedade) {
        this.propriedades.remove(propriedade);
        notifyObservers("propriedadeRemovida");
    }

    public boolean isFalido() {
        return saldo <= 0;
    }

    // ---------------- OBSERVER ----------------
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
