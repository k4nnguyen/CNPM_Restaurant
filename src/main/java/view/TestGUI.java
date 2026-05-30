package view;

import model.*;
import view.manager.BestSellingDishStatFrm;
import view.manager.ManagerHomeFrm;
import view.staff.BillDetailFrm;
import view.staff.SelectTableToPayFrm;
import view.staff.StaffHomeFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Launcher GUI test - dùng dữ liệu giả (mock), không cần kết nối CSDL.
 * Cho phép kiểm tra giao diện của 2 module:
 *   1. Thanh toán cho bàn (Nhân viên)
 *   2. Xem món ăn bán chạy (Quản lý)
 */
public class TestGUI extends JFrame implements ActionListener {

    private JButton btnTestStaff;
    private JButton btnTestManager;
    private JButton btnTestBillDetail;
    private JButton btnTestBestSelling;

    public TestGUI() {
        super("🧪 Test GUI - Hệ thống quản lý nhà hàng");
        initComponents();
        setSize(480, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel lblTitle = new JLabel("KIỂM TRA GIAO DIỆN (MOCK DATA)", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(44, 62, 80));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // Panel các nút
        JPanel pnlButtons = new JPanel(new GridLayout(4, 1, 10, 10));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        // Module nhân viên
        JLabel lblStaff = new JLabel("─── Module Nhân viên ───────────────────");
        lblStaff.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStaff.setForeground(new Color(41, 128, 185));
        pnlButtons.add(lblStaff);

        btnTestStaff = new JButton("💳  Trang chủ Nhân viên  →  Chọn bàn  →  Hóa đơn");
        btnTestStaff.setBackground(new Color(39, 174, 96));
        btnTestStaff.setForeground(Color.WHITE);
        btnTestStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTestStaff.setFocusPainted(false);
        btnTestStaff.addActionListener(this);
        pnlButtons.add(btnTestStaff);

        // Module quản lý
        JLabel lblManager = new JLabel("─── Module Quản lý ─────────────────────");
        lblManager.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblManager.setForeground(new Color(142, 68, 173));
        pnlButtons.add(lblManager);

        btnTestManager = new JButton("📊  Trang chủ Quản lý  →  Chọn báo cáo  →  Thống kê");
        btnTestManager.setBackground(new Color(142, 68, 173));
        btnTestManager.setForeground(Color.WHITE);
        btnTestManager.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTestManager.setFocusPainted(false);
        btnTestManager.addActionListener(this);
        pnlButtons.add(btnTestManager);

        add(pnlButtons, BorderLayout.CENTER);

        // Footer hint
        JLabel lblNote = new JLabel("⚠ Dữ liệu mock – không cần kết nối DB", JLabel.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNote.setForeground(new Color(127, 140, 141));
        lblNote.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(lblNote, BorderLayout.SOUTH);
    }

    // === Tạo User mock ===
    private User makeMockStaff() {
        User u = new User();
        u.setUsername("nvA");
        u.setFullName("Nguyễn Văn A");
        u.setRole("staff");
        return u;
    }

    private User makeMockManager() {
        User u = new User();
        u.setUsername("ttB");
        u.setFullName("Trần Thị B");
        u.setRole("manager");
        return u;
    }

    // === Tạo Order mock có sẵn dữ liệu ===
    private Order makeMockOrder() {
        Table tbl = new Table();
        tbl.setId(1);
        tbl.setTableCode("B01");
        tbl.setName("Bàn VIP 1");
        tbl.setCapacity(6);
        tbl.setStatus("Đang phục vụ");

        Dish d1 = new Dish(1, "D001", "Phở bò đặc biệt", "Món chính", 75000);
        Dish d2 = new Dish(2, "D005", "Bún bò Huế",       "Món chính", 65000);
        Dish d3 = new Dish(3, "D012", "Nước ngọt",        "Đồ uống",   20000);

        OrderItem i1 = new OrderItem(); i1.setDish(d1); i1.setQuantity(2); i1.setUnitPrice(75000);
        OrderItem i2 = new OrderItem(); i2.setDish(d2); i2.setQuantity(1); i2.setUnitPrice(65000);
        OrderItem i3 = new OrderItem(); i3.setDish(d3); i3.setQuantity(3); i3.setUnitPrice(20000);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(i1); items.add(i2); items.add(i3);

        Order order = new Order();
        order.setId(101);
        order.setTable(tbl);
        order.setOrderItems(items);
        return order;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTestStaff) {
            // Mở trang chủ nhân viên bình thường (flow thật qua StaffHomeFrm)
            new StaffHomeFrm(makeMockStaff()).setVisible(true);
        } else if (e.getSource() == btnTestManager) {
            // Mở trang chủ quản lý bình thường
            new ManagerHomeFrm(makeMockManager()).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TestGUI().setVisible(true);
        });
    }
}
