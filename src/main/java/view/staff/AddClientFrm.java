/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Booking;
import model.Client;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author annguyen
 */
public class AddClientFrm extends JFrame implements ActionListener {
    private User user;
    private JTextField txtClientName, txtClientPhone, txtClientEmail, txtClientAddress;
    private JButton btnAddClient, btnBack;

    public AddClientFrm(User user) {
        super("Add New Client");
        this.user = user;
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(4)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Add New Client", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client name"), gbc);
        gbc.gridx = 1; txtClientName = new JTextField(20); formPanel.add(txtClientName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Client phone number"), gbc);
        gbc.gridx = 1; txtClientPhone = new JTextField(20); formPanel.add(txtClientPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Client email"), gbc);
        gbc.gridx = 1; txtClientEmail = new JTextField(20); formPanel.add(txtClientEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Client address"), gbc);
        gbc.gridx = 1; txtClientAddress = new JTextField(20); formPanel.add(txtClientAddress, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnAddClient = new JButton("Add Client");
        btnAddClient.setBackground(new Color(255, 255, 153));
        btnAddClient.setPreferredSize(new Dimension(150, 30));
        btnPanel.add(btnAddClient);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
        btnAddClient.addActionListener(this);
        btnBack.addActionListener(e -> {
            new SearchClientFrm(this.user).setVisible(true); 
            this.dispose(); // Đóng form hiện tại
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnAddClient)) {
            String name = txtClientName.getText().trim();
            String phone = txtClientPhone.getText().trim();
            String email = txtClientEmail.getText().trim();
            String address = txtClientAddress.getText().trim();
            
            // 1. Check rỗng
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tất cả các trường!");
                return;
            }
            
            // 2. Validate Phone
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải đúng 10 chữ số!");
                return;
            }
            
            // 3. Validate Email
            if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ (ví dụ: abcxyz@gmail.com)!");
                return;
            }
            
            Client c = new Client();
            c.setName(name); c.setPhone(phone); c.setEmail(email); c.setAddress(address);
            
            dao.ClientDAO dao = new dao.ClientDAO();
            if (dao.addClient(c)) {
                JOptionPane.showMessageDialog(this, "Them khach hang thanh cong!"); 
                new SearchClientFrm(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi! Số điện thoại có thể đã tồn tại.");
            }
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
                new AddClientFrm(dummyUser).setVisible(true);
            }
        });
    }
}



