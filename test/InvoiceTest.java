import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/** Tests the invoice model without opening a Swing window. */
public class InvoiceTest
{
    private Address sampleAddress()
    {
        return new Address("Sam's Small Appliances", "100 Main Street", "Anytown, CA 98765");
    }

    @Test
    void addressFormatsThreeLines()
    {
        assertEquals("Sam's Small Appliances\n100 Main Street\nAnytown, CA 98765",
                sampleAddress().formatAddress());
    }

    @Test
    void addressRequiresAllFields()
    {
        assertThrows(IllegalArgumentException.class, () -> new Address("", "Street", "City"));
        assertThrows(IllegalArgumentException.class, () -> new Address("Sam", " ", "City"));
        assertThrows(IllegalArgumentException.class, () -> new Address("Sam", "Street", null));
    }

    @Test
    void productStoresNameAndPrice()
    {
        Product product = new Product(" Toaster ", new BigDecimal("29.95"));
        assertEquals("Toaster", product.getName());
        assertEquals(new BigDecimal("29.95"), product.getUnitPrice());
    }

    @Test
    void invalidProductsAreRejected()
    {
        assertThrows(IllegalArgumentException.class, () -> new Product("", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Product("Toaster", new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> new Product("Toaster", null));
        assertThrows(ArithmeticException.class, () -> new Product("Toaster", new BigDecimal("1.999")));
    }

    @Test
    void lineItemMultipliesPriceByQuantity()
    {
        Product product = new Product("Toaster", new BigDecimal("29.95"));
        LineItem item = new LineItem(product, 3);
        assertSame(product, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("89.85"), item.getTotal());
    }

    @Test
    void invalidLineItemsAreRejected()
    {
        Product product = new Product("Toaster", BigDecimal.ONE);
        assertThrows(IllegalArgumentException.class, () -> new LineItem(product, 0));
        assertThrows(IllegalArgumentException.class, () -> new LineItem(product, -2));
        assertThrows(IllegalArgumentException.class, () -> new LineItem(null, 1));
    }

    @Test
    void sampleInvoiceTotals154Dollars78Cents()
    {
        Invoice invoice = new Invoice("INVOICE", sampleAddress());
        invoice.addItem(new LineItem(new Product("Toaster", new BigDecimal("29.95")), 3));
        invoice.addItem(new LineItem(new Product("Hair Dryer", new BigDecimal("24.95")), 1));
        invoice.addItem(new LineItem(new Product("Car Vacuum", new BigDecimal("19.99")), 2));
        assertEquals(new BigDecimal("154.78"), invoice.getAmountDue());
        String text = invoice.formatInvoice();
        for (String value : new String[]{"INVOICE", "Sam's Small Appliances", "100 Main Street",
                "Anytown, CA 98765", "Item", "Qty", "Price", "Total", "Toaster", "$89.85",
                "Hair Dryer", "$24.95", "Car Vacuum", "$39.98", "AMOUNT DUE:  $154.78"})
            assertTrue(text.contains(value), "Missing invoice value: " + value);
    }

    @Test
    void moneyCalculationsRemainExact()
    {
        Invoice invoice = new Invoice("INVOICE", sampleAddress());
        invoice.addItem(new LineItem(new Product("Small part", new BigDecimal("0.10")), 3));
        assertEquals(new BigDecimal("0.30"), invoice.getAmountDue());
    }

    @Test
    void emptyInvoiceStartsAtZeroAndItemsCannotBeChangedExternally()
    {
        Invoice invoice = new Invoice("INVOICE", sampleAddress());
        assertEquals(new BigDecimal("0.00"), invoice.getAmountDue());
        assertThrows(UnsupportedOperationException.class,
                () -> invoice.getItems().add(new LineItem(new Product("Part", BigDecimal.ONE), 1)));
    }

    @Test
    void invoiceRejectsMissingHeaderAndNullItems()
    {
        assertThrows(IllegalArgumentException.class, () -> new Invoice("", sampleAddress()));
        assertThrows(IllegalArgumentException.class, () -> new Invoice("INVOICE", null));
        Invoice invoice = new Invoice("INVOICE", sampleAddress());
        assertThrows(IllegalArgumentException.class, () -> invoice.addItem(null));
    }
}
