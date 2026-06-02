/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.BookedTable;
import model.Booking;
import model.Table;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author annguyen
 */
public class SearchFreeTableFrm extends JFrame implements ActionListener {
    private User user;
    private JTextField txtDatetime, txtQuantity;
    private JButton btnSearch, btnBack;
    private JTable tblFreeTable;
    private DefaultTableModel tableModel;
    private ArrayList<Table> listTable;

    public SearchFreeTableFrm(User user) {
        super("Search Free Table");
        this.user = user;
        
        // Phần giao diện
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Header & Form
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(2)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Search Free Table", SwingConstants.CENTER), BorderLayout.CENTER);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Datetime (dd/MM/yyyy HH:mm)"), gbc);
        gbc.gridx = 1; txtDatetime = new JTextField("", 20); formPanel.add(txtDatetime, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Number of people"), gbc);
        gbc.gridx = 1; txtQuantity = new JTextField("", 20); formPanel.add(txtQuantity, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnPanel.add(btnSearch);

        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);
        
        // Phần xử lý logic edit bảng (Không cho sửa bảng)
        // Table
        String[] cols = {"ID", "Table Code", "Capacity", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Luôn trả về false -> Không ô nào sửa được
            }
        };
        tblFreeTable = new JTable(tableModel);
        // Bật tính năng cho phép giữ Ctrl/Shift để chọn nhiều bàn
        tblFreeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblFreeTable), BorderLayout.CENTER);
        this.add(mainPanel);

        // --- Bắt sự kiện  ---
        btnSearch.addActionListener(this);
        btnBack.addActionListener(e -> {
            new StaffHomeFrm(this.user).setVisible(true); // Quay lại trang chủ nhân viên
            this.dispose(); // Đóng form hiện tại
        });
        // Sự kiện phím Enter trên JTable
        tblFreeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        tblFreeTable.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                processTableSelection();
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            // 1. Kiểm tra không để trống
            if (txtDatetime.getText().trim().isEmpty() || txtQuantity.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không được để trống ngày giờ hoặc số lượng!");
                return;
            }

            // 2. Kiểm tra định dạng ngày và thời gian tương lai
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setLenient(false); // Bắt lỗi nhập sai ngày chặt chẽ
            try {
                Date inputDate = sdf.parse(txtDatetime.getText().trim());
                if (inputDate.before(new Date())) {
                    JOptionPane.showMessageDialog(this, "Thời gian đặt bàn phải lớn hơn thời gian hiện tại!");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày giờ không hợp lệ! (Ví dụ: 20/05/2026 19:00)");
                return;
            }

            // 3. Kiểm tra số người > 0
            try {
                int qty = Integer.parseInt(txtQuantity.getText().trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng khách phải lớn hơn 0!");
                    return;
                }
                
                // --- KẾT NỐI DAO TẠI ĐÂY ---
                // Format lại ngày từ dd/MM/yyyy (UI) sang yyyy-MM-dd (SQL Server)
                Date inputDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(txtDatetime.getText().trim());
                String dbDate = new SimpleDateFormat("yyyy-MM-dd").format(inputDate);
                String dbTime = new SimpleDateFormat("HH:mm").format(inputDate);

                dao.TableDAO dao = new dao.TableDAO();
                listTable = dao.searchFreeTable(dbDate, dbTime, qty);

                tableModel.setRowCount(0); 
                if (listTable != null && !listTable.isEmpty()) {
                    for (Table t : listTable) {
                        tableModel.addRow(new Object[]{t.getId(), t.getTableCode(), t.getCapacity(), t.getStatus()});
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Không có bàn nào trống phù hợp!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi định dạng!");
            }
        }
    }
    
    // Hàm xử lý chọn nhiều bàn khi ấn Enter
    private void processTableSelection() {
        int[] selectedRows = tblFreeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 bàn!");
            return;
        }

        int requiredQty = Integer.parseInt(txtQuantity.getText().trim());
        int totalCapacity = 0; // Biến tính tổng sức chứa các bàn đã chọn

        Booking tempBooking = new Booking();
        tempBooking.setUser(this.user);
        // Tách chuỗi theo khoảng trắng
        String[] parts = txtDatetime.getText().trim().split(" ");
        try {
            tempBooking.setBookDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse(parts[0]));
            tempBooking.setBookTime(parts.length > 1 ? parts[1] : "");
        } catch (Exception ex) {}
        tempBooking.setQuantity(requiredQty);
        
        // Lặp qua các bàn được chọn để cộng dồn sức chứa và thêm vào Booking
        for (int i = 0; i < selectedRows.length; i++) {
            Table selectedTable = listTable.get(selectedRows[i]);
            totalCapacity += selectedTable.getCapacity(); // Cộng dồn
            
            BookedTable bt = new BookedTable();
            bt.setTable(selectedTable);
            tempBooking.addBookedTable(bt);
        }
        
        // KIỂM TRA ĐIỀU KIỆN GHÉP BÀN
        if (totalCapacity < requiredQty) {
            JOptionPane.showMessageDialog(this, 
                "Tổng sức chứa của các bàn đã chọn (" + totalCapacity + ") không đủ cho " + requiredQty + " khách!\n" +
                "Vui lòng giữ phím Ctrl và click chọn thêm bàn để ghép."
            );
            return; // Chặn lại, không cho sang trang tiếp theo
        }
        
        new SearchClientFrm(user, tempBooking).setVisible(true);
        this.dispose();
    }
    // --- Hàm main để test giao diện độc lập ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Tạo dữ liệu giả
                User dummyUser = new User();
                dummyUser.setName("An");
                
                // 2. Gọi form
                new SearchFreeTableFrm(dummyUser).setVisible(true);
            }
        });
    }
}