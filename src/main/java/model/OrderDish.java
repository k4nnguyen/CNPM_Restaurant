/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.io.Serializable;
/**
 *
 * @author annguyen
 */
public class OrderDish implements Serializable {
    private int id;
    private int quantity;
    private double currentPrice; // Giữ nguyên tên chuẩn của bạn
    private Dish dish;

    public OrderDish() {
        super();
    }

    // Bổ sung hàm khởi tạo từ Lam (Đã sửa unitPrice thành currentPrice)
    public OrderDish(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
        this.currentPrice = dish.getPrice();
    }

    // --- CÁC HÀM GETTER / SETTER GỐC ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    // --- CÁC HÀM BỔ SUNG TỪ NHÁNH LAM ---
    
    /**
     * Hàm tính tổng tiền tạm thời cho món này (Biến đổi thuộc tính temporaryAmount của Lam thành hàm động)
     */
    public double getTemporaryAmount() {
        return this.quantity * this.currentPrice;
    }

    // Bổ sung hàm toString hỗ trợ in ra Console để Debug
    @Override
    public String toString() {
        return "OrderDish{dish=" + (dish != null ? dish.getName() : "N/A")
                + ", quantity=" + quantity 
                + ", currentPrice=" + currentPrice
                + ", temporaryAmount=" + getTemporaryAmount() + "}";
    }
}