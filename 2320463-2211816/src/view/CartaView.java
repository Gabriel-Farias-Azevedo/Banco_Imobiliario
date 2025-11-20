package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CartaView extends JDialog {

    public CartaView(JFrame parent, String descricao, String tipo, String caminhoImagem) {
        super(parent, "Carta de " + tipo, true);
        setLayout(new BorderLayout());

        // Painel da imagem
        JLabel lblImagem = new JLabel();
        if (caminhoImagem != null) {
            try {
                // Carrega imagem via RESOURCE (correto)
                BufferedImage img = ImageIO.read(
                    getClass().getResourceAsStream(caminhoImagem)
                );

                lblImagem.setIcon(new ImageIcon(
                        img.getScaledInstance(200, 120, Image.SCALE_SMOOTH)
                ));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem da carta: " + caminhoImagem);
            }
        }
        add(lblImagem, BorderLayout.NORTH);

        // Descrição
        JLabel lblDescricao = new JLabel("<html><center>" + descricao + "</center></html>");
        lblDescricao.setFont(new Font("Arial", Font.BOLD, 16));
        lblDescricao.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblDescricao, BorderLayout.CENTER);

        // Botão Ok
        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(e -> dispose());
        add(btnOk, BorderLayout.SOUTH);

        setSize(300, 300);
        setLocationRelativeTo(parent);
    }
}
