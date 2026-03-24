package tests;

import models.AppData;
import models.sale.SaleLine;
import models.sale.sellable.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.billing.ProdutosOpções;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {

    private AppData appData;

    @BeforeEach
    void setup() {
        appData = AppData.getInstance();
        appData.getProducts().clear();

        appData.getProducts().add(new Product("Pipocas Pequenas", 2.0F));
        appData.getProducts().add(new Product("Coca cola", 3.5F));
        appData.getProducts().add(new Product("Chocolate", 1.5F));
    }

    @Test
    void testAdicionarProduto() {
        AtomicReference<SaleLine<Product>> callbackResult = new AtomicReference<>();

        ProdutosOpções produtosOpções = new ProdutosOpções(callbackResult::set);


        produtosOpções.list1.setSelectedIndex(0);
        Product produtoSelecionado = appData.getProducts().get(0);
        int quantidade = 3;
        double total = produtoSelecionado.getPrice() * quantidade;

        SaleLine<Product> linha = new SaleLine<>(produtoSelecionado, quantidade, total);
        produtosOpções.callback.accept(linha);

        SaleLine<Product> resultado = callbackResult.get();
        assertNotNull(resultado);
        assertEquals("Popcorn", resultado.getProduct().getName());
        assertEquals(3, resultado.getQuantity());
        assertEquals(15.0, resultado.getPrice(), 0.01);
    }

}
