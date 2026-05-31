/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Order;
import model.OrderDish;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author annguyen
 */
public class ConfirmOrderFrm extends JFrame implements ActionListener {
    private User user;
    private Order order;
    private JButton btnBack, btnCancel, btnConfirm;

    public ConfirmOrderFrm(User u, Order order) {
        super("Confirm Order");
        this.user = u;
        this.order = order;
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(10)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Confirm Order", SwingConstants.CENTER), BorderLayout.CENTER);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153));
        btnBack.setFocusPainted(false);
        headerPanel.add(btnBack, BorderLayout.EAST);

        String[] cols = {"No.", "Dish name", "Quantity", "Price"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Khóa bảng
        };
        JTable tblOrderDetails = new JTable(model);
        
        double total = 0;
        int stt = 1;
        if (order.getOrderDishes() != null) {
            for (OrderDish od : order.getOrderDishes()) {
                if (od.getQuantity() > 0) {
                    double lineTotal = od.getQuantity() * od.getCurrentPrice();
                    total += lineTotal;
                    model.addRow(new Object[]{ String.format("%02d", stt++), od.getDish().getName(), od.getQuantity(), lineTotal });
                }
            }
        }
        order.setTotalAmount(total);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        JLabel lblTotalPrice = new JLabel("Total Price: " + total, SwingConstants.RIGHT);
        lblTotalPrice.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalPanel.add(new JLabel("Total Price:"), BorderLayout.WEST);
        totalPanel.add(lblTotalPrice, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(new JScrollPane(tblOrderDetails), BorderLayout.CENTER);
        centerPanel.add(totalPanel, BorderLayout.SOUTH);

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
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        this.add(mainPanel);

        btnBack.addActionListener(this);
        btnCancel.addActionListener(this);
        btnConfirm.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnConfirm)) {
            // --- KẾT NỐI DAO TẠI ĐÂY ---
            dao.OrderDAO dao = new dao.OrderDAO();
            order.setOrderTime(new java.util.Date()); // Chốt thời gian hiện tại
            
            if (dao.addOrder(order)) {
                JOptionPane.showMessageDialog(this, "Gọi món thành công! Hóa đơn đã được lưu.");
                new StaffHomeFrm(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn gọi món!");
            }
        } else if (e.getSource().equals(btnCancel)) {
            new StaffHomeFrm(user).setVisible(true); // Về Frm 1
            this.dispose();
        } else if (e.getSource().equals(btnBack)) {
            new OrderFrm(user, order).setVisible(true); // Lùi về Frm 9
            this.dispose();
        }
    }
}

