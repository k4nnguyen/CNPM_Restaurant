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
    public boolean addOrder(model.Order order) {
        boolean result = false;
        
        // 1. Các câu lệnh SQL cần thiết
        String sqlCheckActiveOrder = "SELECT id FROM tblOrder WHERE tblTableId = ? AND isPaid = 0";
        String sqlCreateOrder = "INSERT INTO tblOrder(orderTime, tblUserId, tblTableId, isPaid) VALUES(?,?,?,0)";
        
        String sqlCheckDishExist = "SELECT quantity FROM tblOrderDish WHERE tblOrderId = ? AND tblDishId = ?";
        String sqlUpdateDish = "UPDATE tblOrderDish SET quantity = quantity + ? WHERE tblOrderId = ? AND tblDishId = ?";
        String sqlInsertDish = "INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) VALUES(?,?,?,?)";

        try {
            con.setAutoCommit(false); // Bắt đầu Transaction

            int orderId = -1;

            // BƯỚC 1: Kiểm tra bàn này đã có Order nào đang mở chưa?
            PreparedStatement psCheckOrder = con.prepareStatement(sqlCheckActiveOrder);
            psCheckOrder.setInt(1, order.getTable().getId());
            ResultSet rsOrder = psCheckOrder.executeQuery();

            if (rsOrder.next()) {
                // Đã có khách ngồi và đang gọi món dở -> Dùng lại Order ID cũ
                orderId = rsOrder.getInt("id");
            } else {
                // Khách mới toanh -> Tạo Order mới
                PreparedStatement psCreateOrder = con.prepareStatement(sqlCreateOrder, Statement.RETURN_GENERATED_KEYS);
                psCreateOrder.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                psCreateOrder.setInt(2, order.getUser().getId());
                psCreateOrder.setInt(3, order.getTable().getId());
                psCreateOrder.executeUpdate();

                ResultSet generatedKeys = psCreateOrder.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }

            // BƯỚC 2: Xử lý danh sách món ăn (Cộng dồn hoặc Thêm mới)
            if (orderId != -1) {
                PreparedStatement psCheckDish = con.prepareStatement(sqlCheckDishExist);
                PreparedStatement psUpdateDish = con.prepareStatement(sqlUpdateDish);
                PreparedStatement psInsertDish = con.prepareStatement(sqlInsertDish);

                for (model.OrderDish od : order.getOrderDishes()) {
                    // Kiểm tra món này đã có trong bill chưa
                    psCheckDish.setInt(1, orderId);
                    psCheckDish.setInt(2, od.getDish().getId());
                    ResultSet rsDish = psCheckDish.executeQuery();

                    if (rsDish.next()) {
                        // Món đã có -> UPDATE cộng dồn số lượng
                        psUpdateDish.setInt(1, od.getQuantity()); // Số lượng gọi thêm
                        psUpdateDish.setInt(2, orderId);
                        psUpdateDish.setInt(3, od.getDish().getId());
                        psUpdateDish.executeUpdate();
                    } else {
                        // Món mới toanh -> INSERT
                        psInsertDish.setInt(1, od.getQuantity());
                        psInsertDish.setDouble(2, od.getCurrentPrice());
                        psInsertDish.setInt(3, orderId);
                        psInsertDish.setInt(4, od.getDish().getId());
                        psInsertDish.executeUpdate();
                    }
                }
            }

            con.commit(); // Hoàn tất Transaction
            result = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}