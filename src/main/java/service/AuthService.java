package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) throws SQLException {
        String normalizedUsername = validateUsername(username);
        validatePassword(password);

        Optional<User> authenticatedUser = userDAO.findActiveByUsernameAndPassword(normalizedUsername, password);
        return authenticatedUser.orElseThrow(() ->
                new IllegalArgumentException("Ten dang nhap hoac mat khau khong dung"));
    }

    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap ten dang nhap");
        }
        return username.trim();
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap mat khau");
        }
    }
}
