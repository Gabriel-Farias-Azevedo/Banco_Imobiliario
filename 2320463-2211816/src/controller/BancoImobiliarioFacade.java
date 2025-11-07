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
        // Singleton do JogoController
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

        // 🔹 Cria o JogadorController e sorteia a ordem
        List<Jogador> listaJogadores = jogoController.getJogadores();
        this.jogadorController = new JogadorController(listaJogadores);
        jogadorController.sortearOrdemJogadores();

        // 🔹 Exibe a ordem sorteada (opcional, mas recomendado pela 2ª Iteração)
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
        // 🔹 Cria JogadorView na nova ordem sorteada
        List<JogadorView> jogadoresView = jogadoresOrdenados.stream()
                .map(j -> new JogadorView(j.getNome(), j.getCor()))
                .collect(Collectors.toList());

        // Cria piões (1 por jogador)
        List<Piao> pioes = jogadoresOrdenados.stream()
                .map(j -> new Piao())
                .collect(Collectors.toList());

        Tabuleiro tabuleiroModel = jogoController.getTabuleiro();

        // Cria PiaoController primeiro
        this.piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);

        // Cria TabuleiroView passando o controller
        this.tabuleiro = new TabuleiroView(jogadoresView, pioes, piaoController);

        // Registra TabuleiroView como observador do PiaoController
        piaoController.addObserver(tabuleiro);

        // Inicializa TabuleiroView com os jogadores
        tabuleiro.setJogadores(jogadoresView);

        if (!jogadoresOrdenados.isEmpty()) {
            tabuleiro.atualizarInfoJogador(jogadoresOrdenados.get(0));
        }
    }

    /**
     * Lança os dados do jogador atual e movimenta o pião.
     */
    public void jogarDados() {
        if (tabuleiro == null || piaoController == null) return;

        tabuleiro.abrirDadosDialog(valores -> {
            int dado1 = valores[0];
            int dado2 = valores[1];

            // Define jogador da vez no controller de piões
            int jogadorAtual = jogoController.getJogadorAtualIndex();
            piaoController.setJogadorVez(jogadorAtual);

            // Move pião
            piaoController.moverPiao(dado1, dado2);

            // Passa a vez no JogoController
            jogoController.proximoJogador();
        });
    }

    /**
     * Reinicia o jogo: limpa estado e descarta View/Controllers.
     */
    public void reiniciarJogo() {
        jogoController.reiniciar();
        if (tabuleiro != null) {
            tabuleiro.dispose();
            tabuleiro = null;
        }
        piaoController = null;
        jogadorController = null;
    }

    // ===================================================
    // Getters para acessar View e Controllers
    // ===================================================

    public TabuleiroView getTabuleiroView() {
        return tabuleiro;
    }

    public PiaoController getPiaoController() {
        return piaoController;
    }

    public JogoController getJogoController() {
        return jogoController;
    }

    public JogadorController getJogadorController() {
        return jogadorController;
    }
}
