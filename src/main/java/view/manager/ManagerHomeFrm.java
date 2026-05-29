package view.manager;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManagerHomeFrm extends JFrame {
    private final User user;

    public ManagerHomeFrm(User user) {
        super("Manager Home");
        this.user = user;
        setSize(520, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(208, 232, 247));

        JLabel title = new JLabel("Manager Home", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 24));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(6, 1, 0, 12));
        buttons.setOpaque(false);
        buttons.add(createButton("Quản lý món ăn", e -> openDishManager()));
        buttons.add(createButton("Quản lý bàn", e -> openTableManager()));
        buttons.add(createButton("Quản lý khách hàng", e -> showOtherModule()));
        buttons.add(createButton("Quản lý nhân viên", e -> showOtherModule()));
        buttons.add(createButton("Báo cáo thống kê", e -> showOtherModule()));
        buttons.add(createButton("Thoát", e -> dispose()));

        mainPanel.add(buttons, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void openDishManager() {
        new ManageDishFrm(user).setVisible(true);
        dispose();
    }

    private void openTableManager() {
        new ManageTableFrm(user).setVisible(true);
        dispose();
    }

    private void showOtherModule() {
        JOptionPane.showMessageDialog(this, "Chức năng thuộc module khác.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User manager = new User();
            manager.setName("Manager");
            new ManagerHomeFrm(manager).setVisible(true);
        });
    }
}
