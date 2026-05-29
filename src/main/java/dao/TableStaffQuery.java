package dao;

import model.Table;
import java.sql.*;
import java.util.ArrayList;

class TableStaffQuery {
    private static final String STATUS_PENDING = "Chờ nhận bàn";

    private TableStaffQuery() {
    }

    static ArrayList<Table> searchFreeTable(Connection con, String date, String time, int quantity) {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) {
            return list;
        }
        String sql = "SELECT id, tableCode, name, capacity, description, status, isActive "
                + "FROM tblTable WHERE isActive = 1 AND capacity >= ? AND id NOT IN ("
                + "SELECT tblTableId FROM tblBookedTable bt "
                + "JOIN tblBooking b ON bt.tblBookingId = b.id "
                + "WHERE b.bookDate = ? AND b.bookTime = ? AND b.status = ?) "
                + "ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    static boolean checkTableAvailability(Connection con, int tableId, String date, String time) {
        if (con == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM tblBookedTable bt "
                + "JOIN tblBooking b ON bt.tblBookingId = b.id "
                + "WHERE bt.tblTableId = ? AND b.bookDate = ? AND b.bookTime = ? AND b.status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            return false;
        }
    }

    static ArrayList<Table> getOccupiedTables(Connection con) {
        ArrayList<Table> list = new ArrayList<>();
        if (con == null) {
            return list;
        }
        String sql = "SELECT id, tableCode, name, capacity, description, status, isActive "
                + "FROM tblTable WHERE isActive = 1 AND status = ? ORDER BY tableCode";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Table.STATUS_SERVING);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readTable(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    private static Table readTable(ResultSet rs) throws SQLException {
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
}
