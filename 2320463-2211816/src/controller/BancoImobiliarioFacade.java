package controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

import model.Jogador;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView;
import view.TabuleiroView.JogadorView;

public class BancoImobiliarioFacade {

    private final JogoController jogoController;
    private TabuleiroView tabuleiro;
    private PiaoController piaoController;
    private JogadorController jogadorController;

    public BancoImobiliarioFacade() {
        this.jogoController = JogoController.getInstancia();
    }

    public void iniciarJogo(List<String> nomes, List<String> cores) {

        // Garante estado limpo
        jogoController.reiniciar();

        // Adiciona jogadores no Model
        for (int i = 0; i < nomes.size(); i++) {
            jogoController.adicionarJogador(nomes.get(i), cores.get(i));
        }

        // Sorteio de ordem
        List<Jogador> listaJogadores = jogoController.getJogadores();
        jogadorController = new JogadorController(listaJogadores);
        jogadorController.sortearOrdemJogadores();

        // Exibir ordem sorteada
        StringBuilder ordem = new StringBuilder("Ordem dos Jogadores:\n\n");
        for (Jogador j : listaJogadores) {
            ordem.append(j.getOrdem()).append("º - ")
                 .append(j.getNome()).append(" (").append(j.getCor()).append(")\n");
        }
        JOptionPane.showMessageDialog(null, ordem.toString());

        // Reordena lista pela ordem
        List<Jogador> ordenados = listaJogadores.stream()
                .sorted((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem()))
                .collect(Collectors.toList());

        // Cria views dos jogadores
        List<JogadorView> jogadoresView = ordenados.stream()
                .map(j -> new JogadorView(j.getNome(), j.getCor()))
                .collect(Collectors.toList());

        // Cria lista de pioes
        List<Piao> pioes = ordenados.stream()
                .map(Jogador::getPiao)
                .collect(Collectors.toList());

        // Modelo do tabuleiro
        Tabuleiro tabuleiroModel = jogoController.getTabuleiro();

        // Controller de pião
        piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);

        // Criação da VIEW principal
        tabuleiro = new TabuleiroView(jogadoresView, pioes, piaoController);

        // View observa o controller de pião
        piaoController.addObserver(tabuleiro);

        // Atualiza painel inicial com o primeiro jogador
        if (!ordenados.isEmpty()) {
            tabuleiro.atualizarInfoJogador(ordenados.get(0));
        }
    }

    /**
     * Mantido apenas por compatibilidade, mas o fluxo normal agora
     * usa o botão "Rolar Dados" dentro do TabuleiroView.
     */
    public void jogarDados() {
        if (tabuleiro == null || piaoController == null) return;

        tabuleiro.abrirDadosDialog(valores -> {
            int d1 = valores[0];
            int d2 = valores[1];

            int jogadorIndex = jogoController.getJogadorAtualIndex();
            piaoController.setJogadorVez(jogadorIndex);
            piaoController.moverPiao(d1, d2);
        });
    }

    public void reiniciarJogo() {
        jogoController.reiniciar();
        if (tabuleiro != null) {
            tabuleiro.dispose();
        }
        tabuleiro = null;
        piaoController = null;
        jogadorController = null;
    }
}
