package model;

import java.io.Serializable;

/**
 * Lớp thực thể đại diện cho món ăn trong thực đơn nhà hàng.
 */
public class Dish implements Serializable {
    private int id;
    private String dishCode;
    private String name;
    private String category;
    private double price;
    private String description;
    private boolean available;
    
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    private String status = STATUS_ACTIVE;

    public Dish() {
        super();
        this.available = true;
    }

    public Dish(int id, String dishCode, String name, String category, double price) {
        this.id = id;
        this.dishCode = dishCode;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDishCode() { return dishCode; }
    public void setDishCode(String dishCode) { this.dishCode = dishCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getCode() { return dishCode; }
    public void setCode(String code) { this.dishCode = code; }

    public double getCurrentPrice() { return price; }
    public void setCurrentPrice(double currentPrice) { this.price = currentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return dishCode + " - " + name + " (" + category + ") - " + String.format("%,.0f VNĐ", price);
    }
}