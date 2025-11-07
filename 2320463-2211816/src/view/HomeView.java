package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import controller.JogoController;


public class HomeView extends JFrame {

    private JComboBox<Integer> comboNumJogadores;
    private JPanel painelJogadores;
    private JButton botaoIniciar;

    private java.util.List<JTextField> camposNomes = new ArrayList<>();
    private java.util.List<JComboBox<String>> combosCores = new ArrayList<>();

    private static final String[] CORES_DISPONIVEIS = {
    		"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Preto"
    };
    
// TESTE COMMIT
    public HomeView() {
        setTitle("Banco Imobiliário - Configuração Inicial");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel labelTitulo = new JLabel("Configuração do Jogo", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitulo.setBounds(0, 20, 500, 30);
        add(labelTitulo);

        JLabel labelNum = new JLabel("Número de jogadores:");
        labelNum.setBounds(60, 80, 180, 25);
        add(labelNum);

        comboNumJogadores = new JComboBox<>(new Integer[]{3, 4, 5, 6});
        comboNumJogadores.setBounds(250, 80, 60, 25);
        comboNumJogadores.addActionListener(e -> atualizarCamposJogadores());
        add(comboNumJogadores);

        painelJogadores = new JPanel();
        painelJogadores.setLayout(new GridLayout(6, 2, 10, 10));
        painelJogadores.setBounds(60, 130, 370, 220);
        add(painelJogadores);

        botaoIniciar = new JButton("Iniciar Jogo");
        botaoIniciar.setBounds(170, 380, 150, 35);
        botaoIniciar.addActionListener(e -> iniciarJogo());
        add(botaoIniciar);

        atualizarCamposJogadores(); // inicializa com 3 jogadores
        setVisible(true);
    }

    private void atualizarCamposJogadores() {
        painelJogadores.removeAll();
        camposNomes.clear();
        combosCores.clear();

        int num = (int) comboNumJogadores.getSelectedItem();

        for (int i = 0; i < num; i++) {
            JTextField campoNome = new JTextField();
            JComboBox<String> comboCor = new JComboBox<>(CORES_DISPONIVEIS);

            painelJogadores.add(new JLabel("Jogador " + (i + 1) + ":"));
            JPanel linha = new JPanel(new GridLayout(1, 2, 5, 5));
            linha.add(campoNome);
            linha.add(comboCor);
            painelJogadores.add(linha);

            camposNomes.add(campoNome);
            combosCores.add(comboCor);
        }

        painelJogadores.revalidate();
        painelJogadores.repaint();
    }

    private void iniciarJogo() {
        JogoController controller = JogoController.getInstancia();
        controller.reiniciar(); // limpa dados anteriores

        int num = (int) comboNumJogadores.getSelectedItem();
        Set<String> coresUsadas = new HashSet<>();

        for (int i = 0; i < num; i++) {
            String nome = camposNomes.get(i).getText().trim();
            String cor = (String) combosCores.get(i).getSelectedItem();

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite o nome do Jogador " + (i + 1));
                return;
            }
            if (coresUsadas.contains(cor)) {
                JOptionPane.showMessageDialog(this, "A cor '" + cor + "' já foi escolhida!");
                return;
            }
            coresUsadas.add(cor);
            controller.adicionarJogador(nome, cor);
        }

        controller.iniciarTabuleiro();
        dispose();
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomeView::new);
    }
}
