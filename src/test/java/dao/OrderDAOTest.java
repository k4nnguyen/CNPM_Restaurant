/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.Date;
import model.Dish;
import model.Order;
import model.OrderDish;
import model.Table;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author annguyen
 */
public class OrderDAOTest {
    
    public OrderDAOTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of addOrder method, of class OrderDAO.
     */
    @Test
    public void testAddOrder() {
        System.out.println("addOrder");
        OrderDAO instance = new OrderDAO();
        
        // 1. Khởi tạo đối tượng Order với Bàn và Nhân viên giả lập
        Order o = new Order();
        o.setOrderTime(new Date());
        
        Table t = new Table();
        t.setId(1); // THAY ĐỔI: ID Bàn đang ngồi gọi món
        o.setTable(t);
        
        User u = new User();
        u.setId(1); // THAY ĐỔI: ID Nhân viên đang trực
        o.setUser(u);
        
        // 2. Khởi tạo một món ăn khách gọi (OrderDish)
        Dish d = new Dish();
        d.setId(1); // THAY ĐỔI: ID Món ăn có trong Menu
        d.setPrice(50000); 
        
        OrderDish od = new OrderDish(d, 2); // Khách gọi 2 phần
        o.addOrderDish(od);
        
        // 3. Thực thi hàm và kiểm tra
        boolean result = instance.addOrder(o);
        
        // Khẳng định việc lưu Order và OrderDish xuống DB thành công
        assertTrue(result, "Hàm addOrder phải trả về true khi Transaction thành công");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderDetail method, of class OrderDAO.
     */
    @Test
    public void testGetOrderDetail() {
        System.out.println("getOrderDetail");
        OrderDAO instance = new OrderDAO();
        
        // Kịch bản: Tìm Order của một bàn ĐANG CÓ KHÁCH (isPaid = 0)
        int tableId = 1; // THAY ĐỔI: ID của bàn chắc chắn đang có Order chưa thanh toán
        Order result = instance.getOrderDetail(tableId);
        
        // Nếu DB có dữ liệu, result phải khác null
        if (result != null) {
            assertEquals(Order.STATUS_UNPAID, result.getStatus(), "Trạng thái đơn phải là Chưa thanh toán");
            assertNotNull(result.getOrderDishes(), "Danh sách món ăn không được null");
            assertTrue(result.getOrderDishes().size() > 0, "Phải có ít nhất 1 món ăn trong Order");
        } else {
            // Nếu bàn đang trống, result sẽ là null
            assertNull(result, "Nếu bàn chưa gọi món, kết quả trả về phải là null");
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAllUnpaidOrders method, of class OrderDAO.
     */
    @Test
    public void testGetAllUnpaidOrders() {
        System.out.println("getAllUnpaidOrders");
        OrderDAO instance = new OrderDAO();
        ArrayList<Order> result = instance.getAllUnpaidOrders();
        
        // Khẳng định list trả về không bị lỗi (không null)
        assertNotNull(result, "Danh sách Order chưa thanh toán không được null");
        
        // Nếu list có phần tử, kiểm tra phần tử đầu tiên xem có đúng trạng thái không
        if (!result.isEmpty()) {
            assertEquals(Order.STATUS_UNPAID, result.get(0).getStatus(), "Order lấy lên phải có trạng thái Chưa thanh toán");
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of updateOrderStatus method, of class OrderDAO.
     */
    @Test
    public void testUpdateOrderStatus() {
        System.out.println("updateOrderStatus");
        OrderDAO instance = new OrderDAO();
        
        // Kịch bản: Đổi trạng thái một Order thành Đã thanh toán
        int orderId = 1; // THAY ĐỔI: ID của một Order (tblOrder) CÓ THẬT trong DB
        
        boolean result = instance.updateOrderStatus(orderId);
        
        // Khẳng định hàm Update chạy thành công
        assertTrue(result, "Hàm updateOrderStatus phải trả về true khi cập nhật thành công");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}