package view.manager;

import dao.DishStatDAO;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Màn hình thống kê món ăn bán chạy theo khoảng thời gian.
 * Quản lý nhập ngày bắt đầu và kết thúc, hệ thống hiển thị bảng xếp hạng
 * các món ăn sắp xếp theo doanh thu giảm dần.
 */
public class BestSellingDishStatFrm extends JFrame implements ActionListener {
    private User user;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JButton btnStat;
    private JButton btnBack;
    private JTable tblDishStat;
    private DefaultTableModel tableModel;
    private ArrayList<DishStat> listStat;
    private JLabel lblTotalRevenue;

    public BestSellingDishStatFrm(User user) {
        super("Thống kê món ăn bán chạy");
        this.user = user;
        initComponents();
        setSize(780, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // === Panel Tiêu đề ===
        JLabel lblTitle = new JLabel("THỐNG KÊ MÓN ĂN BÁN CHẠY", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(142, 68, 173));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // === Panel Bộ lọc thời gian ===
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Khoảng thời gian thống kê"));

        pnlFilter.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        txtStartDate = new JTextField(12);
        txtStartDate.setText("2026-01-01");
        txtStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlFilter.add(txtStartDate);

        pnlFilter.add(new JLabel("Đến ngày (yyyy-MM-dd):"));
        txtEndDate = new JTextField(12);
        txtEndDate.setText("2026-12-31");
        txtEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlFilter.add(txtEndDate);

        btnStat = new JButton("📊  Thống kê");
        btnStat.setBackground(new Color(142, 68, 173));
        btnStat.setForeground(Color.WHITE);
        btnStat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStat.setFocusPainted(false);
        btnStat.addActionListener(this);
        pnlFilter.add(btnStat);

        add(pnlFilter, BorderLayout.NORTH);

        // === Bảng kết quả ===
        String[] columns = {"Hạng", "Mã món", "Tên món", "Danh mục", "Đơn giá (VNĐ)", "Tổng SL bán", "Tổng doanh thu (VNĐ)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDishStat = new JTable(tableModel);
        tblDishStat.setRowHeight(28);
        tblDishStat.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblDishStat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Đặt độ rộng cột
        tblDishStat.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblDishStat.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblDishStat.getColumnModel().getColumn(2).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(tblDishStat);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bảng xếp hạng món ăn"));
        add(scrollPane, BorderLayout.CENTER);

        // === Panel tổng và nút điều hướng ===
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JPanel pnlRevenue = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblRevLabel = new JLabel("TỔNG DOANH THU: ");
        lblRevLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalRevenue = new JLabel("0 VNĐ");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalRevenue.setForeground(new Color(192, 57, 43));
        pnlRevenue.add(lblRevLabel);
        pnlRevenue.add(lblTotalRevenue);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBack = new JButton("← Quay lại");
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(this);
        pnlButtons.add(btnBack);

        pnlBottom.add(pnlButtons, BorderLayout.WEST);
        pnlBottom.add(pnlRevenue, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void performStatistic() {
        String startDate = txtStartDate.getText().trim();
        String endDate = txtEndDate.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày bắt đầu và ngày kết thúc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        DishStatDAO dao = new DishStatDAO();
        listStat = dao.getBestSellingDish(startDate, endDate);

        if (listStat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu bán hàng trong khoảng thời gian này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            lblTotalRevenue.setText("0 VNĐ");
            return;
        }

        double totalRevenue = 0;
        int rank = 1;
        for (DishStat ds : listStat) {
            tableModel.addRow(new Object[]{
                rank++,
                ds.getDishCode(),
                ds.getName(),
                ds.getCategory(),
                String.format("%,.0f", ds.getPrice()),
                ds.getTotalQuantity(),
                String.format("%,.0f", ds.getTotalRevenue())
            });
            totalRevenue += ds.getTotalRevenue();
        }
        lblTotalRevenue.setText(String.format("%,.0f VNĐ", totalRevenue));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnStat)) {
            performStatistic();
        } else if (e.getSource().equals(btnBack)) {
            new SelectStatFrm(user).setVisible(true);
            this.dispose();
        }
    }
}
