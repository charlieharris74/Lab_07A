import javax.swing.SwingUtilities;

/** Starts the invoice application. */
public class InvoiceRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new InvoiceFrame().setVisible(true));
    }
}
