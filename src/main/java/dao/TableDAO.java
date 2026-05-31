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

    // =================================================================
    // PHẦN 1: CODE CỦA AN (LUỒNG ĐẶT BÀN & GỌI MÓN)
    // =================================================================

    // 1. Tìm kiếm bàn trống (Hỗ trợ ghép bàn & Chặn khoảng thời gian 75 phút)
    public ArrayList<Table> searchFreeTable(String date, String time, int quantity) {
        ArrayList<Table> list = new ArrayList<>();
        String sql = "SELECT * FROM tblTable WHERE id NOT IN (" +
                 "SELECT tblTableId FROM tblBookedTable bt " +
                 "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                 "WHERE b.bookDate = ? AND b.status IN (N'Chờ nhận bàn', N'Đã xác nhận') " +
                 "AND ABS(DATEDIFF(MINUTE, CAST(b.bookTime AS TIME), CAST(? AS TIME))) < 75)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, time); 
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                // Bổ sung đọc cột name (từ code của Lam)
                t.setName(rs.getString("name")); 
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

    // 2. Kiểm tra trạng thái 1 bàn cụ thể có trống hay không
    public boolean checkTableAvailability(int tableId, String date, String time) {
        boolean isAvailable = true;
        String sql = "SELECT COUNT(*) AS count FROM tblBookedTable bt " +
                     "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                     "WHERE bt.tblTableId = ? AND b.bookDate = ? AND b.status IN (N'Chờ nhận bàn', N'Đã xác nhận') " +
                     "AND ABS(DATEDIFF(MINUTE, CAST(b.bookTime AS TIME), CAST(? AS TIME))) < 75";
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

    // 3. Lấy danh sách các bàn ĐANG CÓ KHÁCH ngồi (Gộp chung logic với hàm getServingTables của Lam)
    public ArrayList<Table> getOccupiedTables() {
        ArrayList<Table> list = new ArrayList<>();
        String sql = "SELECT * FROM tblTable WHERE status = N'Đang phục vụ'";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                // Bổ sung đọc cột name
                t.setName(rs.getString("name")); 
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

    // =================================================================
    // PHẦN 2: CODE CỦA LAM (LUỒNG THANH TOÁN & QUẢN LÝ)
    // =================================================================

    // Lấy toàn bộ danh sách bàn
    public ArrayList<Table> getAllTables() {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable ORDER BY tableCode";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                t.setName(rs.getString("name"));
                t.setCapacity(rs.getInt("capacity"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật trạng thái của bàn theo ID
    public boolean updateTableStatus(int tableId, String status) {
        if (con == null) return false;
        String sql = "UPDATE tblTable SET status = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, tableId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm bàn theo mã bàn (tableCode)
    public Table getTableByCode(String tableCode) {
        if (con == null) return null;
        String sql = "SELECT * FROM tblTable WHERE tableCode = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tableCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                t.setName(rs.getString("name"));
                t.setCapacity(rs.getInt("capacity"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
