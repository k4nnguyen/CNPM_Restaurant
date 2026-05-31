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
public class Table implements Serializable {
    private int id;
    private String tableCode;
    private String name; // Bổ sung từ code của Lam
    private int capacity;
    private String description;
    private String status;

    public Table() {
        super();
    }

    // Bổ sung hàm khởi tạo có tham số từ Lam (Phòng trường hợp nhánh Lam có dùng đến)
    public Table(int id, String tableCode, String name, int capacity, String status) {
        this.id = id;
        this.tableCode = tableCode;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    // --- CÁC HÀM GETTER / SETTER GỐC ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTableCode() { return tableCode; }
    public void setTableCode(String tableCode) { this.tableCode = tableCode; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- CÁC HÀM GETTER / SETTER MỚI BỔ SUNG ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Bổ sung hàm toString hỗ trợ hiển thị UI và Debug
    @Override
    public String toString() {
        return tableCode + " - " + name + " (Sức chứa: " + capacity + ") - " + status;
    }
}