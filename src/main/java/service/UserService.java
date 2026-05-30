package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String MANAGER_ROLE = "MANAGER";
    private static final String STAFF_ROLE = "STAFF";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAO());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
    }

    public List<User> getActiveUsers() throws SQLException {
        return userDAO.findAllActive();
    }

    public List<User> searchActiveUsers(String keyword) throws SQLException {
        return userDAO.searchActive(normalizeOptional(keyword));
    }

    public User addUser(User user) throws SQLException {
        User normalizedUser = normalizeAndValidateUser(user, null, true);
        normalizedUser.setUserCode(userDAO.generateNextUserCode());
        normalizedUser.setStatus(ACTIVE_STATUS);
        return userDAO.addUser(normalizedUser);
    }

    public void updateUser(User user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Nhan vien can cap nhat khong hop le");
        }

        User previousUser = userDAO.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien can cap nhat"));
        User normalizedUser = normalizeAndValidateUser(user, user.getId(), false);

        normalizedUser.setId(previousUser.getId());
        normalizedUser.setUserCode(previousUser.getUserCode());
        normalizedUser.setStatus(previousUser.getStatus());
        if (normalizedUser.getPassword() == null || normalizedUser.getPassword().trim().isEmpty()) {
            normalizedUser.setPassword(previousUser.getPassword());
        }

        userDAO.updateUser(normalizedUser);
    }

    public void softDeleteUser(int id, User currentUser) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Nhan vien can xoa khong hop le");
        }
        if (currentUser == null || currentUser.getId() <= 0) {
            throw new IllegalArgumentException("Khong xac dinh duoc tai khoan dang dang nhap");
        }
        if (currentUser.getId() == id) {
            throw new IllegalArgumentException("Khong the xoa tai khoan dang dang nhap cua chinh minh");
        }

        User userToDelete = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien can xoa"));
        if (MANAGER_ROLE.equalsIgnoreCase(normalizeRequired(userToDelete.getRole()))
                && userDAO.countActiveManagers() <= 1) {
            throw new IllegalArgumentException("Khong the xoa quan ly cuoi cung dang hoat dong");
        }

        userDAO.softDeleteUser(id);
    }

    private User normalizeAndValidateUser(User source, Integer excludingId, boolean passwordRequired) throws SQLException {
        if (source == null) {
            throw new IllegalArgumentException("Thong tin nhan vien khong hop le");
        }

        User normalizedUser = new User();
        normalizedUser.setId(source.getId());
        normalizedUser.setUserCode(source.getUserCode());
        normalizedUser.setUsername(requireUsername(source.getUsername()));
        normalizedUser.setPassword(validatePassword(source.getPassword(), passwordRequired));
        normalizedUser.setName(requireFullName(source.getName()));
        normalizedUser.setRole(validateRole(source.getRole()));
        normalizedUser.setPhone(validatePhone(normalizeOptional(source.getPhone())));
        normalizedUser.setEmail(validateEmail(normalizeOptional(source.getEmail())));
        normalizedUser.setStatus(source.getStatus());

        validateDuplicateUsername(normalizedUser.getUsername(), excludingId);
        validateDuplicatePhone(normalizedUser.getPhone(), excludingId);
        validateDuplicateEmail(normalizedUser.getEmail(), excludingId);
        return normalizedUser;
    }

    private String requireUsername(String username) {
        String normalizedUsername = normalizeRequired(username);
        if (normalizedUsername.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap ten dang nhap");
        }
        return normalizedUsername;
    }

    private String requireFullName(String fullName) {
        String normalizedFullName = normalizeRequired(fullName);
        if (normalizedFullName.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap ho ten nhan vien");
        }
        return normalizedFullName;
    }

    private String validatePassword(String password, boolean required) {
        if (password == null) {
            if (required) {
                throw new IllegalArgumentException("Vui long nhap mat khau");
            }
            return null;
        }
        String normalizedPassword = password.trim();
        if (normalizedPassword.isEmpty() && required) {
            throw new IllegalArgumentException("Vui long nhap mat khau");
        }
        return normalizedPassword.isEmpty() ? null : normalizedPassword;
    }

    private String validateRole(String role) {
        String normalizedRole = normalizeRequired(role).toUpperCase();
        if (!MANAGER_ROLE.equals(normalizedRole) && !STAFF_ROLE.equals(normalizedRole)) {
            throw new IllegalArgumentException("Vai tro nhan vien chi duoc la MANAGER hoac STAFF");
        }
        return normalizedRole;
    }

    private String validatePhone(String phone) {
        if (phone == null) {
            return null;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("So dien thoai chi gom so va co the bat dau bang dau +");
        }
        int digitCount = phone.startsWith("+") ? phone.length() - 1 : phone.length();
        if (digitCount < 8 || digitCount > 15) {
            throw new IllegalArgumentException("So dien thoai phai co tu 8 den 15 chu so");
        }
        return phone;
    }

    private String validateEmail(String email) {
        if (email == null) {
            return null;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email khong dung dinh dang local@domain.tld");
        }
        return email;
    }

    private void validateDuplicateUsername(String username, Integer excludingId) throws SQLException {
        if (userDAO.existsByUsername(username, excludingId)) {
            throw new IllegalArgumentException("Ten dang nhap da trung voi nhan vien dang hoat dong");
        }
    }

    private void validateDuplicatePhone(String phone, Integer excludingId) throws SQLException {
        if (phone != null && userDAO.existsByPhone(phone, excludingId)) {
            throw new IllegalArgumentException("So dien thoai da trung voi nhan vien dang hoat dong");
        }
    }

    private void validateDuplicateEmail(String email, Integer excludingId) throws SQLException {
        if (email != null && userDAO.existsByEmail(email, excludingId)) {
            throw new IllegalArgumentException("Email da trung voi nhan vien dang hoat dong");
        }
    }

    private String normalizeRequired(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
