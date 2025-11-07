package view;

import java.awt.*;
import javax.swing.*;

public class CartaView extends JDialog {

    public CartaView(JFrame parent, String descricao, String tipo) {
        super(parent, "Carta de " + tipo, true);
        setLayout(new BorderLayout());

        JLabel lblDescricao = new JLabel("<html><center>" + descricao + "</center></html>");
        lblDescricao.setFont(new Font("Arial", Font.BOLD, 16));
        lblDescricao.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(e -> dispose());

        add(lblDescricao, BorderLayout.CENTER);
        add(btnOk, BorderLayout.SOUTH);

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }
}