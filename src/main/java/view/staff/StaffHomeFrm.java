package view.staff;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình chính của Nhân viên phục vụ.
 * Cung cấp đầy đủ các chức năng: Đặt bàn, Sửa đặt bàn, Gọi món và Thanh toán.
 */
public class StaffHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnBookTable;
    private JButton btnEditBooking;
    private JButton btnOrderDish;
    private JButton btnPayTable;
    private JButton btnLogout;
    private JLabel lblWelcome;

    public StaffHomeFrm(User user) {
        super("Trang chủ - Nhân viên phục vụ");
        this.user = user;
        initComponents();
        setSize(520, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(41, 128, 185));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblWelcome = new JLabel("Xin chào, " + (user != null ? user.getFullName() : "Nhân viên") + " | Vai trò: Nhân viên");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlHeader.add(lblWelcome, BorderLayout.WEST);

        // Nút Đăng xuất
        btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(this);
        btnLogout.setFocusPainted(false);
        pnlHeader.add(btnLogout, BorderLayout.EAST);

        // Panel chức năng
        JPanel pnlMenu = new JPanel(new GridBagLayout());
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // 1. Nút Đặt bàn
        btnBookTable = new JButton("📅  Đặt bàn mới");
        btnBookTable.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBookTable.setPreferredSize(new Dimension(340, 45));
        btnBookTable.setBackground(new Color(41, 128, 185));
        btnBookTable.setForeground(Color.WHITE);
        btnBookTable.setFocusPainted(false);
        btnBookTable.addActionListener(this);
        pnlMenu.add(btnBookTable, gbc);

        // 2. Nút Sửa đặt bàn
        btnEditBooking = new JButton("✏️  Sửa thông tin đặt bàn");
        btnEditBooking.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditBooking.setPreferredSize(new Dimension(340, 45));
        btnEditBooking.setBackground(new Color(41, 128, 185));
        btnEditBooking.setForeground(Color.WHITE);
        btnEditBooking.setFocusPainted(false);
        btnEditBooking.addActionListener(this);
        pnlMenu.add(btnEditBooking, gbc);

        // 3. Nút Gọi món
        btnOrderDish = new JButton("🍽️  Gọi món tại bàn");
        btnOrderDish.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOrderDish.setPreferredSize(new Dimension(340, 45));
        btnOrderDish.setBackground(new Color(41, 128, 185));
        btnOrderDish.setForeground(Color.WHITE);
        btnOrderDish.setFocusPainted(false);
        btnOrderDish.addActionListener(this);
        pnlMenu.add(btnOrderDish, gbc);

        // 4. Nút Thanh toán cho bàn
        btnPayTable = new JButton("💳  Thanh toán hóa đơn");
        btnPayTable.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPayTable.setPreferredSize(new Dimension(340, 45));
        btnPayTable.setBackground(new Color(39, 174, 96)); // Màu xanh lá cho nút thanh toán
        btnPayTable.setForeground(Color.WHITE);
        btnPayTable.setFocusPainted(false);
        btnPayTable.addActionListener(this);
        pnlMenu.add(btnPayTable, gbc);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlMenu, BorderLayout.CENTER);

        // Footer
        JLabel lblFooter = new JLabel("Hệ thống quản lý nhà hàng - Module Nhân viên", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBookTable)) {
            new SearchFreeTableFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnEditBooking)) {
            new SearchBookingFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnOrderDish)) {
            new SelectTableFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnPayTable)) {
            new SelectTableToPayFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnLogout)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
            }
        }
    }
}