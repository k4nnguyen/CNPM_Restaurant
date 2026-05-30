package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO xử lý truy vấn CSDL cho thực thể Order và OrderItem.
 * Cung cấp: lấy chi tiết đơn đặt món theo bàn.
 */
public class OrderDAO extends DAO {

    public OrderDAO() {
        super();
    }

    /**
     * Lấy chi tiết đơn đặt món chưa thanh toán của một bàn.
     * Bao gồm cả danh sách OrderItem và thông tin Dish tương ứng.
     *
     * @param tableId ID của bàn cần lấy thông tin order.
     * @return Đối tượng Order đầy đủ, hoặc null nếu không tìm thấy.
     */
    public Order getOrderDetail(int tableId) {
        if (con == null) return null;
        Order order = null;
        String sqlOrder = "SELECT * FROM tblOrder WHERE tblTableId = ? AND status = N'Chưa thanh toán'";
        String sqlOrderItem = "SELECT oi.*, d.dishCode, d.name, d.category, d.price "
                + "FROM tblOrderItem oi "
                + "JOIN tblDish d ON oi.tblDishId = d.id "
                + "WHERE oi.tblOrderId = ?";
        try {
            PreparedStatement psOrder = con.prepareStatement(sqlOrder);
            psOrder.setInt(1, tableId);
            ResultSet rsOrder = psOrder.executeQuery();
            if (rsOrder.next()) {
                order = new Order();
                order.setId(rsOrder.getInt("id"));
                order.setOrderTime(rsOrder.getTimestamp("orderTime"));
                order.setTotalAmount(rsOrder.getDouble("totalAmount"));
                order.setStatus(rsOrder.getString("status"));

                Table table = new Table();
                table.setId(tableId);
                order.setTable(table);

                // Tải danh sách OrderItem
                ArrayList<OrderItem> items = new ArrayList<>();
                PreparedStatement psItem = con.prepareStatement(sqlOrderItem);
                psItem.setInt(1, order.getId());
                ResultSet rsItem = psItem.executeQuery();
                while (rsItem.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rsItem.getInt("id"));
                    item.setQuantity(rsItem.getInt("quantity"));
                    item.setUnitPrice(rsItem.getDouble("unitPrice"));
                    item.setTemporaryAmount(item.getQuantity() * item.getUnitPrice());

                    Dish dish = new Dish();
                    dish.setId(rsItem.getInt("tblDishId"));
                    dish.setDishCode(rsItem.getString("dishCode"));
                    dish.setName(rsItem.getString("name"));
                    dish.setCategory(rsItem.getString("category"));
                    dish.setPrice(rsItem.getDouble("price"));
                    item.setDish(dish);
                    items.add(item);
                }
                order.setOrderItems(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * Lấy danh sách tất cả các đơn đặt món chưa thanh toán.
     *
     * @return Danh sách các Order chưa thanh toán.
     */
    public ArrayList<Order> getAllUnpaidOrders() {
        ArrayList<Order> list = new ArrayList<>();
        if (con == null) return list;
        String sql = "SELECT o.*, t.tableCode, t.name AS tableName "
                + "FROM tblOrder o JOIN tblTable t ON o.tblTableId = t.id "
                + "WHERE o.status = N'Chưa thanh toán' ORDER BY o.orderTime";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderTime(rs.getTimestamp("orderTime"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                order.setStatus(rs.getString("status"));

                Table table = new Table();
                table.setId(rs.getInt("tblTableId"));
                table.setTableCode(rs.getString("tableCode"));
                table.setName(rs.getString("tableName"));
                order.setTable(table);
                list.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cập nhật trạng thái của đơn đặt món.
     *
     * @param orderId ID đơn đặt món.
     * @param status  Trạng thái mới.
     * @return true nếu cập nhật thành công.
     */
    public boolean updateOrderStatus(int orderId, String status) {
        if (con == null) return false;
        String sql = "UPDATE tblOrder SET status = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}