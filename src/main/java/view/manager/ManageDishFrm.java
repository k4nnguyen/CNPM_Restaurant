package view.manager;

import dao.DishDAO;
import model.Dish;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ManageDishFrm extends JFrame {
    private final User user;
    private final DishDAO dishDAO = new DishDAO();
    private final JTextField txtKeyword = new JTextField();
    private final DefaultTableModel tableModel;
    private final JTable tblDish;
    private ArrayList<Dish> dishes = new ArrayList<>();

    public ManageDishFrm(User user) {
        super("Quản lý món ăn");
        this.user = user;
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(208, 232, 247));

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Mã món", "Loại món", "Tên món", "Mô tả", "Giá", "Trạng thái"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDish = new JTable(tableModel);
        tblDish.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tblDish), BorderLayout.CENTER);

        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        add(mainPanel);
        loadAllDishes();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JLabel title = new JLabel("Quản lý thông tin món ăn", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Từ khóa mã hoặc tên:"), BorderLayout.WEST);
        searchPanel.add(txtKeyword, BorderLayout.CENTER);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(this::search);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);
        panel.add(createButton("Tải lại", e -> loadAllDishes()));
        panel.add(createButton("Thêm", e -> new AddDishFrm(this).setVisible(true)));
        panel.add(createButton("Sửa", e -> editSelectedDish()));
        panel.add(createButton("Xóa", e -> deleteSelectedDish()));
        panel.add(createButton("Quay lại", e -> backToHome()));
        return panel;
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    public void loadAllDishes() {
        dishes = dishDAO.getAllDishes();
        fillTable();
    }

    private void search(ActionEvent e) {
        String keyword = txtKeyword.getText().trim();
        dishes = keyword.isEmpty() ? dishDAO.getAllDishes() : dishDAO.searchDishes(keyword);
        fillTable();
        if (dishes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn.");
        }
    }

    private void fillTable() {
        tableModel.setRowCount(0);
        for (Dish dish : dishes) {
            tableModel.addRow(new Object[]{
                dish.getId(), dish.getDishCode(), dish.getCategory(), dish.getName(),
                dish.getDescription(), dish.getPrice(), dish.getStatus()
            });
        }
    }

    private Dish getSelectedDish() {
        int row = tblDish.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn.");
            return null;
        }
        return dishes.get(row);
    }

    private void editSelectedDish() {
        Dish dish = getSelectedDish();
        if (dish != null) {
            new EditDishFrm(this, dish).setVisible(true);
        }
    }

    private void deleteSelectedDish() {
        Dish dish = getSelectedDish();
        if (dish == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hoặc ngừng kinh doanh món này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean hadBusiness = dishDAO.hasDishBeenOrdered(dish.getId());
        if (dishDAO.deleteOrDeactivateDish(dish.getId())) {
            JOptionPane.showMessageDialog(this,
                    hadBusiness ? "Món đã phát sinh nghiệp vụ nên đã ngừng kinh doanh."
                            : "Đã xóa món ăn.");
            loadAllDishes();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể xóa món ăn.");
        }
    }

    private void backToHome() {
        new ManagerHomeFrame(user).setVisible(true);
        dispose();
    }
}
