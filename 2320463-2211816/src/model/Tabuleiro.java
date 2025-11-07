package model;

import java.util.*;

import javax.swing.SwingUtilities;

import view.CartaView;

/**
 * Representa o modelo lógico do tabuleiro do jogo.
 * Responsável apenas por armazenar e descrever as casas (propriedades e especiais),
 * sem regras de controle (essas ficam no Controller).
 */
public class Tabuleiro {

    private final List<Propriedade> propriedades;

    public Tabuleiro() {
        propriedades = new ArrayList<>();
        inicializarTabuleiro();
    }

    /**
     * Cria as 40 casas do tabuleiro (propriedades e especiais).
     */
    private void inicializarTabuleiro() {
        propriedades.add(new Propriedade("Partida", 0, true));             // 0
        propriedades.add(new Propriedade("Leblon", 100));                  // 1
        propriedades.add(new Propriedade("Sorte", 0, true));               // 2
        propriedades.add(new Propriedade("Av. Paulista", 200));            // 3
        propriedades.add(new Propriedade("Imposto de Renda", 200, true));  // 4
        propriedades.add(new Propriedade("Copacabana", 180));              // 5
        propriedades.add(new Propriedade("Ipanema", 220));                 // 6
        propriedades.add(new Propriedade("Revés", 0, true));               // 7
        propriedades.add(new Propriedade("Flamengo", 180));                // 8
        propriedades.add(new Propriedade("Botafogo", 200));                // 9
        propriedades.add(new Propriedade("Vá para prisão", 0, true));      // 10

        // Cria as demais até 39
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

    /**
     * Move o peão do jogador adiante um número de casas.
     * Retorna a nova posição.
     */
    public int moverPiao(Jogador jogador, int passos) {
        jogador.deslocar(passos);
        return jogador.getPosicao();
    }

    /**
     * Retorna a propriedade ou casa na posição indicada.
     */
    public Propriedade getPropriedadeNaPosicao(int pos) {
        if (pos >= 0 && pos < propriedades.size()) {
            return propriedades.get(pos);
        }
        return null;
    }

    /**
     * Tenta comprar a propriedade onde o jogador está.
     * Retorna true se a compra foi realizada.
     */
    public boolean comprarPropriedade(Jogador jogador, Banco banco) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return false;

        if (!prop.isEspecial() && !prop.temDono() && jogador.getSaldo() >= prop.getPreco()) {
            jogador.debitar(prop.getPreco());
            banco.creditar(prop.getPreco());
            prop.setDono(jogador);
            jogador.getPropriedades().add(prop);
            return true;
        }
        return false;
    }

    /**
     * Retorna o tipo da casa em que o jogador parou.
     * O Controller usa isso para decidir o que fazer.
     */
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

    /**
     * Enum para simplificar a interpretação das casas.
     */
    public enum TipoCasa {
        PARTIDA, PROPRIEDADE, SORTE, REVES, PRISÃO, IMPOSTO, NENHUMA
    }
    
    /**
     * Aplica o efeito da casa onde o jogador parou.
     * Usa cartas, impostos, prisão e movimentação.
     * Retorna uma descrição do que aconteceu.
     */


    public String verificarEfeito(Jogador jogador, Jogo jogo) {
        Propriedade prop = getPropriedadeNaPosicao(jogador.getPosicao());
        if (prop == null) return "Casa inválida";

        TipoCasa tipo = verificarTipoCasa(jogador);
        Banco banco = jogo.getBanco();
        Prisao prisao = jogo.getPrisao();
        List<Carta> deckSorte = jogo.getDeckSorte();
        List<Carta> deckReves = jogo.getDeckReves();

        switch (tipo) {
            case PROPRIEDADE:
                if (!prop.temDono()) {
                    // jogador pode comprar
                    if (jogador.getSaldo() >= prop.getPreco()) {
                        jogador.debitar(prop.getPreco());
                        banco.creditar(prop.getPreco());
                        prop.setDono(jogador);
                        jogador.getPropriedades().add(prop);
                        return jogador.getNome() + " comprou " + prop.getNome() + " por $" + prop.getPreco();
                    } else {
                        return jogador.getNome() + " não tem saldo suficiente para comprar " + prop.getNome();
                    }
                } else if (prop.getDono() != jogador) {
                    // paga aluguel
                    int aluguel = prop.calcularAluguel();
                    jogador.debitar(aluguel);
                    prop.getDono().creditar(aluguel);
                    return jogador.getNome() + " pagou $" + aluguel + " de aluguel para " + prop.getDono().getNome();
                } else {
                    return jogador.getNome() + " caiu em sua própria propriedade " + prop.getNome();
                }

            case PRISÃO:
                prisao.prender(jogador);
                return jogador.getNome() + " foi para a prisão!";

            case SORTE:
                if (!deckSorte.isEmpty()) {
                    Carta carta = deckSorte.remove(0);
                    carta.executarAcao(jogador, jogo, prisao);
                    deckSorte.add(carta);

                    SwingUtilities.invokeLater(() -> {
                        new CartaView(null, carta.getDescricao(), "SORTE").setVisible(true);
                    });
                    return jogador.getNome() + " recebeu carta de SORTE: " + carta.getDescricao();
                }
                return jogador.getNome() + " caiu em SORTE, mas o deck está vazio.";

            case REVES:
                if (!deckReves.isEmpty()) {
                    Carta carta = deckReves.remove(0);
                    carta.executarAcao(jogador, jogo, prisao);
                    deckReves.add(carta);
                    return jogador.getNome() + " recebeu carta de REVÉS: " + carta.getDescricao();
                }
                return jogador.getNome() + " caiu em REVÉS, mas o deck está vazio.";

            case IMPOSTO:
                int valor = 200; // ou outro valor da regra
                jogador.debitar(valor);
                banco.creditar(valor);
                return jogador.getNome() + " pagou $" + valor + " de imposto";

            case PARTIDA:
                jogador.setPosicao(0);
                jogador.creditar(200); // bônus por passar pela partida
                return jogador.getNome() + " passou pela partida";

            default:
                return "Nada aconteceu";
        }
    }


}
