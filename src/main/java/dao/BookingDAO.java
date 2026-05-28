/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.*;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author annguyen
 */
public class BookingDAO extends DAO {
    
    public BookingDAO() { 
        super(); 
    }

    // 1. Lưu thông tin đặt bàn mới (Sử dụng Transaction)
    public boolean addBooking(Booking b) {
        boolean result = false;
        String sqlBooking = "INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) VALUES(?,?,?,?,?,?)";
        String sqlBookedTable = "INSERT INTO tblBookedTable(isCheckedIn, tblBookingId, tblTableId) VALUES(?,?,?)";
        
        try {
            con.setAutoCommit(false); // Bắt đầu Transaction
            
            // 1. Insert Booking
            PreparedStatement ps1 = con.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS);
            ps1.setDate(1, new java.sql.Date(b.getBookDate().getTime()));
            ps1.setString(2, b.getBookTime());
            ps1.setInt(3, b.getQuantity());
            ps1.setString(4, Booking.STATUS_PENDING);
            ps1.setInt(5, b.getClient().getId());
            ps1.setInt(6, b.getUser().getId());
            ps1.executeUpdate();
            
            // Lấy ID vừa sinh ra
            ResultSet generatedKeys = ps1.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
                
                // 2. Insert các BookedTable
                PreparedStatement ps2 = con.prepareStatement(sqlBookedTable);
                for (BookedTable bt : b.getBookedTables()) {
                    ps2.setBoolean(1, false);
                    ps2.setInt(2, b.getId());
                    ps2.setInt(3, bt.getTable().getId());
                    ps2.executeUpdate();
                }
            }
            
            con.commit(); // Hoàn tất Transaction
            result = true;
        } catch (SQLException e) {
            try {
                con.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException ex) {
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
            }
        }
        return result;
    }

    // 2. Tìm kiếm phiếu đặt bàn theo số điện thoại khách hàng (Module Sửa đặt bàn)
    public ArrayList<Booking> searchBooking(String phone) {
        ArrayList<Booking> list = new ArrayList<>();
        // JOIN bảng tblBooking và tblClient để lấy được cả thông tin hóa đơn lẫn khách hàng
        String sql = "SELECT b.*, c.name, c.phone, c.email, c.address FROM tblBooking b "
                   + "JOIN tblClient c ON b.tblClientId = c.id "
                   + "WHERE c.phone LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + phone + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                
                // Đóng gói thông tin Client
                Client c = new Client();
                c.setId(rs.getInt("tblClientId"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                
                b.setClient(c);
                list.add(b);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 3. Cập nhật thông tin phiếu đặt bàn (Module Sửa đặt bàn)
    public boolean updateBooking(Booking b) {
        // Cập nhật ngày, giờ, số lượng dựa theo ID
        String sql = "UPDATE tblBooking SET bookDate = ?, bookTime = ?, quantity = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(b.getBookDate().getTime()));
            ps.setString(2, b.getBookTime());
            ps.setInt(3, b.getQuantity());
            ps.setInt(4, b.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
