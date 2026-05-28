package view.manager;

import model.Client;
import service.ClientService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class ManageClientFrame extends JFrame {
    private static final String[] CLIENT_COLUMNS = {
            "ID", "Ma KH", "Ho ten", "So dien thoai", "Email", "Dia chi", "Trang thai"
    };

    private final Frame parentFrame;
    private final ClientService clientService = new ClientService();
    private final JTextField searchField = new JTextField(24);
    private final DefaultTableModel tableModel = createTableModel();
    private final JTable clientTable = new JTable(tableModel);
    private List<Client> displayedClients = new ArrayList<>();

    public ManageClientFrame(Frame parentFrame) {
        super("Quan ly khach hang");
        this.parentFrame = parentFrame;
        configureFrame();
        buildContent();
        loadClients();
    }

    private void configureFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(parentFrame);
    }

    private void buildContent() {
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel("Quan ly khach hang dang hoat dong");
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton searchButton = new JButton("Tim kiem");
        JButton reloadButton = new JButton("Tai lai");
        searchPanel.add(new JLabel("Tu khoa:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(reloadButton);

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        configureTable();

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton addButton = new JButton("Them");
        JButton editButton = new JButton("Sua");
        JButton deleteButton = new JButton("Xoa");
        JButton closeButton = new JButton("Dong");
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(closeButton);

        searchButton.addActionListener(event -> searchClients());
        searchField.addActionListener(event -> searchClients());
        reloadButton.addActionListener(event -> reloadClients());
        addButton.addActionListener(event -> showAddDialog());
        editButton.addActionListener(event -> showEditDialog());
        deleteButton.addActionListener(event -> deleteSelectedClient());
        closeButton.addActionListener(event -> dispose());

        rootPanel.add(topPanel, BorderLayout.NORTH);
        rootPanel.add(new JScrollPane(clientTable), BorderLayout.CENTER);
        rootPanel.add(actionPanel, BorderLayout.SOUTH);
        add(rootPanel);
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(CLIENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureTable() {
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setDefaultEditor(Object.class, null);
        clientTable.setAutoCreateRowSorter(true);
        clientTable.getTableHeader().setReorderingAllowed(false);
        clientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    showEditDialog();
                }
            }
        });
    }

    private void loadClients() {
        try {
            setClients(clientService.getActiveClients());
        } catch (Exception exception) {
            showDatabaseError("Khong the tai danh sach khach hang", exception);
        }
    }

    private void searchClients() {
        try {
            setClients(clientService.searchActiveClients(searchField.getText()));
        } catch (Exception exception) {
            showDatabaseError("Khong the tim kiem khach hang", exception);
        }
    }

    private void reloadClients() {
        searchField.setText("");
        loadClients();
    }

    private void setClients(List<Client> clients) {
        displayedClients = new ArrayList<>(clients);
        tableModel.setRowCount(0);
        for (Client client : displayedClients) {
            tableModel.addRow(new Object[] {
                    client.getId(),
                    safeText(client.getClientCode()),
                    safeText(client.getName()),
                    safeText(client.getPhone()),
                    safeText(client.getEmail()),
                    safeText(client.getAddress()),
                    safeText(client.getStatus())
            });
        }
    }

    private void showAddDialog() {
        ClientFormDialog dialog = new ClientFormDialog(this, clientService);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadClients();
            JOptionPane.showMessageDialog(this, "Da them khach hang thanh cong.");
        }
    }

    private void showEditDialog() {
        Client selectedClient = getSelectedClient();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot khach hang de sua.");
            return;
        }

        ClientFormDialog dialog = new ClientFormDialog(this, clientService, selectedClient);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadClients();
            JOptionPane.showMessageDialog(this, "Da cap nhat khach hang thanh cong.");
        }
    }

    private void deleteSelectedClient() {
        Client selectedClient = getSelectedClient();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot khach hang de xoa.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Ban co chac muon xoa khach hang \"" + selectedClient.getName() + "\"?",
                "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clientService.softDeleteClient(selectedClient.getId());
            loadClients();
            JOptionPane.showMessageDialog(this, "Da xoa khach hang khoi danh sach dang hoat dong.");
        } catch (Exception exception) {
            showDatabaseError("Khong the xoa khach hang", exception);
        }
    }

    private Client getSelectedClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = clientTable.convertRowIndexToModel(selectedRow);
        return modelRow >= 0 && modelRow < displayedClients.size() ? displayedClients.get(modelRow) : null;
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void showDatabaseError(String message, Exception exception) {
        JOptionPane.showMessageDialog(this, message + ": " + exception.getMessage(),
                "Loi du lieu", JOptionPane.ERROR_MESSAGE);
    }
}
