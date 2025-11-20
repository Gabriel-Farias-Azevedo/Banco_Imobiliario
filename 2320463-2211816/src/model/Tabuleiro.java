package model;

import java.util.*;
import controller.CartaController;

public class Tabuleiro implements Observable {

    private final List<Propriedade> propriedades;
    private final List<Observer> observers;

    public Tabuleiro() {
        propriedades = new ArrayList<>();
        observers = new ArrayList<>();
        inicializarTabuleiro();
    }

    // ------------------------------------------------------------
    // INICIALIZAÇÃO DO TABULEIRO
    // ------------------------------------------------------------
    private void inicializarTabuleiro() {

        propriedades.clear();

        // ---- LADO DE BAIXO (da direita para a esquerda) ----
        propriedades.add(new Propriedade("Ponto de Partida", 0, true));  // 0
        propriedades.add(new Propriedade("Leblon", 100));                // 1
        propriedades.add(new Propriedade("Sorte", 0, true));             // 2
        propriedades.add(new Propriedade("Av. Presidente Vargas", 60));  // 3
        propriedades.add(new Propriedade("Av. Nossa S. de Copacabana", 60)); // 4

        propriedades.add(new Propriedade("Companhia Ferroviária", 200)); // 5
        propriedades.add(new Propriedade("Av. Brigadeiro Faria Lima", 240));  // 6
        propriedades.add(new Propriedade("Companhia de Viação", 200));   // 7

        propriedades.add(new Propriedade("Av. Rebouças", 220));          // 8
        propriedades.add(new Propriedade("Av. 9 de Julho", 220));        // 9

        propriedades.add(new Propriedade("Prisão", 0, true));            // 10

        // ---- LADO ESQUERDO ----
        propriedades.add(new Propriedade("Av. Europa", 200));            // 11
        propriedades.add(new Propriedade("Revés", 0, true));             // 12
        propriedades.add(new Propriedade("Rua Augusta", 180));           // 13
        propriedades.add(new Propriedade("Av. Pacaembú", 180));          // 14

        propriedades.add(new Propriedade("Companhia de Táxi", 150));     // 15
        propriedades.add(new Propriedade("Sorte", 0, true));             // 16
        propriedades.add(new Propriedade("Interlagos", 350));            // 17
        propriedades.add(new Propriedade("Lucros ou Dividendos", 200, true)); // 18

        propriedades.add(new Propriedade("Morumbi", 400));               // 19
        propriedades.add(new Propriedade("Parada Livre", 0, true));      // 20

        // ---- LADO DE CIMA ----
        propriedades.add(new Propriedade("Flamengo", 120));              // 21
        propriedades.add(new Propriedade("Sorte", 0, true));             // 22
        propriedades.add(new Propriedade("Botafogo", 100));              // 23
        propriedades.add(new Propriedade("Imposto de Renda", 200, true));// 24

        propriedades.add(new Propriedade("Companhia de Navegação", 150));// 25
        propriedades.add(new Propriedade("Av. Brasil", 160));            // 26
        propriedades.add(new Propriedade("Revés", 0, true));             // 27

        propriedades.add(new Propriedade("Av. Paulista", 140));          // 28
        propriedades.add(new Propriedade("Jardim Europa", 140));         // 29

        propriedades.add(new Propriedade("Vá para a Prisão", 0, true));  // 30

        // ---- LADO DIREITO ----
        propriedades.add(new Propriedade("Copacabana", 260));            // 31
        propriedades.add(new Propriedade("Companhia de Aviação", 200));  // 32

        propriedades.add(new Propriedade("Av. Vieira Souto", 320));      // 33
        propriedades.add(new Propriedade("Av. Atlântica", 300));         // 34

        propriedades.add(new Propriedade("Companhia de Táxi Aéreo", 200)); // 35
        propriedades.add(new Propriedade("Ipanema", 300));                 // 36

        propriedades.add(new Propriedade("Sorte", 0, true));             // 37
        propriedades.add(new Propriedade("Jardim Paulista", 280));       // 38
        propriedades.add(new Propriedade("Brooklin", 260));              // 39
    }

    // ------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // ------------------------------------------------------------

    public Propriedade getPropriedadeNaPosicao(int pos) {
        if (pos >= 0 && pos < propriedades.size()) {
            return propriedades.get(pos);
        }
        return null;
    }

    public int moverPiao(Jogador jogador, int passos) {
        jogador.deslocar(passos);
        return jogador.getPosicao();
    }

    // ------------------------------------------------------------
    // TIPOS DE CASA
    // ------------------------------------------------------------

    public enum TipoCasa {
        PARTIDA, PROPRIEDADE, SORTE, REVES, PRISÃO, IMPOSTO, LUCRO, NENHUMA
    }

    public TipoCasa verificarTipoCasa(Jogador jogador) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return TipoCasa.NENHUMA;

        String nome = prop.getNome().toLowerCase();

        if (nome.contains("partida")) return TipoCasa.PARTIDA;
        if (nome.contains("pris")) return TipoCasa.PRISÃO;
        if (nome.contains("sorte")) return TipoCasa.SORTE;
        if (nome.contains("rev")) return TipoCasa.REVES;
        if (nome.contains("imposto")) return TipoCasa.IMPOSTO;
        if (nome.contains("lucros") || nome.contains("dividendos")) return TipoCasa.LUCRO;

        if (!prop.isEspecial()) return TipoCasa.PROPRIEDADE;

        return TipoCasa.NENHUMA;
    }

    // ------------------------------------------------------------
    // REGRAS DAS CASAS
    // ------------------------------------------------------------

    public String verificarEfeito(Jogador jogador, Jogo jogo) {

        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return "Casa inválida";

        TipoCasa tipo = verificarTipoCasa(jogador);
        Banco banco = jogo.getBanco();
        Prisao prisao = jogo.getPrisao();
        CartaController baralho = jogo.getBaralhoCartas();

        switch (tipo) {

            case PROPRIEDADE:
                // PROPRIEDADE SEM DONO
                if (!prop.temDono()) {
                    if (!prop.isEspecial() && jogador.getSaldo() >= prop.getPreco()) {
                        jogador.ajustarSaldo(-prop.getPreco());
                        banco.creditar(prop.getPreco());
                        prop.setDono(jogador);
                        jogador.getPropriedades().add(prop);
                        notifyObservers("propriedadeComprada");
                        return jogador.getNome() + " comprou " + prop.getNome() + " por $" + prop.getPreco();
                    }
                    return jogador.getNome() + " não tem saldo suficiente para comprar " + prop.getNome();
                }

                // JÁ TEM DONO E É OUTRO JOGADOR → ALUGUEL
                if (prop.getDono() != jogador) {
                    double aluguel;

                    if (prop.isCompanhia()) {
                        int valorDados = jogo.getDado().getSoma();
                        aluguel = prop.calcularAluguel(valorDados);
                    } else {
                        aluguel = prop.calcularAluguel();
                    }

                    jogador.ajustarSaldo(-aluguel);
                    prop.getDono().ajustarSaldo(aluguel);
                    notifyObservers("aluguelPago");

                    return jogador.getNome() + " pagou $" + aluguel +
                            " de aluguel para " + prop.getDono().getNome();
                }

                return jogador.getNome() + " caiu em sua própria propriedade " + prop.getNome() + ".";

            case PRISÃO:
                prisao.prender(jogador);
                notifyObservers("prisao");
                return jogador.getNome() + " foi preso!";

            case PARTIDA:
                jogador.ajustarSaldo(200);
                notifyObservers("passouPartida");
                return jogador.getNome() + " passou pela Partida e recebeu $200.";

            case SORTE:
                Carta cartaS = baralho.pegarCartaSorte();
                cartaS.executarAcao(jogador, jogo, prisao);
                baralho.devolverCarta(cartaS);
                notifyObservers("cartaSorte");
                return jogador.getNome() + " recebeu SORTE: " + cartaS.getDescricao();

            case REVES:
                Carta cartaR = baralho.pegarCartaReves();
                cartaR.executarAcao(jogador, jogo, prisao);
                baralho.devolverCarta(cartaR);
                notifyObservers("cartaReves");
                return jogador.getNome() + " recebeu REVÉS: " + cartaR.getDescricao();

            case IMPOSTO:
                jogador.ajustarSaldo(-200);
                banco.creditar(200);
                notifyObservers("impostoPago");
                return jogador.getNome() + " pagou $200 de imposto.";

            case LUCRO:
                jogador.ajustarSaldo(200);
                notifyObservers("lucroRecebido");
                return jogador.getNome() + " recebeu $200 em lucros/dividendos.";

            default:
                return "Nada aconteceu.";
        }
    }

    // ------------------------------------------------------------
    // OBSERVER
    // ------------------------------------------------------------

    @Override
    public void addObserver(Observer o) { observers.add(o); }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers(String evento) {
        for (Observer o : observers) {
            o.update(this, evento);
        }
    }
}
