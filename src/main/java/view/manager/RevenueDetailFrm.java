package view.manager;

import dao.BillDAO;
import model.Bill;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RevenueDetailFrm extends JFrame {
    private JTable tblBills;
    private DefaultTableModel tableModel;

    public RevenueDetailFrm(int month, int year) {
        super("Chi tiết hóa đơn tháng " + month + "/" + year);
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Danh sách hóa đơn trong tháng " + month + "/" + year, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(lblTitle, BorderLayout.NORTH);

        String[] headers = {"Mã HD", "Ngày thanh toán", "Giờ thanh toán", "Tổng tiền (VND)", "Mã đặt bàn"};
        tableModel = new DefaultTableModel(headers, 0);
        tblBills = new JTable(tableModel);

        BillDAO dao = new BillDAO();
        ArrayList<Bill> list = dao.getBillsByMonth(month, year);

        for (Bill b : list) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getPaymentDate(),
                b.getPaymentTime(),
                String.format("%,.2f", b.getTotalAmount()),
                b.getBooking() != null ? b.getBooking().getId() : "N/A"
            });
        }

        this.add(new JScrollPane(tblBills), BorderLayout.CENTER);
    }
}
