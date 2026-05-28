/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.staff;
import dao.TableDAO;
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
public class SelectTableFrm extends JFrame implements ActionListener {
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("(8)"), BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Select Table", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "Table Code", "Datetime", "Number of people"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Luôn trả về false -> Không ô nào sửa được
            }
        };
        tblSelectTable = new JTable(tableModel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblSelectTable), BorderLayout.CENTER);
        this.add(mainPanel);

        // Giả lập load bàn đang phục vụ
        listTable = new ArrayList<>();
        Table t1 = new Table(); 
        t1.setId(1); t1.setTableCode("T001"); t1.setCapacity(4); t1.setStatus("Đang phục vụ");
        Table t2 = new Table(); 
        t2.setId(2); t2.setTableCode("T002"); t2.setCapacity(6); t2.setStatus("Đang phục vụ");
        listTable.add(t1);
        listTable.add(t2);
        
        //TableDAO dao = new TableDAO();
        //listTable = dao.getOccupiedTables();
        if (listTable != null) {
            for (Table t : listTable) {
                // Mockup dữ liệu datetime và number of people
                tableModel.addRow(new Object[]{t.getId(), t.getTableCode(), "21/05/2025 19:00", t.getCapacity()});
            }
        }

        tblSelectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if(listTable != null && !listTable.isEmpty()){
                        Table selectedTable = listTable.get(tblSelectTable.getSelectedRow());
                        Order tmpOrder = new Order();
                        tmpOrder.setTable(selectedTable);
                        tmpOrder.setUser(user);
                        
                        // Yêu cầu bạn đã tạo OrderFrm để dòng dưới không lỗi
                        // new OrderFrm(user, tmpOrder).setVisible(true);
                        dispose();
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    // --- Hàm main test giao diện ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User u = new User(); u.setName("An");
            new SelectTableFrm(u).setVisible(true);
        });
    }
}