
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

    private void inicializarTabuleiro() {
        propriedades.add(new Propriedade("Partida", 0, true));
        propriedades.add(new Propriedade("Leblon", 100));
        propriedades.add(new Propriedade("Sorte", 0, true));
        propriedades.add(new Propriedade("Av. Paulista", 200));
        propriedades.add(new Propriedade("Imposto de Renda", 200, true));
        propriedades.add(new Propriedade("Copacabana", 180));
        propriedades.add(new Propriedade("Ipanema", 220));
        propriedades.add(new Propriedade("Revés", 0, true));
        propriedades.add(new Propriedade("Flamengo", 180));
        propriedades.add(new Propriedade("Botafogo", 200));
        propriedades.add(new Propriedade("Vá para prisão", 0, true));

        for (int i = 11; i < 40; i++) {
            if (i == 17 || i == 33) {
                propriedades.add(new Propriedade("Sorte", 0, true));
            } else if (i == 22 || i == 36) {
                propriedades.add(new Propriedade("Revés", 0, true));
            } else if (i == 30) {
                propriedades.add(new Propriedade("Vá para prisão", 0, true));
            } else {
                propriedades.add(new Propriedade("Propriedade " + i, 100 + (i * 5)));
            }
        }
    }

    public int moverPiao(Jogador jogador, int passos) {
        jogador.deslocar(passos);
        return jogador.getPosicao();
    }

    public Propriedade getPropriedadeNaPosicao(int pos) {
        if (pos >= 0 && pos < propriedades.size()) {
            return propriedades.get(pos);
        }
        return null;
    }

    public boolean comprarPropriedade(Jogador jogador, Banco banco) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return false;
        if (!prop.isEspecial() && !prop.temDono() && jogador.getSaldo() >= prop.getPreco()) {
            jogador.ajustarSaldo(-prop.getPreco());
            banco.creditar(prop.getPreco());
            prop.setDono(jogador);
            jogador.getPropriedades().add(prop);
            notifyObservers("propriedadeComprada");
            return true;
        }
        return false;
    }

    public TipoCasa verificarTipoCasa(Jogador jogador) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return TipoCasa.NENHUMA;
        String nome = prop.getNome().toLowerCase();
        if (nome.contains("prisão")) return TipoCasa.PRISÃO;
        if (nome.contains("sorte")) return TipoCasa.SORTE;
        if (nome.contains("revés")) return TipoCasa.REVES;
        if (nome.contains("imposto")) return TipoCasa.IMPOSTO;
        if (!prop.isEspecial()) return TipoCasa.PROPRIEDADE;
        return TipoCasa.NENHUMA;
    }

    public enum TipoCasa {
        PARTIDA, PROPRIEDADE, SORTE, REVES, PRISÃO, IMPOSTO, NENHUMA
    }

    public String verificarEfeito(Jogador jogador, Jogo jogo) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return "Casa inválida";

        TipoCasa tipo = verificarTipoCasa(jogador);
        Banco banco = jogo.getBanco();
        Prisao prisao = jogo.getPrisao();
        CartaController baralho = jogo.getBaralhoCartas();

        switch (tipo) {
            case PROPRIEDADE:
                if (!prop.temDono()) {
                    if (jogador.getSaldo() >= prop.getPreco()) {
                        jogador.ajustarSaldo(-prop.getPreco());
                        banco.creditar(prop.getPreco());
                        prop.setDono(jogador);
                        jogador.getPropriedades().add(prop);
                        notifyObservers("propriedadeComprada");
                        return jogador.getNome() + " comprou " + prop.getNome() + " por $" + prop.getPreco();
                    } else {
                        return jogador.getNome() + " não tem saldo suficiente para comprar " + prop.getNome();
                    }
                } else if (prop.getDono() != jogador) {
                    double aluguel = prop.calcularAluguel();
                    jogador.ajustarSaldo(-aluguel);
                    prop.getDono().ajustarSaldo(aluguel);
                    notifyObservers("aluguelPago");
                    return jogador.getNome() + " pagou $" + aluguel + " de aluguel para " + prop.getDono().getNome();
                } else {
                    return jogador.getNome() + " caiu em sua própria propriedade " + prop.getNome();
                }

            case PRISÃO:
                prisao.prender(jogador);
                notifyObservers("prisao");
                return jogador.getNome() + " foi para a prisão!";

            case SORTE:
                Carta cartaSorte = baralho.pegarCartaSorte();
                cartaSorte.executarAcao(jogador, jogo, prisao);
                baralho.devolverCarta(cartaSorte);
                notifyObservers("cartaSorte");
                return jogador.getNome() + " recebeu carta de SORTE: " + cartaSorte.getDescricao();

            case REVES:
                Carta cartaReves = baralho.pegarCartaReves();
                cartaReves.executarAcao(jogador, jogo, prisao);
                baralho.devolverCarta(cartaReves);
                notifyObservers("cartaReves");
                return jogador.getNome() + " recebeu carta de REVÉS: " + cartaReves.getDescricao();

            case IMPOSTO:
                int valor = 200;
                jogador.ajustarSaldo(-valor);
                banco.creditar(valor);
                notifyObservers("impostoPago");
                return jogador.getNome() + " pagou $" + valor + " de imposto";

            case PARTIDA:
                jogador.setPosicao(0);
                jogador.ajustarSaldo(200);
                notifyObservers("passouPartida");
                return jogador.getNome() + " passou pela partida";

            default:
                return "Nada aconteceu";
        }
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
