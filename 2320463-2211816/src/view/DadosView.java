package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DadosView extends JPanel {

    private JLabel lblDado1, lblDado2;
    private JComboBox<Integer> comboDado1, comboDado2;
    private JButton btnLancar;

    public DadosView() {
        setLayout(new BorderLayout());

        // Painel central para exibir os dados
        JPanel painelDados = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        int tamanho = 80; // largura desejada para todos os dados
        lblDado1 = new JLabel(new ImageIcon(redimensionarImagem("imagens-01/dados/die_face_1.png", tamanho)));
        lblDado2 = new JLabel(new ImageIcon(redimensionarImagem("imagens-01/dados/die_face_1.png", tamanho)));

        painelDados.add(lblDado1);
        painelDados.add(lblDado2);
        add(painelDados, BorderLayout.CENTER);

        // Painel inferior com controles
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        comboDado1 = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
        comboDado2 = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
        
        comboDado1.setSelectedIndex(-1);
        comboDado2.setSelectedIndex(-1);
        
        btnLancar = new JButton("Lançar Dados");

        painelControles.add(new JLabel("Dado 1:"));
        painelControles.add(comboDado1);
        painelControles.add(new JLabel("Dado 2:"));
        painelControles.add(comboDado2);
        painelControles.add(btnLancar);

        add(painelControles, BorderLayout.SOUTH);
    }

    /**
     * Atualiza a visualização dos dados.
     */
    public void atualizarView(int v1, int v2) {
        int tamanho = 80; // largura desejada
        lblDado1.setIcon(new ImageIcon(redimensionarImagem("imagens-01/dados/die_face_" + v1 + ".png", tamanho)));
        lblDado2.setIcon(new ImageIcon(redimensionarImagem("imagens-01/dados/die_face_" + v2 + ".png", tamanho)));
        repaint();
    }

    private Image redimensionarImagem(String caminho, int largura) {
        try {
            BufferedImage img = ImageIO.read(new File(caminho));
            int altura = (int)(img.getHeight() * (largura / (double) img.getWidth()));
            return img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            return null;
        }
    }


    // Getters para o Controller
    public JButton getBtnLancar() { return btnLancar; }
    public JComboBox<Integer> getComboDado1() { return comboDado1; }
    public JComboBox<Integer> getComboDado2() { return comboDado2; }

    // Teste rápido standalone da view
    public static void main(String[] args) {
        JFrame frame = new JFrame("Lançamento de Dados");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        DadosView dv = new DadosView();
        frame.add(dv);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
