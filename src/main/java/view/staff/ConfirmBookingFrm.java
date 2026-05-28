/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.BookedTable;
import model.Booking;
import model.Client;
import model.Table;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author annguyen
 */
public class ConfirmBookingFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JButton btnConfirm, btnCancel;
    private JTextField txtClientName, txtClientPhone, txtDatetime, txtQuantity, txtTableCode;

    public ConfirmBookingFrm(User user, Booking b) {
        super("Confirm Booking");
        this.user = user;
        this.booking = b;
        this.setSize(500, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(5)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Confirm Booking", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các Textfield bị khóa chỉnh sửa ngay từ khi khởi tạo
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client name"), gbc);
        gbc.gridx = 1; txtClientName = new JTextField(booking.getClient() != null ? booking.getClient().getName() : "", 20); 
        txtClientName.setEditable(false); formPanel.add(txtClientName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Client phone number"), gbc);
        gbc.gridx = 1; txtClientPhone = new JTextField(booking.getClient() != null ? booking.getClient().getPhone() : "", 20); 
        txtClientPhone.setEditable(false); formPanel.add(txtClientPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Datetime"), gbc);
        gbc.gridx = 1; 
        String dateStr = booking.getBookDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(booking.getBookDate()) : "";
        txtDatetime = new JTextField(dateStr + " " + (booking.getBookTime() != null ? booking.getBookTime() : ""), 20); 
        txtDatetime.setEditable(false); formPanel.add(txtDatetime, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Number of people"), gbc);
        gbc.gridx = 1; txtQuantity = new JTextField(String.valueOf(booking.getQuantity()), 20); 
        txtQuantity.setEditable(false); formPanel.add(txtQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Table Code"), gbc);
        gbc.gridx = 1; 
        StringBuilder tables = new StringBuilder();
        if(booking.getBookedTables() != null) {
            for(BookedTable bt : booking.getBookedTables()) {
                tables.append(bt.getTable().getTableCode()).append(", ");
            }
        }
        txtTableCode = new JTextField(tables.length() > 0 ? tables.substring(0, tables.length() - 2) : "", 20); 
        txtTableCode.setEditable(false); formPanel.add(txtTableCode, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        btnPanel.setOpaque(false);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(255, 69, 0));
        btnCancel.setPreferredSize(new Dimension(120, 35));

        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); 
        btnConfirm.setPreferredSize(new Dimension(120, 35));

        btnPanel.add(btnCancel); btnPanel.add(btnConfirm);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        btnCancel.addActionListener(this);
        btnConfirm.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCancel)) {
            // Hủy: Quay về màn 1
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
        } else if (e.getSource().equals(btnConfirm)) {
            // BỎ QUA XỬ LÝ DAO THEO YÊU CẦU
            JOptionPane.showMessageDialog(this, "Booking successfully!");
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
        }
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                User u = new User(); u.setName("An");
                Client c = new Client(); c.setName("An"); c.setPhone("0916385989");
                Table t1 = new Table(); t1.setTableCode("T001");
                Table t2 = new Table(); t2.setTableCode("T002");
                BookedTable bt1 = new BookedTable(); bt1.setTable(t1);
                BookedTable bt2 = new BookedTable(); bt2.setTable(t2);
                
                Booking b = new Booking();
                b.setClient(c);
                // Set chuẩn Date và Time tách biệt
                b.setBookDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse("20/05/2025"));
                b.setBookTime("19:00");
                b.setQuantity(6);
                b.addBookedTable(bt1);
                b.addBookedTable(bt2);
                
                new ConfirmBookingFrm(u, b).setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}