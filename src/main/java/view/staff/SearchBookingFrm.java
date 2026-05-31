/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Booking;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/**
 *
 * @author annguyen
 */
public class SearchBookingFrm extends JFrame implements ActionListener {
    private User user;
    private JTextField txtClientPhone;
    private JButton btnSearch, btnBack;
    private JTable tblBooking;
    private DefaultTableModel tableModel;
    private ArrayList<Booking> listBooking;

    public SearchBookingFrm(User u) {
        super("Search Booking");
        this.user = u;
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(6)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Search Booking", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client phone number"), gbc);
        gbc.gridx = 1; txtClientPhone = new JTextField("", 20); formPanel.add(txtClientPhone, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(150, 30));
        btnPanel.add(btnSearch);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        String[] cols = {"ID", "Table Code", "Datetime", "Number of people"};
        // Khóa không cho sửa dữ liệu bảng
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tblBooking = new JTable(tableModel);
        tblBooking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ chọn 1 dòng

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblBooking), BorderLayout.CENTER);
        this.add(mainPanel);

        btnSearch.addActionListener(this);
        btnBack.addActionListener(e -> {
            new StaffHomeFrm(this.user).setVisible(true); 
            this.dispose();
        });
        
        // Sự kiện Enter để chọn
        tblBooking.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        tblBooking.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                processSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            String phone = txtClientPhone.getText().trim();
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải bao gồm đúng 10 chữ số (không chứa chữ cái)!");
                return;
            }
            
            // --- KẾT NỐI DAO TẠI ĐÂY ---
            dao.BookingDAO dao = new dao.BookingDAO();
            listBooking = dao.searchBooking(txtClientPhone.getText().trim());

            tableModel.setRowCount(0);
            if (listBooking != null && !listBooking.isEmpty()) {
                for (Booking b : listBooking) {
                    String dateStr = "";
                    if(b.getBookDate() != null) {
                        dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(b.getBookDate());
                    }
                    String timeStr = b.getBookTime() != null ? b.getBookTime() : "";
                    
                    // --- BỔ SUNG: Ghép nối danh sách mã bàn ---
                    String tableCodes = "";
                    if (b.getBookedTables() != null && !b.getBookedTables().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (model.BookedTable bt : b.getBookedTables()) {
                            sb.append(bt.getTable().getTableCode()).append(", ");
                        }
                        // Cắt bỏ dấu phẩy và khoảng trắng thừa ở cuối chuỗi
                        tableCodes = sb.substring(0, sb.length() - 2); 
                    }
                    
                    // Đẩy dữ liệu chuẩn vào bảng
                    tableModel.addRow(new Object[]{b.getId(), tableCodes, dateStr + " " + timeStr, b.getQuantity()});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Khách hàng này chưa có phiếu đặt bàn nào!");
            }
        }
    }

    private void processSelection() {
        int row = tblBooking.getSelectedRow();
        if (row >= 0) {
            Booking selectedBooking = listBooking.get(row);
            new EditBookingFrm(user, selectedBooking).setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            new SearchBookingFrm(u).setVisible(true);
        });
    }
}