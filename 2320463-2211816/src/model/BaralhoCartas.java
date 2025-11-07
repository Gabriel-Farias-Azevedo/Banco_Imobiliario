package model;

import java.util.*;

public class BaralhoCartas {
    private List<Carta> cartasSorte;
    private List<Carta> cartasReves;
    private int proximaCartaSorte;
    private int proximaCartaReves;
    private Random random;

    public BaralhoCartas(){
        this.cartasSorte = new ArrayList<>();
        this.cartasReves = new ArrayList<>();
        this.proximaCartaSorte = 0;
        this.proximaCartaReves = 0;
        this.random = new Random();
        inicializarCartas();
        embaralhar();
    }   

    private void inicializarCartas() {
        // Inicializando Cartas de Sorte
        cartasSorte.add(new Carta("Receba $200 de honorários", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 200));
        cartasSorte.add(new Carta("Vá para a partida e receba $200", TipoCarta.SORTE, AcaoCarta.IR_PARA_PARTIDA, 0));
        cartasSorte.add(new Carta("Avance até Copacabana", TipoCarta.SORTE, AcaoCarta.MOVER_PARA_POSICAO, 39));
        cartasSorte.add(new Carta("Vá para a prisão", TipoCarta.SORTE, AcaoCarta.IR_PARA_PRISAO, 0));
        cartasSorte.add(new Carta("Saída livre da prisão", TipoCarta.SORTE, AcaoCarta.SAIR_LIVRE_PRISAO, 0));
        cartasSorte.add(new Carta("Receba $100", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasSorte.add(new Carta("Receba $50", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 50));
        cartasSorte.add(new Carta("Avance 3 casas", TipoCarta.SORTE, AcaoCarta.MOVER_ESPACOS, 3));
        cartasSorte.add(new Carta("Pague $50 de multa", TipoCarta.SORTE, AcaoCarta.PAGAR_DINHEIRO, 50));
        cartasSorte.add(new Carta("Pague $100 de imposto", TipoCarta.SORTE, AcaoCarta.PAGAR_DINHEIRO, 100));
        cartasSorte.add(new Carta("Volte 3 casas", TipoCarta.SORTE, AcaoCarta.MOVER_ESPACOS, -3));
        cartasSorte.add(new Carta("Pague $25 por cada casa e $100 por cada hotel", TipoCarta.SORTE, AcaoCarta.PAGAR_POR_PROPRIEDADE, 25));
        cartasSorte.add(new Carta("Receba $300", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 300));
        cartasSorte.add(new Carta("Pague $200", TipoCarta.SORTE, AcaoCarta.PAGAR_DINHEIRO, 200));
        cartasSorte.add(new Carta("Receba $150", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 150));

        // Inicializando Cartas de Revés
        cartasReves.add(new Carta("Pague $150 de imposto", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 150));
        cartasReves.add(new Carta("Vá para a prisão", TipoCarta.REVES, AcaoCarta.IR_PARA_PRISAO, 0));
        cartasReves.add(new Carta("Saída livre da prisão", TipoCarta.REVES, AcaoCarta.SAIR_LIVRE_PRISAO, 0));
        cartasReves.add(new Carta("Receba $100", TipoCarta.REVES, AcaoCarta.RECEBER_DINHEIRO, 100));
        cartasReves.add(new Carta("Pague $50 de multa", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 50));
        cartasReves.add(new Carta("Avance até a partida", TipoCarta.REVES, AcaoCarta.IR_PARA_PARTIDA, 0));
        cartasReves.add(new Carta("Pague $100", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 100));
        cartasReves.add(new Carta("Receba $200", TipoCarta.REVES, AcaoCarta.RECEBER_DINHEIRO, 200));
        cartasReves.add(new Carta("Pague $200 de imposto", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 200));
        cartasReves.add(new Carta("Receba $50", TipoCarta.REVES, AcaoCarta.RECEBER_DINHEIRO, 50));
        cartasReves.add(new Carta("Volte 2 casas", TipoCarta.REVES, AcaoCarta.MOVER_ESPACOS, -2));
        cartasReves.add(new Carta("Avance 5 casas", TipoCarta.REVES, AcaoCarta.MOVER_ESPACOS, 5));
        cartasReves.add(new Carta("Pague $75", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 75));
        cartasReves.add(new Carta("Receba $250", TipoCarta.REVES, AcaoCarta.RECEBER_DINHEIRO, 250));
        cartasReves.add(new Carta("Pague $40 por cada casa e $115 por cada hotel", TipoCarta.REVES, AcaoCarta.PAGAR_POR_PROPRIEDADE, 40));
    } 

    public void embaralhar() {
        Collections.shuffle(cartasSorte, random);
        Collections.shuffle(cartasReves, random);
        proximaCartaSorte = 0;
        proximaCartaReves = 0;
    }
    
    public Carta pegarCartaSorte() {
        if (proximaCartaSorte >= cartasSorte.size()) {
            embaralhar();
        }
        Carta carta = cartasSorte.get(proximaCartaSorte);
        proximaCartaSorte++;
        return carta;
    }
    
    public Carta pegarCartaReves() {
        if (proximaCartaReves >= cartasReves.size()) {
            embaralhar();
        }
        Carta carta = cartasReves.get(proximaCartaReves);
        proximaCartaReves++;
        return carta;
    }

    public void devolverCarta(Carta carta) {
        if (carta.getTipo() == TipoCarta.SORTE) {
            cartasSorte.add(carta);
        } else {
            cartasReves.add(carta);
        }
    }

    public List<Carta> getCartasSorte() {
        return cartasSorte;
    }

    public List<Carta> getCartasReves() {
        return cartasReves;
    }

}