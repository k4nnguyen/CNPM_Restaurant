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
public class Table implements Serializable{
    public static final String STATUS_EMPTY = "Trống";
    public static final String STATUS_SERVING = "Đang phục vụ";

    private int id;
    private String tableCode;
    private String name;
    private int capacity;
    private String description;
    private String status;
    private boolean active = true;

    public Table(){super();}
    
    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTableCode() { return tableCode; }
    public void setTableCode(String tableCode) { this.tableCode = tableCode; }
    public String getCode() { return tableCode; }
    public void setCode(String code) { this.tableCode = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getMaxNumberOfClients() { return capacity; }
    public void setMaxNumberOfClients(int maxNumberOfClients) { this.capacity = maxNumberOfClients; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
