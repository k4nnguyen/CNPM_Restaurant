package view.manager;

import model.Client;
import service.ClientService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ClientFormDialog extends JDialog {
    private final ClientService clientService;
    private final Client editingClient;
    private final JTextField clientCodeField = new JTextField(24);
    private final JTextField fullNameField = new JTextField(24);
    private final JTextField phoneField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JTextArea addressArea = new JTextArea(4, 24);
    private boolean saved;

    public ClientFormDialog(Frame owner, ClientService clientService) {
        this(owner, clientService, null);
    }

    public ClientFormDialog(Frame owner, ClientService clientService, Client editingClient) {
        super(owner, editingClient == null ? "Them khach hang" : "Sua khach hang", true);
        this.clientService = clientService;
        this.editingClient = editingClient;
        configureDialog(owner);
        buildContent();
        populateForm();
    }

    public boolean isSaved() {
        return saved;
    }

    private void configureDialog(Frame owner) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(460, 360);
        setLocationRelativeTo(owner);
    }

    private void buildContent() {
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        clientCodeField.setEditable(false);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        addField(formPanel, constraints, 0, "Ma khach hang:", clientCodeField);
        addField(formPanel, constraints, 1, "Ho ten:", fullNameField);
        addField(formPanel, constraints, 2, "So dien thoai:", phoneField);
        addField(formPanel, constraints, 3, "Email:", emailField);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0;
        formPanel.add(new JLabel("Dia chi:"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        formPanel.add(addressArea, constraints);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Luu");
        JButton cancelButton = new JButton("Huy");
        saveButton.addActionListener(event -> saveClient());
        cancelButton.addActionListener(event -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(rootPanel);
    }

    private void addField(JPanel formPanel, GridBagConstraints constraints, int row, String label, JTextField field) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        formPanel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        formPanel.add(field, constraints);
    }

    private void populateForm() {
        if (editingClient == null) {
            clientCodeField.setText("Tu dong tao khi luu");
            return;
        }
        clientCodeField.setText(safeText(editingClient.getClientCode()));
        fullNameField.setText(safeText(editingClient.getName()));
        phoneField.setText(safeText(editingClient.getPhone()));
        emailField.setText(safeText(editingClient.getEmail()));
        addressArea.setText(safeText(editingClient.getAddress()));
    }

    private void saveClient() {
        Client client = buildClientFromForm();
        try {
            if (editingClient == null) {
                clientService.addClient(client);
            } else {
                clientService.updateClient(client);
            }
            saved = true;
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Du lieu khong hop le", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Client buildClientFromForm() {
        Client client = new Client();
        if (editingClient != null) {
            client.setId(editingClient.getId());
            client.setClientCode(editingClient.getClientCode());
            client.setStatus(editingClient.getStatus());
        }
        client.setName(fullNameField.getText());
        client.setPhone(phoneField.getText());
        client.setEmail(emailField.getText());
        client.setAddress(addressArea.getText());
        return client;
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }
}
