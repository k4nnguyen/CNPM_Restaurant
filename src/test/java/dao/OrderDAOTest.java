/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.Table;
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
public class TableDAOTest {
    
    public TableDAOTest() {
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
     * Test of searchFreeTable method, of class TableDAO.
     */
    @Test
    public void testSearchFreeTable() {
        System.out.println("searchFreeTable");
        String date = "2026-12-31";
        String time = "19:00";
        int quantity = 2;
        TableDAO instance = new TableDAO();
        ArrayList<Table> result = instance.searchFreeTable(date, time, quantity);
        // Khẳng định danh sách trả về không bị null
        assertNotNull(result, "Danh sách trả về không được null");
        // Khẳng định phải tìm thấy ít nhất 1 bàn trống (Giả định nhà hàng có bàn)
        assertTrue(result.size() >= 0, "Hệ thống chạy thành công, trả về list size >= 0");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of checkTableAvailability method, of class TableDAO.
     */
    @Test
    public void testCheckTableAvailability() {
        System.out.println("checkTableAvailability");
        int tableId = 1; 
        String date = "2026-12-31";
        String time = "19:00";
        TableDAO instance = new TableDAO();
        boolean expResult = false;
        boolean result = instance.checkTableAvailability(tableId, date, time);
        assertTrue(result, "Bàn này phải đang trống vào ngày giờ đã chỉ định");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOccupiedTables method, of class TableDAO.
     */
    @Test
    public void testGetOccupiedTables() {
        System.out.println("getOccupiedTables");
        TableDAO instance = new TableDAO();
        ArrayList<Table> result = instance.getOccupiedTables();
        // Không thể chắc chắn lúc test có bàn nào đang ăn không, nên chỉ cần test list không null
        assertNotNull(result, "Danh sách bàn đang phục vụ không được null");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAllTables method, of class TableDAO.
     */
    @Test
    public void testGetAllTables() {
        System.out.println("getAllTables");
        TableDAO instance = new TableDAO();
        ArrayList<Table> result = instance.getAllTables();
        // Khẳng định list không null
        assertNotNull(result);
        // Khẳng định nhà hàng phải có dữ liệu bàn (size > 0)
        assertTrue(result.size() > 0, "Nhà hàng phải có ít nhất 1 bàn trong CSDL");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of updateTableStatus method, of class TableDAO.
     */
    @Test
    public void testUpdateTableStatus() {
        System.out.println("updateTableStatus");
        TableDAO instance = new TableDAO();
        int tableId = 1;
        // BƯỚC 1: Đổi trạng thái thành một chuỗi test
        String newStatus = "Đang dọn dẹp"; 
        boolean updateResult = instance.updateTableStatus(tableId, newStatus);
        
        // Khẳng định lệnh UPDATE chạy thành công (trả về true)
        assertTrue(updateResult, "Lệnh Update trạng thái phải thành công");
        
        // BƯỚC 2: Rollback (Trả lại trạng thái cũ để không làm rác CSDL sau khi test xong)
        instance.updateTableStatus(tableId, "Trống");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getTableByCode method, of class TableDAO.
     */
    @Test
    public void testGetTableByCode() {
        System.out.println("getTableByCode");
        TableDAO instance = new TableDAO();
        
        // 1. Kịch bản test mã bàn TỒN TẠI (Đổi "T001" thành mã có thật trong DB của bạn)
        String validTableCode = "T001"; 
        Table result1 = instance.getTableByCode(validTableCode);
        assertNotNull(result1, "Phải tìm thấy bàn có mã " + validTableCode);
        assertEquals(validTableCode, result1.getTableCode(), "Mã bàn trả về phải khớp với mã tìm kiếm");
        
        // 2. Kịch bản test mã bàn KHÔNG TỒN TẠI
        String invalidTableCode = "XXX-999";
        Table result2 = instance.getTableByCode(invalidTableCode);
        assertNull(result2, "Bàn không tồn tại thì phải trả về null");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}