package models.sale;

import java.io.Serializable;
import java.util.Date;

public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;

    private SaleLine[] sales;
    private Date date;

    public Receipt(SaleLine[] sales, Date date) {
        this.sales = sales;
        this.date = date;
    }

    public float getPrice() {
        float price = 0;
        for (SaleLine sale: sales) {
            price += sale.getPrice();
        }
        return price;
    }

    public Date getDate() {
        return date;
    }

    public SaleLine[] getSales() {
        return sales;
    }
}
