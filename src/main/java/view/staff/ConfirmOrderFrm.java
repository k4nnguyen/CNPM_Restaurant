/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import dao.OrderDAO;
import model.Dish;
import model.Order;
import model.OrderDish;
import model.Table;
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
    private JTable tblOrderDetails;
    private JLabel lblTotalPrice;
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

        // Header (Có nút Back màu vàng)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(10)"), BorderLayout.WEST);
        
        JLabel lblTitle = new JLabel("Confirm Order", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153)); // Vàng nhạt
        btnBack.setFocusPainted(false);
        headerPanel.add(btnBack, BorderLayout.EAST);

        // Bảng chi tiết món đã chọn
        String[] cols = {"No.", "Dish name", "Quantity", "Price"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Luôn trả về false -> Không ô nào sửa được
            }
        };
        tblOrderDetails = new JTable(model);
        
        double total = 0;
        int stt = 1;
        if (order.getOrderDishes() != null) {
            for (OrderDish od : order.getOrderDishes()) {
                double lineTotal = od.getQuantity() * od.getCurrentPrice();
                total += lineTotal;
                model.addRow(new Object[]{
                    String.format("%02d", stt++), // Định dạng số thứ tự 01, 02...
                    od.getDish().getName(), 
                    od.getQuantity(), 
                    od.getCurrentPrice()
                });
            }
        }
        order.setTotalAmount(total);

        // Hiển thị tổng tiền
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        lblTotalPrice = new JLabel("Total Price: " + total, SwingConstants.RIGHT);
        lblTotalPrice.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalPanel.add(new JLabel("Total Price:"), BorderLayout.WEST);
        totalPanel.add(lblTotalPrice, BorderLayout.EAST);

        // Gom Table và Total vào một khu vực Center
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(new JScrollPane(tblOrderDetails), BorderLayout.CENTER);
        centerPanel.add(totalPanel, BorderLayout.SOUTH);

        // Buttons Footer
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        btnPanel.setOpaque(false);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(255, 69, 0)); // Đỏ cam
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(120, 35));

        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); // Xanh lá
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(120, 35));

        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        // Bắt sự kiện
        btnBack.addActionListener(this);
        btnCancel.addActionListener(this);
        btnConfirm.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnConfirm)) {
            // Tạm thời comment DAO để test luồng
            // OrderDAO dao = new OrderDAO();
            // order.setOrderTime(new java.util.Date());
            // if (dao.addOrder(order)) { ... }
            
            JOptionPane.showMessageDialog(this, "Order successfully!");
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
            
        } else if (e.getSource().equals(btnCancel)) {
            new StaffHomeFrm(user).setVisible(true);
            this.dispose();
        } else if (e.getSource().equals(btnBack)) {
            new OrderFrm(user, order).setVisible(true);
            this.dispose();
        }
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            Order tmp = new Order();
            
            // Tạo vài món ăn giả để test bảng hiển thị
            Dish d1 = new Dish(); d1.setName("Pho bo"); d1.setPrice(45000);
            Dish d2 = new Dish(); d2.setName("Com rang"); d2.setPrice(50000);
            
            OrderDish od1 = new OrderDish(); od1.setDish(d1); od1.setQuantity(3); od1.setCurrentPrice(45000);
            OrderDish od2 = new OrderDish(); od2.setDish(d2); od2.setQuantity(2); od2.setCurrentPrice(50000);
            
            tmp.addOrderDish(od1);
            tmp.addOrderDish(od2);
            
            new ConfirmOrderFrm(u, tmp).setVisible(true);
        });
    }
}