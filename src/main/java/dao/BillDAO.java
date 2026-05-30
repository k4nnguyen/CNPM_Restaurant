package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO xử lý việc tạo hóa đơn thanh toán và thống kê hóa đơn.
 */
public class BillDAO extends DAO {
    public BillDAO() {
        super();
    }

    /**
     * Tạo hóa đơn mới và cập nhật trạng thái đơn đặt món.
     * Thực hiện trong một transaction để đảm bảo tính nhất quán dữ liệu.
     *
     * @param bill Đối tượng Bill cần lưu vào CSDL.
     * @return true nếu tạo hóa đơn thành công, false nếu có lỗi.
     */
    public boolean createBill(Bill bill) {
        boolean result = false;
        String sqlBill = "INSERT INTO tblBill(createdTime, totalAmount, paymentMethod, tblOrderId, tblUserId) "
                + "VALUES(?,?,?,?,?)";
        String sqlUpdateOrder = "UPDATE tblOrder SET status = N'Đã thanh toán' WHERE id = ?";
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

    /**
     * Lấy hóa đơn theo ID.
     *
     * @param billId ID của hóa đơn cần tìm.
     * @return Đối tượng Bill nếu tìm thấy, null nếu không.
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT b.*, u.fullName AS staffName FROM tblBill b "
                + "JOIN tblUser u ON b.tblUserId = u.id WHERE b.id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setCreatedTime(rs.getTimestamp("createdTime"));
                bill.setTotalAmount(rs.getDouble("totalAmount"));
                bill.setPaymentMethod(rs.getString("paymentMethod"));

                User user = new User();
                user.setId(rs.getInt("tblUserId"));
                user.setFullName(rs.getString("staffName"));
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

    // --- CÁC PHƯƠNG THỨC THỐNG KÊ (MANAGER MODULE) ---

    public ArrayList<Bill> getBillsByDateRange(String startDate, String endDate) {
        ArrayList<Bill> list = new ArrayList<>();
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return list;
        }
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ORDER BY bl.paymentDate ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Bill> getBillsByTimeFrame(String timeFrame, String startDate, String endDate) {
        ArrayList<Bill> list = new ArrayList<>();
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return list;
        }
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ";
        
        if (timeFrame.equals("11:00-13:00")) {
            sql += "AND bl.paymentTime BETWEEN '11:00:00' AND '13:00:00' ";
        } else if (timeFrame.equals("18:00-20:00")) {
            sql += "AND bl.paymentTime BETWEEN '18:00:00' AND '20:00:00' ";
        } else {
            sql += "AND bl.paymentTime NOT BETWEEN '11:00:00' AND '13:00:00' AND bl.paymentTime NOT BETWEEN '18:00:00' AND '20:00:00' ";
        }
        sql += "ORDER BY bl.paymentDate ASC, bl.paymentTime ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Bill> getBillsByMonth(int month, int year) {
        ArrayList<Bill> list = new ArrayList<>();
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return list;
        }
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE MONTH(bl.paymentDate) = ? AND YEAR(bl.paymentDate) = ? " +
                     "ORDER BY bl.paymentDate ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
