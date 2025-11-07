package controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

import model.Jogador;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView;
import view.TabuleiroView.JogadorView;

/**
 * Facade do Banco Imobiliário.
 * Centraliza a interação entre Model, Controllers e View.
 */
public class BancoImobiliarioFacade {

    private final JogoController jogoController;
    private TabuleiroView tabuleiro;
    private PiaoController piaoController;
    private JogadorController jogadorController;

    public BancoImobiliarioFacade() {
        this.jogoController = JogoController.getInstancia();
    }

    /**
     * Inicializa o jogo: cria jogadores, sorteia a ordem, cria piões e a view.
     */
    public void iniciarJogo(List<String> nomes, List<String> cores) {
        // Adiciona jogadores no Model
        for (int i = 0; i < nomes.size(); i++) {
            jogoController.adicionarJogador(nomes.get(i), cores.get(i));
        }

        // Cria JogadorController e sorteia a ordem
        List<Jogador> listaJogadores = jogoController.getJogadores();
        jogadorController = new JogadorController(listaJogadores);
        jogadorController.sortearOrdemJogadores();

        // Exibe a ordem sorteada
        StringBuilder ordemTexto = new StringBuilder("Ordem dos Jogadores:\n\n");
        listaJogadores.forEach(j -> 
            ordemTexto.append(j.getOrdem()).append("º - ")
                      .append(j.getNome()).append(" (").append(j.getCor()).append(")\n")
        );
        JOptionPane.showMessageDialog(null, ordemTexto.toString(),
                "Sorteio da Ordem de Jogada", JOptionPane.INFORMATION_MESSAGE);

        List<Jogador> jogadoresOrdenados = listaJogadores.stream()
                .sorted((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem()))
                .collect(Collectors.toList());

        List<JogadorView> jogadoresView = jogadoresOrdenados.stream()
                .map(j -> new JogadorView(j.getNome(), j.getCor()))
                .collect(Collectors.toList());

        List<Piao> pioes = jogadoresOrdenados.stream()
                .map(Jogador::getPiao)
                .collect(Collectors.toList());

        Tabuleiro tabuleiroModel = jogoController.getTabuleiro();

        piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);
        tabuleiro = new TabuleiroView(jogadoresView, pioes, piaoController);
        piaoController.addObserver(tabuleiro);
        tabuleiro.setJogadores(jogadoresView);

        if (!jogadoresOrdenados.isEmpty()) {
            tabuleiro.atualizarInfoJogador(jogadoresOrdenados.get(0));
        }
    }

    public void jogarDados() {
        if (tabuleiro == null || piaoController == null) return;

        tabuleiro.abrirDadosDialog(valores -> {
            int dado1 = valores[0];
            int dado2 = valores[1];

            int jogadorAtual = jogoController.getJogadorAtualIndex();
            piaoController.setJogadorVez(jogadorAtual);
            piaoController.moverPiao(dado1, dado2);
            jogoController.proximoJogador();
        });
    }

    public void reiniciarJogo() {
        jogoController.reiniciar();
        if (tabuleiro != null) {
            tabuleiro.dispose();
            tabuleiro = null;
        }
        piaoController = null;
        jogadorController = null;
    }

    public TabuleiroView getTabuleiroView() { return tabuleiro; }
    public PiaoController getPiaoController() { return piaoController; }
    public JogoController getJogoController() { return jogoController; }
    public JogadorController getJogadorController() { return jogadorController; }
}
