package model;

public class Banco {

    private int saldo;

    public Banco() {
        this.saldo = 200000; // duzentos mil
    }

    public void creditar(int valor) {
        saldo += valor;
    }

    public void debitar(int valor) {
        saldo -= valor;
    }

    public int getSaldo() {
        return saldo;
    }

    public void pagar(Jogador j, int valor) {
        j.ajustarSaldo(valor);
        this.debitar(valor);
    }

    public void receber(Jogador j, int valor) {
        j.ajustarSaldo(-valor);
        this.creditar(valor);
    }
}
