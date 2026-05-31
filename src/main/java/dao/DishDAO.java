package dao;

import model.Dish;
import java.sql.*;
import java.util.ArrayList;

public class DishDAO extends DAO {

    public DishDAO() {
        super();
    }

    public ArrayList<Dish> getAllDishes() {
        ArrayList<Dish> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblDish WHERE status = ? ORDER BY category, name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Dish.STATUS_ACTIVE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Dish> searchDish(String keyword) {
        ArrayList<Dish> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblDish WHERE name LIKE ? AND status = ? ORDER BY name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, Dish.STATUS_ACTIVE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Dish> searchDishes(String keyword) {
        ArrayList<Dish> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblDish WHERE (dishCode LIKE ? OR name LIKE ?) AND status = ? ORDER BY name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, Dish.STATUS_ACTIVE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Dish> getDishesByCategory(String category) {
        ArrayList<Dish> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT * FROM tblDish WHERE category = ? AND status = ? ORDER BY name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setString(2, Dish.STATUS_ACTIVE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Dish getDishByCode(String dishCode) {
        if (con == null) return null;
        String sql = "SELECT * FROM tblDish WHERE dishCode = ? AND status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dishCode);
            ps.setString(2, Dish.STATUS_ACTIVE);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return readDish(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDish(Dish dish) {
        if (con == null || dish == null || !isValidDish(dish) || isDishCodeExists(dish.getDishCode())) {
            return false;
        }
        String sql = "INSERT INTO tblDish(dishCode, category, name, description, price, status) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dish.getDishCode().trim());
            ps.setString(2, dish.getCategory().trim());
            ps.setString(3, dish.getName().trim());
            ps.setString(4, emptyToNull(dish.getDescription()));
            ps.setDouble(5, dish.getPrice());
            ps.setString(6, Dish.STATUS_ACTIVE);
            int affected = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dish.setId(keys.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDish(Dish dish) {
        if (con == null || dish == null || dish.getId() <= 0 || !isValidDish(dish)) {
            return false;
        }
        String sql = "UPDATE tblDish SET category = ?, name = ?, description = ?, price = ? WHERE id = ? AND status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dish.getCategory().trim());
            ps.setString(2, dish.getName().trim());
            ps.setString(3, emptyToNull(dish.getDescription()));
            ps.setDouble(4, dish.getPrice());
            ps.setInt(5, dish.getId());
            ps.setString(6, Dish.STATUS_ACTIVE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDish(Dish dish) {
        return dish != null && deleteDish(dish.getId());
    }

    public boolean deleteDish(int id) {
        return deleteOrDeactivateDish(id);
    }

    public boolean deleteOrDeactivateDish(int id) {
        if (con == null || id <= 0) {
            return false;
        }
        try {
            con.setAutoCommit(false);
            boolean related = countRelatedRows("tblOrderDish", "tblDishId", id) > 0;
            String sql = related
                    ? "UPDATE tblDish SET status = ? WHERE id = ? AND status = ?"
                    : "DELETE FROM tblDish WHERE id = ? AND status = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (related) {
                    ps.setString(1, Dish.STATUS_INACTIVE);
                    ps.setInt(2, id);
                    ps.setString(3, Dish.STATUS_ACTIVE);
                } else {
                    ps.setInt(1, id);
                    ps.setString(2, Dish.STATUS_ACTIVE);
                }
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

    public boolean isDishCodeExists(String code) {
        if (con == null || isBlank(code)) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM tblDish WHERE dishCode = ?";
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

    public boolean hasDishBeenOrdered(int id) {
        if (con == null) return true;
        try {
            return countRelatedRows("tblOrderDish", "tblDishId", id) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean checkDishCode(String code) {
        return isDishCodeExists(code);
    }

    private int countRelatedRows(String tableName, String columnName, int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Dish readDish(ResultSet rs) throws SQLException {
        Dish d = new Dish();
        d.setId(rs.getInt("id"));
        d.setDishCode(rs.getString("dishCode"));
        d.setCategory(rs.getString("category"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));
        d.setPrice(rs.getDouble("price"));
        try {
            d.setStatus(rs.getString("status"));
        } catch (SQLException ignored) {}
        return d;
    }

    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        return readDish(rs);
    }

    private boolean isValidDish(Dish dish) {
        return dish != null
                && !isBlank(dish.getDishCode())
                && !isBlank(dish.getCategory())
                && !isBlank(dish.getName())
                && dish.getPrice() > 0;
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
