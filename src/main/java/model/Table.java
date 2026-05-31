package model;

import java.io.Serializable;

/**
 * Lớp thực thể đại diện cho bàn ăn trong nhà hàng.
 */
public class Table implements Serializable {
    private int id;
    private String tableCode;
    private String name;
    private int capacity;
    private String description;
    private String status; // "Trống", "Đang phục vụ", "Đã đặt trước"
    private boolean active = true;

    public static final String STATUS_EMPTY = "Trống";
    public static final String STATUS_SERVING = "Đang phục vụ";

    public Table() {
        super();
    }

    public Table(int id, String tableCode, String name, int capacity, String status) {
        this.id = id;
        this.tableCode = tableCode;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTableCode() { return tableCode; }
    public void setTableCode(String tableCode) { this.tableCode = tableCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCode() { return tableCode; }
    public void setCode(String code) { this.tableCode = code; }

    public int getMaxNumberOfClients() { return capacity; }
    public void setMaxNumberOfClients(int maxNumberOfClients) { this.capacity = maxNumberOfClients; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return tableCode + " - " + name + " (Sức chứa: " + capacity + ") - " + status;
    }
}