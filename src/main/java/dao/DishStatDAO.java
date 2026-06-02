package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO xử lý thống kê doanh thu và món ăn bán chạy.
 * Truy vấn join nhiều bảng để tổng hợp dữ liệu theo khoảng thời gian.
 */
public class DishStatDAO extends DAO {

    public DishStatDAO() {
        super();
    }

    /**
     * Lấy danh sách các món ăn bán chạy nhất trong khoảng thời gian,
     * sắp xếp giảm dần theo tổng doanh thu.
     *
     * @param startDate Ngày bắt đầu (định dạng: yyyy-MM-dd).
     * @param endDate   Ngày kết thúc (định dạng: yyyy-MM-dd).
     * @return Danh sách DishStat sắp xếp theo doanh thu giảm dần.
     */
    public ArrayList<DishStat> getBestSellingDish(String startDate, String endDate) {
        ArrayList<DishStat> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT d.id, d.dishCode, d.name, d.category, d.price, "
                + "SUM(oi.quantity) AS totalQuantity, "
                + "SUM(oi.quantity * oi.currentPrice) AS totalRevenue "
                + "FROM tblDish d "
                + "JOIN tblOrderDish oi ON d.id = oi.tblDishID "
                + "JOIN tblOrder o ON oi.tblOrderID = o.id "
                + "JOIN tblBill b ON o.id = b.tblOrderID "
                + "WHERE b.createTime >= ? AND b.createTime <= ? "
                + "GROUP BY d.id, d.dishCode, d.name, d.category, d.price "
                + "ORDER BY totalRevenue DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate + " 00:00:00");
            ps.setString(2, endDate + " 23:59:59");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DishStat ds = new DishStat();
                ds.setId(rs.getInt("id"));
                ds.setDishCode(rs.getString("dishCode"));
                ds.setName(rs.getString("name"));
                ds.setCategory(rs.getString("category"));
                ds.setPrice(rs.getDouble("price"));
                ds.setTotalQuantity(rs.getInt("totalQuantity"));
                ds.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(ds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy tổng doanh thu của nhà hàng trong khoảng thời gian.
     *
     * @param startDate Ngày bắt đầu (định dạng: yyyy-MM-dd).
     * @param endDate   Ngày kết thúc (định dạng: yyyy-MM-dd).
     * @return Tổng doanh thu, hoặc 0.0 nếu không có dữ liệu.
     */
    public double getTotalRevenue(String startDate, String endDate) {
        if (con == null) return 0.0;
        String sql = "SELECT SUM(totalAmount) AS totalRevenue FROM tblBill "
                + "WHERE createTime >= ? AND createTime <= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate + " 00:00:00");
            ps.setString(2, endDate + " 23:59:59");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Lấy danh sách món ăn bán chạy với giới hạn top N.
     *
     * @param startDate Ngày bắt đầu (định dạng: yyyy-MM-dd).
     * @param endDate   Ngày kết thúc (định dạng: yyyy-MM-dd).
     * @param topN      Số lượng món ăn muốn lấy (top N).
     * @return Danh sách top N DishStat sắp xếp theo doanh thu giảm dần.
     */
    public ArrayList<DishStat> getTopSellingDish(String startDate, String endDate, int topN) {
        ArrayList<DishStat> allList = getBestSellingDish(startDate, endDate);
        ArrayList<DishStat> topList = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, allList.size()); i++) {
            topList.add(allList.get(i));
        }
        return topList;
    }
}
