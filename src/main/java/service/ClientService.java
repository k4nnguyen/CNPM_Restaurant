package service;

import dao.ClientDAO;
import model.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class ClientService {
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final ClientDAO clientDAO;

    public ClientService() {
        this(new ClientDAO());
    }

    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = Objects.requireNonNull(clientDAO, "clientDAO");
    }

    public List<Client> getActiveClients() throws SQLException {
        return clientDAO.findAllActive();
    }

    public List<Client> searchActiveClients(String keyword) throws SQLException {
        return clientDAO.searchActive(normalizeOptional(keyword));
    }

    public Client addClient(Client client) throws SQLException {
        Client normalizedClient = normalizeAndValidateClient(client, null);
        normalizedClient.setStatus(ACTIVE_STATUS);
        return clientDAO.saveNew(normalizedClient);
    }

    public void updateClient(Client client) throws SQLException {
        if (client == null || client.getId() <= 0) {
            throw new IllegalArgumentException("Khach hang can cap nhat khong hop le");
        }

        Optional<Client> existingClient = clientDAO.findById(client.getId());
        Client normalizedClient = normalizeAndValidateClient(client, client.getId());
        existingClient.ifPresent(existing -> {
            normalizedClient.setClientCode(existing.getClientCode());
            normalizedClient.setStatus(existing.getStatus());
        });

        clientDAO.saveExisting(normalizedClient);
    }

    public void softDeleteClient(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Khach hang can xoa khong hop le");
        }
        clientDAO.softDelete(id);
    }

    private Client normalizeAndValidateClient(Client source, Integer excludingId) throws SQLException {
        if (source == null) {
            throw new IllegalArgumentException("Thong tin khach hang khong hop le");
        }

        Client normalizedClient = new Client();
        normalizedClient.setId(source.getId());
        normalizedClient.setClientCode(source.getClientCode());
        normalizedClient.setName(requireFullName(source.getName()));
        normalizedClient.setPhone(validatePhone(normalizeOptional(source.getPhone())));
        normalizedClient.setEmail(validateEmail(normalizeOptional(source.getEmail())));
        normalizedClient.setAddress(normalizeOptional(source.getAddress()));
        normalizedClient.setStatus(source.getStatus());

        validateDuplicatePhone(normalizedClient.getPhone(), excludingId);
        validateDuplicateEmail(normalizedClient.getEmail(), excludingId);
        return normalizedClient;
    }

    private String requireFullName(String fullName) {
        String normalizedFullName = normalizeRequired(fullName);
        if (normalizedFullName.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap ho ten khach hang");
        }
        return normalizedFullName;
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

    private void validateDuplicatePhone(String phone, Integer excludingId) throws SQLException {
        if (phone == null) {
            return;
        }
        if (clientDAO.findAllActive().stream()
                .anyMatch(client -> sameValue(phone, client.getPhone()) && !isExcluded(client, excludingId))) {
            throw new IllegalArgumentException("So dien thoai da trung voi khach hang dang hoat dong");
        }
    }

    private void validateDuplicateEmail(String email, Integer excludingId) throws SQLException {
        if (email == null) {
            return;
        }
        if (clientDAO.findAllActive().stream()
                .anyMatch(client -> sameValue(email, client.getEmail()) && !isExcluded(client, excludingId))) {
            throw new IllegalArgumentException("Email da trung voi khach hang dang hoat dong");
        }
    }

    private boolean sameValue(String expected, String actual) {
        return expected != null && actual != null && expected.equalsIgnoreCase(actual.trim());
    }

    private boolean isExcluded(Client client, Integer excludingId) {
        return excludingId != null && client.getId() == excludingId;
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
