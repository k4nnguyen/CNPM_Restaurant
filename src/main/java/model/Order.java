package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Lớp thực thể đại diện cho đơn đặt món (order) tại bàn.
 * Mỗi Order gắn với một Table và một User (nhân viên phục vụ).
 */
public class Order implements Serializable {
    public static final String STATUS_UNPAID = "Chưa thanh toán";
    public static final String STATUS_PAID = "Đã thanh toán";

    private int id;
    private Date orderTime;
    private double totalAmount;
    private String status;
    private User user;
    private Table table;
    private ArrayList<OrderDish> orderDishes;

    public Order() {
        super();
        this.orderDishes = new ArrayList<OrderDish>();
    }

    public void addOrderDish(OrderDish od) {
        this.orderDishes.add(od);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public ArrayList<OrderDish> getOrderDishes() { return orderDishes; }
    public void setOrderDishes(ArrayList<OrderDish> orderDishes) { this.orderDishes = orderDishes; }

    /**
     * Tính lại tổng tiền dựa trên danh sách các món đã gọi.
     * (Đã sửa đổi logic để tương thích với class OrderDish)
     */
    public void recalculateTotal() {
        double total = 0;
        for (OrderDish od : orderDishes) {
            double subTotal = od.getQuantity() * od.getCurrentPrice();
            total += subTotal;
        }
        this.totalAmount = total;
    }

    /**
     * Hỗ trợ in nhanh thông tin Hóa đơn ra màn hình Console để Debug
     */
    @Override
    public String toString() {
        return "Order{id=" + id + ", table=" + (table != null ? table.getTableCode() : "N/A")
                + ", status='" + status + "', totalAmount=" + totalAmount + "}";
    }
}
