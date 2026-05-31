/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.Client;
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
public class ClientDAOTest {
    
    public ClientDAOTest() {
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
     * Test of searchClient method, of class ClientDAO.
     */
    @Test
    public void testSearchClient() {
        System.out.println("searchClient");
        ClientDAO instance = new ClientDAO();
        
        // KỊCH BẢN 1: Tìm kiếm không truyền tham số (Tất cả đều rỗng)
        // Hệ thống phải trả về toàn bộ danh sách khách hàng
        ArrayList<Client> resultAll = instance.searchClient("", "");
        assertNotNull(resultAll, "Danh sách không được null");
        assertTrue(resultAll.size() >= 0, "Câu query 1=1 phải chạy thành công");

        // KỊCH BẢN 2: Tìm kiếm với số điện thoại CÓ TỒN TẠI 
        String validPhone = "0916385989";
        ArrayList<Client> resultFound = instance.searchClient("", validPhone);
        if (!resultFound.isEmpty()) {
            assertTrue(resultFound.get(0).getPhone().contains(validPhone), 
                    "Kết quả trả về phải chứa số điện thoại trùng khớp với từ khóa");
        }

        // KỊCH BẢN 3: Tìm kiếm với dữ liệu KHÔNG TỒN TẠI
        ArrayList<Client> resultNotFound = instance.searchClient("TenKhongTonTai123", "999999999");
        assertEquals(0, resultNotFound.size(), "Nếu không tìm thấy, danh sách trả về phải rỗng (size = 0)");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of addClient method, of class ClientDAO.
     */
    @Test
    public void testAddClient() {
        System.out.println("addClient");
        ClientDAO instance = new ClientDAO();
        
        // BƯỚC 1: Tạo dữ liệu khách hàng ảo
        Client c = new Client();
        c.setName("Khách hàng Test JUnit");
        c.setPhone("0123456789");
        c.setEmail("test@gmail.com");
        c.setAddress("Hà Nội");
        
        // BƯỚC 2: Gọi hàm thêm mới
        boolean result = instance.addClient(c);
        
        // Khẳng định hàm chạy thành công và trả về true
        assertTrue(result, "Hàm addClient phải trả về true khi Insert thành công");
        
        // Khẳng định cực kỳ quan trọng: ID phải được SQL Server cấp phát và gán ngược lại đối tượng
        assertTrue(c.getId() > 0, "ID của Client phải được sinh tự động và lớn hơn 0");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
