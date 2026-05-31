package dao;

import model.*;
import java.sql.*;

/**
 * DAO xử lý việc tạo hóa đơn thanh toán.
 * Sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu:
 * - Ghi hóa đơn vào tblBill
 * - Cập nhật trạng thái tblOrder sang "Đã thanh toán"
 */
public class BillDAO extends DAO {

    public BillDAO() {
        super();
    }

    public boolean createBill(Bill bill) {
        if (con == null) return false;
        boolean result = false;
        
        // FIX 1: Đảm bảo tên cột createTime ở đây khớp 100% với SQL Server
        String sqlBill = "INSERT INTO tblBill(createTime, totalAmount, paymentMethod, tblOrderID, tblUserID) "
                + "VALUES(?,?,?,?,?)";
        String sqlUpdateOrder = "UPDATE tblOrder SET isPaid = 1 WHERE id = ?";
        
        try {
            con.setAutoCommit(false);

            // Bước 1: Chèn hóa đơn vào tblBill
            PreparedStatement ps1 = con.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS);
            ps1.setTimestamp(1, new Timestamp(bill.getCreatedTime().getTime()));
            ps1.setDouble(2, bill.getTotalAmount());
            ps1.setString(3, bill.getPaymentMethod());
            ps1.setInt(4, bill.getOrder().getId());
            ps1.setInt(5, bill.getUser().getId());
            ps1.executeUpdate();

            // Lấy ID vừa được tạo
            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) {
                bill.setId(rs.getInt(1));
            }

            // Bước 2: Cập nhật trạng thái Order sang "Đã thanh toán"
            PreparedStatement ps2 = con.prepareStatement(sqlUpdateOrder);
            ps2.setInt(1, bill.getOrder().getId());
            ps2.executeUpdate();

            con.commit();
            result = true;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public Bill getBillById(int billId) {
        if (con == null) return null;
        
        // FIX 2: Sửa u.fullName thành u.name cho khớp với CSDL của An
        String sql = "SELECT b.*, u.name AS staffName FROM tblBill b "
                + "JOIN tblUser u ON b.tblUserId = u.id WHERE b.id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                
                // FIX 1 (tiếp): Đổi thành createTime cho đồng bộ với câu INSERT ở trên
                bill.setCreatedTime(rs.getTimestamp("createTime"));
                bill.setTotalAmount(rs.getDouble("totalAmount"));
                bill.setPaymentMethod(rs.getString("paymentMethod"));

                User user = new User();
                user.setId(rs.getInt("tblUserId"));
                user.setName(rs.getString("staffName"));
                bill.setUser(user);

                Order order = new Order();
                order.setId(rs.getInt("tblOrderId"));
                bill.setOrder(order);
                
                return bill;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}