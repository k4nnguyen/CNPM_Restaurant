package view.manager;

import dao.DishDAO;
import model.Dish;
import javax.swing.*;
import java.awt.*;

class DishFormFrm extends JFrame {
    private final ManageDishFrm parent;
    private final Dish editingDish;
    private final DishDAO dishDAO = new DishDAO();
    private final JTextField txtCode = new JTextField();
    private final JComboBox<String> cbCategory = new JComboBox<>(
            new String[]{"Món chính", "Món phụ", "Đồ uống", "Tráng miệng", "Khác"});
    private final JTextField txtName = new JTextField();
    private final JTextArea txtDescription = new JTextArea(4, 20);
    private final JTextField txtPrice = new JTextField();

    DishFormFrm(ManageDishFrm parent, Dish dish, String title) {
        super(title);
        this.parent = parent;
        this.editingDish = dish;
        setSize(460, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        add(createMainPanel());
        if (dish != null) {
            fillForm(dish);
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(208, 232, 247));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, gbc, 0, "Mã món:", txtCode);
        addRow(form, gbc, 1, "Loại món:", cbCategory);
        addRow(form, gbc, 2, "Tên món:", txtName);
        addRow(form, gbc, 3, "Mô tả:", new JScrollPane(txtDescription));
        addRow(form, gbc, 4, "Giá hiện tại:", txtPrice);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        buttons.add(createButton("Lưu", e -> saveDish()));
        buttons.add(createButton("Hủy", e -> dispose()));

        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        return mainPanel;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, Component input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void fillForm(Dish dish) {
        txtCode.setText(dish.getDishCode());
        txtCode.setEditable(false);
        cbCategory.setSelectedItem(dish.getCategory());
        txtName.setText(dish.getName());
        txtDescription.setText(dish.getDescription());
        txtPrice.setText(String.valueOf(dish.getPrice()));
    }

    private void saveDish() {
        String code = txtCode.getText().trim();
        String category = ((String) cbCategory.getSelectedItem()).trim();
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        if (code.isEmpty() || category.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã món, loại món, tên món và giá không được bỏ trống.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá hiện tại phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá hiện tại phải là số.");
            return;
        }

        Dish dish = editingDish == null ? new Dish() : editingDish;
        dish.setDishCode(code);
        dish.setCategory(category);
        dish.setName(name);
        dish.setDescription(txtDescription.getText().trim());
        dish.setPrice(price);

        boolean success;
        if (editingDish == null) {
            if (dishDAO.isDishCodeExists(code)) {
                JOptionPane.showMessageDialog(this, "Mã món ăn đã tồn tại.");
                return;
            }
            success = dishDAO.addDish(dish);
        } else {
            success = dishDAO.updateDish(dish);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Lưu thông tin món ăn thành công.");
            parent.loadAllDishes();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu thông tin món ăn.");
        }
    }
}
