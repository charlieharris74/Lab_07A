/** The customer's name and mailing address. */
public class Address
{
    private final String customerName;
    private final String street;
    private final String cityStateZip;

    public Address(String customerName, String street, String cityStateZip)
    {
        if (customerName == null || customerName.trim().isEmpty()
                || street == null || street.trim().isEmpty()
                || cityStateZip == null || cityStateZip.trim().isEmpty())
            throw new IllegalArgumentException("Enter the customer name and complete address.");
        this.customerName = customerName.trim();
        this.street = street.trim();
        this.cityStateZip = cityStateZip.trim();
    }

    public String getCustomerName() { return customerName; }
    public String getStreet() { return street; }
    public String getCityStateZip() { return cityStateZip; }

    public String formatAddress()
    {
        return customerName + "\n" + street + "\n" + cityStateZip;
    }
}
