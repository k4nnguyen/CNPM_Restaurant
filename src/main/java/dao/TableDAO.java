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
public class TableDAO extends DAO {
    
    public TableDAO() { 
        super(); 
    }

    // 1. Tìm kiếm bàn trống (Module 1: Đặt bàn)
    public ArrayList<Table> searchFreeTable(String date, String time, int quantity) {
        ArrayList<Table> list = new ArrayList<>();
        // Cập nhật SQL: Lọc trực tiếp status = 'Trống' và kiểm tra không bị trùng lịch đặt trước
        String sql = "SELECT * FROM tblTable WHERE status = N'Trống' AND id NOT IN (" +
                     "SELECT tblTableId FROM tblBookedTable bt " +
                     "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                     "WHERE b.bookDate = ? AND b.bookTime = ? AND b.status IN (N'Pending', N'Confirmed'))";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            // Vì đã bỏ capacity, ta chỉ truyền tham số ngày và giờ (Index 1 và 2)
            ps.setString(1, date);
            ps.setString(2, time);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                t.setCapacity(rs.getInt("capacity"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                list.add(t);
            }
        } catch (SQLException e) {             
            e.printStackTrace(); 
        }
        return list;
    }

    // 2. Kiểm tra trạng thái 1 bàn cụ thể có trống hay không (Module 2: Sửa đặt bàn)
    public boolean checkTableAvailability(int tableId, String date, String time) {
        boolean isAvailable = true;
        // Logic: Đếm số lượng phiếu đặt bàn bị trùng lịch của bàn này. 
        // Nếu count > 0 nghĩa là bàn đã bị kẹt lịch -> false.
        String sql = "SELECT COUNT(*) AS count FROM tblBookedTable bt " +
                     "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                     "WHERE bt.tblTableId = ? AND b.bookDate = ? AND b.bookTime = ? AND b.status IN (N'Pending', N'Confirmed')";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, tableId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    isAvailable = false; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAvailable;
    }

    // 3. Lấy danh sách các bàn ĐANG CÓ KHÁCH ngồi (Module 3: Gọi món)
    public ArrayList<Table> getOccupiedTables() {
        ArrayList<Table> list = new ArrayList<>();
        // Thêm N'' để đảm bảo đọc đúng tiếng Việt có dấu trong SQL Server
        String sql = "SELECT * FROM tblTable WHERE status = N'Đang phục vụ'";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                t.setCapacity(rs.getInt("capacity"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

