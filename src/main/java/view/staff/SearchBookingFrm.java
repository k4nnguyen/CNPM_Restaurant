/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import dao.BookingDAO;
import model.Booking;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/**
 *
 * @author annguyen
 */
public class SearchBookingFrm extends JFrame implements ActionListener {
    private User user;
    private JTextField txtClientPhone;
    private JButton btnSearch;
    private JTable tblBooking;
    private DefaultTableModel tableModel;
    private ArrayList<Booking> listBooking;

    public SearchBookingFrm(User u) {
        super("Search Booking");
        this.user = u;
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(6)"), BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Search Booking", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Form & Button
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client phone number"), gbc);
        gbc.gridx = 1; txtClientPhone = new JTextField("0916385989", 20); formPanel.add(txtClientPhone, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(150, 30));
        btnPanel.add(btnSearch);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        // Table
        String[] cols = {"ID", "Table Code", "Datetime", "Number of people"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Luôn trả về false -> Không ô nào sửa được
            }
        };
        tblBooking = new JTable(tableModel);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblBooking), BorderLayout.CENTER);
        this.add(mainPanel);

        btnSearch.addActionListener(this);
        tblBooking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if(listBooking != null && !listBooking.isEmpty()){
                        Booking selectedBooking = listBooking.get(tblBooking.getSelectedRow());
                        new EditBookingFrm(user, selectedBooking).setVisible(true);
                        dispose();
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            BookingDAO dao = new BookingDAO();
            listBooking = dao.searchBooking(txtClientPhone.getText().trim());
            tableModel.setRowCount(0);
            if (listBooking != null) {
                for (Booking b : listBooking) {
                    tableModel.addRow(new Object[]{b.getId(), "T001", b.getBookDate() + " " + b.getBookTime(), b.getQuantity()});
                }
            }
        }
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            new SearchBookingFrm(u).setVisible(true);
        });
    }
}