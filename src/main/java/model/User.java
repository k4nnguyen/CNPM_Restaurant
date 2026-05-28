package model;

import java.io.Serializable;

/**
 * Lớp thực thể đại diện cho người dùng hệ thống (nhân viên / quản lý).
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role; // "Staff" hoặc "Manager"
    private String phone;

    public User() {
        super();
    }

    public User(int id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', fullName='" + fullName + "', role='" + role + "'}";
    }
}