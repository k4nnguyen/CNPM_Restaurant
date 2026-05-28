package view.manager;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình chọn loại báo cáo thống kê.
 * Cung cấp menu để quản lý chọn loại thống kê muốn xem:
 * - Thống kê món ăn bán chạy
 */
public class SelectStatFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnBestSellingDish;
    private JButton btnBack;

    public SelectStatFrm(User user) {
        super("Chọn loại báo cáo thống kê");
        this.user = user;
        initComponents();
        setSize(480, 330);
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

        btnBestSellingDish = new JButton("🍽️  Thống kê món ăn bán chạy");
        btnBestSellingDish.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBestSellingDish.setPreferredSize(new Dimension(320, 50));
        btnBestSellingDish.setBackground(new Color(142, 68, 173));
        btnBestSellingDish.setForeground(Color.WHITE);
        btnBestSellingDish.setFocusPainted(false);
        btnBestSellingDish.addActionListener(this);
        pnlOptions.add(btnBestSellingDish, gbc);

        add(pnlOptions, BorderLayout.CENTER);

        // Nút quay lại
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        btnBack = new JButton("← Quay lại trang chủ");
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(this);
        pnlBottom.add(btnBack);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBestSellingDish)) {
            new BestSellingDishStatFrm(user).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource().equals(btnBack)) {
            new ManagerHomeFrm(user).setVisible(true);
            this.dispose();
        }
    }
}
