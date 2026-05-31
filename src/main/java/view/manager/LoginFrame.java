package view.manager;

import model.User;
import service.AuthService;
import view.staff.StaffHomeFrm;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        this(new AuthService());
    }

    LoginFrame(AuthService authService) {
        super("Dang nhap - Quan ly nha hang");
        this.authService = authService;
        configureFrame();
        buildContent();
    }

    private void configureFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420, 240);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buildContent() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 16, 32));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        formPanel.add(new JLabel("Ten dang nhap"), constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Mat khau"), constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, constraints);

        JButton loginButton = new JButton("Dang nhap");
        loginButton.addActionListener(event -> attemptLogin());

        JPanel actionPanel = new JPanel();
        actionPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        char[] passwordChars = passwordField.getPassword();
        try {
            User currentUser = authService.login(usernameField.getText(), new String(passwordChars));
            openHomeByRole(currentUser);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (Exception exception) {
            showError("Khong the dang nhap do loi ket noi du lieu: " + exception.getMessage());
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    private void openHomeByRole(User currentUser) {
        dispose();
        if ("MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            new ManagerHomeFrame(currentUser).setVisible(true);
            return;
        }
        if ("STAFF".equalsIgnoreCase(currentUser.getRole())) {
            new StaffHomeFrm(currentUser).setVisible(true);
            return;
        }
        showError("Vai tro khong duoc ho tro: " + currentUser.getRole());
        new LoginFrame().setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dang nhap that bai", JOptionPane.ERROR_MESSAGE);
        passwordField.selectAll();
        passwordField.requestFocusInWindow();
    }
}
