package view.staff;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình chính của Nhân viên phục vụ.
 * Cung cấp đầy đủ các chức năng: Đặt bàn, Sửa đặt bàn, Gọi món và Thanh toán.
 */
public class StaffHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnBookTable;
    private JButton btnEditBooking;
    private JButton btnOrderDish;
    private JButton btnPayment; 
    private JButton btnLogout; // Thêm biến cho nút Logout

    public StaffHomeFrm(User user) {
        super("Trang chủ - Nhân viên phục vụ");
        this.user = user;
        
        // 1. Cài đặt thông số cơ bản cho Cửa sổ
        this.setSize(600, 450); // Tăng chút chiều cao tổng thể của cửa sổ cho thoáng
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Căn giữa màn hình
        
        // 2. Tạo Panel chính với màu nền xanh nhạt giống bản thiết kế
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(208, 232, 247)); // Mã màu xanh nhạt (Light Blue)
        
        // 3. Tạo phần Header (Chứa số (1), Tiêu đề và Welcome)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Trong suốt để lộ màu nền của mainPanel
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20)); // Căn lề trên, trái, dưới, phải
        
        JLabel lblStep = new JLabel("(1)");
        lblStep.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel lblTitle = new JLabel("Staff Home View", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 24));
        
        JLabel lblWelcome = new JLabel("Welcome " + user.getName());
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        headerPanel.add(lblStep, BorderLayout.WEST);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblWelcome, BorderLayout.EAST);
        
        // 4. Tạo phần chứa các nút bấm (Center)
        // Đổi lưới thành 5 hàng để chứa thêm nút Logout
        JPanel btnPanel = new JPanel(new GridLayout(5, 1, 0, 20)); 
        btnPanel.setOpaque(false);
        // Tăng chiều cao Dimension lên 320 để các nút không bị ép lùn lại
        btnPanel.setPreferredSize(new Dimension(300, 320)); 
        
        btnBookTable = createButton("Book a table");
        btnEditBooking = createButton("Edit a booking");
        btnOrderDish = createButton("Order dishes");
        btnPayment = createButton("Payment"); 
        btnLogout = createButton("Logout"); // Khởi tạo nút Logout
        
        // Đổi màu nút Logout cho khác biệt (tùy chọn)
        btnLogout.setForeground(Color.RED);
        
        btnPanel.add(btnBookTable);
        btnPanel.add(btnEditBooking);
        btnPanel.add(btnOrderDish);
        btnPanel.add(btnPayment); 
        btnPanel.add(btnLogout); // Thêm nút Logout vào Panel
        
        // Dùng GridBagLayout để căn giữa btnPanel vào chính giữa màn hình mà không bị kéo giãn
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(btnPanel);
        
        // 5. Thêm tất cả vào mainPanel và đưa lên JFrame
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        this.add(mainPanel);
    }
    
    // Hàm hỗ trợ tạo nút bấm với giao diện chuẩn (Màu trắng, viền mảnh)
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE); // Nền nút màu trắng
        btn.setFocusPainted(false); // Bỏ viền outline khi click
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.addActionListener(this); // Đăng ký luôn sự kiện click
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBookTable)) {
            new SearchFreeTableFrm(user).setVisible(true);
            this.dispose(); 
        } 
        else if (e.getSource().equals(btnEditBooking)) {
            new view.staff.SearchBookingFrm(user).setVisible(true);
            this.dispose();
        } 
        else if (e.getSource().equals(btnOrderDish)) {
            new view.staff.SelectTableFrm(user).setVisible(true);
            this.dispose();
        }
        else if (e.getSource().equals(btnPayment)) {
            new view.staff.SelectTableToPayFrm(user).setVisible(true);
            this.dispose();
        }
        else if (e.getSource().equals(btnLogout)) { // Sự kiện cho nút Logout
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn đăng xuất?", 
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Mở lại form LoginFrame của Manager
                new view.manager.LoginFrame().setVisible(true);
                // Đóng form hiện tại
                this.dispose();
            }
        }
    }

    // Hàm main để test nhanh giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User dummyUser = new User();
                dummyUser.setId(1);
                dummyUser.setName("Nguyễn Kim An");
                dummyUser.setUsername("staff01");
                dummyUser.setPassword("123");
                dummyUser.setRole("STAFF");
                dummyUser.setPhone("0123456789");
                dummyUser.setEmail("annguyen@gmail.com");

                new StaffHomeFrm(dummyUser).setVisible(true);
            }
        });
    }
}