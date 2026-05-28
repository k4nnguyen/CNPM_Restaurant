package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Lớp thực thể đại diện cho đơn đặt món (order) tại bàn.
 * Mỗi Order gắn với một Table và một User (nhân viên phục vụ).
 */
public class Order implements Serializable {
    private int id;
    private Date orderTime;
    private double totalAmount;
    private String status; // "Chưa thanh toán", "Đã thanh toán"
    private Table table;
    private User user;
    private ArrayList<OrderItem> orderItems;

    public Order() {
        super();
        orderItems = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ArrayList<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(ArrayList<OrderItem> orderItems) { this.orderItems = orderItems; }

    /**
     * Tính lại tổng tiền dựa trên danh sách các món đã gọi.
     */
    public void recalculateTotal() {
        double total = 0;
        for (OrderItem item : orderItems) {
            item.setTemporaryAmount(item.getQuantity() * item.getUnitPrice());
            total += item.getTemporaryAmount();
        }
        this.totalAmount = total;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", table=" + (table != null ? table.getTableCode() : "N/A")
                + ", status='" + status + "', totalAmount=" + totalAmount + "}";
    }
}
