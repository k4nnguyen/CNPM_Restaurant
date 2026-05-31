package view.manager;

import dao.TableDAO;
import javax.swing.*;
import java.awt.*;

class TableFormFrm extends JFrame {
    private final ManageTableFrm parent;
    private final model.Table editingTable;
    private final TableDAO tableDAO = new TableDAO();
    private final JTextField txtCode = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextField txtCapacity = new JTextField();
    private final JTextArea txtDescription = new JTextArea(4, 20);
    private final JComboBox<String> cbStatus = new JComboBox<>(
            new String[]{"Trống", "Đang phục vụ", "Đã đặt"});

    TableFormFrm(ManageTableFrm parent, model.Table table, String title) {
        super(title);
        this.parent = parent;
        this.editingTable = table;
        setSize(460, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        add(createMainPanel());
        if (table != null) {
            fillForm(table);
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
        addRow(form, gbc, 0, "Mã bàn:", txtCode);
        addRow(form, gbc, 1, "Tên bàn:", txtName);
        addRow(form, gbc, 2, "Số khách tối đa:", txtCapacity);
        addRow(form, gbc, 3, "Mô tả:", new JScrollPane(txtDescription));
        addRow(form, gbc, 4, "Trạng thái:", cbStatus);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        buttons.add(createButton("Lưu", e -> saveTable()));
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

    private void fillForm(model.Table table) {
        txtCode.setText(table.getTableCode());
        txtCode.setEditable(false);
        txtName.setText(table.getName());
        txtCapacity.setText(String.valueOf(table.getCapacity()));
        txtDescription.setText(table.getDescription());
        cbStatus.setSelectedItem(table.getStatus());
    }

    private void saveTable() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String capacityText = txtCapacity.getText().trim();
        if (code.isEmpty() || name.isEmpty() || capacityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bàn, tên bàn và số khách tối đa không được bỏ trống.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Số khách tối đa phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số khách tối đa phải là số nguyên.");
            return;
        }

        model.Table table = editingTable == null ? new model.Table() : editingTable;
        table.setTableCode(code);
        table.setName(name);
        table.setCapacity(capacity);
        table.setDescription(txtDescription.getText().trim());
        table.setStatus((String) cbStatus.getSelectedItem());

        boolean success;
        if (editingTable == null) {
            if (tableDAO.isTableCodeExists(code)) {
                JOptionPane.showMessageDialog(this, "Mã bàn đã tồn tại.");
                return;
            }
            success = tableDAO.addTable(table);
        } else {
            success = tableDAO.updateTable(table);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Lưu thông tin bàn thành công.");
            parent.loadAllTables();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu thông tin bàn.");
        }
    }
}
