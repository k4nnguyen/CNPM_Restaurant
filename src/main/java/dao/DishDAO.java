package dao;

import model.Dish;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO xử lý truy vấn CSDL cho thực thể Dish (món ăn).
 * Cung cấp các thao tác: lấy tất cả món, tìm theo tên/danh mục.
 */
public class DishDAO extends DAO {

    public DishDAO() {
        super();
    }

    /**
     * Lấy tất cả các món ăn đang trong thực đơn (available = true).
     *
     * @return Danh sách tất cả Dish khả dụng.
     */
    public ArrayList<Dish> getAllDishes() {
        ArrayList<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM tblDish WHERE available = 1 ORDER BY category, name";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tìm kiếm món ăn theo từ khóa tên.
     *
     * @param keyword Từ khóa tìm kiếm trong tên món.
     * @return Danh sách món ăn khớp với từ khóa.
     */
    public ArrayList<Dish> searchDish(String keyword) {
        ArrayList<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM tblDish WHERE name LIKE ? ORDER BY name";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách món ăn theo danh mục.
     *
     * @param category Tên danh mục (VD: "Món chính", "Đồ uống", "Tráng miệng").
     * @return Danh sách món thuộc danh mục.
     */
    public ArrayList<Dish> getDishesByCategory(String category) {
        ArrayList<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM tblDish WHERE category = ? ORDER BY name";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tìm món ăn theo mã món (dishCode).
     *
     * @param dishCode Mã món cần tìm.
     * @return Đối tượng Dish nếu tìm thấy, null nếu không.
     */
    public Dish getDishByCode(String dishCode) {
        String sql = "SELECT * FROM tblDish WHERE dishCode = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dishCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToDish(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ánh xạ ResultSet sang đối tượng Dish.
     */
    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        Dish d = new Dish();
        d.setId(rs.getInt("id"));
        d.setDishCode(rs.getString("dishCode"));
        d.setName(rs.getString("name"));
        d.setCategory(rs.getString("category"));
        d.setPrice(rs.getDouble("price"));
        if (rs.getMetaData().getColumnCount() >= 6) {
            try { d.setDescription(rs.getString("description")); } catch (Exception ignored) {}
        }
        return d;
    }
}
