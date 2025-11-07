package controller;

import view.DadosView;
import model.Dado;

import java.util.function.Consumer;

public class DadosController {
    private final DadosView view;
    private final Dado dado;
    private Consumer<int[]> callback; // Callback para enviar os valores lançados

    public DadosController(DadosView view, Dado dado) {
        this.view = view;
        this.dado = dado;

        // Listener único do botão
        this.view.getBtnLancar().addActionListener(e -> lancarDados());
    }

    public void setCallback(Consumer<int[]> cb) {
        this.callback = cb;
    }

    public void lancarDados() {
        int v1, v2;

        // Se houver valores manuais selecionados
        if (view.getComboDado1().getSelectedIndex() != -1 &&
            view.getComboDado2().getSelectedIndex() != -1) {
            v1 = (int) view.getComboDado1().getSelectedItem();
            v2 = (int) view.getComboDado2().getSelectedItem();
        } else {
            // Valores aleatórios
            dado.rolar();
            v1 = dado.getValorA();
            v2 = dado.getValorB();
        }

        // Atualiza o model
        dado.setValorA(v1);
        dado.setValorB(v2);

        // Atualiza a view
        view.atualizarView(v1, v2);

        System.out.println("Dados lançados: " + v1 + " + " + v2 + " = " + (v1 + v2));

        // Dispara callback (movimento do pião)
        if (callback != null) {
            callback.accept(new int[]{v1, v2});
        }
    }

    public int getValorA() { return dado.getValorA(); }
    public int getValorB() { return dado.getValorB(); }
}
