package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class TableDAO extends DAO {
    
    public TableDAO() { 
        super(); 
    }

    private String getSafeName(ResultSet rs, String tableCode) {
        try {
            String name = rs.getString("name");
            return name != null ? name : tableCode;
        } catch (SQLException e) {
            return tableCode;
        }
    }

    // =================================================================
    // PHẦN 1: CODE CỦA AN (LUỒNG ĐẶT BÀN & GỌI MÓN)
    // =================================================================

    // 1. Tìm kiếm bàn trống (Hỗ trợ ghép bàn & Chặn khoảng thời gian 75 phút)
    public ArrayList<Table> searchFreeTable(String date, String time, int quantity) {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable WHERE id NOT IN (" +
                 "SELECT tblTableId FROM tblBookedTable bt " +
                 "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                 "WHERE b.bookDate = ? AND b.status IN (N'Ch\u1edd nh\u1eadn b\u00e0n', N'\u0110\u00e3 x\u00e1c nh\u1eadn') " +
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
                t.setName(getSafeName(rs, t.getTableCode())); 
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
        if (con == null) return false;
        boolean isAvailable = true;
        String sql = "SELECT COUNT(*) AS count FROM tblBookedTable bt " +
                     "JOIN tblBooking b ON bt.tblBookingId = b.id " +
                     "WHERE bt.tblTableId = ? AND b.bookDate = ? AND b.status IN (N'Ch\u1edd nh\u1eadn b\u00e0n', N'\u0110\u00e3 x\u00e1c nh\u1eadn') " +
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

    // 3. Lấy danh sách các bàn ĐANG CÓ KHÁCH ngồi
    public ArrayList<Table> getOccupiedTables() {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable WHERE status = N'\u0110ang ph\u1ee5c v\u1ee5'";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setId(rs.getInt("id"));
                t.setTableCode(rs.getString("tableCode"));
                t.setName(getSafeName(rs, t.getTableCode())); 
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

    // Tương thích ngược với tên gọi getServingTables của nhánh Lam cũ
    public ArrayList<Table> getServingTables() {
        return getOccupiedTables();
    }

    // =================================================================
    // PHẦN 2: CODE CỦA LAM (LUỒNG THANH TOÁN & QUẢN LÝ)
    // =================================================================

    // Lấy toàn bộ danh sách bàn
    public ArrayList<Table> getAllTables() {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable WHERE isActive = 1 ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
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
        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM tblTable WHERE tableCode = ? AND isActive = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tableCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return readTable(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Table> searchTables(String keyword) {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblTable WHERE (tableCode LIKE ? OR name LIKE ?) AND isActive = 1 ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addTable(Table table) {
        if (con == null || table == null || !isValidTable(table) || isTableCodeExists(table.getTableCode())) {
            return false;
        }
        String sql = "INSERT INTO tblTable(tableCode, name, capacity, description, status, isActive) VALUES(?,?,?,?,?,1)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, table.getTableCode().trim());
            ps.setString(2, table.getName().trim());
            ps.setInt(3, table.getCapacity());
            ps.setString(4, emptyToNull(table.getDescription()));
            ps.setString(5, isBlank(table.getStatus()) ? Table.STATUS_EMPTY : table.getStatus().trim());
            int affected = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    table.setId(keys.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTable(Table table) {
        if (con == null || table == null || table.getId() <= 0 || !isValidTable(table)) {
            return false;
        }
        String sql = "UPDATE tblTable SET name = ?, capacity = ?, description = ?, status = ? WHERE id = ? AND isActive = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, table.getName().trim());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, emptyToNull(table.getDescription()));
            ps.setString(4, isBlank(table.getStatus()) ? Table.STATUS_EMPTY : table.getStatus().trim());
            ps.setInt(5, table.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTable(Table table) {
        return table != null && deleteTable(table.getId());
    }

    public boolean deleteTable(int id) {
        return deleteOrDeactivateTable(id);
    }

    public boolean deleteOrDeactivateTable(int id) {
        if (con == null || id <= 0) {
            return false;
        }
        try {
            con.setAutoCommit(false);
            boolean related = hasTableRelatedBusiness(id);
            String sql = related
                    ? "UPDATE tblTable SET isActive = 0 WHERE id = ? AND isActive = 1"
                    : "DELETE FROM tblTable WHERE id = ? AND isActive = 1";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean success = ps.executeUpdate() > 0;
                con.commit();
                return success;
            }
        } catch (SQLException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            restoreAutoCommit();
        }
    }

    public boolean isTableCodeExists(String code) {
        if (con == null || isBlank(code)) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM tblTable WHERE tableCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasTableRelatedBusiness(int id) {
        if (con == null) return true;
        return hasRelatedRows("tblBookedTable", "tblTableId", id)
                || hasRelatedRows("tblOrder", "tblTableId", id);
    }

    public boolean checkTableCode(String code) {
        return isTableCodeExists(code);
    }

    private boolean hasRelatedRows(String tableName, String columnName, int id) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private Table readTable(ResultSet rs) throws SQLException {
        Table t = new Table();
        t.setId(rs.getInt("id"));
        t.setTableCode(rs.getString("tableCode"));
        t.setName(getSafeName(rs, t.getTableCode()));
        t.setCapacity(rs.getInt("capacity"));
        t.setDescription(rs.getString("description"));
        t.setStatus(rs.getString("status"));
        try {
            t.setActive(rs.getBoolean("isActive"));
        } catch (SQLException ignored) {}
        return t;
    }

    private boolean isValidTable(Table table) {
        return table != null
                && !isBlank(table.getTableCode())
                && !isBlank(table.getName())
                && table.getCapacity() > 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private void rollback() {
        try {
            con.rollback();
        } catch (SQLException ignored) {}
    }

    private void restoreAutoCommit() {
        try {
            con.setAutoCommit(true);
        } catch (SQLException ignored) {}
    }
}
