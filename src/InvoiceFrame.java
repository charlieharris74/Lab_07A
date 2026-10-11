import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Collects invoice information and displays a text invoice. */
public class InvoiceFrame extends JFrame
{
    private final JTextField titleField = new JTextField("INVOICE");
    private final JTextField customerField = new JTextField();
    private final JTextField streetField = new JTextField();
    private final JTextField cityStateZipField = new JTextField();
    private final JTextField productField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final List<LineItem> pendingItems = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Item", "Qty", "Price", "Total"}, 0)
    {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable itemTable = new JTable(tableModel);
    private final JTextArea invoiceArea = new JTextArea(13, 65);

    public InvoiceFrame()
    {
        super("Invoice Form");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(mainPanel);
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel itemPanel = new JPanel(new BorderLayout(6, 6));
        itemPanel.setBorder(BorderFactory.createTitledBorder("Line Items"));
        itemPanel.add(createItemEntryPanel(), BorderLayout.NORTH);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setPreferredScrollableViewportSize(new Dimension(700, 100));
        itemPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        centerPanel.add(itemPanel, BorderLayout.NORTH);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Invoice Display"));
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        invoiceArea.setMargin(new Insets(8, 8, 8, 8));
        displayPanel.add(new JScrollPane(invoiceArea), BorderLayout.CENTER);
        centerPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        // A changed header makes the old displayed invoice out of date.
        javax.swing.event.DocumentListener headerListener = new javax.swing.event.DocumentListener()
        {
            public void insertUpdate(javax.swing.event.DocumentEvent event) { invoiceArea.setText(""); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { invoiceArea.setText(""); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { invoiceArea.setText(""); }
        };
        for (JTextField field : new JTextField[]{titleField, customerField, streetField, cityStateZipField})
            field.getDocument().addDocumentListener(headerListener);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event) { quitApp(); }
        });
        pack();
        setMinimumSize(new Dimension(850, 700));
        setSize(900, 760);
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel()
    {
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Invoice and Customer"));
        panel.add(new JLabel("Invoice title:")); panel.add(titleField);
        panel.add(new JLabel("Customer name:")); panel.add(customerField);
        panel.add(new JLabel("Street address:")); panel.add(streetField);
        panel.add(new JLabel("City, state, ZIP:")); panel.add(cityStateZipField);
        return panel;
    }

    private JPanel createItemEntryPanel()
    {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 4));
        panel.add(new JLabel("Product name"));
        panel.add(new JLabel("Quantity"));
        panel.add(new JLabel("Unit price ($)"));
        panel.add(new JLabel(""));
        panel.add(productField); panel.add(quantityField); panel.add(priceField);
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(event -> addItem());
        panel.add(addButton);
        return panel;
    }

    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        JButton displayButton = new JButton("Display Invoice");
        JButton removeButton = new JButton("Remove Selected Item");
        JButton clearButton = new JButton("Clear");
        JButton quitButton = new JButton("Quit");
        displayButton.addActionListener(event -> displayInvoice());
        removeButton.addActionListener(event -> removeItem());
        clearButton.addActionListener(event -> clearForm());
        quitButton.addActionListener(event -> quitApp());
        panel.add(displayButton); panel.add(removeButton); panel.add(clearButton); panel.add(quitButton);
        return panel;
    }

    private void addItem()
    {
        try
        {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            Product product = new Product(productField.getText(), price);
            LineItem item = new LineItem(product, quantity);
            pendingItems.add(item);
            tableModel.addRow(new Object[]{product.getName(), quantity,
                    "$" + product.getUnitPrice().toPlainString(), "$" + item.getTotal().toPlainString()});
            invoiceArea.setText("");
            productField.setText(""); quantityField.setText(""); priceField.setText("");
            productField.requestFocusInWindow();
        }
        catch (NumberFormatException exception)
        {
            showError("Enter a positive whole-number quantity and a numeric price, such as 29.95.");
        }
        catch (ArithmeticException exception)
        {
            showError("Enter a unit price with no more than two decimal places.");
        }
        catch (IllegalArgumentException exception)
        {
            showError(exception.getMessage());
        }
    }

    private void displayInvoice()
    {
        try
        {
            if (!productField.getText().trim().isEmpty() || !quantityField.getText().trim().isEmpty()
                    || !priceField.getText().trim().isEmpty())
            {
                showError("Click Add Item to save the item you are entering, or clear its three fields.");
                return;
            }
            Address address = new Address(customerField.getText(), streetField.getText(), cityStateZipField.getText());
            Invoice invoice = new Invoice(titleField.getText(), address);
            if (pendingItems.isEmpty())
            {
                showError("Add at least one line item before displaying the invoice.");
                return;
            }
            for (LineItem item : pendingItems)
                invoice.addItem(item);
            invoiceArea.setText(invoice.formatInvoice());
            invoiceArea.setCaretPosition(0);
        }
        catch (IllegalArgumentException exception)
        {
            showError(exception.getMessage());
        }
    }

    private void removeItem()
    {
        int row = itemTable.getSelectedRow();
        if (row == -1)
        {
            showError("Select a line item to remove.");
            return;
        }
        pendingItems.remove(row);
        tableModel.removeRow(row);
        invoiceArea.setText("");
    }

    private void clearForm()
    {
        titleField.setText("INVOICE");
        customerField.setText(""); streetField.setText(""); cityStateZipField.setText("");
        productField.setText(""); quantityField.setText(""); priceField.setText("");
        pendingItems.clear(); tableModel.setRowCount(0); invoiceArea.setText("");
    }

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Check invoice", JOptionPane.WARNING_MESSAGE);
    }

    private void quitApp()
    {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            dispose();
    }
}
