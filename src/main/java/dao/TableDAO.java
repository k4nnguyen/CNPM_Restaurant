package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO xử lý truy vấn CSDL cho thực thể Table (bàn ăn).
 * Cung cấp các thao tác: lấy danh sách bàn đang phục vụ, cập nhật trạng thái bàn.
 */
public class TableDAO extends DAO {

    public TableDAO() {
        super();
    }

    /**
     * Lấy danh sách các bàn đang ở trạng thái "Đang phục vụ".
     * Dùng cho màn hình SelectTableToPayFrm.
     *
     * @return Danh sách bàn đang phục vụ.
     */
    public ArrayList<Table> getServingTables() {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable WHERE status = N'Đang phục vụ'";
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

    /**
     * Lấy toàn bộ danh sách bàn.
     *
     * @return Danh sách tất cả bàn.
     */
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

    /**
     * Cập nhật trạng thái của bàn theo ID.
     *
     * @param tableId ID của bàn cần cập nhật.
     * @param status  Trạng thái mới: "Trống", "Đang phục vụ", "Đã đặt trước".
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
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

    /**
     * Tìm bàn theo mã bàn (tableCode).
     *
     * @param tableCode Mã bàn cần tìm.
     * @return Đối tượng Table nếu tìm thấy, null nếu không.
     */
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
