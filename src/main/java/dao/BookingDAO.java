package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class BookingDAO extends DAO {
    
    public BookingDAO() { 
        super(); 
    }

    // 1. Lưu thông tin đặt bàn mới (Sử dụng Transaction)
    public boolean addBooking(Booking b) {
        if (b == null) {
            return false;
        }
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return false;
        }
        boolean result = false;
        String sqlBooking = "INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) VALUES(?,?,?,?,?,?)";
        // Thêm cột checkin vào câu lệnh lệnh SQL
        String sqlBookedTable = "INSERT INTO tblBookedTable(isCheckedIn, checkin, tblBookingId, tblTableId) VALUES(?,?,?,?)";
        // Câu lệnh cập nhật trạng thái bàn để Form 8 nhận diện được
        String sqlUpdateTable = "UPDATE tblTable SET status = N'Đang phục vụ' WHERE id = ?";
        
        try {
            con.setAutoCommit(false); // Bắt đầu Transaction
            
            // 1. Insert Booking
            PreparedStatement ps1 = con.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS);
            ps1.setDate(1, new java.sql.Date(b.getBookDate().getTime()));
            ps1.setString(2, b.getBookTime());
            ps1.setInt(3, b.getQuantity());
            ps1.setString(4, "Ch\u1edd nh\u1eadn b\u00e0n");
            ps1.setInt(5, b.getClient().getId());
            ps1.setInt(6, b.getUser().getId());
            ps1.executeUpdate();
            
            // Lấy ID vừa sinh ra
            ResultSet generatedKeys = ps1.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
                
                // Trích xuất ngày và giờ từ Booking để ghép thành Timestamp check-in
                String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(b.getBookDate());
                String timeStr = b.getBookTime(); // ví dụ: "19:00"
                java.sql.Timestamp checkInTimestamp;
                try {
                    java.util.Date parsedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr + " " + timeStr);
                    checkInTimestamp = new java.sql.Timestamp(parsedDate.getTime());
                } catch (Exception ex) {
                    checkInTimestamp = new java.sql.Timestamp(System.currentTimeMillis()); // Phòng hờ lỗi thì lấy giờ hiện tại
                }

                // 2. Insert các BookedTable & 3. Update trạng thái tblTable
                PreparedStatement ps2 = con.prepareStatement(sqlBookedTable);
                PreparedStatement ps3 = con.prepareStatement(sqlUpdateTable);
                
                for (BookedTable bt : b.getBookedTables()) {
                    // Thực hiện lưu chi tiết bàn đã đặt kèm giờ check-in giả lập
                    ps2.setInt(1, 1); // 1 nghĩa là đã check-in
                    ps2.setTimestamp(2, checkInTimestamp);
                    ps2.setInt(3, b.getId());
                    ps2.setInt(4, bt.getTable().getId());
                    ps2.executeUpdate();
                    
                    // Thực hiện đổi trạng thái bàn sang 'Đang phục vụ' ngoài thực tế
                    ps3.setInt(1, bt.getTable().getId());
                    ps3.executeUpdate();
                }
            }
            
            con.commit(); // Hoàn tất Transaction thành công rực rỡ
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback(); // Hoàn tác toàn bộ nếu có bất kỳ lỗi nào xảy ra
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    // 2. Tìm kiếm phiếu đặt bàn theo số điện thoại khách hàng (Module Sửa đặt bàn)
    public ArrayList<Booking> searchBooking(String phone) {
        ArrayList<Booking> list = new ArrayList<>();
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return list;
        }
        // SQL lấy Booking và Client
        String sql = "SELECT b.*, c.name, c.phone, c.email, c.address FROM tblBooking b "
                   + "JOIN tblClient c ON b.tblClientId = c.id "
                   + "WHERE c.phone LIKE ?";
                   
        // SQL lấy danh sách Bàn theo ID của Booking
        String sqlTable = "SELECT t.id, t.tableCode FROM tblBookedTable bt "
                        + "JOIN tblTable t ON bt.tblTableId = t.id "
                        + "WHERE bt.tblBookingId = ?";
                        
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

                // --- BỔ SUNG: Chạy SQL phụ để lấy các Bàn đã đặt ---
                PreparedStatement psTable = con.prepareStatement(sqlTable);
                psTable.setInt(1, b.getId());
                ResultSet rsTable = psTable.executeQuery();
                
                while(rsTable.next()) {
                    Table t = new Table();
                    t.setId(rsTable.getInt("id"));
                    t.setTableCode(rsTable.getString("tableCode"));
                    
                    BookedTable bt = new BookedTable();
                    bt.setTable(t);
                    b.addBookedTable(bt); // Gắn bàn vào Booking
                }
                
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Cập nhật thông tin phiếu đặt bàn (Module Sửa đặt bàn)
    public boolean updateBooking(Booking b) {
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return false;
        }
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

    // 4. Lấy danh sách đặt bàn theo khoảng ngày phục vụ thống kê (Module Quản lý)
    public ArrayList<Booking> getBookingsByDateRange(String startDate, String endDate) {
        ArrayList<Booking> list = new ArrayList<>();
        if (con == null) {
            System.err.println("Lỗi: Kết nối CSDL chưa được khởi tạo!");
            return list;
        }
        String sql = "SELECT b.id, b.bookDate, b.bookTime, b.quantity, b.status, " +
                      "c.id AS cid, c.name AS cname, c.phone AS cphone, " +
                      "u.id AS uid, u.name AS uname " +
                      "FROM tblBooking b " +
                      "JOIN tblClient c ON b.tblClientId = c.id " +
                      "LEFT JOIN tblUser u ON b.tblUserId = u.id " +
                      "WHERE b.bookDate BETWEEN ? AND ? ORDER BY b.bookDate ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));

                Client c = new Client();
                c.setId(rs.getInt("cid"));
                c.setName(rs.getString("cname"));
                c.setPhone(rs.getString("cphone"));
                b.setClient(c);

                User u = new User();
                u.setId(rs.getInt("uid"));
                u.setFullName(rs.getString("uname"));
                b.setUser(u);

                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
