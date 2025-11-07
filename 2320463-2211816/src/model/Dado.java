package model;

import java.util.Random;

public class Dado {
    private int valorA;
    private int valorB;
    private final Random random = new Random();

    public void rolar() {
        // Garante valores de 1 a 6
        valorA = random.nextInt(6) + 1;
        valorB = random.nextInt(6) + 1;
    }

    public int getValorA() { return valorA; }
    public int getValorB() { return valorB; }

    public void setValorA(int valorA) { this.valorA = Math.max(1, Math.min(6, valorA)); }
    public void setValorB(int valorB) { this.valorB = Math.max(1, Math.min(6, valorB)); }
}
