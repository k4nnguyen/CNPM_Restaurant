package view.manager;

import dao.BookingDAO;
import dao.BillDAO;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuestStatFrm extends JFrame implements ActionListener {
    private JTextField txtStartDate, txtEndDate;
    private JButton btnSearch, btnBack;
    private JTable tblStat;
    private DefaultTableModel tableModel;
    private ArrayList<GuestStat> listStat;

    public GuestStatFrm() {
        super("Thống kê lượng khách theo khung giờ");
        this.setSize(720, 450);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel pnlNorth = new JPanel();
        pnlNorth.add(new JLabel("Từ ngày:"));
        txtStartDate = new JTextField("2026-05-01", 10);
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

        String[] headers = {"Khung giờ", "SL Khách trung bình", "Doanh thu bình quân/đầu khách", "Tổng doanh thu"};
        tableModel = new DefaultTableModel(headers, 0);
        tblStat = new JTable(tableModel);
        
        tblStat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblStat.getSelectedRow();
                if (row >= 0 && listStat != null && row < listStat.size()) {
                    GuestStat selected = listStat.get(row);
                    new GuestStatDetailFrm(selected.getTimeFrame(), txtStartDate.getText().trim(), txtEndDate.getText().trim()).setVisible(true);
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
            
            BookingDAO bookingDAO = new BookingDAO();
            BillDAO billDAO = new BillDAO();
            
            ArrayList<Booking> bookings = bookingDAO.getBookingsByDateRange(sd, ed);
            ArrayList<Bill> bills = billDAO.getBillsByDateRange(sd, ed);
            
            listStat = calculateGuestStats(bookings, bills);
            
            tableModel.setRowCount(0);
            for (GuestStat gs : listStat) {
                tableModel.addRow(new Object[]{
                    gs.getTimeFrame(),
                    gs.getAvgCustomers(),
                    String.format("%,.2f", gs.getAvgRevenuePerHead()),
                    String.format("%,.2f", gs.getTotalRevenue())
                });
            }
        } else if (e.getSource() == btnBack) {
            new SelectStatFrm().setVisible(true);
            this.dispose();
        }
    }

    private ArrayList<GuestStat> calculateGuestStats(ArrayList<Booking> bookings, ArrayList<Bill> bills) {
        ArrayList<GuestStat> result = new ArrayList<>();
        
        Map<String, Integer> customerCount = new HashMap<>();
        Map<String, Double> revenueSum = new HashMap<>();
        Map<String, Integer> bookingCount = new HashMap<>();
        
        String[] frames = {"11:00-13:00", "18:00-20:00", "Khác"};
        for (String f : frames) {
            customerCount.put(f, 0);
            revenueSum.put(f, 0.0);
            bookingCount.put(f, 0);
        }
        
        for (Booking b : bookings) {
            String tf = getFrameFromTime(b.getBookTime());
            customerCount.put(tf, customerCount.get(tf) + b.getQuantity());
            bookingCount.put(tf, bookingCount.get(tf) + 1);
        }
        
        for (Bill bl : bills) {
            String tf = getFrameFromTime(bl.getPaymentTime());
            revenueSum.put(tf, revenueSum.get(tf) + bl.getTotalAmount());
        }
        
        for (String f : frames) {
            int bookingsQty = bookingCount.get(f);
            int totalCust = customerCount.get(f);
            double totalRev = revenueSum.get(f);
            
            int avgCust = bookingsQty > 0 ? (totalCust / bookingsQty) : 0;
            double avgRevPerHead = totalCust > 0 ? (totalRev / totalCust) : 0.0;
            
            result.add(new GuestStat(f, avgCust, avgRevPerHead, totalRev));
        }
        
        return result;
    }

    private String getFrameFromTime(String time) {
        if (time == null) return "Khác";
        if (time.startsWith("11") || time.startsWith("12")) return "11:00-13:00";
        if (time.startsWith("18") || time.startsWith("19")) return "18:00-20:00";
        return "Khác";
    }
}
