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

    // Tìm kiếm khách hàng theo từ khóa (Tên hoặc Số điện thoại)
    public ArrayList<Client> searchClient(String keyword) {
        ArrayList<Client> list = new ArrayList<>();
        // Tìm tương đối (LIKE) trên cả 2 trường name và phone
        String sql = "SELECT * FROM tblClient WHERE name LIKE ? OR phone LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                list.add(c);
            }
        } catch (SQLException e) { 
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
