package model;

import java.util.ArrayList;
import java.util.List;

public class Carta implements Observable {

    private String descricao;
    private TipoCarta tipo;
    private AcaoCarta acao;
    private int valor;
    private int parametro; // usado, por exemplo, para valores adicionais (ex: hotéis)
    private final List<Observer> observers;

    public Carta(String descricao, TipoCarta tipo, AcaoCarta acao, int valor) {
        this(descricao, tipo, acao, valor, 0);
    }

    public Carta(String descricao, TipoCarta tipo, AcaoCarta acao, int valor, int parametro) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.acao = acao;
        this.valor = valor;
        this.parametro = parametro;
        this.observers = new ArrayList<>();
    }

    public String getDescricao() {return descricao;}

    public TipoCarta getTipo() {return tipo;}

    public AcaoCarta getAcao() {return acao;}

    public int getValor() {return valor;}

    public int getParametro() {return parametro;}

    public void executarAcao(Jogador jogador, Jogo jogo, Prisao prisao) {
        Banco banco = Banco.getInstance();

        switch (acao) {
            case RECEBER_DINHEIRO:
                banco.pagar(jogador, valor);
                notifyObservers("cartaReceber");
                break;

            case PAGAR_DINHEIRO:
                banco.receber(jogador, valor);
                notifyObservers("cartaPagar");
                break;

            case MOVER_PARA_POSICAO:
                jogador.setPosicao(valor);
                jogo.atualizarPosicaoJogador(jogador);
                notifyObservers("cartaMoverPosicao");
                break;

            case MOVER_ESPACOS:
                int novaPosicao = (jogador.getPiao().getPosicao() + valor) % 40;
                if (novaPosicao < 0) {
                    novaPosicao += 40; // Ajuste para não negativar
                }
                jogador.setPosicao(novaPosicao);
                jogo.atualizarPosicaoJogador(jogador);
                notifyObservers("cartaMoverEspacos");
                break;

            case IR_PARA_PRISAO:
                // 1. Move para a casa da prisão (posição 10)
                jogador.setPosicao(10);
                jogo.atualizarPosicaoJogador(jogador);

                // 2. Prende o jogador
                jogo.getPrisao().prender(jogador);

                notifyObservers("cartaIrPrisao");
                break;


            case SAIR_LIVRE_PRISAO:
                jogador.receberCartaSairPrisao();
                notifyObservers("cartaRecebeuSaidaPrisao");
                break;


            case IR_PARA_PARTIDA:
                jogador.setPosicao(0);
                banco.pagar(jogador, 200);
                jogo.atualizarPosicaoJogador(jogador);
                notifyObservers("cartaIrInicio");
                break;

            case PAGAR_POR_PROPRIEDADE:
                int totalCasas = 0;
                int totalHoteis = 0;
                for (Propriedade prop : jogador.getPropriedades()) {
                    totalCasas += prop.getCasas();
                    totalHoteis += prop.getHoteis();
                }
                int totalPagar = (totalCasas * valor) + (totalHoteis * parametro);
                banco.receber(jogador, totalPagar);
                notifyObservers("cartaPagarPropriedades");
                break;
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
