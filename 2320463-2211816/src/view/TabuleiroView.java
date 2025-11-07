package view;

import controller.DadosController;
import controller.PiaoController;
import controller.JogoController;
import controller.CartaController;
import model.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

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
    private Map<String, Integer> corParaPin = new HashMap<>();
    private final Point[][] offsetsPorCasa = new Point[40][6];

    private JPanel painelInfoJogador;
    private JLabel lblNomeJogador, lblSaldoJogador;
    private JComboBox<String> comboPropriedades;
    private JTextArea areaDetalhesPropriedade;

    private BufferedImage imgTabuleiro;
    private final BufferedImage[] imgDados = new BufferedImage[7];
    private final Map<Integer, BufferedImage> imgPioes = new HashMap<>();
    private DadosView dadosView;

    // ---------------- Constructor ----------------
    public TabuleiroView(List<JogadorView> listaJogadores, List<Piao> pioes, PiaoController controller) {
        super("Banco Imobiliário - Tabuleiro");
        this.pioes = pioes;
        this.jogadores.addAll(listaJogadores);
        this.piaoController = controller;

        this.piaoController.addObserver(this);

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
            atualizarInfoJogador(JogoController.getInstancia().getJogadores().get(0));
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------------- Listener Dados ----------------
    private void configurarListenerBotaoDados() {
        btnRolarDados.addActionListener(e -> {
            if (turnoEmAndamento) return;
            turnoEmAndamento = true;

            abrirDadosDialog(valores -> {
                diceA = valores[0];
                diceB = valores[1];

                piaoController.setJogadorVez(jogadorAtualIndex);
                piaoController.moverPiao(diceA, diceB);
                setDiceValues(diceA, diceB);

                new javax.swing.Timer(3000, ev -> {
                    diceA = 0;
                    diceB = 0;
                    canvas.repaint();
                    jogadorAtualIndex = (jogadorAtualIndex + 1) % jogadores.size();
                    turnoEmAndamento = false;
                }) {{
                    setRepeats(false);
                    start();
                }};
            });
        });
    }

    public DadosView getDadosView() {
        if (dadosView == null) dadosView = new DadosView();
        return dadosView;
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

    // ---------------- Observer ----------------
    @Override
    public void update(model.Observable observado, String evento) {
        if (!(observado instanceof PiaoController)) return;
        PiaoController pc = (PiaoController) observado;

        int idxJogador = pc.getJogadorVez();
        Jogador jogadorAtual = JogoController.getInstancia().getJogadores().get(idxJogador);

        switch (evento) {
            case "PiaoMovido":
                repaint();

                Piao piao = pioes.get(idxJogador);
                int posicao = piao.getPosicao();

                Propriedade prop = JogoController.getInstancia()
                        .getJogo().getTabuleiro()
                        .getPropriedadeNaPosicao(posicao);

                if (prop != null) {
                    if (!prop.isEspecial()) {
                        // Carta de propriedade
                        exibirCartaPropriedade(prop);

                    } else {
                        // Casas especiais: SORTE ou REVÉS
                        Tabuleiro.TipoCasa tipo = JogoController.getInstancia()
                                .getJogo().getTabuleiro().verificarTipoCasa(jogadorAtual);

                        if (tipo == Tabuleiro.TipoCasa.SORTE || tipo == Tabuleiro.TipoCasa.REVES) {
                            CartaController baralho = JogoController.getInstancia()
                                    .getJogo().getBaralhoCartas();

                            Carta carta = (tipo == Tabuleiro.TipoCasa.SORTE)
                                    ? baralho.pegarCartaSorte()
                                    : baralho.pegarCartaReves();

                            String descricaoCarta = carta.getDescricao();
                            String tipoCarta = tipo == Tabuleiro.TipoCasa.SORTE ? "SORTE" : "REVÉS";
                            String caminhoImagem = baralho.getCaminhoImagem(carta);

                            new CartaView(this, descricaoCarta, tipoCarta, caminhoImagem).setVisible(true);

                            // Devolve carta para o baralho
                            baralho.devolverCarta(carta);
                        }
                    }
                }
                break;

            case "AtualizarInfoJogador":
                atualizarInfoJogador(jogadorAtual);
                break;

            case "VezMudou":
                repaint();
                break;
        }
    }

    // ---------------- Painel lateral ----------------
    public void atualizarInfoJogador(Jogador jogador) {
        lblNomeJogador.setText("Nome: " + jogador.getNome());
        lblSaldoJogador.setText("Saldo: $" + jogador.getSaldo());

        comboPropriedades.removeAllItems();
        for (Propriedade p : jogador.getPropriedades()) comboPropriedades.addItem(p.getNome());

        if (!jogador.getPropriedades().isEmpty())
            exibirDetalhesPropriedade();
        else
            areaDetalhesPropriedade.setText("Sem propriedades.");
    }

    public void setDiceValues(int a, int b) {
        diceA = Math.max(1, Math.min(6, a));
        diceB = Math.max(1, Math.min(6, b));
        canvas.repaint();
    }

    // ---------------- Painel info jogador ----------------
    private void criarPainelInfoJogador() {
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
        if (nomeProp == null) { JOptionPane.showMessageDialog(this,"Selecione uma propriedade!"); return; }

        Jogador atual = JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                if (p.construirCasa()) JOptionPane.showMessageDialog(this,"Casa/Hotel construído!");
                else JOptionPane.showMessageDialog(this,"Não é possível construir!");
                atualizarInfoJogador(atual);
                return;
            }
        }
    }

    private void venderPropriedade() {
        String nomeProp = (String) comboPropriedades.getSelectedItem();
        if (nomeProp == null) { JOptionPane.showMessageDialog(this,"Selecione uma propriedade!"); return; }

        Jogador atual = JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                int valorVenda = (int) (p.getPreco() * 0.9);
                atual.ajustarSaldo(valorVenda);
                atual.getPropriedades().remove(p);
                p.setDono(null);
                JOptionPane.showMessageDialog(this,
                        String.format("Propriedade vendida por $%d\nNovo saldo: $%d", valorVenda, atual.getSaldo()));
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

        Jogador atual = JogoController.getInstancia().getJogadores().get(jogadorAtualIndex);
        for (Propriedade p : atual.getPropriedades()) {
            if (p.getNome().equals(nomeProp)) {
                String info = String.format(
                        "Nome: %s\nPreço: $%d\nCasas: %d\nHotéis: %d\nAluguel: $%d\nEspecial: %s",
                        p.getNome(), p.getPreco(), p.getCasas(), p.getHoteis(),
                        p.calcularAluguel(), p.isEspecial() ? "Sim" : "Não");
                areaDetalhesPropriedade.setText(info);
                return;
            }
        }
        areaDetalhesPropriedade.setText("Propriedade não encontrada.");
    }

    // ---------------- Cartas ----------------
    private void exibirCartaPropriedade(Propriedade prop) {
        String caminhoImagem = "/imagens-01/cartas/" + prop.getNome().replace(" ", "_") + ".png";
        String descricao = String.format(
                "<html>Propriedade: %s<br/>Preço: $%d<br/>Dono: %s<br/>Casas: %d<br/>Hotéis: %d<br/>Aluguel: $%d</html>",
                prop.getNome(),
                prop.getPreco(),
                prop.getDono() != null ? prop.getDono().getNome() + " (" + prop.getDono().getCor() + ")" : "Nenhum",
                prop.getCasas(),
                prop.getHoteis(),
                prop.calcularAluguel()
        );
        new CartaView(this, descricao, "Propriedade", caminhoImagem).setVisible(true);
    }

    // ---------------- Helpers ----------------
    public void setJogadores(List<JogadorView> lista) { jogadores.clear(); jogadores.addAll(lista); canvas.repaint(); }
    public void setJogadorVez(int idx) { jogadorAtualIndex = idx; canvas.repaint(); }

    private void inicializarCorParaPin() {
        corParaPin.put("Vermelho", 0); corParaPin.put("Azul", 1);
        corParaPin.put("Laranja", 2); corParaPin.put("Amarelo",3);
        corParaPin.put("Roxo",4); corParaPin.put("Preto",5);
    }

    private void inicializarOffsets() {
        int tamanhoOffset = 20;
        for (int i=0;i<40;i++)
            for(int j=0;j<6;j++)
                offsetsPorCasa[i][j]=new Point((j%3)*tamanhoOffset,(j/3)*tamanhoOffset);
    }

    private void carregarImagens() {
        try {
            imgTabuleiro = ImageIO.read(new File("imagens-01/tabuleiro.png"));
            for(int i=0;i<=5;i++){ 
                File f=new File("/imagens-01/pinos/pin"+i+".png"); 
                if(f.exists()) imgPioes.put(i,ImageIO.read(f)); 
            }
            for(int i=1;i<=6;i++){ 
                File f=new File("/imagens-01/dados/die_face_"+i+".png"); 
                if(f.exists()) imgDados[i]=ImageIO.read(f); 
            }
        } catch(Exception e){ 
            System.err.println("Erro ao carregar imagens: "+e.getMessage()); 
        }
    }

    // ---------------- JogadorView ----------------
    public static class JogadorView { 
        public final String nome, cor; 
        public int pistaIndex, x=-1, y=-1; 
        public JogadorView(String nome,String cor){this.nome=nome;this.cor=cor;} 
    }

    // ---------------- BoardPanel ----------------
    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setColor(new Color(30,30,30));
            g2.fillRect(0,0,getWidth(),getHeight());
            int bx=(getWidth()-imgTabuleiro.getWidth())/2;
            int by=(getHeight()-imgTabuleiro.getHeight())/2;
            if(imgTabuleiro!=null) g2.drawImage(imgTabuleiro,bx,by,this);

            Map<Integer,Integer> contagemCasa=new HashMap<>();
            synchronized (jogadores) {
                for(JogadorView j:jogadores){
                    int casa=j.pistaIndex;
                    int idx=contagemCasa.getOrDefault(casa,0);
                    Point p=pistaIndexToXY(casa,bx,by,imgTabuleiro.getWidth(),imgTabuleiro.getHeight(),idx);
                    contagemCasa.put(casa,idx+1);
                    BufferedImage pImg=imgPioes.get(corParaPin.get(j.cor));
                    if(pImg!=null){
                        int tamanho=(int)(Math.min(imgTabuleiro.getWidth(),imgTabuleiro.getHeight())*0.05);
                        g2.drawImage(pImg,p.x-tamanho/2,p.y-tamanho/2,tamanho,tamanho,this);
                    } else { 
                        g2.setColor(Color.GRAY); 
                        g2.fillOval(p.x-12,p.y-12,24,24); 
                    }
                    drawTextAsShape(g2,j.nome,p.x+16,p.y-16);
                }
            }

            int dadoSize=50;
            if(diceA>0 && imgDados[diceA]!=null) g2.drawImage(imgDados[diceA],getWidth()-220,getHeight()-180,dadoSize,dadoSize,null);
            if(diceB>0 && imgDados[diceB]!=null) g2.drawImage(imgDados[diceB],getWidth()-140,getHeight()-180,dadoSize,dadoSize,null);
            g2.dispose();
        }

        private void drawTextAsShape(Graphics2D g2, String text,float x,float y){
            if(text==null||text.isEmpty()) return;
            Font font=new Font("SansSerif",Font.PLAIN,14);
            g2.setFont(font);
            GlyphVector gv=font.createGlyphVector(g2.getFontRenderContext(),text);
            Shape outline=gv.getOutline(x,y);
            g2.setColor(Color.WHITE); g2.fill(outline); g2.setColor(Color.BLACK); g2.draw(outline);
        }

        private Point pistaIndexToXY(int index,int bx,int by,int w,int h,int ocupanteIndex){
            int idx=((index%40)+40)%40;
            int margem=(int)(0.07*w);
            int esquerda=bx+margem; int topo=by+margem; int direita=bx+w-margem; int base=by+h-margem;
            double passoX=(direita-esquerda)/10.0; double passoY=(base-topo)/10.0; Point basePoint;
            if(idx<=9) basePoint=new Point((int)(direita-idx*passoX),base);
            else if(idx<=19) basePoint=new Point(esquerda,(int)(base-(idx-10)*passoY));
            else if(idx<=29) basePoint=new Point((int)(esquerda+(idx-20)*passoX),topo);
            else basePoint=new Point(direita,(int)(topo+(idx-30)*passoY));
            if(ocupanteIndex>=0 && ocupanteIndex<6) { 
                Point offset=offsetsPorCasa[idx][ocupanteIndex]; 
                return new Point(basePoint.x+offset.x,basePoint.y+offset.y);
            } else return basePoint;
        }
    }
}
