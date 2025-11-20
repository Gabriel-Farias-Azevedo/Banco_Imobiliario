package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DadosView extends JPanel {

    private JLabel lblDado1, lblDado2;
    private JComboBox<Integer> comboDado1, comboDado2;
    private JButton btnLancar;

    private static final int TAMANHO_DADO = 80;

    public DadosView() {
        setLayout(new BorderLayout());
        criarPainelDados();
        criarPainelControles();
    }

    // ---------------- Painel com imagens dos dados ----------------
    private void criarPainelDados() {
        JPanel painelDados = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));

        lblDado1 = new JLabel(new ImageIcon(carregarImagemResource("/Imagens-01/dados/die_face_1.png", TAMANHO_DADO)));
        lblDado2 = new JLabel(new ImageIcon(carregarImagemResource("/Imagens-01/dados/die_face_1.png", TAMANHO_DADO)));

        painelDados.add(lblDado1);
        painelDados.add(lblDado2);
        add(painelDados, BorderLayout.CENTER);
    }

    // ---------------- Painel inferior com controles ----------------
    private void criarPainelControles() {
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        comboDado1 = criarComboDado();
        comboDado2 = criarComboDado();

        btnLancar = new JButton("Lançar Dados");

        painelControles.add(new JLabel("Dado 1:"));
        painelControles.add(comboDado1);
        painelControles.add(new JLabel("Dado 2:"));
        painelControles.add(comboDado2);
        painelControles.add(btnLancar);

        add(painelControles, BorderLayout.SOUTH);
    }

    private JComboBox<Integer> criarComboDado() {
        JComboBox<Integer> combo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6});
        combo.setSelectedIndex(-1);
        return combo;
    }

    // ---------------- Atualiza os ícones dos dados ----------------
    public void atualizarView(int v1, int v2) {
        lblDado1.setIcon(new ImageIcon(
                carregarImagemResource("/Imagens-01/dados/die_face_" + v1 + ".png", TAMANHO_DADO)));

        lblDado2.setIcon(new ImageIcon(
                carregarImagemResource("/Imagens-01/dados/die_face_" + v2 + ".png", TAMANHO_DADO)));

        repaint();
    }

    // ---------------- Carrega imagem como recurso ----------------
    private Image carregarImagemResource(String resourcePath, int largura) {
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream(resourcePath));
            int altura = (int) (img.getHeight() * (largura / (double) img.getWidth()));
            return img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem via resource: " + resourcePath);
            return null;
        }
    }

    public JButton getBtnLancar() { return btnLancar; }
    public JComboBox<Integer> getComboDado1() { return comboDado1; }
    public JComboBox<Integer> getComboDado2() { return comboDado2; }
}
