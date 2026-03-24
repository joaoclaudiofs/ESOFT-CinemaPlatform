package app.management;

import app.generics.ObjectPreview;
import models.*;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ComboPreview extends ObjectPreview {
    private JPanel panelMain;
    private JComboBox comboTicket;
    private JComboBox comboProduct;
    private JTextField fieldPrice;
    private ManagementMenu managementMenu;

    public ComboPreview(ManagementMenu managementMenu, PreviewType type, Combo combo) {
        super(
            type == PreviewType.VIEW
                ? "Ver Combo"
                : type == PreviewType.EDIT
                    ? "Editar Combo"
                    : "Criar Combo",
            type == PreviewType.VIEW ? Button.BACK : Button.CANCEL,
            type != PreviewType.VIEW ? Button.SAVE : null);

        this.managementMenu = managementMenu;
        this.previewType = type;
        setMainPanel(panelMain);

        for (Ticket.Type ticket: Ticket.Type.values()) {
            comboTicket.addItem(ticket);
        }
        comboTicket.setSelectedItem(null);

        for (Product product: AppData.getInstance().getProducts()) {
            comboProduct.addItem(product);
        }
        comboProduct.setSelectedItem(null);

        fieldPrice.setText("0");

        if (combo != null) {
            super.object = combo;

            comboTicket.setSelectedItem(combo.getTicket());
            comboProduct.setSelectedItem(combo.getProduct());
            fieldPrice.setText(String.valueOf(combo.getPrice()));
        }

        if (type == PreviewType.VIEW) {
            comboTicket.setEnabled(false);
            comboProduct.setEnabled(false);
            fieldPrice.setEnabled(false);
        }

        comboTicket.addActionListener(ActionListener -> {
            String selectedTicket = (String) comboTicket.getSelectedItem();
            if (selectedTicket != null && !selectedTicket.isEmpty()) {
                Product selectedProduct = (Product) comboProduct.getSelectedItem();

                List<Product> validProducts = AppData.getInstance().getProducts();
                for (Combo combo1: AppData.getInstance().getCombos()) {
                    if (combo1.getTicket().getName().equals(selectedTicket)) {
                        validProducts.remove(combo1.getProduct());
                    }
                }
                comboProduct.removeAllItems();
                for (Product product: validProducts) {
                    comboProduct.addItem(product);
                }
                comboProduct.setSelectedItem(null);
                if (validProducts.contains(selectedProduct)) {
                    comboProduct.setSelectedItem(selectedProduct);
                }
            }
        });

        this.setVisible(true);
    }

    @Override
    public void onBack(ActionEvent e) {
        this.dispose();
    }

    @Override
    public void onCancel(ActionEvent e) {
        this.dispose();
    }

    @Override
    public void onSave(ActionEvent ev) {
        Ticket.Type ticket = (Ticket.Type) comboTicket.getSelectedItem();
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Selecione um bilhete válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = (Product) comboProduct.getSelectedItem();
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float price = 0;
        try {
            price = Float.valueOf(fieldPrice.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira um preço válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (price < 0) {
            JOptionPane.showMessageDialog(this, "O preço não pode ser negativo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (previewType == PreviewType.CREATE) {
            AppData.getInstance().addCombo(new Combo(
                ticket,
                product,
                price
            ));
            JOptionPane.showMessageDialog(this, "Combo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }

        this.managementMenu.loadCombos();
        this.dispose();
    }
}