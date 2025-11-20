package view;

import controller.CartaController;
import controller.DadosController;
import controller.JogoController;
import controller.PiaoController;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.*;

public class TabuleiroView extends JFrame implements model.Observer {

    private final List<JogadorView> jogadores = new ArrayList<>();
    private final List<Piao> pioes;
    private final PiaoController piaoController;

    private BoardPanel canvas;
    private JButton btnRolarDados;
    private int jogadorAtualIndex = 0;
    public boolean turnoEmAndamento = false;
    public int diceA = 0;
    public int diceB = 0;

    private final Map<String, Integer> corParaPin = new HashMap<>();
    private final Point[][] offsetsPorCasa = new Point[40][6];

    private JPanel painelInfoJogador;
    private JLabel lblNomeJogador, lblSaldoJogador;
    private JComboBox<String> comboPropriedades;
    private JTextArea areaDetalhesPropriedade;

    private BufferedImage imgTabuleiro;
    private final BufferedImage[] imgDados = new BufferedImage[7];
    private final Map<Integer, BufferedImage> imgPioes = new HashMap<>();

    private DadosView dadosView;

    // Botões do painel lateral
    private JButton btnConstruir;
    private JButton btnVender;
    private JButton btnEncerrar;

    // 🔹 Flag para impedir mais de uma construção por turno
    private boolean construcaoRealizadaNoTurno = false;

    // ----------------------------------------------------
    // CONSTRUTOR
    // ----------------------------------------------------
    public TabuleiroView(List<JogadorView> listaJogadores, List<Piao> pioes, PiaoController controller) {
        super("Banco Imobiliário - Tabuleiro");

        this.pioes = pioes;
        this.piaoController = controller;
        this.jogadores.addAll(listaJogadores);

        // Registra PiaoController no JogoController
        JogoController.getInstancia().setPiaoController(controller);

        // Observando PiaoController
        this.piaoController.addObserver(this);

        // Observando Jogadores
        for (Jogador j : JogoController.getInstancia().getJogadores()) {
            j.addObserver(this);
        }

        // Observando JogoController
        JogoController.getInstancia().addObserver(this);

        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        inicializarCorParaPin();
        inicializarOffsets();

        canvas = new BoardPanel();
        canvas.setBounds(0, 0, 1000, 800);
        add(canvas);

        criarPainelInfoJogador();
        painelInfoJogador.setBounds(1000, 0, 280, 500);
        add(painelInfoJogador);

        btnRolarDados = new JButton("Rolar Dados");
        btnRolarDados.setBounds(1050, 550, 200, 40);
        add(btnRolarDados);

        carregarImagens();
        configurarListenerBotaoDados();

        if (!jogadores.isEmpty()) {
            atualizarInfoJogador(JogoController.getInstancia().getJogadorAtual());
        }

        atualizarEstadoBotoesPrisao();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ----------------------------------------------------
    // LISTENER DE DADOS
    // ----------------------------------------------------
    private void configurarListenerBotaoDados() {
        for (var l : btnRolarDados.getActionListeners()) {
            btnRolarDados.removeActionListener(l);
        }

        btnRolarDados.addActionListener(e -> {

            if (turnoEmAndamento) return;

            turnoEmAndamento = true;
            btnRolarDados.setEnabled(false);

            abrirDadosDialog(valores -> {

                if (!turnoEmAndamento) return;

                diceA = valores[0];
                diceB = valores[1];
                setDiceValues(diceA, diceB);

                JogoController.getInstancia().jogarDados(diceA, diceB);

                atualizarInfoJogador(JogoController.getInstancia().getJogadorAtual());
                atualizarEstadoBotoesPrisao();
            });
        });
    }

    public DadosView getDadosView() {
        if (dadosView == null) dadosView = new DadosView();
        return dadosView;
    }

    public void setJogadores(List<JogadorView> lista) {
        jogadores.clear();
        jogadores.addAll(lista);
        canvas.repaint();
    }

    public void setJogadorVez(int idx) {
        jogadorAtualIndex = idx;
        atualizarInfoJogador(JogoController.getInstancia().getJogadores().get(idx));
        atualizarEstadoBotoesPrisao();
        repaint();
    }

    public void abrirDadosDialog(Consumer<int[]> callback) {
        JDialog dialog = new JDialog(this, "Lançamento de Dados", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        DadosView dv = getDadosView();
        dialog.add(dv, BorderLayout.CENTER);

        DadosController dc = new DadosController(dv, new Dado());
        dc.setCallback(valores -> {
            dv.atualizarView(valores[0], valores[1]);

            new javax.swing.Timer(3000, ev -> {
                callback.accept(valores);
                dialog.dispose();
            }) {{
                setRepeats(false);
                start();
            }};
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public JButton getBtnRolarDados() {
        return btnRolarDados;
    }

    // ----------------------------------------------------
    // OBSERVER
    // ----------------------------------------------------
    @Override
    public void update(Observable observado, String evento) {

        if (observado instanceof JogoController) {
            if ("VezMudou".equals(evento)) {

                jogadorAtualIndex = JogoController.getInstancia().getJogadorAtualIndex();
                Jogador novo = JogoController.getInstancia().getJogadorAtual();

                atualizarInfoJogador(novo);
                atualizarEstadoBotoesPrisao();
                repaint();
                return;
            }
        }

        if (observado instanceof PiaoController) {

            PiaoController pc = (PiaoController) observado;
            int idxJogador = pc.getJogadorVez();
            Jogador jogadorAtual = JogoController.getInstancia().getJogadores().get(idxJogador);

            if ("PiaoMovido".equals(evento)) {

                // Atualiza a casa do pino do jogador correspondente
                if (idxJogador >= 0 && idxJogador < jogadores.size()) {
                    Piao piao = pioes.get(idxJogador);
                    JogadorView jv = jogadores.get(idxJogador);
                    jv.pistaIndex = piao.getPosicao();
                }

                repaint();

                Piao piao = pioes.get(idxJogador);
                int posicao = piao.getPosicao();

                Propriedade prop = JogoController.getInstancia()
                        .getJogo().getTabuleiro()
                        .getPropriedadeNaPosicao(posicao);

                if (prop != null) {

                    if (!prop.isEspecial()) {

                        // Se for companhia, chama a carta específica
                        if (prop.getNome().toLowerCase().contains("companhia")) {
                            exibirCartaCompanhia(prop);
                        } else {
                            exibirCartaPropriedade(prop);
                        }

                    } else {
                        // SORTE / REVÉS etc.
                        Tabuleiro.TipoCasa tipo =
                                JogoController.getInstancia().getJogo().getTabuleiro()
                                        .verificarTipoCasa(jogadorAtual);

                        if (tipo == Tabuleiro.TipoCasa.SORTE ||
                                tipo == Tabuleiro.TipoCasa.REVES) {

                            CartaController baralho = JogoController.getInstancia()
                                    .getJogo().getBaralhoCartas();

                            Carta carta = (tipo == Tabuleiro.TipoCasa.SORTE)
                                    ? baralho.pegarCartaSorte()
                                    : baralho.pegarCartaReves();

                            new CartaView(this,
                                    carta.getDescricao(),
                                    tipo == Tabuleiro.TipoCasa.SORTE ? "SORTE" : "REVÉS",
                                    baralho.getCaminhoImagem(carta)
                            ).setVisible(true);

                            baralho.devolverCarta(carta);
                        }
                    }
                }

                atualizarEstadoBotoesPrisao();
                return;
            }
        }

        if (observado instanceof Jogador) {

            Jogador jogador = (Jogador) observado;

            // Se a posição do jogador mudou (por exemplo, carta de prisão),
            // atualizamos o pino visualmente
            if ("posicaoAlterada".equals(evento)) {
                atualizarPosicaoPinoDoJogador(jogador);
            }

            Jogador jogadorDaVez =
                    JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);

            if (jogador == jogadorDaVez) {
                atualizarInfoJogador(jogador);
                atualizarEstadoBotoesPrisao();
                repaint();
            }
        }
    }

    // ----------------------------------------------------
    // PAINEL LATERAL
    // ----------------------------------------------------
    public void atualizarInfoJogador(Jogador jogador) {

        lblNomeJogador.setText("Nome: " + jogador.getNome());
        lblSaldoJogador.setText("Saldo: $" + jogador.getSaldo());

        comboPropriedades.removeAllItems();
        for (Propriedade p : jogador.getPropriedades())
            comboPropriedades.addItem(p.getNome());

        if (!jogador.getPropriedades().isEmpty())
            exibirDetalhesPropriedade();
        else
            areaDetalhesPropriedade.setText("Sem propriedades.");

        atualizarEstadoBotoesPrisao();
    }

    public void setDiceValues(int a, int b) {
        diceA = Math.max(1, Math.min(6, a));
        diceB = Math.max(1, Math.min(6, b));
        canvas.repaint();
    }

    private void criarPainelInfoJogador() {

        painelInfoJogador = new JPanel();
        painelInfoJogador.setLayout(new BoxLayout(painelInfoJogador, BoxLayout.Y_AXIS));
        painelInfoJogador.setBorder(BorderFactory.createTitledBorder("Jogador da Vez"));
        painelInfoJogador.setBackground(Color.WHITE);

        lblNomeJogador = new JLabel("Nome: ");
        lblSaldoJogador = new JLabel("Saldo: $0");

        btnConstruir = new JButton("Construir");
        btnConstruir.addActionListener(e -> construirImovel());

        btnVender = new JButton("Vender Propriedade");
        btnVender.addActionListener(e -> venderPropriedade());

        comboPropriedades = new JComboBox<>();
        comboPropriedades.addActionListener(e -> exibirDetalhesPropriedade());

        areaDetalhesPropriedade = new JTextArea(10, 20);
        areaDetalhesPropriedade.setEditable(false);
        areaDetalhesPropriedade.setLineWrap(true);
        areaDetalhesPropriedade.setWrapStyleWord(true);

        painelInfoJogador.add(Box.createVerticalStrut(10));
        painelInfoJogador.add(lblNomeJogador);
        painelInfoJogador.add(lblSaldoJogador);

        painelInfoJogador.add(new JLabel("Propriedades:"));
        painelInfoJogador.add(comboPropriedades);
        painelInfoJogador.add(new JScrollPane(areaDetalhesPropriedade));

        painelInfoJogador.add(btnConstruir);
        painelInfoJogador.add(btnVender);

        btnEncerrar = new JButton("Encerrar Turno");
        btnEncerrar.addActionListener(e -> encerrarTurno());
        painelInfoJogador.add(btnEncerrar);
    }

    // 🔧 Agora só permite 1 construção por turno
    // e escolhe entre construir casa ou hotel
    private void construirImovel() {

        if (construcaoRealizadaNoTurno) {
            JOptionPane.showMessageDialog(this,
                    "Você já realizou uma construção neste turno.",
                    "Construção não permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeProp = (String) comboPropriedades.getSelectedItem();
        if (nomeProp == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma propriedade!");
            return;
        }

        Jogador atual = JogoController.getInstancia().getJogadorAtual();

        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {

                // Impede construção em propriedades especiais ou companhias
                String nomeLower = p.getNome().toLowerCase();
                if (p.isEspecial() || nomeLower.contains("companhia")) {
                    JOptionPane.showMessageDialog(this,
                            "Não é possível construir nesta propriedade (especial ou companhia).");
                    return;
                }

                Object[] opcoes = {"Construir Casa", "Construir Hotel", "Cancelar"};
                int escolha = JOptionPane.showOptionDialog(
                        this,
                        "O que você deseja construir em " + p.getNome() + "?",
                        "Construir",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                );

                boolean construiu = false;

                if (escolha == 0) { // Casa
                    construiu = p.construirCasa();
                    if (construiu) {
                        JOptionPane.showMessageDialog(this, "Casa construída com sucesso!");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Não foi possível construir casa.\n" +
                                "Verifique se já há 4 casas ou se a propriedade permite construção.");
                    }
                } else if (escolha == 1) { // Hotel
                    construiu = p.construirHotel();
                    if (construiu) {
                        JOptionPane.showMessageDialog(this, "Hotel construído com sucesso!");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Não foi possível construir hotel.\n" +
                                "É necessário já ter casas suficientes e não possuir hotel.");
                    }
                } else {
                    // Cancelou
                    return;
                }

                if (construiu) {
                    construcaoRealizadaNoTurno = true;
                    atualizarInfoJogador(atual);
                    atualizarEstadoBotoesPrisao();
                }

                return;
            }
        }
    }

    private void venderPropriedade() {
        String nomeProp = (String) comboPropriedades.getSelectedItem();
        if (nomeProp == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma propriedade!");
            return;
        }

        Jogador atual = JogoController.getInstancia().getJogadorAtual();

        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {

                double valorTerreno = p.getPreco();
                double valorCasas = p.getCasas() * (p.getPreco() * 0.5);  // exemplo
                double valorHotel = p.getHoteis() * (p.getPreco() * 0.8); // exemplo

                double valorTotal = valorTerreno + valorCasas + valorHotel;
                double valorVenda = valorTotal * 0.9;

                atual.ajustarSaldo(valorVenda);
                atual.getPropriedades().remove(p);
                p.setDono(null);

                JOptionPane.showMessageDialog(this,
                        "Propriedade vendida por $" + (int) valorVenda +
                                "\nNovo saldo: $" + (int) atual.getSaldo());

                atualizarInfoJogador(atual);
                return;
            }
        }
    }

    private void encerrarTurno() {
        diceA = 0;
        diceB = 0;
        canvas.repaint();

        turnoEmAndamento = false;
        construcaoRealizadaNoTurno = false; // 🔹 libera construção no próximo turno
        btnRolarDados.setEnabled(true);

        JogoController.getInstancia().passarTurno();
        atualizarEstadoBotoesPrisao();
    }

    private void exibirDetalhesPropriedade() {
        String nomeProp = (String) comboPropriedades.getSelectedItem();

        if (nomeProp == null || nomeProp.isEmpty()) {
            areaDetalhesPropriedade.setText("Nenhuma propriedade selecionada.");
            return;
        }

        Jogador atual = JogoController.getInstancia().getJogadorAtual();

        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {

                String info = String.format(
                        "Nome: %s\nPreço: $%.0f\nCasas: %d\nHotéis: %d\nAluguel: $%.0f\nEspecial: %s",
                        p.getNome(), p.getPreco(), p.getCasas(), p.getHoteis(),
                        p.calcularAluguel(), p.isEspecial() ? "Sim" : "Não"
                );

                areaDetalhesPropriedade.setText(info);
                return;
            }
        }

        areaDetalhesPropriedade.setText("Propriedade não encontrada.");
    }

    private void exibirCartaPropriedade(Propriedade prop) {

        String caminhoImagem = "/Imagens-01/territorios/" + prop.getNome() + ".png";

        String descricao = String.format(
                "<html>Propriedade: %s<br/>Preço: $%.0f<br/>Dono: %s<br/>Casas: %d<br/>Hotéis: %d<br/>Aluguel: $%.0f</html>",
                prop.getNome(),
                prop.getPreco(),
                prop.getDono() != null ? prop.getDono().getNome() + " (" + prop.getDono().getCor() + ")" : "Nenhum",
                prop.getCasas(),
                prop.getHoteis(),
                prop.calcularAluguel()
        );

        new CartaView(this, descricao, "Propriedade", caminhoImagem).setVisible(true);
    }

    private void exibirCartaCompanhia(Propriedade prop) {

        String caminhoImagem = getCaminhoImagemCompanhia(prop);

        String descricao = String.format(
                "<html>Companhia: %s<br/>Preço: $%.0f<br/>Dono: %s<br/>Aluguel (depende dos dados): consulte as regras.</html>",
                prop.getNome(),
                prop.getPreco(),
                prop.getDono() != null ? prop.getDono().getNome() + " (" + prop.getDono().getCor() + ")" : "Nenhum"
        );

        new CartaView(this, descricao, "Companhia", caminhoImagem).setVisible(true);
    }

    private String getCaminhoImagemCompanhia(Propriedade prop) {
        String nome = prop.getNome();

        switch (nome) {
            case "Companhia Ferroviária":
                return "/Imagens-01/companhias/company1.png";
            case "Companhia de Viação":
                return "/Imagens-01/companhias/company2.png";
            case "Companhia de Táxi":
                return "/Imagens-01/companhias/company3.png";
            case "Companhia de Navegação":
                return "/Imagens-01/companhias/company4.png";
            case "Companhia de Aviação":
                return "/Imagens-01/companhias/company5.png";
            case "Companhia de Táxi Aéreo":
                return "/Imagens-01/companhias/company6.png";
            default:
                return "/Imagens-01/companhias/company1.png";
        }
    }

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------
    private void inicializarCorParaPin() {
        corParaPin.put("Vermelho", 0);
        corParaPin.put("Azul", 1);
        corParaPin.put("Laranja", 2);
        corParaPin.put("Amarelo", 3);
        corParaPin.put("Roxo", 4);
        corParaPin.put("Preto", 5);
    }

    private void inicializarOffsets() {
        int tam = 20;

        for (int i = 0; i < 40; i++)
            for (int j = 0; j < 6; j++)
                offsetsPorCasa[i][j] = new Point((j % 3) * tam, (j / 3) * tam);
    }

    private void carregarImagens() {
        try {
            imgTabuleiro = ImageIO.read(
                    getClass().getResourceAsStream("/Imagens-01/tabuleiro.png")
            );

            for (int i = 0; i <= 5; i++) {
                imgPioes.put(i, ImageIO.read(
                        getClass().getResourceAsStream("/Imagens-01/pinos/pin" + i + ".png")
                ));
            }

            for (int i = 1; i <= 6; i++) {
                imgDados[i] = ImageIO.read(
                        getClass().getResourceAsStream("/Imagens-01/dados/die_face_" + i + ".png")
                );
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens via resource: " + e.getMessage());
        }
    }

    // Atualiza o estado dos botões com base em estar preso ou não e se já construiu
    private void atualizarEstadoBotoesPrisao() {
        try {
            Jogador atual = JogoController.getInstancia().getJogadorAtual();
            boolean preso = atual.isPreso();
            boolean temPropriedades = !atual.getPropriedades().isEmpty();

            if (btnConstruir != null)
                btnConstruir.setEnabled(!preso && temPropriedades && !construcaoRealizadaNoTurno);

            if (btnVender != null)
                btnVender.setEnabled(!preso && temPropriedades);
            // Encerrar turno continua habilitado mesmo preso
        } catch (Exception e) {
            // Ignora se ainda não estiver tudo inicializado
        }
    }

    // Atualiza visualmente o pino quando a posição do jogador muda
    private void atualizarPosicaoPinoDoJogador(Jogador jogador) {
        synchronized (jogadores) {
            for (JogadorView jv : jogadores) {
                if (jv.nome.equals(jogador.getNome())) {
                    jv.pistaIndex = jogador.getPosicao();
                    break;
                }
            }
        }
        canvas.repaint();
    }

    // ----------------------------------------------------
    // CLASSE INTERNA: JogadorView
    // ----------------------------------------------------
    public static class JogadorView {

        public final String nome, cor;
        public int pistaIndex, x = -1, y = -1;

        public JogadorView(String nome, String cor) {
            this.nome = nome;
            this.cor = cor;
        }
    }

    // ----------------------------------------------------
    // CLASSE INTERNA: BoardPanel
    // ----------------------------------------------------
    private class BoardPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (imgTabuleiro != null) {

                int bx = (getWidth() - imgTabuleiro.getWidth()) / 2;
                int by = (getHeight() - imgTabuleiro.getHeight()) / 2;

                g2.drawImage(imgTabuleiro, bx, by, this);

                Map<Integer, Integer> contagemCasa = new HashMap<>();

                synchronized (jogadores) {
                    for (JogadorView j : jogadores) {

                        int casa = j.pistaIndex;
                        int idx = contagemCasa.getOrDefault(casa, 0);

                        Point p = pistaIndexToXY(
                                casa,
                                bx,
                                by,
                                imgTabuleiro.getWidth(),
                                imgTabuleiro.getHeight(),
                                idx
                        );

                        contagemCasa.put(casa, idx + 1);

                        BufferedImage pImg = imgPioes.get(corParaPin.get(j.cor));

                        if (pImg != null) {
                            int tamanho = (int) (Math.min(imgTabuleiro.getWidth(), imgTabuleiro.getHeight()) * 0.05);
                            g2.drawImage(pImg, p.x - tamanho / 2, p.y - tamanho / 2, tamanho, tamanho, this);
                        } else {
                            g2.setColor(Color.GRAY);
                            g2.fillOval(p.x - 12, p.y - 12, 24, 24);
                        }

                        drawTextAsShape(g2, j.nome, p.x + 16, p.y - 16);
                    }
                }

                int dadoSize = 50;
                if (diceA > 0) g2.drawImage(imgDados[diceA], getWidth() - 220, getHeight() - 180, dadoSize, dadoSize, null);
                if (diceB > 0) g2.drawImage(imgDados[diceB], getWidth() - 140, getHeight() - 180, dadoSize, dadoSize, null);
            }

            g2.dispose();
        }

        private void drawTextAsShape(Graphics2D g2, String text, float x, float y) {
            if (text == null || text.isEmpty()) return;

            Font font = new Font("SansSerif", Font.PLAIN, 14);
            g2.setFont(font);

            GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), text);
            Shape outline = gv.getOutline(x, y);

            g2.setColor(Color.WHITE);
            g2.fill(outline);
            g2.setColor(Color.BLACK);
            g2.draw(outline);
        }

        private Point pistaIndexToXY(int index, int bx, int by, int w, int h, int ocupanteIndex) {

            int idx = ((index % 40) + 40) % 40;

            int margem = (int) (0.07 * w);

            int esquerda = bx + margem;
            int topo = by + margem;
            int direita = bx + w - margem;
            int base = by + h - margem;

            double passoX = (direita - esquerda) / 10.0;
            double passoY = (base - topo) / 10.0;

            Point basePoint;

            if (idx <= 9)
                basePoint = new Point((int) (direita - idx * passoX), base);
            else if (idx <= 19)
                basePoint = new Point(esquerda, (int) (base - (idx - 10) * passoY));
            else if (idx <= 29)
                basePoint = new Point((int) (esquerda + (idx - 20) * passoX), topo);
            else
                basePoint = new Point(direita, (int) (topo + (idx - 30) * passoY));

            if (ocupanteIndex >= 0 && ocupanteIndex < 6) {
                Point offset = offsetsPorCasa[idx][ocupanteIndex];
                return new Point(basePoint.x + offset.x, basePoint.y + offset.y);
            }

            return basePoint;
        }
    }
}
