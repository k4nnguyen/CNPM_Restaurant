/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Client;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author annguyen
 */
public class ClientDAO extends DAO {
    public ClientDAO() {
        super();
    }

    // Cập nhật hàm searchClient nhận 2 tham số
    public ArrayList<Client> searchClient(String name, String phone) {
        ArrayList<Client> list = new ArrayList<>();
        
        // Dùng mẹo 1=1 để dễ dàng nối thêm các điều kiện phía sau bằng chữ AND
        String sql = "SELECT * FROM tblClient WHERE 1=1";
        
        if (name != null && !name.isEmpty()) {
            sql += " AND name LIKE ?"; // Tìm gần đúng theo tên
        }
        if (phone != null && !phone.isEmpty()) {
            sql += " AND phone LIKE ?"; // Tìm gần đúng theo sđt
        }

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            // Đổ dữ liệu vào các dấu chấm hỏi (?) một cách linh hoạt
            int paramIndex = 1;
            if (name != null && !name.isEmpty()) {
                ps.setString(paramIndex++, "%" + name + "%");
            }
            if (phone != null && !phone.isEmpty()) {
                ps.setString(paramIndex++, "%" + phone + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("ID"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm khách hàng mới
    public boolean addClient(Client c) {
        String sql = "INSERT INTO tblClient(name, phone, email, address) VALUES(?,?,?,?)";
        try {
            // Thêm tham số RETURN_GENERATED_KEYS để lấy ID vừa tự tăng
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.executeUpdate();
            
            // Cập nhật lại ID cho đối tượng Client trên RAM để dùng tiếp cho Booking
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                c.setId(generatedKeys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
