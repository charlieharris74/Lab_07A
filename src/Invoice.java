import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** An invoice header, customer address, and collection of line items. */
public class Invoice
{
    private final String title;
    private final Address customerAddress;
    private final List<LineItem> items = new ArrayList<>();

    public Invoice(String title, Address customerAddress)
    {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Enter an invoice title.");
        if (customerAddress == null)
            throw new IllegalArgumentException("An invoice needs a customer address.");
        this.title = title.trim();
        this.customerAddress = customerAddress;
    }

    public String getTitle() { return title; }
    public Address getCustomerAddress() { return customerAddress; }
    public List<LineItem> getItems() { return Collections.unmodifiableList(items); }

    public void addItem(LineItem item)
    {
        if (item == null)
            throw new IllegalArgumentException("Cannot add an empty line item.");
        items.add(item);
    }

    public BigDecimal getAmountDue()
    {
        BigDecimal total = new BigDecimal("0.00");
        for (LineItem item : items)
            total = total.add(item.getTotal());
        return total;
    }

    /** Produces the sample's title, address, item columns, and amount due. */
    public String formatInvoice()
    {
        int itemWidth = 24;
        for (LineItem item : items)
            itemWidth = Math.max(itemWidth, item.getProduct().getName().length());
        String rowFormat = "%-" + itemWidth + "s %7s %12s %14s%n";
        int tableWidth = itemWidth + 36;
        String divider = "-".repeat(tableWidth);
        StringBuilder text = new StringBuilder();
        int titlePadding = Math.max(0, (tableWidth - title.length()) / 2);
        text.append(" ".repeat(titlePadding)).append(title).append("\n\n");
        text.append(customerAddress.formatAddress()).append("\n\n");
        text.append(divider).append("\n");
        text.append(String.format(Locale.US, rowFormat, "Item", "Qty", "Price", "Total"));
        text.append(divider).append("\n");
        for (LineItem item : items)
        {
            text.append(String.format(Locale.US, rowFormat, item.getProduct().getName(),
                    item.getQuantity(), "$" + item.getProduct().getUnitPrice().toPlainString(),
                    "$" + item.getTotal().toPlainString()));
        }
        text.append(divider).append("\n");
        text.append("AMOUNT DUE:  $").append(getAmountDue().toPlainString()).append("\n");
        return text.toString();
    }
}
