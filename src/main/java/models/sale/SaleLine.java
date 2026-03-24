package models.sale;

import java.io.Serializable;

public class SaleLine<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T product;
    private int quantity;
    private double price;

    public SaleLine(T product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public T getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return this.price;
    }
}
