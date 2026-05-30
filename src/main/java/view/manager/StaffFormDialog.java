package view.manager;

import model.User;
import service.UserService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class StaffFormDialog extends JDialog {
    private final UserService userService;
    private final User editingUser;
    private final JTextField userCodeField = new JTextField(24);
    private final JTextField usernameField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JTextField fullNameField = new JTextField(24);
    private final JComboBox<String> roleComboBox = new JComboBox<>(new String[] {"MANAGER", "STAFF"});
    private final JTextField phoneField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private boolean saved;

    public StaffFormDialog(Frame owner, UserService userService) {
        this(owner, userService, null);
    }

    public StaffFormDialog(Frame owner, UserService userService, User editingUser) {
        super(owner, editingUser == null ? "Them nhan vien" : "Sua nhan vien", true);
        this.userService = userService;
        this.editingUser = editingUser;
        configureDialog(owner);
        buildContent();
        populateForm();
    }

    public boolean isSaved() {
        return saved;
    }

    private void configureDialog(Frame owner) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(500, 430);
        setLocationRelativeTo(owner);
    }

    private void buildContent() {
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        userCodeField.setEditable(false);

        addField(formPanel, constraints, 0, "Ma nhan vien:", userCodeField);
        addField(formPanel, constraints, 1, "Ten dang nhap:", usernameField);
        addField(formPanel, constraints, 2, "Mat khau:", passwordField);
        addField(formPanel, constraints, 3, "Ho ten:", fullNameField);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0;
        formPanel.add(new JLabel("Vai tro:"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        formPanel.add(roleComboBox, constraints);

        addField(formPanel, constraints, 5, "So dien thoai:", phoneField);
        addField(formPanel, constraints, 6, "Email:", emailField);

        JLabel passwordHintLabel = new JLabel("Khi sua, de trong mat khau de giu mat khau hien tai.");
        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.weightx = 1;
        formPanel.add(passwordHintLabel, constraints);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Luu");
        JButton cancelButton = new JButton("Huy");
        saveButton.addActionListener(event -> saveUser());
        cancelButton.addActionListener(event -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(rootPanel);
    }

    private void addField(JPanel formPanel, GridBagConstraints constraints, int row, String label, JTextField field) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        formPanel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        formPanel.add(field, constraints);
    }

    private void populateForm() {
        if (editingUser == null) {
            userCodeField.setText("Tu dong tao khi luu");
            roleComboBox.setSelectedItem("STAFF");
            return;
        }

        userCodeField.setText(safeText(editingUser.getUserCode()));
        usernameField.setText(safeText(editingUser.getUsername()));
        passwordField.setText("");
        fullNameField.setText(safeText(editingUser.getName()));
        roleComboBox.setSelectedItem(safeText(editingUser.getRole()).isEmpty() ? "STAFF" : editingUser.getRole());
        phoneField.setText(safeText(editingUser.getPhone()));
        emailField.setText(safeText(editingUser.getEmail()));
    }

    private void saveUser() {
        if (editingUser == null && getPassword().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap mat khau khi them nhan vien.");
            return;
        }

        User user = buildUserFromForm();
        try {
            if (editingUser == null) {
                userService.addUser(user);
            } else {
                userService.updateUser(user);
            }
            saved = true;
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Du lieu khong hop le", JOptionPane.WARNING_MESSAGE);
        }
    }

    private User buildUserFromForm() {
        User user = new User();
        if (editingUser != null) {
            user.setId(editingUser.getId());
            user.setUserCode(editingUser.getUserCode());
            user.setStatus(editingUser.getStatus());
        }
        user.setUsername(usernameField.getText());
        user.setPassword(getPassword());
        user.setName(fullNameField.getText());
        user.setRole((String) roleComboBox.getSelectedItem());
        user.setPhone(phoneField.getText());
        user.setEmail(emailField.getText());
        return user;
    }

    private String getPassword() {
        return new String(passwordField.getPassword());
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }
}
