package view.staff;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StaffHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnSearchClient;

    public StaffHomeFrm(User user) {
        super("Trang chu Nhan vien phuc vu");
        this.user = user;
        
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(208, 232, 247));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        JLabel lblStep = new JLabel("(1)");
        lblStep.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel lblTitle = new JLabel("Staff Home View", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 24));
        
        JLabel lblWelcome = new JLabel("Welcome " + user.getName());
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        headerPanel.add(lblStep, BorderLayout.WEST);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblWelcome, BorderLayout.EAST);
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 1, 0, 25)); 
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(300, 60)); 
        
        btnSearchClient = createButton("Manage Clients");
        btnPanel.add(btnSearchClient);
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(btnPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        this.add(mainPanel);
    }
    
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearchClient)) {
            new view.staff.SearchClientFrm(user).setVisible(true);
            this.dispose(); 
        } 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User dummyUser = new User();
                dummyUser.setId(1);
                dummyUser.setName("Nguyen Kim An");
                dummyUser.setUsername("staff01");
                dummyUser.setPassword("123");
                dummyUser.setRole("nhanvien");
                dummyUser.setPhone("0123456789");
                dummyUser.setEmail("annguyen@gmail.com");

                new StaffHomeFrm(dummyUser).setVisible(true);
            }
        });
    }
}
