/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import dao.DishDAO;
import model.Dish;
import model.Order;
import model.OrderDish;
import model.Table;
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
public class OrderFrm extends JFrame implements ActionListener {
    private User user;
    private Order tmpOrder;
    private JTextField txtDishName;
    private JButton btnSearch, btnConfirm;
    private JTable tblDish;
    private DefaultTableModel tableModel;
    private ArrayList<Dish> listDish;

    public OrderFrm(User u, Order tmpOrder) {
        super("Order Dishes");
        this.user = u;
        this.tmpOrder = tmpOrder;
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(9)"), BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Order Dishes", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // --- Form nhập liệu và Nút bấm (Gộp chung vào GridBagLayout để căn chỉnh giống ảnh) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Label và TextBox
        gbc.gridx = 0; gbc.gridy = 0; 
        formPanel.add(new JLabel("Dish name"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; 
        txtDishName = new JTextField("Pho bo", 20); 
        formPanel.add(txtDishName, gbc);

        // Dòng 2: Nút Search và Nút Confirm
        gbc.gridx = 0; gbc.gridy = 1; 
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(150, 35));
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); // Màu xanh lá
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(150, 35));
        formPanel.add(btnConfirm, gbc);

        // Gom Header và Form vào phần trên (NORTH)
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);

        // --- Bảng thực đơn ---
        String[] cols = {"ID", "Dish name", "Quantity", "Price"};
        tableModel = new DefaultTableModel(cols, 0);
        tblDish = new JTable(tableModel);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblDish), BorderLayout.CENTER);

        this.add(mainPanel);

        // --- Bắt sự kiện ---
        btnSearch.addActionListener(this);
        btnConfirm.addActionListener(this);
        
        // Load Dummy Data ngay khi mở form để test UI
        loadDummyData();

        // Xử lý Double Click để chọn món
        tblDish.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (listDish != null && !listDish.isEmpty()) {
                        int row = tblDish.getSelectedRow();
                        Dish selectedDish = listDish.get(row);
                        String qtyStr = JOptionPane.showInputDialog("Nhập số lượng cho món " + selectedDish.getName() + ":");
                        
                        if (qtyStr != null && !qtyStr.isEmpty()) {
                            try {
                                int qty = Integer.parseInt(qtyStr);
                                OrderDish od = new OrderDish();
                                od.setDish(selectedDish);
                                od.setQuantity(qty);
                                od.setCurrentPrice(selectedDish.getPrice());
                                tmpOrder.addOrderDish(od);
                                
                                // Cập nhật hiển thị số lượng lên bảng
                                tableModel.setValueAt(qty, row, 2);
                                JOptionPane.showMessageDialog(null, "Đã thêm vào danh sách!");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ!");
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadDummyData() {
        listDish = new ArrayList<>();
        Dish d1 = new Dish(); d1.setId(1); d1.setName("Pho bo"); d1.setPrice(45000);
        Dish d2 = new Dish(); d2.setId(2); d2.setName("Pho ga"); d2.setPrice(40000);
        Dish d3 = new Dish(); d3.setId(3); d3.setName("Com rang"); d3.setPrice(50000);
        listDish.add(d1); listDish.add(d2); listDish.add(d3);

        tableModel.setRowCount(0);
        for (Dish d : listDish) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), 0, d.getPrice()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            // Tạm ẩn kết nối CSDL, gọi hàm load dummy data
            // DishDAO dao = new DishDAO();
            // listDish = dao.searchDish(txtDishName.getText().trim());
            loadDummyData();
        } else if (e.getSource().equals(btnConfirm)) {
            // Chuyển sang form ConfirmOrderFrm
            new ConfirmOrderFrm(user, tmpOrder).setVisible(true);
            this.dispose();
        }
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            Order tmp = new Order(); tmp.setTable(new Table());
            new OrderFrm(u, tmp).setVisible(true);
        });
    }
}