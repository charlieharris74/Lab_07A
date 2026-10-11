import java.math.BigDecimal;

/** One product and quantity on the invoice. */
public class LineItem
{
    private final Product product;
    private final int quantity;

    public LineItem(Product product, int quantity)
    {
        if (product == null)
            throw new IllegalArgumentException("A line item needs a product.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be a positive whole number.");
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public BigDecimal getTotal()
    {
        return product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
