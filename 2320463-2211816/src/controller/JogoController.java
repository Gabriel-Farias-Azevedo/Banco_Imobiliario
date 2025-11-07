package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Jogador;
import model.Jogo;
import model.Piao;
import model.Tabuleiro;
import view.TabuleiroView;
import view.TabuleiroView.JogadorView;

public class JogoController {

    private static JogoController instancia;

    private final List<Jogador> jogadores;
    private int jogadorAtualIndex = 0;

    private TabuleiroView tabuleiroView;
    private PiaoController piaoController;
    private DadosController dadosController;
    private JogadorController jogadorController;

    private Jogo jogo;
    private Tabuleiro tabuleiroModel;
    private List<Piao> pioes;
    private final Random random = new Random();

    private JogoController() {
        jogadores = new ArrayList<>();
        pioes = new ArrayList<>();
        jogo = new Jogo(4); // quantidade padrão
        tabuleiroModel = jogo.getTabuleiro();
    }

    public static JogoController getInstancia() {
        if (instancia == null) instancia = new JogoController();
        return instancia;
    }

    // === MODEL ===
    public void adicionarJogador(String nome, String cor) {
        Jogador j = new Jogador(nome, cor);
        jogadores.add(j);
        jogo.getJogadores().add(j);
    }

    public List<Jogador> getJogadores() { return jogadores; }
    public Jogador getJogadorAtual() { return jogadores.get(jogadorAtualIndex); }
    public int getJogadorAtualIndex() { return jogadorAtualIndex; }

    public void proximoJogador() {
        jogadorAtualIndex = (jogadorAtualIndex + 1) % jogadores.size();
    }

    public void reiniciar() {
        jogadores.clear();
        pioes.clear();
        jogadorAtualIndex = 0;
        if (tabuleiroView != null) {
            tabuleiroView.dispose();
            tabuleiroView = null;
        }
        piaoController = null;
        dadosController = null;
        jogadorController = null;
        jogo = new Jogo(4);
        tabuleiroModel = jogo.getTabuleiro();
    }

    // INTEGRAÇÃO COM VIEW
    public void iniciarTabuleiro() {
    // Cria o Jogo com base no número de jogadores adicionados
    jogo = new Jogo(jogadores.size());
    tabuleiroModel = jogo.getTabuleiro();

    List<JogadorView> jogadoresView = new ArrayList<>();
    pioes = new ArrayList<>();

    for (Jogador j : jogadores) {
        JogadorView jv = new JogadorView(j.getNome(), j.getCor());
        jv.pistaIndex = 0;
        jogadoresView.add(jv);

        // Usa o Piao do próprio jogador
        Piao p = j.getPiao();
        pioes.add(p);
    }

    // Cria controllers
    jogadorController = new JogadorController(jogadores);
    piaoController = new PiaoController(pioes, jogadoresView, tabuleiroModel);

    // Cria TabuleiroView
    tabuleiroView = new TabuleiroView(jogadoresView, pioes, piaoController);

    // Registra view como observador de Piao e Jogador
    for (Piao p : pioes) p.addObserver(tabuleiroView);
    for (Jogador j : jogadores) j.addObserver(tabuleiroView);

    // Cria DadosController ligado ao Dado do jogo
    dadosController = new DadosController(tabuleiroView.getDadosView(), jogo.getDado());
}

    // === FLUXO DE JOGO ===
    public void jogarDados(int d1, int d2) {
        Piao piao = pioes.get(jogadorAtualIndex);
        piaoController.setJogadorVez(jogadorAtualIndex);
        piaoController.moverPiao(d1, d2);

        // Verifica falência
        Jogador atual = getJogadorAtual();
        jogo.verificarFalencia(atual);
        if (atual.isFalido()) {
            jogadorController.removerJogador(atual);
            if (jogadores.size() <= 1) {
                System.out.println("Jogo encerrado!");
                return;
            }
        }

        // Passa a vez
        proximoJogador();
    }

    public void jogarDadosRandom() {
        int d1 = random.nextInt(6) + 1;
        int d2 = random.nextInt(6) + 1;
        jogarDados(d1, d2);
        System.out.println(getJogadorAtual().getNome() + " tirou " + d1 + " e " + d2);
    }

    public void jogarDadosViaView() {
        if (tabuleiroView == null || dadosController == null) return;

        tabuleiroView.abrirDadosDialog(valores -> {
            jogarDados(valores[0], valores[1]);
        });
    }

    // === GETTERS ===
    public TabuleiroView getTabuleiroView() { return tabuleiroView; }
    public PiaoController getPiaoController() { return piaoController; }
    public DadosController getDadosController() { return dadosController; }
    public JogadorController getJogadorController() { return jogadorController; }
    public Jogo getJogo() { return jogo; }
    public Tabuleiro getTabuleiro() {return tabuleiroModel;}

}
