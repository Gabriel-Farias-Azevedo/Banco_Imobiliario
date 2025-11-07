package view;

import controller.DadosController;
import controller.JogoController;
import controller.PiaoController;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Dado;
import model.Jogador;
import model.Observable;
import model.Piao;
import model.Propriedade;

public class TabuleiroView extends JFrame implements Observer {

    private BoardPanel canvas;
    private JButton btnRolarDados;
    private BufferedImage imgTabuleiro, imgCarta;
    private BufferedImage[] imgDados = new BufferedImage[7];
    private Map<Integer, BufferedImage> imgPioes = new HashMap<>();

    private final List<JogadorView> jogadores = new ArrayList<>();
    private final List<Piao> pioes;
    private final PiaoController piaoController;

    public int jogadorAtualIndex = 0;
    public boolean turnoEmAndamento = false;
    public int diceA = 0;
    public int diceB = 0;
    private final Map<String, Integer> corParaPin = new HashMap<>();

    private JPanel painelInfoJogador;
    private JLabel lblNomeJogador;
    private JLabel lblSaldoJogador;
    private JComboBox<String> comboPropriedades;
    private JTextArea areaDetalhesPropriedade;

    // Pistas imaginárias para piões na mesma casa
    private final Point[][] offsetsPorCasa = new Point[40][6];

    // --------------------------------------------------------
    public TabuleiroView(List<JogadorView> listaJogadores, List<Piao> pioes, PiaoController controller) {
        super("Banco Imobiliário - Tabuleiro");
        this.pioes = pioes;
        this.jogadores.addAll(listaJogadores);
        this.piaoController = controller;

        // Registra a própria View como observadora do PiaoController
        this.piaoController.addObserver(this);

        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        inicializarCorParaPin();
        inicializarOffsets();

        // Canvas
        canvas = new BoardPanel();
        canvas.setBounds(0, 0, 1000, 800);

        criarPainelInfoJogador();
        painelInfoJogador.setBounds(1000, 0, 280, 500);
        add(canvas);
        add(painelInfoJogador);

        // Botão "Rolar Dados"
        btnRolarDados = new JButton("Rolar Dados");
        btnRolarDados.setBounds(1050, 550, 200, 40);
        add(btnRolarDados);

        carregarImagens();
        configurarListenerBotaoDados();

        if (!jogadores.isEmpty()) {
            Jogador primeiro = JogoController.getInstancia().getJogadores().get(0);
            atualizarInfoJogador(primeiro);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --------------------------------------------------------
    private void configurarListenerBotaoDados() {
        btnRolarDados.addActionListener(e -> {
            if (turnoEmAndamento) return;
            turnoEmAndamento = true;

            abrirDadosDialog(valores -> {
                diceA = valores[0];
                diceB = valores[1];

                // Move pião usando PiaoController
                piaoController.setJogadorVez(jogadorAtualIndex);
                piaoController.moverPiao(diceA, diceB);

                setDiceValues(diceA, diceB);

                // Mantém dados na tela por 3 segundos
                new javax.swing.Timer(3000, ev -> {
                    diceA = 0;
                    diceB = 0;
                    canvas.repaint();

                    // Passa a vez
                    jogadorAtualIndex = (jogadorAtualIndex + 1) % jogadores.size();
                    turnoEmAndamento = false;
                }) {{
                    setRepeats(false);
                    start();
                }};
            });
        });
    }

    // --------------------------------------------------------
    private void inicializarCorParaPin() {
        corParaPin.put("Vermelho", 0);
        corParaPin.put("Azul", 1);
        corParaPin.put("Laranja", 2);
        corParaPin.put("Amarelo", 3);
        corParaPin.put("Roxo", 4);
        corParaPin.put("Preto", 5);
    }

    private void inicializarOffsets() {
        int tamanhoOffset = 20;
        for (int i = 0; i < 40; i++)
            for (int j = 0; j < 6; j++)
                offsetsPorCasa[i][j] = new Point((j % 3) * tamanhoOffset, (j / 3) * tamanhoOffset);
    }

    public JButton getBtnRolarDados() { return btnRolarDados; }

    public void setJogadores(List<JogadorView> lista) {
        synchronized (jogadores) {
            jogadores.clear();
            jogadores.addAll(lista);
        }
        canvas.repaint();
    }

    public void setPawnPosition(String nome, int pistaIndex, int x, int y) {
        synchronized (jogadores) {
            for (JogadorView j : jogadores) {
                if (j.nome.equals(nome)) {
                    j.pistaIndex = pistaIndex;
                    j.x = x;
                    j.y = y;
                    break;
                }
            }
        }
        canvas.repaint();
    }

    public void setJogadorVez(int idx) {
        jogadorAtualIndex = idx;
        canvas.repaint();
    }

    public void setDiceValues(int a, int b) {
        diceA = Math.max(1, Math.min(6, a));
        diceB = Math.max(1, Math.min(6, b));
        canvas.repaint();
    }

    public void abrirDadosDialog(Consumer<int[]> callback) {
        JDialog dialog = new JDialog(this, "Lançamento de Dados", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        DadosView dv = new DadosView();
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

    // --------------------------------------------------------
    private void carregarImagens() {
        try {
            imgTabuleiro = ImageIO.read(new File("imagens-01/tabuleiro.png"));
            for (int i = 0; i <= 5; i++) {
                File f = new File("imagens-01/pinos/pin" + i + ".png");
                if (f.exists()) imgPioes.put(i, ImageIO.read(f));
            }
            for (int i = 1; i <= 6; i++) {
                File f = new File("imagens-01/dados/die_face_" + i + ".png");
                if (f.exists()) imgDados[i] = ImageIO.read(f);
            }
            File fc = new File("imagens-01/carta_exemplo.png");
            if (fc.exists()) imgCarta = ImageIO.read(fc);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }
    }

    private void exibirCartaPropriedade(Propriedade prop) {
        String info = String.format(
            "Propriedade: %s\n" +
            "Preço: $%d\n" +
            "Dono: %s\n" +
            "Casas: %d\n" +
            "Hotéis: %d\n" +
            "Aluguel: $%d",
            prop.getNome(),
            prop.getPreco(),
            prop.getDono() != null ? prop.getDono().getNome() + " (" + prop.getDono().getCor() + ")" : "Nenhum",
            prop.getCasas(),
            prop.getNumeroHoteis(),
            prop.calcularAluguel()
        );
        
        JOptionPane.showMessageDialog(this, info, "Carta da Propriedade", JOptionPane.INFORMATION_MESSAGE);
    }

    private void criarPainelInfoJogador(){
        painelInfoJogador = new JPanel();
        painelInfoJogador.setLayout(new BoxLayout(painelInfoJogador, BoxLayout.Y_AXIS));
        painelInfoJogador.setBorder(BorderFactory.createTitledBorder("Jogador da Vez"));
        painelInfoJogador.setBackground(Color.WHITE);

        lblNomeJogador = new JLabel("Nome: ");
        lblSaldoJogador = new JLabel("Saldo: $0");

        JButton btnConstruir = new JButton("Construir");
        btnConstruir.addActionListener(e -> construirImovel());

        JButton btnVender = new JButton("Vender Propriedade");
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
        painelInfoJogador.add(Box.createVerticalStrut(10));
        painelInfoJogador.add(new JLabel("Propriedades:"));
        painelInfoJogador.add(comboPropriedades);
        painelInfoJogador.add(Box.createVerticalStrut(5));
        painelInfoJogador.add(new JScrollPane(areaDetalhesPropriedade));
        painelInfoJogador.add(Box.createVerticalStrut(10));
        painelInfoJogador.add(btnConstruir);
        painelInfoJogador.add(btnVender);
    }

    private void construirImovel() {
        String nomeProp = (String) comboPropriedades.getSelectedItem();
        if (nomeProp == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma propriedade!");
            return;
        }
        
        Jogador atual = JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                if (p.construirCasa()) {
                    JOptionPane.showMessageDialog(this, "Casa/Hotel construído!");
                    atualizarInfoJogador(atual);
                } else {
                    JOptionPane.showMessageDialog(this, "Não é possível construir!");
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
        
        Jogador atual = JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                int valorVenda = (int) (p.getPreco() * 0.9);
                atual.creditar(valorVenda);
                atual.getPropriedades().remove(p);
                p.setDono(null);
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Propriedade vendida por $%d\nNovo saldo: $%d", 
                    valorVenda, atual.getSaldo()));
                
                atualizarInfoJogador(atual);
                return;
            }
        }
    }

    private void exibirDetalhesPropriedade() {
        String nomeProp = (String) comboPropriedades.getSelectedItem();
        if (nomeProp == null || nomeProp.isEmpty()) {
            areaDetalhesPropriedade.setText("Nenhuma propriedade selecionada.");
            return;
        }

        // 🔹 Busca o jogador atual
        Jogador atual = JogoController.getInstancia()
                .getJogadores().get(jogadorAtualIndex);

        // 🔹 Busca a propriedade selecionada
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                String info = String.format(
                    "Nome: %s\n" +
                    "Preço: $%d\n" +
                    "Casas: %d\n" +
                    "Hotéis: %d\n" +
                    "Aluguel: $%d\n" +
                    "Especial: %s",
                    p.getNome(),
                    p.getPreco(),
                    p.getCasas(),
                    p.getNumeroHoteis(),
                    p.calcularAluguel(),
                    p.isEspecial() ? "Sim" : "Não"
                );
                
                areaDetalhesPropriedade.setText(info);
                return;
            }
        }

        areaDetalhesPropriedade.setText("Propriedade não encontrada.");
    }

    public void atualizarInfoJogador(Jogador jogador){
        lblNomeJogador.setText("Nome: " + jogador.getNome());
        lblSaldoJogador.setText("Saldo: $" + jogador.getSaldo());

        comboPropriedades.removeAllItems();
        for (Propriedade p : jogador.getPropriedades()) {
            comboPropriedades.addItem(p.getNome()); 
        }

        if (jogador.getPropriedades().isEmpty()) {
            areaDetalhesPropriedade.setText("Sem propriedades.");
        } else {
            exibirDetalhesPropriedade();
        }
    }

    // --------------------------------------------------------
    @Override
    public void atualizar(Object fonte, String evento) {
        if (evento.equals("PiaoMovido")) {
            repaint();
            
            // 🔹 Exibe carta da propriedade após movimento
            int idx = ((PiaoController) fonte).getJogadorVez();
            Piao piao = pioes.get(idx);
            Propriedade prop = JogoController.getInstancia().getTabuleiro().getPropriedadeNaPosicao(piao.getPosicao());
            
            if (prop != null && !prop.isEspecial()) {
                exibirCartaPropriedade(prop);
            }
            
        } else if (evento.equals("AtualizarInfoJogador")) {
            int idx = ((PiaoController) fonte).getJogadorVez();
            Jogador atual = JogoController.getInstancia().getJogadores().get(idx);
            atualizarInfoJogador(atual);
        } else if (evento.equals("VezMudou")) {
            repaint();
        }
    }

    // --------------------------------------------------------
    public static class JogadorView {
        public final String nome;
        public final String cor;
        public int pistaIndex;
        public int x, y;

        public JogadorView(String nome, String cor) {
            this.nome = nome;
            this.cor = cor;
            this.pistaIndex = 0;
            this.x = -1;
            this.y = -1;
        }
    }

    // --------------------------------------------------------
    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int bx = (getWidth() - imgTabuleiro.getWidth()) / 2;
            int by = (getHeight() - imgTabuleiro.getHeight()) / 2;

            if (imgTabuleiro != null) g2.drawImage(imgTabuleiro, bx, by, this);

            Map<Integer, Integer> contagemCasa = new HashMap<>();
            synchronized (jogadores) {
                for (JogadorView j : jogadores) {
                    int casa = j.pistaIndex;
                    int idx = contagemCasa.getOrDefault(casa, 0);

                    Point p = pistaIndexToXY(casa, bx, by, imgTabuleiro.getWidth(),
                            imgTabuleiro.getHeight(), idx);
                    contagemCasa.put(casa, idx + 1);

                    Integer pinIndex = corParaPin.get(j.cor);
                    BufferedImage pImg = (pinIndex != null) ? imgPioes.get(pinIndex) : null;

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

            // Desenha dados
            int dadoSize = 50;
            if (diceA > 0 && imgDados[diceA] != null)
                g2.drawImage(imgDados[diceA], getWidth() - 220, getHeight() - 180, dadoSize, dadoSize, null);
            if (diceB > 0 && imgDados[diceB] != null)
                g2.drawImage(imgDados[diceB], getWidth() - 140, getHeight() - 180, dadoSize, dadoSize, null);

            // --------------------------
            // Jogador da vez (com cor real)
            if (!jogadores.isEmpty()) {
                JogadorView vez = jogadores.get(jogadorAtualIndex);

                // converte string da cor em Color
                Color corJogador;
                switch (vez.cor.toLowerCase()) {
                    case "vermelho": corJogador = Color.RED; break;
                    case "azul": corJogador = new Color(0, 90, 200); break;
                    case "laranja": corJogador = new Color(255, 140, 0); break;
                    case "amarelo": corJogador = Color.YELLOW; break;
                    case "roxo": corJogador = new Color(128, 0, 128); break;
                    case "preto": corJogador = Color.BLACK; break;
                    default: corJogador = Color.GRAY; break;
                }

                // calcula contraste para texto
                Color corTexto = getContrastingColor(corJogador);

                // Retângulo bem mais estreito e baixo
                g2.setColor(corJogador);
                g2.fillRoundRect(40, getHeight() - 110, 140, 50, 10, 10);

                // Borda
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(40, getHeight() - 110, 140, 50, 10, 10);

                // Texto do jogador da vez
                g2.setColor(getContrastingColor(corJogador));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("Jogador:", 50, getHeight() - 90);
                g2.drawString(vez.nome, 50, getHeight() - 70);

               
            }
            // --------------------------

            // Carta atual
            if (imgCarta != null)
                g2.drawImage(imgCarta, getWidth() - imgCarta.getWidth() - 20, 20, this);

            g2.dispose();
        }

        // --------------------------
        private Color getContrastingColor(Color c) {
            double lum = (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255.0;
            return lum > 0.6 ? Color.BLACK : Color.WHITE;
        }
        // --------------------------

        private void drawTextAsShape(Graphics2D g2, String text, float x, float y) {
            if (text == null || text.isEmpty()) return;
            Font font = new Font("SansSerif", Font.PLAIN, 14);
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, text);
            Shape outline = gv.getOutline(x, y);
            g2.setColor(Color.WHITE);
            g2.fill(outline);
            g2.setColor(Color.BLACK);
            g2.draw(outline);
        }

        private Point pistaIndexToXY(int index, int boardX, int boardY, int boardW, int boardH, int ocupanteIndex) {
            int idx = ((index % 40) + 40) % 40;
            int margem = (int) (0.07 * boardW);
            int esquerda = boardX + margem;
            int topo = boardY + margem;
            int direita = boardX + boardW - margem;
            int base = boardY + boardH - margem;
            double passoX = (double) (direita - esquerda) / 10.0;
            double passoY = (double) (base - topo) / 10.0;
            Point basePoint;
            if (idx <= 9) basePoint = new Point((int)(direita - idx*passoX), base);
            else if (idx <= 19) basePoint = new Point(esquerda, (int)(base - (idx-10)*passoY));
            else if (idx <= 29) basePoint = new Point((int)(esquerda + (idx-20)*passoX), topo);
            else basePoint = new Point(direita, (int)(topo + (idx-30)*passoY));

            if (ocupanteIndex >= 0 && ocupanteIndex < 6) {
                Point offset = offsetsPorCasa[idx][ocupanteIndex];
                return new Point(basePoint.x + offset.x, basePoint.y + offset.y);
            } else {
                return basePoint;
            }
        }
    }

	@Override
	public void atualizar(Observable observado, String evento) {
		// TODO Auto-generated method stub
		
	}
}
