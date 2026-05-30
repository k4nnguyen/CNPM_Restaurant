package view.manager;

import model.User;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ManagerHomeFrame extends JFrame {
    private final User currentUser;

    public ManagerHomeFrame(User currentUser) {
        super("Quan ly nha hang - Manager");
        this.currentUser = currentUser;
        configureFrame();
        buildContent();
    }

    private void configureFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(560, 360);
        setLocationRelativeTo(null);
    }

    private void buildContent() {
        JPanel rootPanel = new JPanel(new BorderLayout(16, 16));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel titleLabel = new JLabel("Quan ly nha hang", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(22.0f));

        JLabel userLabel = new JLabel(buildUserGreeting(), SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.add(titleLabel);
        headerPanel.add(userLabel);

        JPanel navigationPanel = new JPanel(new GridLayout(3, 1, 12, 12));
        JButton clientButton = new JButton("Quan ly khach hang");
        JButton staffButton = new JButton("Quan ly nhan vien");
        JButton logoutButton = new JButton("Dang xuat");

        clientButton.addActionListener(event -> new ManageClientFrame(this).setVisible(true));
        staffButton.addActionListener(event -> new ManageStaffFrame(currentUser).setVisible(true));
        logoutButton.addActionListener(event -> logout());

        navigationPanel.add(clientButton);
        navigationPanel.add(staffButton);
        navigationPanel.add(logoutButton);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(navigationPanel, BorderLayout.CENTER);
        add(rootPanel);
    }

    private String buildUserGreeting() {
        if (currentUser == null) {
            return "Dang dang nhap voi quyen Manager";
        }

        String displayName = currentUser.getName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = currentUser.getUsername();
        }

        String username = currentUser.getUsername() == null ? "" : currentUser.getUsername();
        return "Xin chao " + displayName + " (" + username + ") - " + currentUser.getRole();
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
