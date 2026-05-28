/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Booking;
import model.User;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author annguyen
 */
public class EditBookingFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JTextField txtDate, txtTime, txtQuantity;
    private JButton btnCheckFreeTable, btnConfirm;
    private boolean isCheckedAndFree = false; // Cờ kiểm tra trạng thái hợp lệ

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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(7)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Edit Booking", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String dateStr = booking.getBookDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(booking.getBookDate()) : "";
        
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Date (dd/MM/yyyy)"), gbc);
        gbc.gridx = 1; txtDate = new JTextField(dateStr, 20); formPanel.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Time (HH:mm)"), gbc);
        gbc.gridx = 1; txtTime = new JTextField(booking.getBookTime() != null ? booking.getBookTime() : "", 20); formPanel.add(txtTime, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Number of people"), gbc);
        gbc.gridx = 1; txtQuantity = new JTextField(String.valueOf(booking.getQuantity()), 20); formPanel.add(txtQuantity, gbc);

        // Lắng nghe sự thay đổi text -> Nếu sửa thì bắt check lại
        DocumentListener resetFlagListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { isCheckedAndFree = false; }
            public void removeUpdate(DocumentEvent e) { isCheckedAndFree = false; }
            public void changedUpdate(DocumentEvent e) { isCheckedAndFree = false; }
        };
        txtDate.getDocument().addDocumentListener(resetFlagListener);
        txtTime.getDocument().addDocumentListener(resetFlagListener);
        txtQuantity.getDocument().addDocumentListener(resetFlagListener);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        btnPanel.setOpaque(false);
        
        btnCheckFreeTable = new JButton("Check Free Table");
        btnCheckFreeTable.setBackground(new Color(255, 255, 153)); 
        btnCheckFreeTable.setPreferredSize(new Dimension(150, 35));

        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); 
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

    private boolean validateInputs() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setLenient(false);
        try {
            Date inputDate = sdf.parse(txtDate.getText().trim() + " " + txtTime.getText().trim());
            if (inputDate.before(new Date())) {
                JOptionPane.showMessageDialog(this, "Thời gian phải lớn hơn hiện tại!");
                return false;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày/giờ sai! (VD: 20/05/2026 và 19:00)");
            return false;
        }

        try {
            int q = Integer.parseInt(txtQuantity.getText().trim());
            if (q <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải > 0!");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số!");
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCheckFreeTable)) {
            if (validateInputs()) {
                // Giả lập DAO trả về true (Bàn còn trống)
                isCheckedAndFree = true;
                JOptionPane.showMessageDialog(this, "Bàn hợp lệ, có thể xác nhận đổi!");
            }
        } else if (e.getSource().equals(btnConfirm)) {
            if (!isCheckedAndFree) {
                JOptionPane.showMessageDialog(this, "Vui lòng bấm 'Check Free Table' trước khi Confirm!");
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            Booking b = new Booking(); b.setQuantity(5);
            new EditBookingFrm(u, b).setVisible(true);
        });
    }
}