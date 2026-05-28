/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Booking;
import model.Client;
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
public class SearchClientFrm extends JFrame implements ActionListener {
    private User user;
    private Booking tmpBooking;
    private JTextField txtClientName, txtClientPhone;
    private JButton btnSearch, btnAddClient;
    private JTable tblClient;
    private DefaultTableModel tableModel;
    private ArrayList<Client> listClient;

    public SearchClientFrm(User user, Booking tmpBooking) {
        super("Search Client");
        this.user = user;
        this.tmpBooking = tmpBooking;
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
        headerPanel.add(new JLabel("(3)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Search Client", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client name"), gbc);
        gbc.gridx = 1; txtClientName = new JTextField(20); formPanel.add(txtClientName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Client phone number"), gbc);
        gbc.gridx = 1; txtClientPhone = new JTextField(20); formPanel.add(txtClientPhone, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(120, 30));
        
        btnAddClient = new JButton("Add Client");
        btnAddClient.setBackground(new Color(255, 255, 153));
        btnAddClient.setPreferredSize(new Dimension(120, 30));

        btnPanel.add(btnSearch); btnPanel.add(btnAddClient);

        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        String[] cols = {"ID", "Name", "Phone number", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Luôn trả về false -> Không ô nào sửa được
            }
        };
        tblClient = new JTable(tableModel);
        // Chỉ cho chọn 1 dòng
        tblClient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblClient), BorderLayout.CENTER);
        this.add(mainPanel);

        // Events
        btnSearch.addActionListener(this);
        btnAddClient.addActionListener(this);
        
        // Sự kiện phím Enter trên JTable để chọn khách
        tblClient.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        tblClient.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                processClientSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            String name = txtClientName.getText().trim();
            String phone = txtClientPhone.getText().trim();

            // 1. Validate: Phải nhập ít nhất 1 trong 2 trường
            if (name.isEmpty() && phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên hoặc Số điện thoại để tìm kiếm!");
                return;
            }

            // 2. Validate SDT: Chỉ kiểm tra nếu người dùng có nhập SDT
            if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải bao gồm đúng 10 chữ số!");
                return;
            }
            
            // --- KẾT NỐI DAO TẠI ĐÂY ---
            dao.ClientDAO dao = new dao.ClientDAO();
            // Truyền cả 2 tham số xuống DAO xử lý
            listClient = dao.searchClient(name, phone);
            
            tableModel.setRowCount(0);
            if (listClient != null && !listClient.isEmpty()) {
                for (Client c : listClient) {
                    tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getAddress()});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng nào phù hợp. Vui lòng thêm mới!");
            }
        } 
        else if (e.getSource().equals(btnAddClient)) {
            new AddClientFrm(user, tmpBooking).setVisible(true);
            this.dispose();
        }
    }
    
    private void processClientSelection() {
        int row = tblClient.getSelectedRow();
        if (row >= 0) {
            Client selectedClient = listClient.get(row);
            tmpBooking.setClient(selectedClient);
            new ConfirmBookingFrm(user, tmpBooking).setVisible(true);
            this.dispose(); 
        }
    }
    // --- Hàm main để test giao diện độc lập ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Tạo dữ liệu giả
                User dummyUser = new User();
                dummyUser.setName("An");
                
                Booking dummyBooking = new Booking(); // Phiếu đặt bàn ảo
                
                // 2. Gọi form
                new SearchClientFrm(dummyUser, dummyBooking).setVisible(true);
            }
        });
    }
}