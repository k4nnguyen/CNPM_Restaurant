/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
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
    private JButton btnSearch, btnConfirm, btnBack;
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
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(9)"), BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Order Dishes", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // --- Form nhập liệu và Nút bấm ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; 
        formPanel.add(new JLabel("Dish name"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; 
        txtDishName = new JTextField("", 20); 
        formPanel.add(txtDishName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; 
        btnSearch = new JButton("Search");
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(150, 35));
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(50, 205, 50)); 
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(150, 35));
        formPanel.add(btnConfirm, gbc);
        
        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(formPanel, BorderLayout.CENTER);

        // --- Bảng thực đơn ---
        String[] cols = {"ID", "Dish name", "Quantity", "Price"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Chỉ mở khóa cột Quantity
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 2) {
                    try {
                        int q = Integer.parseInt(aValue.toString());
                        if (q < 0 || q > 20) {
                            JOptionPane.showMessageDialog(null, "Số lượng món chỉ được từ 0 đến 20!");
                        } else {
                            super.setValueAt(q, row, column);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ!");
                    }
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };
        tblDish = new JTable(tableModel);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblDish), BorderLayout.CENTER);
        this.add(mainPanel);

        btnSearch.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnBack.addActionListener(e -> {
            new SelectTableFrm(this.user).setVisible(true); 
            this.dispose();
        });  
        // Gọi dữ liệu (Đã tích hợp giữ lại state)
        loadData();
    }

    private void loadData() {
        // --- KẾT NỐI DAO TẠI ĐÂY ---
        dao.DishDAO dao = new dao.DishDAO();
        // Lấy từ khóa, nếu ô textbox rỗng thì lấy toàn bộ
        String keyword = txtDishName.getText().trim(); 
        listDish = dao.searchDish(keyword);

        tableModel.setRowCount(0);
        if (listDish != null) {
            for (Dish d : listDish) {
                int qty = 0;
                // Đồng bộ state số lượng cũ
                if (tmpOrder != null && tmpOrder.getOrderDishes() != null) {
                    for (model.OrderDish od : tmpOrder.getOrderDishes()) {
                        if (od.getDish().getId() == d.getId()) {
                            qty = od.getQuantity();
                            break;
                        }
                    }
                }
                tableModel.addRow(new Object[]{d.getId(), d.getName(), qty, d.getPrice()});
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSearch)) {
            loadData();
        } else if (e.getSource().equals(btnConfirm)) {
            if(tblDish.isEditing()) {
                tblDish.getCellEditor().stopCellEditing(); 
            }
            
            // Xóa danh sách cũ để cập nhật mới hoàn toàn từ bảng
            tmpOrder.setOrderDishes(new ArrayList<>()); 
            boolean hasItem = false;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int qty = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                if (qty > 0) {
                    OrderDish od = new OrderDish();
                    od.setDish(listDish.get(i));
                    od.setQuantity(qty);
                    od.setCurrentPrice(listDish.get(i).getPrice());
                    tmpOrder.addOrderDish(od);
                    hasItem = true;
                }
            }
            
            if (!hasItem) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 món (Quantity > 0) trước khi Confirm!");
                return;
            }

            new ConfirmOrderFrm(user, tmpOrder).setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            Order tmp = new Order(); tmp.setTable(new Table());
            new OrderFrm(u, tmp).setVisible(true);
        });
    }
}