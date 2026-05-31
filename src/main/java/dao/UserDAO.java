package dao;

import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO extends DAO {
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String INACTIVE_STATUS = "INACTIVE";

    public UserDAO() {
        super();
    }

    public Optional<User> findActiveByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, userCode, username, password, name, role, phone, email, status "
                + "FROM tblUser WHERE username = ? AND password = ? AND status = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, ACTIVE_STATUS);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public List<User> findAllActive() throws SQLException {
        String sql = "SELECT id, userCode, username, password, name, role, phone, email, status "
                + "FROM tblUser WHERE status = ? ORDER BY name, userCode, id";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, ACTIVE_STATUS);
            return readUsers(statement);
        }
    }

    public List<User> searchActive(String keyword) throws SQLException {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return findAllActive();
        }

        String sql = "SELECT id, userCode, username, password, name, role, phone, email, status "
                + "FROM tblUser WHERE status = ? AND (userCode LIKE ? OR username LIKE ? OR name LIKE ? "
                + "OR role LIKE ? OR phone LIKE ? OR email LIKE ?) ORDER BY name, userCode, id";
        String likeKeyword = "%" + normalizedKeyword + "%";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, ACTIVE_STATUS);
            statement.setString(2, likeKeyword);
            statement.setString(3, likeKeyword);
            statement.setString(4, likeKeyword);
            statement.setString(5, likeKeyword);
            statement.setString(6, likeKeyword);
            statement.setString(7, likeKeyword);
            return readUsers(statement);
        }
    }

    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT id, userCode, username, password, name, role, phone, email, status FROM tblUser WHERE id = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public boolean existsByUsername(String username, Integer excludingId) throws SQLException {
        return existsByValue("username", username, excludingId);
    }

    public boolean existsByPhone(String phone, Integer excludingId) throws SQLException {
        return existsByValue("phone", phone, excludingId);
    }

    public boolean existsByEmail(String email, Integer excludingId) throws SQLException {
        return existsByValue("email", email, excludingId);
    }

    public int countActiveManagers() throws SQLException {
        String sql = "SELECT COUNT(*) AS activeManagerCount FROM tblUser WHERE role = ? AND status = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, "MANAGER");
            statement.setString(2, ACTIVE_STATUS);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("activeManagerCount");
                }
            }
        }
        return 0;
    }

    public String generateNextUserCode() throws SQLException {
        String sql = "SELECT MAX(userCode) AS maxUserCode FROM tblUser WHERE userCode LIKE ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, "NV%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String maxCode = resultSet.getString("maxUserCode");
                    if (maxCode != null && maxCode.startsWith("NV")) {
                        try {
                            return String.format("NV%03d", Integer.parseInt(maxCode.substring(2)) + 1);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return "NV001";
    }

    public User addUser(User user) throws SQLException {
        String sql = "INSERT INTO tblUser(userCode, username, password, name, role, phone, email, status) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUserCode());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getName());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getPhone());
            statement.setString(7, user.getEmail());
            statement.setString(8, ACTIVE_STATUS);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            user.setStatus(ACTIVE_STATUS);
            return user;
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE tblUser SET username = ?, password = ?, name = ?, role = ?, phone = ?, email = ? WHERE id = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getEmail());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        }
    }

    public void softDeleteUser(int id) throws SQLException {
        String sql = "UPDATE tblUser SET status = ? WHERE id = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, INACTIVE_STATUS);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    private boolean existsByValue(String columnName, String value, Integer excludingId) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM tblUser WHERE " + columnName + " = ? AND status = ? AND (? IS NULL OR id <> ?)";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            statement.setString(2, ACTIVE_STATUS);
            if (excludingId == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, excludingId);
                statement.setInt(4, excludingId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private List<User> readUsers(PreparedStatement statement) throws SQLException {
        List<User> users = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        }
        return users;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserCode(resultSet.getString("userCode"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setName(resultSet.getString("name"));
        user.setRole(resultSet.getString("role"));
        user.setPhone(resultSet.getString("phone"));
        user.setEmail(resultSet.getString("email"));
        user.setStatus(resultSet.getString("status"));
        return user;
    }
}
