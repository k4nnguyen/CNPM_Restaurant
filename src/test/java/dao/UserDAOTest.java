package dao;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testFindAllActive() {
        try {
            List<User> list = userDAO.findAllActive();
            assertNotNull(list, "Danh sach User khong duoc null");
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSearchActive() {
        try {
            List<User> list = userDAO.searchActive("admin");
            assertNotNull(list);
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testFindActiveByUsernameAndPassword_WrongMatch() {
        try {
            Optional<User> user = userDAO.findActiveByUsernameAndPassword("wrong_admin", "wrong_pass");
            assertFalse(user.isPresent(), "Khong nen tim thay user vi sai thong tin");
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testExistsByUsername() {
        try {
            boolean exists = userDAO.existsByUsername("random_non_existent_username_123", null);
            assertFalse(exists);
        } catch (SQLException e) {
            fail("Exception: " + e.getMessage());
        }
    }
}
