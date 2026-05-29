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
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";

    private int id;
    private String dishCode;
    private String category;
    private String name;
    private String description;
    private double price;
    private String status = STATUS_ACTIVE;

    public Dish() {
        super();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDishCode() { return dishCode; }
    public void setDishCode(String dishCode) { this.dishCode = dishCode; }
    public String getCode() { return dishCode; }
    public void setCode(String code) { this.dishCode = code; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getCurrentPrice() { return price; }
    public void setCurrentPrice(double currentPrice) { this.price = currentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
