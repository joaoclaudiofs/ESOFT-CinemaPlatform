package app.billing;

import models.AppData;
import models.sale.SaleLine;
import models.sale.sellable.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProdutosOpções extends JFrame {
    private JPanel painel1;
    private JButton adicionarButton;
    public JList<String> list1;

    public final Consumer<SaleLine<Product>> callback;

    public ProdutosOpções(Consumer<SaleLine<Product>> callback) {
        this.callback = callback;

        setTitle("Selecionar Produtos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        painel1 = new JPanel(new BorderLayout());
        list1 = new JList<>();
        list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        painel1.add(new JScrollPane(list1), BorderLayout.CENTER);

        adicionarButton = new JButton("Adicionar");
        painel1.add(adicionarButton, BorderLayout.SOUTH);

        setContentPane(painel1);

        preencherListaProdutos();
        adicionarButton.addActionListener(this::adicionarProdutos);
    }

    private void preencherListaProdutos() {
        AppData appData = AppData.getInstance();
        DefaultListModel<String> model = new DefaultListModel<>();

        for (Product produto : appData.getProducts()) {
            model.addElement(produto.getName() + " - " + produto.getPrice() + "€");
        }

        list1.setModel(model);
    }

    private void adicionarProdutos(ActionEvent e) {
        String selecionado = list1.getSelectedValue();

        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Deve selecionar um produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomeProduto = selecionado.split(" - ")[0];
        AppData appData = AppData.getInstance();

        Product produto = appData.getProducts().stream()
                .filter(p -> p.getName().equals(nomeProduto))
                .findFirst()
                .orElse(null);

        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidade = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            String input = JOptionPane.showInputDialog(this, "Quantidade para " + nomeProduto + ":");
            if (input == null) return;

            try {
                quantidade = Integer.parseInt(input);
                if (quantidade > 0) {
                    entradaValida = true;
                } else {
                    JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que 0.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

        double totalProduto = produto.getPrice() * quantidade;
        SaleLine<Product> linhaVenda = new SaleLine<>(produto, quantidade, totalProduto);
        callback.accept(linhaVenda);
        dispose();
    }
}
