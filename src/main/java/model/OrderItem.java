package model;

import java.io.Serializable;

/**
 * Lớp thực thể đại diện cho một dòng chi tiết trong đơn gọi món.
 * Mỗi OrderItem ứng với một Dish được gọi trong Order.
 */
public class OrderItem implements Serializable {
    private int id;
    private int quantity;
    private double unitPrice;
    private double temporaryAmount;
    private Dish dish;

    public OrderItem() {
        super();
    }

    public OrderItem(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
        this.unitPrice = dish.getPrice();
        this.temporaryAmount = quantity * unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTemporaryAmount() { return temporaryAmount; }
    public void setTemporaryAmount(double temporaryAmount) { this.temporaryAmount = temporaryAmount; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    @Override
    public String toString() {
        return "OrderItem{dish=" + (dish != null ? dish.getName() : "N/A")
                + ", quantity=" + quantity + ", unitPrice=" + unitPrice
                + ", temporaryAmount=" + temporaryAmount + "}";
    }
}
