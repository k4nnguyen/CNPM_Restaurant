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

    @Override
    public String toString() {
        return dishCode + " - " + name + " (" + category + ") - " + String.format("%,.0f VNĐ", price);
    }
}