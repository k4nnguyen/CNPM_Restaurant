package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Dish;
import model.Order;
import model.OrderDish;
import model.Table;

public class OrderDAO extends DAO {

    public OrderDAO() { 
        super(); 
    }

    // =================================================================
    // PHẦN 1: CODE CỦA AN (LUỒNG GỌI MÓN)
    // =================================================================

    public boolean addOrder(model.Order order) {
        boolean result = false;
        
        String sqlCheckActiveOrder = "SELECT id FROM tblOrder WHERE tblTableId = ? AND isPaid = 0";
        String sqlCreateOrder = "INSERT INTO tblOrder(orderTime, tblUserId, tblTableId, isPaid) VALUES(?,?,?,0)";
        
        String sqlCheckDishExist = "SELECT quantity FROM tblOrderDish WHERE tblOrderId = ? AND tblDishId = ?";
        String sqlUpdateDish = "UPDATE tblOrderDish SET quantity = quantity + ? WHERE tblOrderId = ? AND tblDishId = ?";
        String sqlInsertDish = "INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) VALUES(?,?,?,?)";

        try {
            con.setAutoCommit(false); 
            int orderId = -1;

            PreparedStatement psCheckOrder = con.prepareStatement(sqlCheckActiveOrder);
            psCheckOrder.setInt(1, order.getTable().getId());
            ResultSet rsOrder = psCheckOrder.executeQuery();

            if (rsOrder.next()) {
                orderId = rsOrder.getInt("id");
            } else {
                PreparedStatement psCreateOrder = con.prepareStatement(sqlCreateOrder, Statement.RETURN_GENERATED_KEYS);
                psCreateOrder.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                psCreateOrder.setInt(2, order.getUser().getId());
                psCreateOrder.setInt(3, order.getTable().getId());
                psCreateOrder.executeUpdate();

                ResultSet generatedKeys = psCreateOrder.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }

            if (orderId != -1) {
                PreparedStatement psCheckDish = con.prepareStatement(sqlCheckDishExist);
                PreparedStatement psUpdateDish = con.prepareStatement(sqlUpdateDish);
                PreparedStatement psInsertDish = con.prepareStatement(sqlInsertDish);

                for (model.OrderDish od : order.getOrderDishes()) {
                    psCheckDish.setInt(1, orderId);
                    psCheckDish.setInt(2, od.getDish().getId());
                    ResultSet rsDish = psCheckDish.executeQuery();

                    if (rsDish.next()) {
                        psUpdateDish.setInt(1, od.getQuantity()); 
                        psUpdateDish.setInt(2, orderId);
                        psUpdateDish.setInt(3, od.getDish().getId());
                        psUpdateDish.executeUpdate();
                    } else {
                        psInsertDish.setInt(1, od.getQuantity());
                        psInsertDish.setDouble(2, od.getCurrentPrice());
                        psInsertDish.setInt(3, orderId);
                        psInsertDish.setInt(4, od.getDish().getId());
                        psInsertDish.executeUpdate();
                    }
                }
            }

            con.commit(); 
            result = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return result;
    }

    // =================================================================
    // PHẦN 2: CODE CỦA LAM ĐÃ ĐƯỢC CHUẨN HÓA (LUỒNG THANH TOÁN)
    // =================================================================

    // Lấy chi tiết đơn để in bill (Đã đổi sang tblOrderDish)
    public Order getOrderDetail(int tableId) {
        if (con == null) return null;
        Order order = null;
        
        // Sửa query: Dùng isPaid = 0 thay vì status = 'Chưa thanh toán'
        String sqlOrder = "SELECT * FROM tblOrder WHERE tblTableId = ? AND isPaid = 0";
        // Sửa query: Dùng tblOrderDish và currentPrice
        String sqlOrderDish = "SELECT od.*, d.dishCode, d.name, d.category, d.price "
                + "FROM tblOrderDish od "
                + "JOIN tblDish d ON od.tblDishId = d.id "
                + "WHERE od.tblOrderId = ?";
        try {
            PreparedStatement psOrder = con.prepareStatement(sqlOrder);
            psOrder.setInt(1, tableId);
            ResultSet rsOrder = psOrder.executeQuery();
            
            if (rsOrder.next()) {
                order = new Order();
                order.setId(rsOrder.getInt("id"));
                order.setOrderTime(rsOrder.getTimestamp("orderTime"));
                order.setStatus(Order.STATUS_UNPAID); 

                Table table = new Table();
                table.setId(tableId);
                order.setTable(table);

                ArrayList<OrderDish> items = new ArrayList<>();
                PreparedStatement psItem = con.prepareStatement(sqlOrderDish);
                psItem.setInt(1, order.getId());
                ResultSet rsItem = psItem.executeQuery();
                
                while (rsItem.next()) {
                    OrderDish item = new OrderDish();
                    item.setQuantity(rsItem.getInt("quantity"));
                    item.setCurrentPrice(rsItem.getDouble("currentPrice")); // Lấy giá lúc gọi món

                    Dish dish = new Dish();
                    dish.setId(rsItem.getInt("tblDishId"));
                    dish.setDishCode(rsItem.getString("dishCode"));
                    dish.setName(rsItem.getString("name"));
                    dish.setCategory(rsItem.getString("category"));
                    dish.setPrice(rsItem.getDouble("price"));
                    
                    item.setDish(dish);
                    items.add(item);
                }
                order.setOrderDishes(items);
                order.recalculateTotal(); // Gọi hàm tự tính tổng tiền vừa thêm ở model
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    // Lấy danh sách bàn đang ăn (Đã đổi isPaid = 0)
    public ArrayList<Order> getAllUnpaidOrders() {
        ArrayList<Order> list = new ArrayList<>();
        if (con == null) return list;
        
        String sql = "SELECT o.*, t.tableCode, t.name AS tableName, t.capacity "
                + "FROM tblOrder o JOIN tblTable t ON o.tblTableId = t.id "
                + "WHERE o.isPaid = 0 ORDER BY o.orderTime";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderTime(rs.getTimestamp("orderTime"));
                order.setStatus(Order.STATUS_UNPAID);

                Table table = new Table();
                table.setId(rs.getInt("tblTableId"));
                table.setTableCode(rs.getString("tableCode"));
                table.setName(rs.getString("tableName"));
                table.setCapacity(rs.getInt("capacity"));
                
                order.setTable(table);
                list.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đổi trạng thái hóa đơn sang Đã thanh toán
    public boolean updateOrderStatus(int orderId) {
        if (con == null) return false;
        // Đổi isPaid thành 1 thay vì update string
        String sql = "UPDATE tblOrder SET isPaid = 1 WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}