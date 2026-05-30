package view.manager;

import dao.BillDAO;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MonthlyRevenueFrm extends JFrame implements ActionListener {
    private JTextField txtStartDate, txtEndDate;
    private JButton btnSearch, btnBack;
    private JTable tblStat;
    private DefaultTableModel tableModel;
    private ArrayList<MonthlyRevenueStat> listStat;

    public MonthlyRevenueFrm() {
        super("Báo cáo doanh thu theo tháng");
        this.setSize(720, 450);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel pnlNorth = new JPanel();
        pnlNorth.add(new JLabel("Từ ngày:"));
        txtStartDate = new JTextField("2026-01-01", 10);
        pnlNorth.add(txtStartDate);
        pnlNorth.add(new JLabel("Đến ngày:"));
        txtEndDate = new JTextField("2026-05-31", 10);
        pnlNorth.add(txtEndDate);

        btnSearch = new JButton("Thống kê");
        btnSearch.addActionListener(this);
        pnlNorth.add(btnSearch);

        btnBack = new JButton("Quay lại");
        btnBack.addActionListener(this);
        pnlNorth.add(btnBack);

        this.add(pnlNorth, BorderLayout.NORTH);

        String[] headers = {"Tháng", "Năm", "Tổng doanh thu (VND)"};
        tableModel = new DefaultTableModel(headers, 0);
        tblStat = new JTable(tableModel);

        tblStat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblStat.getSelectedRow();
                if (row >= 0 && listStat != null && row < listStat.size()) {
                    MonthlyRevenueStat selected = listStat.get(row);
                    new RevenueDetailFrm(selected.getMonth(), selected.getYear()).setVisible(true);
                }
            }
        });

        this.add(new JScrollPane(tblStat), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String sd = txtStartDate.getText().trim();
            String ed = txtEndDate.getText().trim();

            BillDAO dao = new BillDAO();
            ArrayList<Bill> bills = dao.getBillsByDateRange(sd, ed);

            listStat = calculateMonthlyRevenue(bills);

            tableModel.setRowCount(0);
            for (MonthlyRevenueStat ms : listStat) {
                tableModel.addRow(new Object[]{
                    ms.getMonth(),
                    ms.getYear(),
                    String.format("%,.2f", ms.getTotalRevenue())
                });
            }
        } else if (e.getSource() == btnBack) {
            new SelectStatFrm().setVisible(true);
            this.dispose();
        }
    }

    private ArrayList<MonthlyRevenueStat> calculateMonthlyRevenue(ArrayList<Bill> bills) {
        ArrayList<MonthlyRevenueStat> result = new ArrayList<>();
        Map<String, Double> revenueMap = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        for (Bill b : bills) {
            if (b.getPaymentDate() != null) {
                cal.setTime(b.getPaymentDate());
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);
                String key = month + "-" + year;
                revenueMap.put(key, revenueMap.getOrDefault(key, 0.0) + b.getTotalAmount());
            }
        }

        for (Map.Entry<String, Double> entry : revenueMap.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int m = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            result.add(new MonthlyRevenueStat(m, y, entry.getValue()));
        }

        return result;
    }
}
