package view.manager;

import javax.swing.*;
import java.awt.event.*;

public class ManagerHomeFrm extends JFrame implements ActionListener {
    private JButton btnStat;

    public ManagerHomeFrm() {
        super("Màn hình chính của Quản lý");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        btnStat = new JButton("Thống kê & Báo cáo");
        btnStat.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.add(btnStat);
        this.add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnStat) {
            new SelectStatFrm().setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        new ManagerHomeFrm().setVisible(true);
    }
}
