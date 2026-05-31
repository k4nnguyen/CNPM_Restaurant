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
        if (con == null) return false;
        boolean result = false;
        
        // Tra cứu tblBookingId từ bàn đang check-in của Order
        Integer bookingId = null;
        String sqlLookupBooking = "SELECT TOP 1 bt.tblBookingId FROM tblBookedTable bt "
                + "JOIN tblOrder o ON bt.tblTableId = o.tblTableId "
                + "WHERE o.id = ? AND bt.isCheckedIn = 1 ORDER BY bt.id DESC";
        try {
            PreparedStatement psLookup = con.prepareStatement(sqlLookupBooking);
            psLookup.setInt(1, bill.getOrder().getId());
            ResultSet rsLookup = psLookup.executeQuery();
            if (rsLookup.next()) {
                bookingId = rsLookup.getInt("tblBookingId");
            }
        } catch (Exception e) {
            // Nếu không tìm thấy hoặc lỗi thì bỏ qua, để null
        }

        String sqlBill = "INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        String sqlUpdateOrder = "UPDATE tblOrder SET status = N'\u0110\u00e3 thanh to\u00e1n', isPaid = 1 WHERE id = ?";
        try {
            con.setAutoCommit(false);

            // Bước 1: Chèn hóa đơn vào tblBill
            PreparedStatement ps1 = con.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS);
            ps1.setTimestamp(1, new Timestamp(bill.getCreatedTime().getTime()));
            ps1.setDate(2, new java.sql.Date(bill.getCreatedTime().getTime()));
            ps1.setString(3, new java.text.SimpleDateFormat("HH:mm:ss").format(bill.getCreatedTime()));
            ps1.setDouble(4, bill.getTotalAmount());
            ps1.setString(5, bill.getPaymentMethod());
            ps1.setInt(6, bill.getOrder().getId());
            ps1.setInt(7, bill.getUser().getId());
            if (bookingId != null) {
                ps1.setInt(8, bookingId);
            } else {
                ps1.setNull(8, java.sql.Types.INTEGER);
            }
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
        if (con == null) return null;
        String sql = "SELECT b.*, u.name AS staffName FROM tblBill b "
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
                     "LEFT JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ORDER BY bl.paymentDate ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bl = new Bill();
                    bl.setId(rs.getInt("id"));
                    bl.setPaymentDate(rs.getDate("paymentDate"));
                    bl.setPaymentTime(rs.getString("paymentTime"));
                    bl.setTotalAmount(rs.getDouble("totalAmount"));

                    int bid = rs.getInt("bid");
                    if (!rs.wasNull()) {
                        Booking b = new Booking();
                        b.setId(bid);
                        b.setBookDate(rs.getDate("bookDate"));
                        b.setBookTime(rs.getString("bookTime"));
                        b.setQuantity(rs.getInt("quantity"));
                        b.setStatus(rs.getString("status"));
                        bl.setBooking(b);
                    }

                    list.add(bl);
                }
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
                     "LEFT JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ";
        
        if (timeFrame.equals("11:00-13:00")) {
            sql += "AND bl.paymentTime BETWEEN '11:00:00' AND '13:00:00' ";
        } else if (timeFrame.equals("18:00-20:00")) {
            sql += "AND bl.paymentTime BETWEEN '18:00:00' AND '20:00:00' ";
        } else {
            sql += "AND bl.paymentTime NOT BETWEEN '11:00:00' AND '13:00:00' AND bl.paymentTime NOT BETWEEN '18:00:00' AND '20:00:00' ";
        }
        sql += "ORDER BY bl.paymentDate ASC, bl.paymentTime ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bl = new Bill();
                    bl.setId(rs.getInt("id"));
                    bl.setPaymentDate(rs.getDate("paymentDate"));
                    bl.setPaymentTime(rs.getString("paymentTime"));
                    bl.setTotalAmount(rs.getDouble("totalAmount"));

                    int bid = rs.getInt("bid");
                    if (!rs.wasNull()) {
                        Booking b = new Booking();
                        b.setId(bid);
                        b.setBookDate(rs.getDate("bookDate"));
                        b.setBookTime(rs.getString("bookTime"));
                        b.setQuantity(rs.getInt("quantity"));
                        b.setStatus(rs.getString("status"));
                        bl.setBooking(b);
                    }

                    list.add(bl);
                }
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
                     "LEFT JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE MONTH(bl.paymentDate) = ? AND YEAR(bl.paymentDate) = ? " +
                     "ORDER BY bl.paymentDate ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bl = new Bill();
                    bl.setId(rs.getInt("id"));
                    bl.setPaymentDate(rs.getDate("paymentDate"));
                    bl.setPaymentTime(rs.getString("paymentTime"));
                    bl.setTotalAmount(rs.getDouble("totalAmount"));

                    int bid = rs.getInt("bid");
                    if (!rs.wasNull()) {
                        Booking b = new Booking();
                        b.setId(bid);
                        b.setBookDate(rs.getDate("bookDate"));
                        b.setBookTime(rs.getString("bookTime"));
                        b.setQuantity(rs.getInt("quantity"));
                        b.setStatus(rs.getString("status"));
                        bl.setBooking(b);
                    }

                    list.add(bl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
