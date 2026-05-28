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
    private JButton btnSearch;
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
            
            // Giả lập Dữ liệu
            listBooking = new ArrayList<>();
            Booking b1 = new Booking(); b1.setId(1); b1.setBookTime("19:00"); b1.setQuantity(4);
            try { b1.setBookDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse("20/05/2026")); } catch(Exception ex){}
            listBooking.add(b1);

            tableModel.setRowCount(0);
            if (listBooking != null) {
                for (Booking b : listBooking) {
                    // 1. Ép định dạng ngày thành dd/MM/yyyy
                    String dateStr = "";
                    if (b.getBookDate() != null) {
                        dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(b.getBookDate());
                    }
                    
                    // 2. Ghép ngày và giờ (Nếu bạn chỉ muốn hiện ngày thì bỏ phần b.getBookTime() đi)
                    String timeStr = b.getBookTime() != null ? b.getBookTime() : "";
                    String dateTimeDisplay = (dateStr + " " + timeStr).trim();

                    // 3. Đẩy vào bảng
                    tableModel.addRow(new Object[]{
                        b.getId(), 
                        "T001", // Mã bàn (Bạn có thể lấy từ list table của Booking)
                        dateTimeDisplay, 
                        b.getQuantity()
                    });
                }
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