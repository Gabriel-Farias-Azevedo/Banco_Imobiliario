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
    private boolean preso;
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
        this.ordem = 0;
        this.observers = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public String getCor() { return cor; }
    public double getSaldo() { return saldo; }
    public Piao getPiao() { return piao; }
    public List<Propriedade> getPropriedades() { return propriedades; }
    public int getPosicao() { return posicao; }
    public boolean isPreso() { return preso; }
    public int getOrdem() { return ordem; }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
        notifyObservers("ordemDefinida");
    }

    public void setPreso(boolean preso) {
        this.preso = preso;
        notifyObservers(preso ? "jogadorPreso" : "jogadorSolto");
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
