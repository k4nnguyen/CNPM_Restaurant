package view.manager;

import javax.swing.*;
import java.awt.event.*;

public class SelectStatFrm extends JFrame implements ActionListener {
    private JButton btnGuestStat, btnRevenueStat, btnBack;

    public SelectStatFrm() {
        super("Chọn loại Thống kê & Báo cáo");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        btnGuestStat = new JButton("Thống kê khách theo khung giờ");
        btnRevenueStat = new JButton("Báo cáo doanh thu theo tháng");
        btnBack = new JButton("Quay lại");
        
        btnGuestStat.addActionListener(this);
        btnRevenueStat.addActionListener(this);
        btnBack.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.add(btnGuestStat);
        panel.add(btnRevenueStat);
        panel.add(btnBack);
        this.add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGuestStat) {
            new GuestStatFrm().setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnRevenueStat) {
            new MonthlyRevenueFrm().setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnBack) {
            new ManagerHomeFrm().setVisible(true);
            this.dispose();
        }
    }
}
