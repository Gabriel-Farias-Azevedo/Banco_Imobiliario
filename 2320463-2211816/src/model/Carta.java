package model;

public class Carta {

    private String descricao;
    private TipoCarta tipo;
    private AcaoCarta acao;
    private int valor;
    private int parametro; // usado, por exemplo, para valores adicionais (ex: hotéis)

    public Carta(String descricao, TipoCarta tipo, AcaoCarta acao, int valor) {
        this(descricao, tipo, acao, valor, 0);
    }

    public Carta(String descricao, TipoCarta tipo, AcaoCarta acao, int valor, int parametro) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.acao = acao;
        this.valor = valor;
        this.parametro = parametro;
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
                banco.notifyObservers("cartaReceber");
                break;

            case PAGAR_DINHEIRO:
                banco.receber(jogador, valor);
                banco.notifyObservers("cartaPagar");
                break;

            case MOVER_PARA_POSICAO:
                jogador.setPosicao(valor);
                jogo.atualizarPosicaoJogador(jogador);
                banco.notifyObservers("cartaMoverPosicao");
                break;

            case MOVER_ESPACOS:
                int novaPosicao = (jogador.getPiao().getPosicao() + valor) % 40;
                if (novaPosicao < 0) {
                    novaPosicao += 40; // Ajuste para não negativar
                }
                jogador.setPosicao(novaPosicao);
                jogo.atualizarPosicaoJogador(jogador);
                banco.notifyObservers("cartaMoverEspacos");
                break;

            case IR_PARA_PRISAO:
                jogo.getPrisao().prender(jogador);
                banco.notifyObservers("cartaIrPrisao");
                break;

            case SAIR_LIVRE_PRISAO:
                prisao.soltarCarta(jogador);
                banco.notifyObservers("cartaSairPrisao");
                break;

            case IR_PARA_PARTIDA:
                jogador.setPosicao(0);
                banco.pagar(jogador, 200);
                jogo.atualizarPosicaoJogador(jogador);
                banco.notifyObservers("cartaIrInicio");
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
                banco.notifyObservers("cartaPagarPropriedades");
                break;
        }
    }
}
