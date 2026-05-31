/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.Date;
import model.Booking;
import model.BookedTable;
import model.Client;
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
public class BookingDAOTest {
    
    public BookingDAOTest() {
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
     * Test of addBooking method, of class BookingDAO.
     */
    @Test
    public void testAddBooking() {
        System.out.println("addBooking");
        BookingDAO instance = new BookingDAO();
        
        // BƯỚC 1: Khởi tạo dữ liệu giả lập (Mock Data)
        Booking b = new Booking();
        b.setBookDate(new Date()); // Lấy ngày hôm nay
        b.setBookTime("19:00");
        b.setQuantity(4);
        b.setStatus("Chờ nhận bàn");
        
        // Truyền ID của một khách hàng và nhân viên có thật trong CSDL
        Client c = new Client(); 
        c.setId(1); // Thay bằng ID Client có thật
        b.setClient(c);
        
        User u = new User();
        u.setId(1); // Thay bằng ID User có thật
        b.setUser(u);
        
        // Khởi tạo danh sách bàn được đặt
        Table t = new Table();
        t.setId(2); // Thay bằng ID một cái bàn đang "Trống" trong CSDL
        BookedTable bt = new BookedTable();
        bt.setTable(t);
        b.addBookedTable(bt);
        
        // BƯỚC 2: Chạy hàm và kiểm tra
        boolean result = instance.addBooking(b);
        
        // Khẳng định Transaction chạy thành công
        assertTrue(result, "Hàm addBooking phải trả về true khi Insert thành công");
        // Khẳng định ID của Booking đã được tự động sinh ra và gán ngược lại vào đối tượng
        assertTrue(b.getId() > 0, "ID của Booking phải được sinh tự động và lớn hơn 0");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of searchBooking method, of class BookingDAO.
     */
    @Test
    public void testSearchBooking() {
        System.out.println("searchBooking");
        BookingDAO instance = new BookingDAO();
        
        // 1. Kịch bản test với số điện thoại CÓ TỒN TẠI trong CSDL
        String validPhone = "0916385989"; 
        ArrayList<Booking> result1 = instance.searchBooking(validPhone);
        
        assertNotNull(result1, "Danh sách trả về không được null");
        assertTrue(result1.size() > 0, "Phải tìm thấy ít nhất 1 đơn đặt bàn với số điện thoại này");
        
        // Kiểm tra xem dữ liệu Client kéo theo có chứa đúng chuỗi SĐT không
        if(result1.size() > 0) {
            assertTrue(result1.get(0).getClient().getPhone().contains(validPhone), 
                    "Số điện thoại của khách hàng trong kết quả phải chứa chuỗi tìm kiếm");
        }

        // 2. Kịch bản test với số điện thoại KHÔNG TỒN TẠI
        String invalidPhone = "0000000000";
        ArrayList<Booking> result2 = instance.searchBooking(invalidPhone);
        assertEquals(0, result2.size(), "Nếu số điện thoại không tồn tại, list trả về phải có size = 0");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of updateBooking method, of class BookingDAO.
     */
    @Test
    public void testUpdateBooking() {
        System.out.println("updateBooking");
        BookingDAO instance = new BookingDAO();
        
        // BƯỚC 1: Tạo một đối tượng Booking với dữ liệu mới
        Booking b = new Booking();
        // ID này PHẢI LÀ ID của một phiếu đặt bàn có thật trong bảng tblBooking
        b.setId(1); 
        
        // Thay đổi giờ và số lượng người
        b.setBookDate(new Date()); 
        b.setBookTime("20:30"); 
        b.setQuantity(10); 
        
        // BƯỚC 2: Chạy hàm update
        boolean result = instance.updateBooking(b);
        
        // Khẳng định việc cập nhật thành công
        assertTrue(result, "Hàm updateBooking phải trả về true nếu ID tồn tại");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
