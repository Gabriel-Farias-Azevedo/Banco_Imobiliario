package model;

import java.util.HashSet;
import java.util.Set;

public class Prisao {
    private Set<Jogador> presos = new HashSet<>();

    public void prender(Jogador j) {
        presos.add(j);
        j.setPreso(true);
    }

    public void soltarDado(Jogador j, int dado1, int dado2) {
        if (dado1 == dado2) {
            presos.remove(j);
            j.setPreso(false);
        }
    }
    
    public void soltarCarta(Jogador j) { {
            presos.remove(j);
            j.setPreso(false);
        }
    }

    public boolean estaPreso(Jogador j) {
        return presos.contains(j);
    }
}
