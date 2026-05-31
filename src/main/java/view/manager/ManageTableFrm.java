package view.manager;

import dao.TableDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ManageTableFrm extends JFrame {
    private final User user;
    private final TableDAO tableDAO = new TableDAO();
    private final JTextField txtKeyword = new JTextField();
    private final DefaultTableModel tableModel;
    private final JTable tblTable;
    private ArrayList<model.Table> tables = new ArrayList<>();

    public ManageTableFrm(User user) {
        super("Quản lý bàn");
        this.user = user;
        setSize(820, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
            "ID", "Mã bàn", "Tên bàn", "Số khách tối đa", "Mô tả", "Trạng thái"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTable = new JTable(tableModel);
        tblTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tblTable), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        add(mainPanel);
        loadAllTables();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JLabel title = new JLabel("Quản lý thông tin bàn", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Từ khóa mã hoặc tên:"), BorderLayout.WEST);
        searchPanel.add(txtKeyword, BorderLayout.CENTER);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(this::search);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);
        panel.add(createButton("Tải lại", e -> loadAllTables()));
        panel.add(createButton("Thêm", e -> new AddTableFrm(this).setVisible(true)));
        panel.add(createButton("Sửa", e -> editSelectedTable()));
        panel.add(createButton("Xóa", e -> deleteSelectedTable()));
        panel.add(createButton("Quay lại", e -> backToHome()));
        return panel;
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    public void loadAllTables() {
        tables = tableDAO.getAllTables();
        fillTable();
    }

    private void search(ActionEvent e) {
        String keyword = txtKeyword.getText().trim();
        tables = keyword.isEmpty() ? tableDAO.getAllTables() : tableDAO.searchTables(keyword);
        fillTable();
        if (tables.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bàn.");
        }
    }

    private void fillTable() {
        tableModel.setRowCount(0);
        for (model.Table table : tables) {
            tableModel.addRow(new Object[]{
                table.getId(), table.getTableCode(), table.getName(), table.getCapacity(),
                table.getDescription(), table.getStatus()
            });
        }
    }

    private model.Table getSelectedTable() {
        int row = tblTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn.");
            return null;
        }
        return tables.get(row);
    }

    private void editSelectedTable() {
        model.Table table = getSelectedTable();
        if (table != null) {
            new EditTableFrm(this, table).setVisible(true);
        }
    }

    private void deleteSelectedTable() {
        model.Table table = getSelectedTable();
        if (table == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hoặc ngừng sử dụng bàn này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean hadBusiness = tableDAO.hasTableRelatedBusiness(table.getId());
        if (tableDAO.deleteOrDeactivateTable(table.getId())) {
            JOptionPane.showMessageDialog(this,
                    hadBusiness ? "Bàn đã phát sinh nghiệp vụ nên đã ngừng sử dụng."
                            : "Đã xóa bàn.");
            loadAllTables();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể xóa bàn.");
        }
    }

    private void backToHome() {
        new ManagerHomeFrame(user).setVisible(true);
        dispose();
    }
}
