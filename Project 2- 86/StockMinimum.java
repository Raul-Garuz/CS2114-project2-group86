import java.util.Objects;

/**
 * A low-stock rule: warn when a product's stock drops below a minimum.
 * Immutable, like Item.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class StockMinimum
{
    private final String name;
    private final int minimum;

    /**
     * Creates a rule.
     *
     * @param name    product name (normalized like Item names)
     * @param minimum warn when stock is below this; 1 to 1000
     * @throws IllegalArgumentException if the name or minimum is invalid
     */
    public StockMinimum(String name, int minimum)
    {
        this.name = Item.normalize(name);
        if (minimum < 1 || minimum > Item.MAX_QUANTITY)
        {
            throw new IllegalArgumentException(
                "Minimum must be between 1 and " + Item.MAX_QUANTITY + ".");
        }
        this.minimum = minimum;
    }

    /**
     * @return the normalized product name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the minimum before an alert
     */
    public int getMinimum()
    {
        return minimum;
    }

    /**
     * @param stock the product's current units
     * @return true if that stock is below the minimum
     */
    public boolean isLow(int stock)
    {
        return stock < minimum;
    }

    /**
     * Two rules are equal when the name and minimum match.
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
        if (!(other instanceof StockMinimum))
        {
            return false;
        }
        StockMinimum that = (StockMinimum)other;
        return name.equals(that.name) && minimum == that.minimum;
    }

    /**
     * @return a hash code consistent with equals
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name, minimum);
    }

    /**
     * @return e.g. "milk: minimum 5"
     */
    @Override
    public String toString()
    {
        return name + ": minimum " + minimum;
    }
}
