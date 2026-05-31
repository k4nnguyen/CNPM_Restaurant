package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Lớp thực thể đại diện cho hóa đơn thanh toán.
 * Mỗi Bill gắn với một Order và một User (nhân viên tạo hóa đơn).
 */
public class Bill implements Serializable {
    private int id;
    private Date createdTime;
    private double totalAmount;
    private String paymentMethod; // "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"
    private Order order;
    private User user;

    public Bill() {
        super();
    }

    public Bill(Order order, User user, String paymentMethod) {
        this.order = order;
        this.user = user;
        this.paymentMethod = paymentMethod;
        this.totalAmount = order.getTotalAmount();
        this.createdTime = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Bill{id=" + id + ", totalAmount=" + totalAmount
                + ", paymentMethod='" + paymentMethod + "', createdTime=" + createdTime + "}";
    }
}
