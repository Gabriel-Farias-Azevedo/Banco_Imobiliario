package model;

public class Carta {
    private String descricao;
    private TipoCarta tipo;
    private AcaoCarta acao;
    private int valor;
    private int parametro; // Dexei caso precise adicionar mais algum parametro depois

    public Carta(String descricao, TipoCarta tipo, AcaoCarta acao, int valor) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.acao = acao;
        this.valor = valor;
        this.parametro = 0;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public TipoCarta getTipo() {
        return tipo;
    }
    
    public AcaoCarta getAcao() {
        return acao;
    }
    
    public int getValor() {
        return valor;
    }
    
    public int getParametro() {
        return parametro;
    }

    public void executarAcao(Jogador jogador, Jogo jogo, Prisao prisao) {
        switch (acao) {
            case RECEBER_DINHEIRO:
                jogador.creditar(valor);
                break;
            case PAGAR_DINHEIRO:
                jogador.debitar(valor);
                break;
            case MOVER_PARA_POSICAO:
            	jogador.setPosicao(valor);
                break;
            case MOVER_ESPACOS:
                int novaPosicao = (jogador.getPiao().getPosicao() + valor) % 40;
                if (novaPosicao < 0) {
                    novaPosicao += 40; // Converte -2 para 38, por exemplo
                }
                jogador.setPosicao(novaPosicao);
                break;
            case IR_PARA_PRISAO:
                jogo.getPrisao().prender(jogador);
                break;
            case SAIR_LIVRE_PRISAO:
                prisao.soltarCarta(jogador);
                break;
            case IR_PARA_PARTIDA:
                jogador.setPosicao(0);
                jogador.creditar(200);
                break;
            case PAGAR_POR_PROPRIEDADE:
                // Pagar valor por cada casa/hotel que possui
                int totalCasas = 0;
                int totalHoteis = 0;
                for (Propriedade prop : jogador.getPropriedades()) {
                    totalCasas += prop.getCasas();
                    totalHoteis += prop.getNumeroHoteis();
                }
                int totalPagar = (totalCasas * valor) + (totalHoteis * parametro); // o parametro nesse caso pode ser um valor por hotel
                jogador.debitar(totalPagar);
                break;
        }
    }

}