/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Dish;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author annguyen
 */
public class DishDAO extends DAO {
    public DishDAO() { 
        super(); 
    }

    // Tìm kiếm món ăn theo tên
    public ArrayList<Dish> searchDish(String keyword) {
        ArrayList<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM tblDish WHERE name LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setDishCode(rs.getString("dishCode"));
                d.setCategory(rs.getString("category"));
                d.setName(rs.getString("name"));
                d.setPrice(rs.getDouble("price"));
                list.add(d);
            }
        } catch (SQLException e) { 
            e.printStackTrace();
        }
        return list;
    }
}
