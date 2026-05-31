package view.manager;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình chọn loại báo cáo thống kê.
 * Cung cấp menu để quản lý chọn loại thống kê muốn xem:
 * - Thống kê lượng khách theo khung giờ
 * - Báo cáo doanh thu theo tháng
 * - Thống kê món ăn bán chạy
 */
public class SelectStatFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnGuestStat;
    private JButton btnRevenueStat;
    private JButton btnBestSellingDish;
    private JButton btnBack;

    public SelectStatFrm() {
        this(new User(1, "admin", "System Manager", "MANAGER"));
    }

    public SelectStatFrm(User user) {
        super("Chọn loại báo cáo thống kê");
        this.user = user;
        initComponents();
        setSize(480, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chọn loại báo cáo", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(142, 68, 173));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Panel các lựa chọn
        JPanel pnlOptions = new JPanel(new GridBagLayout());
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Lựa chọn 1: Thống kê lượng khách theo khung giờ
        btnGuestStat = new JButton("Th\u1ed1ng k\u00ea l\u01b0\u1ee3ng kh\u00e1ch theo khung gi\u1edd");
        btnGuestStat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuestStat.setPreferredSize(new Dimension(340, 45));
        btnGuestStat.setBackground(new Color(142, 68, 173));
        btnGuestStat.setForeground(Color.WHITE);
        btnGuestStat.setFocusPainted(false);
        btnGuestStat.addActionListener(this);
        pnlOptions.add(btnGuestStat, gbc);

        // Lựa chọn 2: Báo cáo doanh thu theo tháng
        btnRevenueStat = new JButton("B\u00e1o c\u00e1o doanh thu theo th\u00e1ng");
        btnRevenueStat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRevenueStat.setPreferredSize(new Dimension(340, 45));
        btnRevenueStat.setBackground(new Color(142, 68, 173));
        btnRevenueStat.setForeground(Color.WHITE);
        btnRevenueStat.setFocusPainted(false);
        btnRevenueStat.addActionListener(this);
        pnlOptions.add(btnRevenueStat, gbc);

        // Lựa chọn 3: Thống kê món ăn bán chạy
        btnBestSellingDish = new JButton("Th\u1ed1ng k\u00ea m\u00f3n \u0103n b\u00e1n ch\u1ea1y");
        btnBestSellingDish.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBestSellingDish.setPreferredSize(new Dimension(340, 45));
        btnBestSellingDish.setBackground(new Color(142, 68, 173));
        btnBestSellingDish.setForeground(Color.WHITE);
        btnBestSellingDish.setFocusPainted(false);
        btnBestSellingDish.addActionListener(this);
        pnlOptions.add(btnBestSellingDish, gbc);

        add(pnlOptions, BorderLayout.CENTER);

        // Nút quay lại
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        btnBack = new JButton("Quay l\u1ea1i trang ch\u1ee7");
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(this);
        pnlBottom.add(btnBack);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGuestStat) {
            new GuestStatFrm().setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnRevenueStat) {
            new MonthlyRevenueFrm().setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnBestSellingDish) {
            new BestSellingDishStatFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnBack) {
            new ManagerHomeFrame(user).setVisible(true);
            this.dispose();
        }
    }
}
