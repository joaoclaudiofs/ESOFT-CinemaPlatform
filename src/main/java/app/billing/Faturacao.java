package app.billing;

import app.Menu;
import models.AppData;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Faturacao extends JFrame {
    private JButton bebidasButton;
    private JButton outrosButton;
    private JButton bilhetesButton;
    private JButton produtosButton;
    private JList<Object> list1;
    private JButton cancelarButton;
    private JButton confirmarButton;
    private JPanel panel1;
    private JButton removerButton;
    private JLabel price;
    private JButton voltarButton;

    private DefaultListModel<Object> listModel = new DefaultListModel<>();

    public Faturacao() {
        setTitle("Faturação");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(panel1);

        list1.setModel(listModel);

        list1.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value instanceof Ticket) {
                Ticket t = (Ticket) value;
                label.setText("€" + String.format("%.2f", t.getPrice()) + " - " + t.toString());
            } else if (value instanceof SaleLine) {
                SaleLine<?> sl = (SaleLine<?>) value;
                label.setText(sl.getProduct().toString() + " x" + sl.getQuantity() + " |€" + String.format("%.2f", sl.getPrice()));
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });

        bilhetesButton.addActionListener(this::openBilheteOpções);
        removerButton.addActionListener(this::removerSelecionado);
        confirmarButton.addActionListener(this::confirmarVenda);
        cancelarButton.addActionListener(this::cancelarVenda);
        produtosButton.addActionListener(e -> {
            ProdutosOpções produtosOpções = new ProdutosOpções(this::adicionarSaleLine);
            produtosOpções.setVisible(true);
        });
        voltarButton.addActionListener(this::voltar);

        atualizarTotal();
    }

    private void voltar(ActionEvent actionEvent) {
        int confirm = JOptionPane.showConfirmDialog(this, "Voltar ao menu principal?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Menu().setVisible(true);
            dispose();
        }
    }

    private void adicionarSaleLine(SaleLine<Product> saleLine) {
        listModel.addElement(saleLine);
        atualizarTotal();
    }

    private void adicionarItem(Ticket ticket) {
        listModel.addElement(ticket);
        atualizarTotal();
    }

    private void openBilheteOpções(ActionEvent e) {
        BilheteOpções bilheteOpções = new BilheteOpções(this::adicionarItem);
        bilheteOpções.setVisible(true);
    }

    private void removerSelecionado(ActionEvent e) {
        int selectedIndex = list1.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            atualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void confirmarVenda(ActionEvent e) {
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item para vender.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<SaleLine<?>> saleLines = new ArrayList<>();
        StringBuilder faturaTexto = new StringBuilder("Fatura:\n");
        AppData appData = AppData.getInstance();

        for (int i = 0; i < listModel.size(); i++) {
            Object item = listModel.getElementAt(i);

            if (item instanceof Ticket ticket) {
                faturaTexto.append("- ").append(ticket).append(" |€").append(String.format("%.2f", ticket.getPrice())).append("\n");
                saleLines.add(new SaleLine<>(ticket, 1, ticket.getPrice()));
            } else if (item instanceof SaleLine<?> sl) {
                faturaTexto.append("- ").append(sl.getProduct()).append(" x").append(sl.getQuantity()).append(" |€").append(String.format("%.2f", sl.getPrice())).append("\n");
                saleLines.add(sl);
            }
        }

        Receipt receipt = new Receipt(saleLines.toArray(new SaleLine[0]), new Date());
        appData.addReceipt(receipt);
        AppData.saveData();

        JOptionPane.showMessageDialog(this, "Venda realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        listModel.clear();
        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = 0.0;
        for (int i = 0; i < listModel.size(); i++) {
            Object item = listModel.getElementAt(i);
            if (item instanceof Ticket) {
                total += ((Ticket) item).getPrice();
                if(((Ticket)item).getCombo() != null) {
                    total += ((Ticket) item).getCombo().getPrice();
                }
            } else if (item instanceof SaleLine<?>) {
                total += ((SaleLine<?>) item).getPrice();
            }
        }
        price.setText(String.format("Total: %.2f €", total));
    }

    private void cancelarVenda(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Cancelar a venda atual?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listModel.clear();
            atualizarTotal();
            new Menu().setVisible(true);
            dispose();
        }
    }
}
