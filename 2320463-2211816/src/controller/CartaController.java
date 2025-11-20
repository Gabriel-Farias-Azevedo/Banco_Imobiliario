package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.*;

public class CartaController {

    private final List<Carta> cartasSorte = new ArrayList<>();
    private final List<Carta> cartasReves = new ArrayList<>();

    public CartaController() {
        inicializarCartas();
    }

    private void inicializarCartas() {
        // ----------------- Cartas de SORTE -----------------
        cartasSorte.clear();
        cartasSorte.add(new Carta("Receba $25", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 25));
        cartasSorte.add(new Carta("Receba $150", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 150));
        cartasSorte.add(new Carta("Receba $80", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 80));
        cartasSorte.add(new Carta("Receba $200", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 200));
        cartasSorte.add(new Carta("Receba $50", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 50));
        cartasSorte.add(new Carta("Receba $50", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 50));
        cartasSorte.add(new Carta("Receba $100", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasSorte.add(new Carta("Receba $100", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasSorte.add(new Carta("Sair Livre da Prisão", TipoCarta.SORTE, AcaoCarta.SAIR_LIVRE_PRISAO, 0));
        cartasSorte.add(new Carta("Avance até o ponto de partida", TipoCarta.SORTE, AcaoCarta.IR_PARA_PARTIDA, 200));
        cartasSorte.add(new Carta("Receba $50 de cada jogador", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 50));
        cartasSorte.add(new Carta("Economizou $45", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 45));
        cartasSorte.add(new Carta("Receba $100", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasSorte.add(new Carta("Receba $100", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasSorte.add(new Carta("Receba $20", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 20));

        // ----------------- Cartas de REVÉS -----------------
        cartasReves.clear();
        cartasReves.add(new Carta("Pague $15", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 15));
        cartasReves.add(new Carta("Pague $25", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 25));
        cartasReves.add(new Carta("Pague $45", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 45));
        cartasReves.add(new Carta("Pague $30", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 30));
        cartasReves.add(new Carta("Pague $100", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 100));
        cartasReves.add(new Carta("Pague $100", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 100));
        cartasReves.add(new Carta("Pague $40", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 40));
        cartasReves.add(new Carta("Vá para a prisão", TipoCarta.REVES, AcaoCarta.IR_PARA_PRISAO, 0));
        cartasReves.add(new Carta("Pague $30", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 30));
        cartasReves.add(new Carta("Imposto de renda pague $30", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 50));
        cartasReves.add(new Carta("Pague $25", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 25));
        cartasReves.add(new Carta("Pague $30", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 30));
        cartasReves.add(new Carta("Pague $45", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 45));
        cartasReves.add(new Carta("Pague $50", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 50));
        cartasReves.add(new Carta("Pague $50", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 50));

        // Embaralhar os baralhos
        Collections.shuffle(cartasSorte);
        Collections.shuffle(cartasReves);
    }

    public Carta pegarCartaSorte() {
        if (cartasSorte.isEmpty()) inicializarCartas();
        return cartasSorte.remove(0);
    }

    public Carta pegarCartaReves() {
        if (cartasReves.isEmpty()) inicializarCartas();
        return cartasReves.remove(0);
    }

    public void devolverCarta(Carta carta) {
        if (carta.getTipo() == TipoCarta.SORTE) {
            cartasSorte.add(carta);
        } else if (carta.getTipo() == TipoCarta.REVES) {
            cartasReves.add(carta);
        }

        Collections.shuffle(cartasSorte);
        Collections.shuffle(cartasReves);
    }

    public void devolverCartaSairPrisao() {
        cartasSorte.add(new Carta("Sair Livre da Prisão", 
            TipoCarta.SORTE,
            AcaoCarta.SAIR_LIVRE_PRISAO,
            0));
        Collections.shuffle(cartasSorte);
    }


    /** Retorna o caminho da imagem da carta */
    public String getCaminhoImagem(Carta carta) {
        if (carta.getTipo() == TipoCarta.SORTE) {
            int indice = cartasSorte.indexOf(carta) + 1; // de 1 a 15
            if (indice < 1) indice = 1; // segurança
            if (indice > 15) indice = 15;
            return "/Imagens-01/sorteReves/chance" + indice + ".png";

        } 
        else if (carta.getTipo() == TipoCarta.REVES) {
            int indice = cartasReves.indexOf(carta) + 1;
            return "/Imagens-01/sorteReves/chance" + (indice + 15) + ".png";
        }
        
        return null;
    }
}
