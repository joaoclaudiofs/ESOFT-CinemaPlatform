package models.sale.sellable;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private float stock;
    private float price;

    public Product(String name, float price) {
        this(name, price, 0);
    }

    public Product(String name, float price, int stock) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public float getStock() {
        return stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void addStock(float quantity) {
        this.stock += quantity;
    }

    public void removeStock(float quantity) {
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return name + (stock > 0 ? " (" + (int) stock + " em stock)" : " (Sem stock)") + " - " + price + "€" ;
    }
}
