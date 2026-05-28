/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author annguyen
 */
public class StaffHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnBookTable;
    private JButton btnEditBooking;
    private JButton btnOrderDish;

    public StaffHomeFrm(User user) {
        super("Trang chủ Nhân viên phục vụ");
        this.user = user;
        
        // 1. Cài đặt thông số cơ bản cho Cửa sổ
        this.setSize(600, 400);
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
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 25)); // 3 hàng, 1 cột, khoảng cách dọc 25px
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(300, 200)); // Cố định kích thước khu vực nút bấm
        
        btnBookTable = createButton("Book a table");
        btnEditBooking = createButton("Edit a booking");
        btnOrderDish = createButton("Order dishes");
        
        btnPanel.add(btnBookTable);
        btnPanel.add(btnEditBooking);
        btnPanel.add(btnOrderDish);
        
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
        // Xử lý chuyển trang tương ứng với từng nút bấm
        if (e.getSource().equals(btnBookTable)) {
            new SearchFreeTableFrm(user).setVisible(true);
            this.dispose(); 
        } 
        else if (e.getSource().equals(btnEditBooking)) {
            // Chuyển sang module Sửa đặt bàn
            new view.staff.SearchBookingFrm(user).setVisible(true);
            this.dispose();
        } 
        else if (e.getSource().equals(btnOrderDish)) {
            // Chuyển sang module Gọi món
            new view.staff.SelectTableFrm(user).setVisible(true);
            this.dispose();
        }
    }

    // Hàm main để test nhanh giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User dummyUser = new User();
                dummyUser.setName("An"); // Để hiển thị "Welcome An"
                new StaffHomeFrm(dummyUser).setVisible(true);
            }
        });
    }
}