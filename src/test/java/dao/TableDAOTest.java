package dao;

import model.Table;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử đơn vị cho TableDAO.
 * Mục tiêu: Đảm bảo updateTableStatus() cập nhật đúng trạng thái bàn
 * và getServingTables() trả về đúng danh sách bàn đang phục vụ.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableDAOTest {

    private static TableDAO tableDAO;
    private static final int TEST_TABLE_ID = 1; // Bàn có sẵn trong CSDL

    @BeforeAll
    static void setUpClass() {
        tableDAO = new TableDAO();
    }

    /**
     * TC-TABLE-01: updateTableStatus() cập nhật thành công trạng thái bàn thành "Trống"
     * sau khi thanh toán.
     */
    @Test
    @Order(1)
    @DisplayName("TC-TABLE-01: Cập nhật trạng thái bàn thành 'Trống'")
    void testUpdateTableStatus_ToTrong() {
        boolean result = tableDAO.updateTableStatus(TEST_TABLE_ID, "Tr\u1ed1ng");
        assertTrue(result, "updateTableStatus() phải trả về true khi cập nhật thành công");
        System.out.println("TC-TABLE-01 PASSED: Cập nhật trạng thái bàn " + TEST_TABLE_ID + " thành 'Trống'");
    }

    /**
     * TC-TABLE-02: Sau khi cập nhật thành "Trống", bàn không còn xuất hiện trong getServingTables().
     */
    @Test
    @Order(2)
    @DisplayName("TC-TABLE-02: Bàn trống không xuất hiện trong danh sách đang phục vụ")
    void testGetServingTables_ExcludesTrongTable() {
        // Đặt bàn về trạng thái Trống
        tableDAO.updateTableStatus(TEST_TABLE_ID, "Tr\u1ed1ng");

        ArrayList<Table> servingTables = tableDAO.getServingTables();
        boolean found = servingTables.stream().anyMatch(t -> t.getId() == TEST_TABLE_ID);
        assertFalse(found, "Bàn ở trạng thái 'Trống' không được xuất hiện trong getServingTables()");
        System.out.println("TC-TABLE-02 PASSED: Bàn " + TEST_TABLE_ID + " không còn trong danh sách phục vụ");
    }

    /**
     * TC-TABLE-03: Sau khi cập nhật thành "Đang phục vụ", bàn phải xuất hiện trong getServingTables().
     */
    @Test
    @Order(3)
    @DisplayName("TC-TABLE-03: Bàn 'Đang phục vụ' xuất hiện trong danh sách")
    void testGetServingTables_IncludesServingTable() {
        // Cập nhật bàn sang trạng thái "Đang phục vụ"
        tableDAO.updateTableStatus(TEST_TABLE_ID, "\u0110ang ph\u1ee5c v\u1ee5");

        ArrayList<Table> servingTables = tableDAO.getServingTables();
        boolean found = servingTables.stream().anyMatch(t -> t.getId() == TEST_TABLE_ID);
        assertTrue(found, "Bàn ở trạng thái 'Đang phục vụ' phải xuất hiện trong getServingTables()");

        // Cleanup: reset về Trống
        tableDAO.updateTableStatus(TEST_TABLE_ID, "Tr\u1ed1ng");
        System.out.println("TC-TABLE-03 PASSED: Bàn " + TEST_TABLE_ID + " xuất hiện trong danh sách phục vụ");
    }

    /**
     * TC-TABLE-04: getAllTables() phải trả về danh sách không rỗng (nếu CSDL có dữ liệu).
     */
    @Test
    @Order(4)
    @DisplayName("TC-TABLE-04: getAllTables() trả về danh sách hợp lệ")
    void testGetAllTables_ReturnsNonNull() {
        ArrayList<Table> allTables = tableDAO.getAllTables();
        assertNotNull(allTables, "getAllTables() không được trả về null");
        System.out.println("✅ TC-TABLE-04 PASSED: getAllTables() trả về " + allTables.size() + " bàn");
    }
}
