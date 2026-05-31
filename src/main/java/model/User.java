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

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String name;  // Giữ nguyên name của bạn để không hỏng giao diện
    private String role;
    private String phone;
    private String email; // Giữ nguyên email của bạn

    public User() {
        super();
    }

    // Bổ sung hàm khởi tạo từ Lam (đã sửa fullName thành name)
    public User(int id, String username, String name, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
    }

    // --- CÁC HÀM GETTER / SETTER GỐC CỦA BẠN ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Bổ sung hàm toString từ code của Lam để hỗ trợ Debug
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', name='" + name + "', role='" + role + "'}";
    }
}