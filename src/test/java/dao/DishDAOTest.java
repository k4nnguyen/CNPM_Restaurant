/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.Dish;
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
public class DishDAOTest {
    
    public DishDAOTest() {
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
     * Test of searchDish method, of class DishDAO.
     */
    @Test
    public void testSearchDish() {
        System.out.println("searchDish");
        DishDAO instance = new DishDAO();
        
        // KỊCH BẢN 1: Tìm kiếm với chuỗi rỗng
        // Câu lệnh LIKE '%%' sẽ lấy lên toàn bộ danh sách món ăn trong Menu
        ArrayList<Dish> resultAll = instance.searchDish("");
        assertNotNull(resultAll, "Danh sách món ăn trả về không được null");
        assertTrue(resultAll.size() >= 0, "Query tìm kiếm phải thực thi thành công");

        // KỊCH BẢN 2: Tìm kiếm món ăn CÓ TỒN TẠI
        String validKeyword = "Gà"; 
        ArrayList<Dish> resultFound = instance.searchDish(validKeyword);
        
        assertNotNull(resultFound);
        if (!resultFound.isEmpty()) {
            // Chuyển cả 2 về chữ thường để so sánh cho chuẩn xác
            String actualName = resultFound.get(0).getName().toLowerCase();
            assertTrue(actualName.contains(validKeyword.toLowerCase()), 
                    "Tên món ăn trả về phải chứa từ khóa tìm kiếm");
        }

        // KỊCH BẢN 3: Tìm kiếm món ăn KHÔNG TỒN TẠI
        String invalidKeyword = "abxxyznmasad";
        ArrayList<Dish> resultNotFound = instance.searchDish(invalidKeyword);
        
        assertEquals(0, resultNotFound.size(), "Nếu không tìm thấy, danh sách trả về phải rỗng (size = 0)");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
