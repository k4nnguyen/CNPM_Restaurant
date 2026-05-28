/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import dao.BookingDAO;
import model.Booking;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author annguyen
 */
public class EditBookingFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JTextField txtDate, txtTime, txtQuantity;
    private JButton btnCheckFreeTable, btnConfirm;

    public EditBookingFrm(User u, Booking booking) {
        super("Edit Booking");
        this.user = u;
        this.booking = booking;
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(7)"), BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Edit Booking", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Date"), gbc);
        gbc.gridx = 1; txtDate = new JTextField(booking.getBookDate() != null ? booking.getBookDate().toString() : "21/05/2025", 20); formPanel.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Time"), gbc);
        gbc.gridx = 1; txtTime = new JTextField(booking.getBookTime() != null ? booking.getBookTime() : "19:30", 20); formPanel.add(txtTime, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Number of people"), gbc);
        gbc.gridx = 1; txtQuantity = new JTextField(String.valueOf(booking.getQuantity()), 20); formPanel.add(txtQuantity, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        btnPanel.setOpaque(false);
        
        btnCheckFreeTable = new JButton("Check Free Table");
        btnCheckFreeTable.setBackground(new Color(255, 255, 153)); // Vàng nhạt
        btnCheckFreeTable.setFocusPainted(false);
        btnCheckFreeTable.setPreferredSize(new Dimension(150, 35));

        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); // Xanh lá
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(150, 35));

        btnPanel.add(btnCheckFreeTable);
        btnPanel.add(btnConfirm);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        btnCheckFreeTable.addActionListener(this);
        btnConfirm.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCheckFreeTable)) {
            JOptionPane.showMessageDialog(this, "Table is available!");
        } else if (e.getSource().equals(btnConfirm)) {
            BookingDAO dao = new BookingDAO();
            booking.setBookTime(txtTime.getText());
            booking.setQuantity(Integer.parseInt(txtQuantity.getText()));
            
            if (dao.updateBooking(booking)) {
                JOptionPane.showMessageDialog(this, "Update successfully!");
                new StaffHomeFrm(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!");
            }
        }
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            Booking b = new Booking(); b.setQuantity(5);
            new EditBookingFrm(u, b).setVisible(true);
        });
    }
}