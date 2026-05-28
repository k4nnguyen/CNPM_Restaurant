/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.*;
import java.sql.*;
/**
 *
 * @author annguyen
 */
public class OrderDAO extends DAO {
    public OrderDAO() { super(); }

    // Lưu hóa đơn gọi món
    public boolean addOrder(Order o) {
        boolean result = false;
        // Câu lệnh SQL thêm vào bảng cha (tblOrder)
        String sqlOrder = "INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId) VALUES(?,?,?,?,?)";
        // Câu lệnh SQL thêm vào bảng con (tblOrderDish)
        String sqlOrderDish = "INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) VALUES(?,?,?,?)";
        
        try {
            con.setAutoCommit(false); // Bắt đầu Transaction
            
            // 1. Insert Order
            PreparedStatement ps1 = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            // Dùng Timestamp để lấy được cả ngày và giờ gọi món
            ps1.setTimestamp(1, new java.sql.Timestamp(o.getOrderTime().getTime()));
            ps1.setDouble(2, o.getTotalAmount());
            ps1.setString(3, Order.STATUS_UNPAID); // Mặc định là "Chưa thanh toán" khi mới gọi món
            ps1.setInt(4, o.getUser().getId());
            ps1.setInt(5, o.getTable().getId());
            ps1.executeUpdate();
            
            // Lấy ID tự tăng của bảng tblOrder vừa sinh ra
            ResultSet generatedKeys = ps1.getGeneratedKeys();
            if (generatedKeys.next()) {
                o.setId(generatedKeys.getInt(1));
                
                // 2. Insert các OrderDish (Chi tiết món ăn)
                PreparedStatement ps2 = con.prepareStatement(sqlOrderDish);
                for (OrderDish od : o.getOrderDishes()) {
                    ps2.setInt(1, od.getQuantity());
                    ps2.setDouble(2, od.getCurrentPrice()); // Lưu giá tại thời điểm gọi để không bị ảnh hưởng nếu sau này đổi giá menu
                    ps2.setInt(3, o.getId());
                    ps2.setInt(4, od.getDish().getId());
                    ps2.executeUpdate();
                }
            }
            
            con.commit(); // Hoàn tất Transaction
            result = true;
        } catch (SQLException e) {
            try {
                con.rollback(); // Hoàn tác (Rollback) nếu có bất kỳ lỗi nào xảy ra
            } catch (SQLException ex) { 
            }
        } finally {
            try {
                con.setAutoCommit(true); // Trả lại trạng thái auto commit mặc định cho connection
            } catch (SQLException ex) { 
            }
        }
        return result;
    }
}