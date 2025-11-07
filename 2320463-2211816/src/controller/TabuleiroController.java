package controller;

import model.Piao;
import model.Jogador;
import model.Tabuleiro;
import view.TabuleiroView;
import view.TabuleiroView.JogadorView;

import java.util.List;
import javax.swing.Timer;

public class TabuleiroController {

    private final TabuleiroView view;
    private final Tabuleiro tabuleiroModel;
    private final List<Piao> pioes;
    private final List<JogadorView> jogadoresView;
    private final PiaoController piaoController;
    private final List<Jogador> jogadores;
    private int jogadorVez = 0;

    public TabuleiroController(TabuleiroView view, Tabuleiro tabuleiro, List<Jogador> jogadores, List<Piao> pioes, List<JogadorView> jogadoresView) {
        this.view = view;
        this.tabuleiroModel = tabuleiro;
        this.jogadores = jogadores;
        this.pioes = pioes;
        this.jogadoresView = jogadoresView;

        // Inicializa PiaoController
        this.piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);

        // Registra view como observadora
        this.piaoController.addObserver(view);

        // Inicializa a View com os jogadores
        view.setJogadores(jogadoresView);

        // Configura listener do botão "Rolar Dados"
        configurarListenerBotaoDados();
    }

    private void configurarListenerBotaoDados() {
        // Remove listeners antigos
        for (var al : view.getBtnRolarDados().getActionListeners()) {
            view.getBtnRolarDados().removeActionListener(al);
        }

        view.getBtnRolarDados().addActionListener(e -> {
            if (jogadorVez < 0 || jogadorVez >= pioes.size()) return;
            if (view.turnoEmAndamento) return;

            view.turnoEmAndamento = true;

            view.abrirDadosDialog(valores -> {
                int dado1 = valores[0];
                int dado2 = valores[1];

                // Move pião via PiaoController
                piaoController.setJogadorVez(jogadorVez);
                piaoController.moverPiao(dado1, dado2);

                // Atualiza posição e executa efeitos da casa
                Jogador jogadorAtual = jogadores.get(jogadorVez);
                String mensagem = tabuleiroModel.verificarEfeito(jogadorAtual, null); // Jogo pode ser passado se necessário
                System.out.println(mensagem);

                // Mantém dados na tela por 3 segundos
                new Timer(3000, ev -> {
                    view.diceA = 0;
                    view.diceB = 0;
                    view.repaint();

                    // Passa a vez
                    jogadorVez = (jogadorVez + 1) % pioes.size();
                    view.setJogadorVez(jogadorVez);
                    view.turnoEmAndamento = false;
                }) {{
                    setRepeats(false);
                    start();
                }};
            });
        });
    }

    public void setJogadorVez(int idx) {
        if (idx >= 0 && idx < pioes.size()) {
            jogadorVez = idx;
            view.setJogadorVez(jogadorVez);
        }
    }

    public int getJogadorVez() {
        return jogadorVez;
    }

    public PiaoController getPiaoController() {
        return piaoController;
    }
}
