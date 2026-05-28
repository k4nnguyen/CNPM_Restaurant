/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import model.Order;
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
public class SelectTableFrm extends JFrame {
    private User user;
    private JTable tblSelectTable;
    private DefaultTableModel tableModel;
    private ArrayList<Table> listTable;

    public SelectTableFrm(User u) {
        super("Select Table");
        this.user = u;
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(208, 232, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(8)"), BorderLayout.WEST);
        headerPanel.add(new JLabel("Select Table", SwingConstants.CENTER), BorderLayout.CENTER);

        String[] cols = {"ID", "Table Code", "Datetime", "Number of people"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSelectTable = new JTable(tableModel);
        tblSelectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblSelectTable), BorderLayout.CENTER);
        this.add(mainPanel);

        // Dummy Data
        listTable = new ArrayList<>();
        Table t1 = new Table(); t1.setId(1); t1.setTableCode("T001"); t1.setCapacity(4);
        listTable.add(t1);
        tableModel.addRow(new Object[]{t1.getId(), t1.getTableCode(), "21/05/2026 19:00", t1.getCapacity()});

        // Chọn bằng phím Enter
        tblSelectTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        tblSelectTable.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int row = tblSelectTable.getSelectedRow();
                if (row >= 0) {
                    Table selectedTable = listTable.get(row);
                    Order tmpOrder = new Order();
                    tmpOrder.setTable(selectedTable);
                    tmpOrder.setUser(user);
                    
                    new OrderFrm(user, tmpOrder).setVisible(true);
                    dispose();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SelectTableFrm(new User()).setVisible(true);
        });
    }
}