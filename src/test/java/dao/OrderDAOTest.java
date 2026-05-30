package dao;

import model.*;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử đơn vị cho OrderDAO.
 * Mục tiêu: Đảm bảo getOrderDetail() lấy đúng chi tiết đơn đặt món
 * và getAllUnpaidOrders() trả về đúng danh sách chưa thanh toán.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderDAOTest {

    private static OrderDAO orderDAO;
    private static final int TEST_TABLE_ID = 1;

    @BeforeAll
    static void setUpClass() {
        orderDAO = new OrderDAO();
    }

    /**
     * TC-ORDER-01: getOrderDetail() với tableId hợp lệ phải trả về Order không null
     * và có danh sách orderItems.
     */
    @Test
    @Order(1)
    @DisplayName("TC-ORDER-01: Lấy chi tiết order hợp lệ")
    void testGetOrderDetail_ValidTable() {
        model.Order order = orderDAO.getOrderDetail(TEST_TABLE_ID);
        // Nếu không có order thì bỏ qua
        Assumptions.assumeTrue(order != null, "Bỏ qua: không có order chưa thanh toán cho bàn " + TEST_TABLE_ID);

        assertNotNull(order, "getOrderDetail() không được trả về null khi có order");
        assertNotNull(order.getOrderItems(), "Danh sách orderItems không được null");
        assertTrue(order.getTotalAmount() >= 0, "Tổng tiền phải >= 0");
        assertEquals("Chưa thanh toán", order.getStatus(), "Status phải là 'Chưa thanh toán'");
        System.out.println("✅ TC-ORDER-01 PASSED: Order ID=" + order.getId() + ", " + order.getOrderItems().size() + " món");
    }

    /**
     * TC-ORDER-02: getOrderDetail() với tableId không có order phải trả về null.
     */
    @Test
    @Order(2)
    @DisplayName("TC-ORDER-02: getOrderDetail() với bàn không có order trả về null")
    void testGetOrderDetail_NoOrder_ReturnsNull() {
        // ID bàn không tồn tại hoặc đã thanh toán hết
        model.Order order = orderDAO.getOrderDetail(99999);
        assertNull(order, "getOrderDetail() phải trả về null khi không có order");
        System.out.println("✅ TC-ORDER-02 PASSED: getOrderDetail() trả về null đúng");
    }

    /**
     * TC-ORDER-03: getAllUnpaidOrders() phải trả về danh sách không null.
     */
    @Test
    @Order(3)
    @DisplayName("TC-ORDER-03: getAllUnpaidOrders() trả về danh sách hợp lệ")
    void testGetAllUnpaidOrders_ReturnsNonNull() {
        ArrayList<model.Order> orders = orderDAO.getAllUnpaidOrders();
        assertNotNull(orders, "getAllUnpaidOrders() không được trả về null");
        // Mỗi order phải có Table liên kết
        for (model.Order o : orders) {
            assertEquals("Chưa thanh toán", o.getStatus(), "Tất cả order phải có trạng thái 'Chưa thanh toán'");
            assertNotNull(o.getTable(), "Mỗi order phải có thông tin bàn");
        }
        System.out.println("✅ TC-ORDER-03 PASSED: " + orders.size() + " order chưa thanh toán");
    }
}
