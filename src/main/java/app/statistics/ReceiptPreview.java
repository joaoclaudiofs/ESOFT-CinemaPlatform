package app.statistics;

import models.AppData;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReceiptPreview extends JFrame {

    private JLabel lbDate;
    private JLabel lbTotal;
    private JList listProducts;
    private JPanel panel1;
    private JButton btnRefund;

    public ReceiptPreview(Receipt receipt) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 300);
        this.setMinimumSize(new Dimension(400, 300));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(panel1);

        lbDate.setText(receipt.getDate().toString());
        lbTotal.setText(String.valueOf(receipt.getPrice()) + "€");

        btnRefund.addActionListener(e -> {
            refund(receipt);
        });

        DefaultListModel<String> model = new DefaultListModel<>();
        listProducts.setModel(model);

        SaleLine[] sales = receipt.getSales();
        for (SaleLine sale : sales) {
            String str = "";
            boolean skip = false;

            Object item = sale.getProduct();
            if (item instanceof Product) {
                str += ((Product) item).getName() + " x" + sale.getQuantity();
            } else if (item instanceof Combo) {
                str += "Combo";
            } else if (item instanceof Ticket) {
                str += "Bilhete " + ((Ticket) item).getType().getName();
                if (((Ticket) item).getState() == Ticket.State.REFUNDED) {
                    str += " (Reembolsado)";
                }
            } else {
                str += item;
                skip = true;
            }

            if (!skip) {
                str += " " + sale.getPrice() + "€";
            }
            model.addElement(str);
        }
    }

    void refund(Receipt receipt) {
        int selectedIndex = listProducts.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um bilhete para reembolsar.");
            return;
        }

        Object item = receipt.getSales()[selectedIndex].getProduct();
        if (item instanceof Ticket) {
            refundTicket((Ticket) item, true);
        /*} else if (item instanceof Combo) {
            refundCombo((Combo) item);*/
        } else {
            JOptionPane.showMessageDialog(null, "Apenas é possivel reembolsar bilhetes.");
        }
    }

    /*void refundCombo(Combo combo) {
        Ticket ticket = combo.getTicket();
        refundTicket(ticket, true);
    }*/

    public static void refundTicket(Ticket ticket, Boolean reply) {
        AppData appData = AppData.getInstance();

        LocalDateTime sessionDate = ticket.getSession().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        if (sessionDate.minusHours((long) appData.getRefundLimitHours()).isBefore(now)) {
            if (reply) {
                JOptionPane.showMessageDialog(null, "Já é demasiado tarde para reembolsar este bilhete.");
            }
            return;
        }

        if (ticket.getState() == Ticket.State.REFUNDED) {
            if (reply) {
                JOptionPane.showMessageDialog(null, "Este bilhete já foi reembolsado anteriormente");
            }
            return;
        }

        ticket.setState(Ticket.State.REFUNDED);
        if (reply) {
            JOptionPane.showMessageDialog(null, "Bilhete reembolsado com sucesso");
        }
    }
}
