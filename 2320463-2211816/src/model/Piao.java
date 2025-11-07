// ------------------------------------------------------------
// Model: Piao.java
package model;

public class Piao {
    private int posicao; // de 0 a 39 (tabuleiro com 40 casas)

    public void mover(int passos) {
        posicao = (posicao + passos) % 40; // loop do tabuleiro
        System.out.println("Pião movido para posição: " + posicao);
    }

    public int getPosicao() { return posicao; }
}
