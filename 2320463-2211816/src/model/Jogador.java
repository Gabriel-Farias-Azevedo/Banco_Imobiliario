package model;

import java.util.*;

public class Jogador {
    private String nome;
    private String cor;
    private int saldo;
    private Piao piao;
    private List<Propriedade> propriedades;
    private int posicao;
    private boolean preso;

    // 🔹 Novo campo para armazenar a ordem de jogada
    private int ordem;

    public Jogador(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
        this.saldo = 4000;
        this.piao = new Piao();
        this.propriedades = new ArrayList<>();
        this.posicao = 0;
        this.preso = false;
        this.ordem = 0; // inicializa com 0 até o sorteio definir
    }

    // --- Getters e Setters ---
    public String getNome() { return nome; }
    public String getCor() { return cor; }
    public int getSaldo() { return saldo; }
    public Piao getPiao() { return piao; }
    public List<Propriedade> getPropriedades() { return propriedades; }
    public int getPosicao() { return posicao; }
    public boolean getPreso() { return preso; }
    public int getOrdem() { return ordem; }     // 🔹 Getter da ordem

    public void setPosicao(int posicao) {
        this.posicao = posicao % 40;
    }

    public void setPreso(boolean preso) {
        this.preso = preso;
    }

    public void setOrdem(int ordem) {           // 🔹 Setter da ordem
        this.ordem = ordem;
    }

    // --- Ações ---
    public void deslocar(int casas) {
        posicao = (posicao + casas) % 40;
    }

    public void debitar(int valor) { saldo -= valor; }
    public void creditar(int valor) { saldo += valor; }
    public void ajustarSaldo(int valor) { saldo += valor; }

    public boolean isFalido() {
        return saldo <= 0;
    }
}
