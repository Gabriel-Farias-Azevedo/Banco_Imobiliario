package controller;

import java.util.*;
import model.Jogador;
import model.Jogo;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView;
import view.TabuleiroView.JogadorView;

/**
 * Controller central do jogo (Singleton)
 */
public class JogoController {

    private static JogoController instancia; // Singleton
    private final List<Jogador> jogadores;
    private int jogadorAtual = 0;
    private TabuleiroView tabuleiro;
    private PiaoController piaoController;
    private Jogo jogo;
    private Tabuleiro tabuleiroModel;
    private final Random random = new Random();
    private List<Piao> pioes; // lista de piões dos jogadores

    private JogoController() {
        jogadores = new ArrayList<>();
        pioes = new ArrayList<>();
        jogo = new Jogo(4); /* Passei fixo, mas da pra passar dinamicamente */
        tabuleiroModel = jogo.getTabuleiro();
    }

    public static JogoController getInstancia() {
        if (instancia == null)
            instancia = new JogoController();
        return instancia;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiroModel;
    }

    // === MÉTODOS DO MODEL ===
    public void adicionarJogador(String nome, String cor) {
        Jogador j = new Jogador(nome, cor);
        jogadores.add(j);
        jogo.getJogadores().add(j);
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Jogador getJogadorAtual() {
        return jogadores.get(jogadorAtual);
    }

    public int getJogadorAtualIndex() {
        return jogadorAtual;
    }

    public void proximoJogador() {
        jogadorAtual = (jogadorAtual + 1) % jogadores.size();
    }

    public void reiniciar() {
        jogadores.clear();
        pioes.clear();
        jogadorAtual = 0;
        if (tabuleiro != null) {
            tabuleiro.dispose();
            tabuleiro = null;
        }
        piaoController = null;
        jogo = new Jogo(4);
        tabuleiroModel = jogo.getTabuleiro();
    }

    // === INTEGRAÇÃO COM A VIEW ===
    public void iniciarTabuleiro() {
        List<JogadorView> jogadoresView = new ArrayList<>();
        pioes = new ArrayList<>();

        for (Jogador j : jogadores) {
            TabuleiroView.JogadorView jv = new TabuleiroView.JogadorView(j.getNome(), j.getCor());
            jv.pistaIndex = 0;
            jogadoresView.add(jv);
            pioes.add(new Piao());
        }

        // Cria PiaoController primeiro
        piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);

        // Cria TabuleiroView passando o controller
        if (tabuleiro == null) {
            tabuleiro = new TabuleiroView(jogadoresView, pioes, piaoController);
        } else {
            tabuleiro.setJogadores(jogadoresView);
        }

        // Registra TabuleiroView como observador do PiaoController
        piaoController.addObserver(tabuleiro);
    }

    public void jogarDadosViaView() {
        if (tabuleiro == null || piaoController == null) return;

        tabuleiro.abrirDadosDialog(valores -> {
            int dado1 = valores[0];
            int dado2 = valores[1];

            // Move o pião usando PiaoController
            piaoController.setJogadorVez(jogadorAtual);
            piaoController.moverPiao(dado1, dado2);

            // Passa a vez
            proximoJogador();
        });
    }

    public void jogarDadosRandom() {
        if (piaoController == null) return;

        int d1 = random.nextInt(6) + 1;
        int d2 = random.nextInt(6) + 1;

        piaoController.setJogadorVez(jogadorAtual);
        piaoController.moverPiao(d1, d2);

        Jogador atual = getJogadorAtual();
        System.out.println(atual.getNome() + " tirou " + d1 + " e " + d2 + " → anda " + (d1 + d2) + " casas.");

        proximoJogador();
    }

    // ===================================================
    // Getters para View e Controller
    // ===================================================
    public TabuleiroView getTabuleiroView() {
        return tabuleiro;
    }

    public PiaoController getPiaoController() {
        return piaoController;
    }
}
