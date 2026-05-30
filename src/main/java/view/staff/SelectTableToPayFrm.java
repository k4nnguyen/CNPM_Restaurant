package view.staff;

import dao.OrderDAO;
import dao.TableDAO;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Màn hình chọn bàn cần thanh toán.
 * Hiển thị danh sách tất cả các bàn đang ở trạng thái "Đang phục vụ".
 * Nhân viên chọn bàn → hệ thống tải Order từ DAO → mở BillDetailFrm(user, order).
 *
 * Theo thiết kế CE02:
 *   - Thuộc tính: user (User), tblServingTable (JTable)
 *   - Constructor: SelectTableToPayFrm(u: User)
 *   - Implement: ActionListener → actionPerformed(e: ActionEvent): void
 */
public class SelectTableToPayFrm extends JFrame implements ActionListener {
    private User user;
    private JTable tblServingTable;
    private DefaultTableModel tableModel;
    private ArrayList<Table> listTable;
    private JButton btnSelect;
    private JButton btnBack;
    private JButton btnRefresh;

    /**
     * Constructor theo thiết kế CE02: SelectTableToPayFrm(u: User)
     */
    public SelectTableToPayFrm(User u) {
        super("Chọn bàn cần thanh toán");
        this.user = u;
        initComponents();
        loadData();
        setSize(650, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel tiêu đề
        JLabel lblTitle = new JLabel("Danh sách bàn đang phục vụ", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        lblTitle.setForeground(new Color(41, 128, 185));
        add(lblTitle, BorderLayout.NORTH);

        // Bảng danh sách bàn
        String[] columns = {"STT", "Mã bàn", "Tên bàn", "Sức chứa", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblServingTable = new JTable(tableModel);
        tblServingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblServingTable.setRowHeight(28);
        tblServingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblServingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(tblServingTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(this);
        btnRefresh.setFocusPainted(false);

        btnSelect = new JButton("📋 Xem chi tiết hóa đơn");
        btnSelect.setBackground(new Color(39, 174, 96));
        btnSelect.setForeground(Color.WHITE);
        btnSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSelect.setFocusPainted(false);
        btnSelect.addActionListener(this);

        btnBack = new JButton("← Quay lại");
        btnBack.addActionListener(this);
        btnBack.setFocusPainted(false);

        pnlButtons.add(btnBack);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnSelect);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        TableDAO dao = new TableDAO();
        listTable = dao.getServingTables();
        if (listTable.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Hiện tại không có bàn nào đang phục vụ.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int stt = 1;
            for (Table t : listTable) {
                tableModel.addRow(new Object[]{
                    stt++, t.getTableCode(), t.getName(), t.getCapacity() + " người", t.getStatus()
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSelect)) {
            int row = tblServingTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Table selectedTable = listTable.get(row);
            // Tải Order từ CSDL rồi truyền vào BillDetailFrm(user, order) theo đúng thiết kế CE02
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.getOrderDetail(selectedTable.getId());
            if (order == null) {
                JOptionPane.showMessageDialog(this,
                        "Bàn " + selectedTable.getTableCode() + " chưa có đơn đặt món nào chưa thanh toán.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new BillDetailFrm(user, order).setVisible(true);
            this.dispose();
        } else if (e.getSource().equals(btnRefresh)) {
            loadData();
        } else if (e.getSource().equals(btnBack)) {
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
        }
    }
}
