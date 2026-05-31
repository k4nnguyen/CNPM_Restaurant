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
public class Dish implements Serializable {
    private int id;
    private String dishCode;
    private String category;
    private String name;
    private double price;
    
    // Bổ sung từ code của Lam
    private String description;
    private boolean available;

    public Dish() {
        super();
        this.available = true; // Mặc định tạo món mới là CÒN HÀNG
    }

    // Bổ sung hàm khởi tạo có tham số từ nhánh Lam
    public Dish(int id, String dishCode, String name, String category, double price) {
        this.id = id;
        this.dishCode = dishCode;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = true;
    }

    // --- CÁC HÀM GETTER / SETTER GỐC CỦA BẠN ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDishCode() { return dishCode; }
    public void setDishCode(String dishCode) { this.dishCode = dishCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // --- CÁC HÀM GETTER / SETTER MỚI BỔ SUNG ---
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // Bổ sung hàm toString từ Lam (Có format tiền tệ rất đẹp)
    @Override
    public String toString() {
        return dishCode + " - " + name + " (" + category + ") - " + String.format("%,.0f VNĐ", price);
    }
}