package view.manager;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình chính của Quản lý nhà hàng.
 * Cung cấp các chức năng: Xem thống kê báo cáo.
 */
public class ManagerHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnViewStat;
    private JButton btnLogout;
    private JLabel lblWelcome;

    public ManagerHomeFrm() {
        this(new User(1, "admin", "System Manager", "MANAGER"));
    }

    public ManagerHomeFrm(User user) {
        super("Trang chủ - Quản lý nhà hàng");
        this.user = user;
        initComponents();
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(142, 68, 173));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblWelcome = new JLabel("Xin chào, " + (user != null ? user.getFullName() : "Quản lý") + " | Vai trò: Quản lý");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlHeader.add(lblWelcome, BorderLayout.WEST);

        btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(this);
        btnLogout.setFocusPainted(false);
        pnlHeader.add(btnLogout, BorderLayout.EAST);

        // Panel chức năng
        JPanel pnlMenu = new JPanel(new GridBagLayout());
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Nút xem thống kê
        btnViewStat = new JButton("📊  Xem thống kê báo cáo");
        btnViewStat.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnViewStat.setPreferredSize(new Dimension(300, 55));
        btnViewStat.setBackground(new Color(142, 68, 173));
        btnViewStat.setForeground(Color.WHITE);
        btnViewStat.setFocusPainted(false);
        btnViewStat.addActionListener(this);
        pnlMenu.add(btnViewStat, gbc);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlMenu, BorderLayout.CENTER);

        // Footer
        JLabel lblFooter = new JLabel("Hệ thống quản lý nhà hàng - Module Quản lý", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnViewStat)) {
            new SelectStatFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnLogout)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
            }
        }
    }

    public static void main(String[] args) {
        new ManagerHomeFrm().setVisible(true);
    }
}
