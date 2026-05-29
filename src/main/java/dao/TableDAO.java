package dao;

import model.Table;
import java.sql.*;
import java.util.ArrayList;

public class TableDAO extends DAO {
    public TableDAO() {
        super();
    }

    public ArrayList<Table> getAllTables() {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) {
            return list;
        }
        String sql = "SELECT id, tableCode, name, capacity, description, status, isActive "
                + "FROM tblTable WHERE isActive = 1 ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    public ArrayList<Table> searchTables(String keyword) {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) {
            return list;
        }
        String sql = "SELECT id, tableCode, name, capacity, description, status, isActive "
                + "FROM tblTable WHERE isActive = 1 AND (tableCode LIKE ? OR name LIKE ?) "
                + "ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    public boolean addTable(Table table) {
        if (con == null) {
            return false;
        }
        if (!isValidTable(table) || isTableCodeExists(table.getTableCode())) {
            return false;
        }
        String sql = "INSERT INTO tblTable(tableCode, name, capacity, description, status, isActive) "
                + "VALUES(?,?,?,?,?,1)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, table.getTableCode().trim());
            ps.setString(2, table.getName().trim());
            ps.setInt(3, table.getCapacity());
            ps.setString(4, emptyToNull(table.getDescription()));
            ps.setString(5, isBlank(table.getStatus()) ? Table.STATUS_EMPTY : table.getStatus().trim());
            int affected = ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                table.setId(keys.getInt(1));
            }
            return affected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateTable(Table table) {
        if (con == null) {
            return false;
        }
        if (table == null || table.getId() <= 0 || !isValidTable(table)) {
            return false;
        }
        String sql = "UPDATE tblTable SET name = ?, capacity = ?, description = ?, status = ? "
                + "WHERE id = ? AND isActive = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, table.getName().trim());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, emptyToNull(table.getDescription()));
            ps.setString(4, isBlank(table.getStatus()) ? Table.STATUS_EMPTY : table.getStatus().trim());
            ps.setInt(5, table.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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
        if (con == null) {
            return false;
        }
        if (id <= 0) {
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
            return false;
        } finally {
            restoreAutoCommit();
        }
    }

    public boolean isTableCodeExists(String code) {
        if (con == null) {
            return false;
        }
        if (isBlank(code)) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM tblTable WHERE tableCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hasTableRelatedBusiness(int id) {
        if (con == null) {
            return true;
        }
        return hasRelatedRows("tblBookedTable", "tblTableId", id)
                || hasRelatedRows("tblOrder", "tblTableId", id);
    }

    public ArrayList<Table> searchFreeTable(String date, String time, int quantity) {
        return TableStaffQuery.searchFreeTable(con, date, time, quantity);
    }

    public boolean checkTableAvailability(int tableId, String date, String time) {
        return TableStaffQuery.checkTableAvailability(con, tableId, date, time);
    }

    public ArrayList<Table> getOccupiedTables() {
        return TableStaffQuery.getOccupiedTables(con);
    }

    public boolean checkTableCode(String code) {
        return isTableCodeExists(code);
    }

    private boolean hasRelatedRows(String tableName, String columnName, int id) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return true;
        }
    }

    private Table readTable(ResultSet rs) throws SQLException {
        Table t = new Table();
        t.setId(rs.getInt("id"));
        t.setTableCode(rs.getString("tableCode"));
        t.setName(rs.getString("name"));
        t.setCapacity(rs.getInt("capacity"));
        t.setDescription(rs.getString("description"));
        t.setStatus(rs.getString("status"));
        t.setActive(rs.getBoolean("isActive"));
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
        } catch (SQLException e) {
        }
    }

    private void restoreAutoCommit() {
        try {
            con.setAutoCommit(true);
        } catch (SQLException e) {
        }
    }
}
