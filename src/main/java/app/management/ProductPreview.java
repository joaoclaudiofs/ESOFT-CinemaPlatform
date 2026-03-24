package app.management;

import app.generics.ObjectPreview;
import models.*;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ProductPreview extends ObjectPreview {
    private JPanel panelMain;
    private JTextField fieldName;
    private JSpinner spinnerStock;
    private JTextField fieldPrice;
    private Product product;
    private ManagementMenu managementMenu;

    public ProductPreview(ManagementMenu managementMenu, PreviewType type, Product product) {
        super(
            type == PreviewType.VIEW
                ? "Ver Produto"
                : type == PreviewType.EDIT
                    ? "Editar Produto"
                    : "Criar Porduto",
            type == PreviewType.VIEW ? Button.BACK : Button.CANCEL,
            type != PreviewType.VIEW ? Button.SAVE : null);

        this.managementMenu = managementMenu;
        this.previewType = type;
        setMainPanel(panelMain);

        fieldPrice.setText("0");

        if (product != null) {
            this.product = product;
            super.object = product;

            fieldName.setText(product.getName());
            spinnerStock.setValue(product.getStock());
            fieldPrice.setText(String.valueOf(product.getPrice()));

            int associatedCombos = 0;
            for (Combo combo: AppData.getInstance().getCombos()) {
                if (combo.getProduct() == product) {
                    associatedCombos++;
                }
            }

            this.setFooterInfo(associatedCombos + " combo(s) associado(s).");
        }

        if (type == PreviewType.VIEW) {
            fieldName.setEnabled(false);
            spinnerStock.setEnabled(false);
            fieldPrice.setEnabled(false);
        }

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
        String name = fieldName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insita um nome válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int stock = 0;
        try {
            stock = Integer.parseInt(spinnerStock.getValue().toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira um stock válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (stock < 0) {
            JOptionPane.showMessageDialog(this, "O stock não pode ser negativo.", "Erro", JOptionPane.ERROR_MESSAGE);
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
            AppData.getInstance().addProduct(new Product(
                name,
                price,
                stock
            ));
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (product != null && previewType == PreviewType.EDIT) {
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);

            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }

        this.managementMenu.loadProducts();
        this.dispose();
    }
}