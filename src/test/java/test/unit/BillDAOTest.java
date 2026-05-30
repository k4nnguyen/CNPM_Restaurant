package test.unit;

import dao.BillDAO;
import dao.OrderDAO;
import dao.TableDAO;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử đơn vị cho BillDAO.
 * Mục tiêu: Đảm bảo createBill() lưu thành công hóa đơn vào tblBill
 * và cập nhật đúng trạng thái đơn hàng trong tblOrder.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BillDAOTest {

    private static BillDAO billDAO;
    private static OrderDAO orderDAO;
    private static TableDAO tableDAO;

    // ID bàn test có sẵn trong CSDL (trạng thái "Đang phục vụ")
    private static final int TEST_TABLE_ID = 1;

    @BeforeAll
    static void setUp() {
        billDAO = new BillDAO();
        orderDAO = new OrderDAO();
        tableDAO = new TableDAO();
    }

    /**
     * TC-BILL-01: Tạo hóa đơn thành công cho một đơn đặt món đang chưa thanh toán.
     * Kết quả mong đợi: createBill() trả về true và bill.getId() > 0.
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("TC-BILL-01: Tạo hóa đơn thành công")
    void testCreateBill_Success() {
        // Lấy đơn đặt món chưa thanh toán của bàn test
        model.Order order = orderDAO.getOrderDetail(TEST_TABLE_ID);

        // Điều kiện: bàn phải có order chưa thanh toán
        Assumptions.assumeTrue(order != null, "Bỏ qua test: không tìm thấy order chưa thanh toán cho bàn " + TEST_TABLE_ID);

        // Tạo user nhân viên giả (cần có trong CSDL)
        User staff = new User();
        staff.setId(1);

        // Tạo hóa đơn
        Bill bill = new Bill();
        bill.setCreatedTime(new Date());
        bill.setTotalAmount(order.getTotalAmount());
        bill.setPaymentMethod("Tiền mặt");
        bill.setOrder(order);
        bill.setUser(staff);

        // Thực hiện
        boolean result = billDAO.createBill(bill);

        // Kiểm tra
        assertTrue(result, "createBill() phải trả về true khi tạo thành công");
        assertTrue(bill.getId() > 0, "Bill phải có ID > 0 sau khi tạo thành công");
        System.out.println("✅ TC-BILL-01 PASSED: Bill ID = " + bill.getId());
    }

    /**
     * TC-BILL-02: Trạng thái tblOrder phải được cập nhật thành "Đã thanh toán" sau khi tạo hóa đơn.
     * Kiểm tra sau TC-BILL-01 (đã thanh toán xong), không còn order nào chưa thanh toán.
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("TC-BILL-02: Trạng thái Order cập nhật thành Đã thanh toán")
    void testCreateBill_OrderStatusUpdated() {
        // Sau khi thanh toán, không còn order chưa thanh toán cho bàn TEST_TABLE_ID
        model.Order order = orderDAO.getOrderDetail(TEST_TABLE_ID);
        assertNull(order, "Sau khi thanh toán, getOrderDetail() phải trả về null (không còn order chưa thanh toán)");
        System.out.println("✅ TC-BILL-02 PASSED: Order đã được cập nhật trạng thái 'Đã thanh toán'");
    }

    /**
     * TC-BILL-03: Không thể tạo hóa đơn với Order null (phải trả về false, không throw exception).
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("TC-BILL-03: Tạo hóa đơn với Order null phải thất bại an toàn")
    void testCreateBill_WithNullOrder_ReturnsFalse() {
        Bill bill = new Bill();
        bill.setCreatedTime(new Date());
        bill.setTotalAmount(100000);
        bill.setPaymentMethod("Tiền mặt");
        bill.setOrder(null); // Order null
        User staff = new User();
        staff.setId(1);
        bill.setUser(staff);

        boolean result = billDAO.createBill(bill);
        assertFalse(result, "createBill() phải trả về false khi Order là null");
        System.out.println("✅ TC-BILL-03 PASSED: Xử lý Order null an toàn");
    }
}
