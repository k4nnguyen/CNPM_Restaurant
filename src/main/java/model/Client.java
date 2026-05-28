package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Client implements Serializable {
    private int id;
    private String clientCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Client() {
        super();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFullName() { return name; }
    public void setFullName(String fullName) { this.name = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
