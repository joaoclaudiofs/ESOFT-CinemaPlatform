package models.sale.sellable;

import java.io.Serializable;

public class Combo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Ticket.Type ticket;
    private Product product;
    private float price;

    public Combo(Ticket.Type ticket, Product product, float price) {
        this.ticket = ticket;
        this.product = product;
        this.price = price;
    }

    /*
    public Ticket getTicket() {
        return ticket;
    }
    */

    public Ticket.Type getTicket() {
        return ticket;
    }

    public float getPrice() {
        return price;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "Bilhete " + ticket.getName().toLowerCase() +
                " + " + product.getName() +
                " => +" + price;
    }
}
