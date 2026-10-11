import java.math.BigDecimal;
import java.math.RoundingMode;

/** A product name and its price per unit. */
public class Product
{
    private final String name;
    private final BigDecimal unitPrice;

    public Product(String name, BigDecimal unitPrice)
    {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Enter a product name.");
        if (unitPrice == null || unitPrice.signum() < 0)
            throw new IllegalArgumentException("Unit price must be zero or greater.");
        this.name = name.trim();
        this.unitPrice = unitPrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
