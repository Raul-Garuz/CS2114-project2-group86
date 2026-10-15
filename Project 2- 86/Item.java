import java.util.Objects;

/**
 * One batch (shipment) of one product: name, quantity and unit price.
 * Immutable: once created, an Item never changes.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class Item
{
    /** Largest quantity allowed in one batch or cart line. */
    public static final int MAX_QUANTITY = 1000;
    /** Longest product name allowed, after normalizing. */
    public static final int MAX_NAME_LENGTH = 30;
    /** Lowest unit price allowed. */
    public static final double MIN_PRICE = 0.01;
    /** Highest unit price allowed. */
    public static final double MAX_PRICE = 10000.00;

    private final String name;
    private final int quantity;
    private final double unitPrice;

    /**
     * Creates a batch.
     *
     * @param name      product name (normalized: trimmed, lowercase)
     * @param quantity  units in the batch, 1 to 1000
     * @param unitPrice price per unit, $0.01 to $10,000 (rounded to cents)
     * @throws IllegalArgumentException if any value is invalid
     */
    public Item(String name, int quantity, double unitPrice)
    {
        this.name = normalize(name);
        if (quantity < 1 || quantity > MAX_QUANTITY)
        {
            throw new IllegalArgumentException(
                "Quantity must be between 1 and " + MAX_QUANTITY + ".");
        }
        if (Double.isNaN(unitPrice) || unitPrice < MIN_PRICE
            || unitPrice > MAX_PRICE)
        {
            throw new IllegalArgumentException(
                "Price must be between $0.01 and $10,000.00.");
        }
        this.quantity = quantity;
        this.unitPrice = Math.round(unitPrice * 100) / 100.0;
    }

    /**
     * Trims, collapses inner spaces and lowercases a product name.
     *
     * @param name the raw name
     * @return the normalized name
     * @throws IllegalArgumentException if blank or over 30 characters
     */
    public static String normalize(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Item name can't be empty.");
        }
        String clean = name.trim().replaceAll("\\s+", " ").toLowerCase();
        
        if (!clean.matches(".*[a-z].*"))
        {
            throw new IllegalArgumentException(
                "Item name must contain at least one letter.");
        }
        
        if (clean.length() > MAX_NAME_LENGTH)
        {
            throw new IllegalArgumentException(
                "Item name must be " + MAX_NAME_LENGTH
                + " characters or fewer.");
        }
        return clean;
    }

    /**
     * @return the normalized product name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the number of units in this batch
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * @return the price per unit, rounded to cents
     */
    public double getUnitPrice()
    {
        return unitPrice;
    }

    /**
     * @return quantity times unit price, rounded to cents
     */
    public double getSubtotal()
    {
        return Math.round(quantity * unitPrice * 100) / 100.0;
    }

    /**
     * Returns a new batch with the same name and price. This one is
     * unchanged.
     *
     * @param newQuantity the quantity for the new batch
     * @return the new Item
     * @throws IllegalArgumentException if newQuantity is out of range
     */
    public Item withQuantity(int newQuantity)
    {
        return new Item(name, newQuantity, unitPrice);
    }

    /**
     * Two Items are equal when name, quantity and price all match.
     *
     * @param other the object to compare with
     * @return true if equal
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Item))
        {
            return false;
        }
        Item that = (Item)other;
        return name.equals(that.name) && quantity == that.quantity
            && Double.compare(unitPrice, that.unitPrice) == 0;
    }

    /**
     * @return a hash code consistent with equals
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name, quantity, unitPrice);
    }

    /**
     * @return e.g. "milk x2 @ $3.49 = $6.98"
     */
    @Override
    public String toString()
    {
        return String.format("%s x%d @ $%.2f = $%.2f",
            name, quantity, unitPrice, getSubtotal());
    }
}
