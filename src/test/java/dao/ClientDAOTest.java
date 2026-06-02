package dao;

import model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClientDAOTest {
    private ClientDAO clientDAO;

    @BeforeEach
    public void setUp() {
        clientDAO = new ClientDAO();
    }

    @Test
    public void testFindAllActive() {
        try {
            List<Client> list = clientDAO.findAllActive();
            assertNotNull(list, "Danh sach Client khong duoc null");
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSearchActive() {
        try {
            List<Client> list = clientDAO.searchActive("Khach hang test");
            assertNotNull(list);
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSearchClientWithTwoParams() {
        ArrayList<Client> list = clientDAO.searchClient("TenAoThuat", "0999999999");
        assertNotNull(list);
        // Vì tên và sdt ảo nên kỳ vọng list sẽ rỗng
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGenerateNextClientCode() {
        try {
            String code = clientDAO.generateNextClientCode();
            assertNotNull(code);
            assertTrue(code.startsWith("KH"), "Ma khach hang phai bat dau bang KH");
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }
}